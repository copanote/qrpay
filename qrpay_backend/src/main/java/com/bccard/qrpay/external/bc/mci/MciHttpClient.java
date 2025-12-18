package com.bccard.qrpay.external.bc.mci;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class MciHttpClient {

    private final RestClient restClient;

}
