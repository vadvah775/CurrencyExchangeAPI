package com.dao.exchange;

import com.dao.ConnectionPool;
import com.dao.currency.Currency;
import com.dao.currency.CurrencyDAO;
import com.dao.currency.CurrencyDAOImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO{

    ConnectionPool connectionPool;

    public ExchangeRateDAOImpl() throws SQLException {
        connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        String sql = "SELECT * FROM exchange_rates;";
        List<ExchangeRate> allExchangeRates = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            while(resultSet.next()){
                allExchangeRates.add(createExchangeRate(resultSet));
            }
        }
        return allExchangeRates;
    }

    private ExchangeRate createExchangeRate(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt(1);
        Integer baseCurrencyId = resultSet.getInt(2);
        Integer targetCurrencyId = resultSet.getInt(3);
        BigDecimal rate = resultSet.getBigDecimal(4);

        CurrencyDAO currencyDAO = new CurrencyDAOImpl();

        Currency baseCurrency = currencyDAO.getCurrencyById(baseCurrencyId).get();
        Currency targetCurrency = currencyDAO.getCurrencyById(targetCurrencyId).get();

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
