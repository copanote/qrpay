package com.bccard.qrpay.controller.api;

import com.bccard.qrpay.config.web.argumentresolver.LoginMember;
import com.bccard.qrpay.controller.api.common.QrpayApiResponse;
import com.bccard.qrpay.controller.api.dtos.MonthlySalesAggreResDto;
import com.bccard.qrpay.controller.api.dtos.TransHistorySearchDto;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.transaction.TransactionService;
import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import com.bccard.qrpay.domain.transaction.dto.TransHistoryResponse;
import com.bccard.qrpay.domain.transaction.dto.TransSearchCondition;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/qrpay/api")
public class TransactionApiController {

    private final TransactionService transactionService;

    @RequestMapping(value = "/v1/transaction/monthly-sales")
    @ResponseBody
    public ResponseEntity<?> monthlySales(@LoginMember Member member) {
        log.info("Member={}", member.getMemberId());
        Merchant m = member.getMerchant();
        List<MonthlySalesDto> monthlySalesDtos = transactionService.fetchMonthlySalesList(m.getMerchantId(), 3);
        return ResponseEntity.ok(QrpayApiResponse.ok(member, MonthlySalesAggreResDto.from(monthlySalesDtos)));
    }

    @RequestMapping(value = "/v1/transaction/history")
    @ResponseBody
    public ResponseEntity<?> history(
            @LoginMember Member member,
            @ModelAttribute @Valid TransHistorySearchDto searchCondition,
            @PageableDefault Pageable pageable) {
        log.info("Member={}", member.getMemberId());
        log.info("searchCondition={}", searchCondition);
        log.info("pageable={}", pageable);
        Merchant m = member.getMerchant();

        TransSearchCondition sc = TransSearchCondition.from(m, searchCondition);
        Slice<TransHistoryResponse> transactionHistory = transactionService.getTransactionHistory(sc, pageable);
        return ResponseEntity.ok(QrpayApiResponse.ok(member, transactionHistory));
    }
}
