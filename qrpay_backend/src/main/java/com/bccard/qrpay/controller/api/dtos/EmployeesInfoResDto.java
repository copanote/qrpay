package com.bccard.qrpay.controller.api.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@Builder
public class EmployeesInfoResDto {
    private int size;
    private List<EmployeesInfoDto> list;

    public static EmployeesInfoResDto of(List<EmployeesInfoDto> list) {
        return EmployeesInfoResDto.builder()
                .size(list.size())
                .list(list)
                .build();
    }


}
