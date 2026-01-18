package com.bccard.qrpay.config;


import com.copanote.emvmpm.definition.EmvMpmDefinition;
import com.copanote.emvmpm.definition.packager.EmvMpmPackager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j
@Configuration
public class EmvMpmQrConfig {

    @Bean
    public EmvMpmDefinition emvmpmBcDefinition() throws ParserConfigurationException, IOException, SAXException {
        EmvMpmPackager emp = new EmvMpmPackager();
        ClassPathResource classPathResource = new ClassPathResource("emvmpm_bc.xml");
        log.info("emvmpm_bc.xml:{}", classPathResource.getPath());
        emp.setEmvMpmPackager(classPathResource.getInputStream());
        return emp.create();
    }
}
