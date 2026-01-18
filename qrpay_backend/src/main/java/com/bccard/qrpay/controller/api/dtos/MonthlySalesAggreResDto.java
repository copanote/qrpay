package com.bccard.qrpay.controller.api.dtos;

import com.bccard.qrpay.domain.transaction.dto.MonthlySalesDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class MonthlySalesAggreResDto {
    private String yearMonth;
    private List<MonthlySalesDto> list;

    public static List<MonthlySalesAggreResDto> from(List<MonthlySalesDto> list) {
        Map<String, List<MonthlySalesDto>> collect =
                list.stream().collect(Collectors.groupingBy(MonthlySalesDto::getYearMonth));
        
        List<MonthlySalesAggreResDto> result = new ArrayList<>();
        for (Map.Entry<String, List<MonthlySalesDto>> e : collect.entrySet()) {
            MonthlySalesAggreResDto ms = MonthlySalesAggreResDto
                    .builder()
                    .yearMonth(e.getKey())
                    .list(e.getValue())
                    .build();
            result.add(ms);
        }
        return result;
    }
}
