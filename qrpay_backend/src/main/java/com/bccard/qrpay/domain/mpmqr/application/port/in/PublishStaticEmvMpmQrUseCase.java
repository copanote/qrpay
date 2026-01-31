package com.bccard.qrpay.domain.mpmqr.application.port.in;

import com.bccard.qrpay.domain.mpmqr.MpmQrPublication;

public interface PublishStaticEmvMpmQrUseCase {
    MpmQrPublication publishStatic(PublishStaticEmvMpmQrCommand command);
}
