package com.bccard.qrpay.controller.api.dtos;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class EmployeesInfoResDto {
    private int size;
    private List<EmployeesInfoDto> list;

    public static EmployeesInfoResDto of(List<EmployeesInfoDto> list) {
        return EmployeesInfoResDto.builder().size(list.size()).list(list).build();
    }
}
