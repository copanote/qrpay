package com.bccard.qrpay.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.regex.Pattern;

public class MpmDateTimeUtils {
    public static final String PATTERN_YEAR_TO_DATE = "yyyyMMdd";
    public static final String PATTERN_YEAR_TO_SEC = "yyyyMMddHHmmss";
    public static final String PATTERN_YEAR_TO_MICROSEC = "yyyyMMddHHmmssSSS";

    public static final DateTimeFormatter FORMATTER_YEAR_TO_DATE = DateTimeFormatter.ofPattern(PATTERN_YEAR_TO_DATE);
    public static final DateTimeFormatter FORMATTER_YEAR_TO_TIME = DateTimeFormatter.ofPattern("HHmmss");
    public static final DateTimeFormatter FORMATTER_YEAR_TO_SEC = DateTimeFormatter.ofPattern(PATTERN_YEAR_TO_SEC);

    public static final DateTimeFormatter FORMATTER_yyyy = DateTimeFormatter.ofPattern("yyyy");
    public static final DateTimeFormatter FORMATTER_yyMMdd = DateTimeFormatter.ofPattern("yyMMdd");
    public static final DateTimeFormatter FORMATTER_MMddHHmmss = DateTimeFormatter.ofPattern("MMddHHmmss");
    public static final DateTimeFormatter FORMATTER_MMdd = DateTimeFormatter.ofPattern("MMdd");
    public static final DateTimeFormatter FORMATTER_HHmmss = DateTimeFormatter.ofPattern("HHmmss");
    public static final DateTimeFormatter FORMATTER_ISO8601_UTC =
            new DateTimeFormatterBuilder().appendInstant(0).toFormatter().withZone(ZoneId.of("UTC"));

    /**
     * text를 pattern의 LocalDateTime으로 변환
     *
     * @param text
     * @param pattern
     * @return
     */
    public static LocalDateTime ofPattern(String text, String pattern) {
        if (text == null || text.length() != pattern.length()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        if (PATTERN_YEAR_TO_MICROSEC.equals(pattern)) {
            formatter = new DateTimeFormatterBuilder()
                    .appendPattern(PATTERN_YEAR_TO_SEC)
                    .appendValue(ChronoField.MILLI_OF_SECOND, 3)
                    .toFormatter();
        }
        return LocalDateTime.parse(text, formatter);
    }

    /**
     * text를 pattern의 OffsetDateTime으로 변환
     *
     * @param text
     * @param zoneId
     * @param pattern
     * @return
     */
    public static OffsetDateTime ofPattern(String text, ZoneId zoneId, String pattern) {
        if (text == null || text.length() != pattern.length() || zoneId == null) {
            return null;
        }

        LocalDateTime localDateTime = ofPattern(text, pattern);
        return localToOffset(localDateTime, zoneId);
    }

    /**
     * MMddHHmmss에 대해 Year를 확인하여 OffsetDateTime으로 응답
     *
     * @param MMddHHmmss
     * @return
     */
    public static OffsetDateTime ofMMddHHmmss(String MMddHHmmss, ZoneId zoneId) {
        String inMonth = MMddHHmmss.substring(0, 2);
        String nowMonth = String.valueOf(OffsetDateTime.now().getMonthValue());

        // 현재 1월이고, 입력월이 12월인 경우 작년 연도 값으로 OffsetDateTime을 생성
        int year = OffsetDateTime.now().getYear();
        if ("12".equals(inMonth) && "1".equals(nowMonth)) {
            year = year - 1;
        }

        String dateTimeStr = year + MMddHHmmss;
        return MpmDateTimeUtils.ofPattern(dateTimeStr, zoneId, MpmDateTimeUtils.PATTERN_YEAR_TO_SEC);
    }

    /**
     * text를 pattern의 OffsetDateTime으로 변환
     *
     * @param localDateTime
     * @param zoneId
     * @return
     */
    public static OffsetDateTime localToOffset(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        return zonedDateTime.toOffsetDateTime();
    }

    /**
     * ZoneId에 해당하는 ZoneOffset으로 변환
     *
     * @param offsetDateTime
     * @param zoneId
     * @return
     */
    public static OffsetDateTime convertByZoneId(OffsetDateTime offsetDateTime, ZoneId zoneId) {
        if (offsetDateTime == null || zoneId == null) {
            return null;
        }

        ZonedDateTime convertedZonedDateTime = offsetDateTime.atZoneSameInstant(zoneId);
        return convertedZonedDateTime.toOffsetDateTime();
    }


    public static String generateDtmNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * <pre>
     * 현재시간을 DateTimeFormatter 형식으로 출력
     *  - DateTimeFormatter에는 타임존이 추가 가능
     * </pre>
     */
    public static String generateDtmNow(DateTimeFormatter formatter) {
        return OffsetDateTime.now().format(formatter);
    }

    /**
     * 현재시간을 ZoneId의 시간정보로 pattern의 형식으로 생성
     *
     * @param pattern
     * @param zoneId
     * @return
     */
    public static String generateDtmNow(String pattern, ZoneId zoneId) {
        OffsetDateTime now = OffsetDateTime.now();

        // zoneId가 존재하는 경우에는 zoneId 정보를 변경
        if (zoneId != null) {
            now = now.atZoneSameInstant(zoneId).toOffsetDateTime();
        }

        return now.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String generateDtmNowAddSeconds(String pattern, long seconds) {
        return LocalDateTime.now().plusSeconds(seconds).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String generateDtmNowAddHours(String pattern, long hours) {
        return LocalDateTime.now().plusHours(hours).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String generateDtmNowAddDays(String pattern, long days) {
        return LocalDateTime.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 해당년월의 마지막날짜를 구하는 메소드
     *
     * @param yyyyMM
     * @return
     */
    public static String getEndOfMonth(String yyyyMM) {
        if (yyyyMM.length() != 6) {
            throw new IllegalArgumentException();
        }
        String yearStr = yyyyMM.substring(0, 4);
        String monthStr = yyyyMM.substring(4, 6);
        YearMonth month = YearMonth.of(Integer.parseInt(yearStr), Integer.parseInt(monthStr));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return month.atEndOfMonth().format(formatter);
    }

    /**
     * OffsetDateTime -> String으로 DateTimeFormatter에 맞게 변환
     *
     * @param offsetDateTime
     * @param formatter
     * @return
     */
    public static String format(OffsetDateTime offsetDateTime, DateTimeFormatter formatter) {
        if (offsetDateTime == null) {
            return null;
        }

        return offsetDateTime.format(formatter);
    }

    /**
     * String -> OffsetDateTime으로 DateTimeFormatter에 맞게 변환(시작시간값 추가)
     *
     * @param yyyyMMDD
     * @return
     */
    public static OffsetDateTime fromYYYYMMDDToStartDateTime(String yyyyMMDD, ZoneId zoneId) {

        if (StringUtils.isEmpty(yyyyMMDD)) {
            throw new IllegalArgumentException();
        }
        Pattern pattern = Pattern.compile("^\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$");
        if (!pattern.matcher(yyyyMMDD).matches()) {
            throw new IllegalArgumentException();
        }
        LocalDateTime startDateTime =
                LocalDateTime.parse(yyyyMMDD.concat("000000"), MpmDateTimeUtils.FORMATTER_YEAR_TO_SEC);

        return localToOffset(startDateTime, zoneId);
    }

    /**
     * String -> OffsetDateTime으로 DateTimeFormatter에 맞게 변환(종료시간값 추가)
     *
     * @param yyyyMMDD
     * @return
     */
    public static OffsetDateTime fromYYYYMMDDToEndDateTime(String yyyyMMDD, ZoneId zoneId) {

        if (StringUtils.isEmpty(yyyyMMDD)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$");
        if (!pattern.matcher(yyyyMMDD).matches()) {
            throw new IllegalArgumentException();
        }
        LocalDateTime startDateTime =
                LocalDateTime.parse(yyyyMMDD.concat("235959"), MpmDateTimeUtils.FORMATTER_YEAR_TO_SEC);

        return localToOffset(startDateTime, zoneId);
    }

    /**
     * 날짜 값을 받아 오늘과 비교한 결과 값을 리턴한다
     *
     * @param yyyyMMddHHmmss
     * @return boolean  yyyyMMDD가 null일경우 or 파라미터의 yyyyMMDD가 오늘보다 작으면 true, 아니면 false
     */
    public static boolean isDatetimeExpired(String yyyyMMddHHmmss) {
        if (StringUtils.isEmpty(yyyyMMddHHmmss) || yyyyMMddHHmmss.length() != 14) {
            return true;
        }
        LocalDate today = LocalDate.now();
        LocalDate compareDate = LocalDate.parse(yyyyMMddHHmmss, FORMATTER_YEAR_TO_SEC);

        return today.isAfter(compareDate);
    }

    /**
     * 날짜 값을 받아 오늘과 비교한 결과 값을 리턴한다
     *
     * @param yyyyMM
     * @return boolean  yyyyMM가 null일경우 or 파라미터의 yyyyMM가 오늘보다 작으면 true, 아니면 false
     */
    public static boolean isYearMonthExpired(String yyyyMM) {
        if (StringUtils.isEmpty(yyyyMM) || yyyyMM.length() != 6) {
            return true;
        }
        int yyyy = Integer.parseInt(yyyyMM.substring(0, 4));
        int mm = Integer.parseInt(yyyyMM.substring(4));
        int dd = LocalDate.of(yyyy, mm, 1).lengthOfMonth();
        return LocalDate.now().isAfter(LocalDate.of(yyyy, mm, dd));
    }
}
