package com.dao.exchange;

import com.dao.currency.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRate {
    private Integer id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
