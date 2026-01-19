package com.bccard.qrpay.domain.mpmqr;

import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationQueryRepository;
import com.bccard.qrpay.domain.mpmqr.repository.MpmQrPublicationRepository;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MpmQrPublicationService {

    private final MpmQrPublicationQueryRepository mpmQrPublicationQueryRepository;
    private final MpmQrPublicationRepository mpmQrPublicationCudRepository;

    public String createQrReferenceId() {
        long seq = mpmQrPublicationQueryRepository.getNextSequenceValue();
        String paddedSeq = StringUtils.leftPad(String.valueOf(seq), 9, '0');
        String yyyyString = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.FORMATTER_yyyy);
        return "MQ" + yyyyString + paddedSeq;
    }

    public void findStaticMpmQrOrCreate(Merchant m) {}
}
