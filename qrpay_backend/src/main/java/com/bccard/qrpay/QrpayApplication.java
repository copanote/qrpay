package com.bccard.qrpay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Slf4j
@SpringBootApplication
public class QrpayApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(QrpayApplication.class, args);
    }

    @Bean
    @Profile({"local", "default"})
    ApplicationRunner initDatabase() {
        return args -> {
            // 1. schema.sql 실행 (스키마 생성)
            //            try (Connection conn = dataSource.getConnection();
            //                 Statement stmt = conn.createStatement()) {
            //                String schemaSql = Files.readString(Paths.get("src/main/resources/schema.sql"));
            //                stmt.execute(schemaSql);
            //            }

            // 2. JPA 테이블 생성 (ddl-auto=create 로 자동 실행됨)

            // 3. data.sql 실행

            log.info("ApplicationRunner:initDatabase");
            //            try (Connection conn = dataSource.getConnection();
            //                 Statement stmt = conn.createStatement()) {
            //                ClassPathResource cpr = new ClassPathResource("sql/data.sql");
            //                String dataSql = "";
            //
            //                try (InputStreamReader reader = new InputStreamReader(cpr.getInputStream(),
            // StandardCharsets.UTF_8)) {
            //                    dataSql = FileCopyUtils.copyToString(reader);
            //                } catch (IOException e) {
            //                    throw new RuntimeException("SQL 파일을 읽을 수 없습니다.", e);
            //                }
            //                stmt.execute(dataSql);
            //                conn.commit();
            //            }
        };
    }
}
