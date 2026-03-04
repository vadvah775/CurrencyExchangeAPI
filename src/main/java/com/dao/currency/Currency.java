package com.dao.currency;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Currency {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;
}
