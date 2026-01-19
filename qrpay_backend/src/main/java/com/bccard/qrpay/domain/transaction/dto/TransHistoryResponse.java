package com.bccard.qrpay.domain.transaction.dto;

import com.bccard.qrpay.domain.common.code.PaymentStatus;
import com.bccard.qrpay.domain.common.code.ServiceType;
import com.bccard.qrpay.domain.transaction.Transaction;
import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TransHistoryResponse {

    private String transactionId;
    private String affiliateTransactionId;
    private String transactionAt;

    private ServiceType serviceType;
    private PaymentStatus paymentStatus;
    private String approvalNo;
    private String approvedAt; // authNo
    private String affiliateApprovalNo;
    private String maskedPan;
    private Long installment;
    private BigDecimal transactionAmount; // 거래금액
    private BigDecimal principalAmount; // 원금
    private BigDecimal tax;
    private BigDecimal serviceFee;
    private BigDecimal amountBeforeDiscount;
    private BigDecimal amountAfterDiscount;
    private BigDecimal discountedAmount;

    private boolean possibleCancel;

    private PaymentStatus cancelPaymentStatus;
    private String reason;
    private String canceledTransactionAt;
    private String canceledAt;

    @QueryProjection
    public TransHistoryResponse(Transaction auth, Transaction cancel) {
        this.transactionId = auth.getTransactionId();
        this.affiliateTransactionId = auth.getAffiliateTransactionId();
        this.transactionAt = auth.getTransactionAt();
        this.serviceType = auth.getServiceType();
        this.paymentStatus = auth.getPaymentStatus();
        this.approvalNo = auth.getApprovalNo();
        this.approvedAt = auth.getApprovedAt();
        this.affiliateApprovalNo = auth.getAffiliateApprovalNo();
        this.maskedPan = auth.getMaskedPanForReceipt();
        this.installment = auth.getInstalmentPeriod();

        this.transactionAmount = auth.getTransactionAmount();
        this.principalAmount = auth.getPrincipalAmount();
        this.tax = auth.getTax();
        this.serviceFee = auth.getServiceFee();
        this.amountBeforeDiscount = auth.getAmountBeforeDiscount();
        this.amountAfterDiscount = auth.getAmountAfterDiscount();
        this.discountedAmount = auth.getDiscountedAmount();

        if (cancel != null) {
            this.cancelPaymentStatus = cancel.getPaymentStatus();
            this.canceledTransactionAt = cancel.getTransactionAt();
            this.canceledAt = cancel.getApprovedAt();
        }

        this.possibleCancel = false;
        if (cancel == null && auth.getPaymentStatus() == PaymentStatus.APPROVED) {
            possibleCancel = true;
        }
    }
}
