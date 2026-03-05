package com.service;

import com.dao.exchange.ExchangeRate;
import com.dao.exchange.ExchangeRateDAO;
import com.dao.exchange.ExchangeRateDAOImpl;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    ObjectMapper mapper;

    public ExchangeService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Optional<ObjectNode> exchange(String form, String to, BigDecimal amount) throws SQLException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();
        Optional<ExchangeRate> optionalExchangeRate = exchangeRateDAO.getExchangeRateByCodes(form, to);
        if(optionalExchangeRate.isPresent()){
            ExchangeRate exchangeRate = optionalExchangeRate.get();
            BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate()).setScale(4, RoundingMode.HALF_UP);
            ObjectNode node = mapper.valueToTree(exchangeRate);
            node.remove("id");
            node.put("amount", amount);
            node.put("convertedAmount", convertedAmount);
            return Optional.of(node);
        }

        optionalExchangeRate = exchangeRateDAO.getExchangeRateByCodes(to, form);
        if(optionalExchangeRate.isPresent()){
            ExchangeRate exchangeRate = optionalExchangeRate.get();
            exchangeRate.setRate(BigDecimal.ONE.divide(exchangeRate.getRate(), exchangeRate.getRate().scale(), RoundingMode.HALF_UP));
            BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate()).setScale(4, RoundingMode.HALF_UP);
            ObjectNode node = mapper.valueToTree(exchangeRate);
            node.remove("id");
            node.put("amount", amount);
            node.put("convertedAmount", convertedAmount);
            return Optional.of(node);
        }

        Optional<ExchangeRate> rateA = exchangeRateDAO.getExchangeRateByCodes("USD", form);
        Optional<ExchangeRate> rateB = exchangeRateDAO.getExchangeRateByCodes("USD", to);

        /*
        * eur -> rub
        * rub -> usd
        * usd -> eur
        * */
        if(rateA.isEmpty() || rateB.isEmpty()){
            return Optional.empty();
        }

        BigDecimal rate = BigDecimal.ONE
                .divide(rateA.get().getRate(), rateA.get().getRate().scale(), RoundingMode.HALF_UP)
                .multiply(rateB.get().getRate())
                .setScale(4, RoundingMode.HALF_UP);
        ExchangeRate tempRate = new ExchangeRate(rateA.get().getTargetCurrency(), rateB.get().getTargetCurrency(), rate);
        BigDecimal convertedAmount = amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);
        ObjectNode node = mapper.valueToTree(tempRate);
        node.remove("id");
        node.put("amount", amount);
        node.put("convertedAmount", convertedAmount);

        return Optional.of(node);
    }
}
