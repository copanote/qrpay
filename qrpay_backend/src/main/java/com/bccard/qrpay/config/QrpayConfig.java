package com.bccard.qrpay.config;


import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Configuration
public class QrpayConfig {

    @Bean
    public EmvMpmDefinition emvmpmBcDefinition() throws ParserConfigurationException, IOException, SAXException {
        EmvMpmPackager emp = new EmvMpmPackager();
        emp.setEmvMpmPackager("emvmpm_bc.xml");
        return emp.create();
    }
}
