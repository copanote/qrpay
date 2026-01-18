package com.bccard.qrpay.domain.transaction;

import com.bccard.qrpay.domain.common.code.AuthorizeType;
import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.merchant.QMerchant;
import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import com.bccard.qrpay.domain.transaction.dto.QTransHistoryResponse;
import com.bccard.qrpay.domain.transaction.dto.TransHistoryResponse;
import com.bccard.qrpay.domain.transaction.dto.TransSearchCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class TransHistoryQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public TransHistoryQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private static final QTransaction auth = new QTransaction("auth");
    private static final QTransaction cancel = new QTransaction("cancel");
    private static final QMerchant merchant = QMerchant.merchant;


    public Page<Transaction> searchPage(TransSearchCondition searchCondition, Pageable pageable) {
        List<Transaction> content = queryFactory
                .selectFrom(auth)
                .where(
                        merchantEq(auth, searchCondition.getMerchant()),
                        between(auth, searchCondition.getStartYmd(), searchCondition.getEndYmd()),
                        endsWith(auth, searchCondition.getAuthNoLast4()),
                        serviceTypeIn(auth, searchCondition.getServiceType()),
                        paymentStatusIn(auth, searchCondition.getPaymentStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(auth.transactionAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(auth.count())
                .from(auth)
                .where(
                        merchantEq(auth, searchCondition.getMerchant()),
                        between(auth, searchCondition.getStartYmd(), searchCondition.getEndYmd()),
                        endsWith(auth, searchCondition.getAuthNoLast4()),
                        serviceTypeIn(auth, searchCondition.getServiceType()),
                        paymentStatusIn(auth, searchCondition.getPaymentStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Slice<TransHistoryResponse> searchSlice(TransSearchCondition searchCondition, Pageable pageable) {
        List<TransHistoryResponse> content = queryFactory
                .select(new QTransHistoryResponse(auth, cancel))
                .from(auth)
                .leftJoin(auth.merchant, merchant)
                .leftJoin(cancel).on(
                        auth.transactionId.eq(cancel.transactionId),
                        auth.affiliateTransactionId.eq(cancel.affiliateTransactionId),
                        cancel.authorizeType.eq(AuthorizeType.CANCEL)
                )
                .where(
                        between(auth, searchCondition.getStartYmd(), searchCondition.getEndYmd()),
                        auth.authorizeType.eq(AuthorizeType.AUTHORIZE),
                        endsWith(auth, searchCondition.getAuthNoLast4()),
                        serviceTypeIn(auth, searchCondition.getServiceType()),
                        paymentStatusIn(auth, searchCondition.getPaymentStatus()),
                        merchantEq(auth, searchCondition.getMerchant())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(auth.transactionAt.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }


    private BooleanExpression merchantEq(QTransaction qt, Merchant m) {
        return m != null ? qt.merchant.eq(m) : null;
    }


    private BooleanExpression endsWith(QTransaction qt, String approvalNoLast4) {

        if (approvalNoLast4 == null || approvalNoLast4.isEmpty()) {
            return null;
        }
        return qt.approvalNo.endsWith(approvalNoLast4);
    }

    private BooleanExpression serviceTypeIn(QTransaction qt, List<ServiceType> serviceType) {
        return serviceType != null ? qt.serviceType.in(serviceType) : null;
    }

    private BooleanExpression paymentStatusIn(QTransaction qt, List<PaymentStatus> paymentStatus) {
        return paymentStatus != null ? qt.paymentStatus.in(paymentStatus) : null;
    }

    private BooleanExpression between(QTransaction qt, String startYmd, String endYmd) {

        if (startYmd == null || startYmd.isEmpty() || endYmd == null || endYmd.isEmpty()) {
            return null;
        }
        return qt.transactionAt.between(startYmd, endYmd);
    }

    public List<MonthlySalesDto> fetchMonthlySalesList(Merchant m, int monthPeriod) {

        LocalDate now = LocalDate.now();

        // 시작일: (monthLimit - 1)만큼 이전으로 가서 그 달의 1일
        String startDate = now.minusMonths(monthPeriod - 1)
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "000000";

        // 종료일: 이번 달의 마지막 날
        String endDate = now.withDayOfMonth(now.lengthOfMonth())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "235959";

        StringExpression yyyymm = auth.transactionAt.substring(0, 6);

        List<MonthlySalesDto> list = queryFactory
                .select(
                        Projections.fields(MonthlySalesDto.class,
                                yyyymm.as("yearMonth"),
                                auth.serviceType.as("serviceType"),
                                auth.transactionAmount.sum().as("totalAuthAmount"),
                                auth.refundAmountToMerchant.sum().as("totalRefundAmount")
                        )
                )
                .from(auth)
                .leftJoin(cancel).on(
                        auth.transactionId.eq(cancel.transactionId)
                                .and(auth.affiliateTransactionId.eq(cancel.affiliateTransactionId))
                                .and(cancel.transactionAt.between(startDate, endDate))
                                .and(cancel.authorizeType.eq(AuthorizeType.CANCEL))
                                .and(cancel.paymentStatus.eq(PaymentStatus.CANCELED))
                                .and(cancel.merchant.eq(m))
                )
                .where(auth.transactionAt.between(startDate, endDate)
                        .and(auth.authorizeType.eq(AuthorizeType.AUTHORIZE))
                        .and(auth.paymentStatus.eq(PaymentStatus.APPROVED))
                        .and(cancel.transactionId.isNull())
                        .and(auth.merchant.eq(m))
                )
                .groupBy(yyyymm, auth.serviceType)
                .orderBy(yyyymm.desc(), auth.serviceType.asc())
                .fetch();

        return list;
    }


}
