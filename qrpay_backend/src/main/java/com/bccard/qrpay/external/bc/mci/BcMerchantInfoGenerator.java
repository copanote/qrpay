package com.bccard.qrpay.external.bc.mci;

import com.bccard.qrpay.controller.api.dtos.BcMerchantInfo;

import java.util.Random;

public class BcMerchantInfoGenerator {

    private static final Random random = new Random();

    public static BcMerchantInfo createRandomMerchant() {
        return BcMerchantInfo.builder()
                .merchantName(generateMerchantName())
                .representativeName(generateKoreanName())
                .merchantNo(generateNumber(10))
                .telNo(generateTelNo())
                .zipCode(generateNumber(5))
                .address(generateAddress1() + random.nextInt(100) + 1 + "층 " + (random.nextInt(900) + 100) + "호")
                .registeredAt(generateRandomDate())
                .build();
    }

    // 1. 다양한 상호명 생성 (업종 + 형용사 조합)
    private static String generateMerchantName() {
        String[] prefix = {"행복한", "신뢰의", "글로벌", "황금", "스마트", "정직한", "강남", "제일"};
        String[] middle = {"커머스", "유통", "푸드", "테크", "시스템", "로지스틱스", "디자인"};
        String[] suffix = {"(주)", "컴퍼니", "마켓", "스토어", ""};

        return prefix[random.nextInt(prefix.length)] + " " +
                middle[random.nextInt(middle.length)] + " " +
                suffix[random.nextInt(suffix.length)];
    }

    // 2. 한국인 이름 조합 생성
    private static String generateKoreanName() {
        String[] lastNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
        String[] firstNames = {"민준", "서연", "도윤", "하은", "지우", "준서", "채원", "현우", "지아", "은우"};
        return lastNames[random.nextInt(lastNames.length)] + firstNames[random.nextInt(firstNames.length)];
    }

    // 3. 지역 및 도로명 조합
    private static String generateAddress1() {
        String[] cities = {"서울특별시", "경기도", "부산광역시", "인천광역시", "경상남도"};
        String[] districts = {"강남구", "수원시 팔달구", "해운대구", "남동구", "창원시 성산구"};
        String[] roads = {"중앙로", "테헤란로", "백범로", "가산디지털로", "한강대로"};
        return cities[random.nextInt(cities.length)] + " " +
                districts[random.nextInt(districts.length)] + " " +
                roads[random.nextInt(roads.length)] + " " + (random.nextInt(200) + 1) + "번길";
    }

    // 4. 랜덤 전화번호 (지역번호 포함)
    private static String generateTelNo() {
        String[] areaCodes = {"02", "031", "032", "051", "010"};
        return areaCodes[random.nextInt(areaCodes.length)] + "-" +
                (random.nextInt(9000) + 1000) + "-" +
                (random.nextInt(9000) + 1000);
    }

    // 5. 고정 길이 숫자 문자열 생성
    private static String generateNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // 6. 날짜 생성 (1990년 ~ 2024년 사이 무작위)
    private static String generateRandomDate() {
        int year = 90 + random.nextInt(35); // 90~124 (2024년까지)
        if (year >= 100) year -= 100; // 00~24년 처리
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return String.format("%02d%02d%02d", year, month, day);
    }
}