package com.dao.exchange;

import com.dao.ConnectionPool;
import com.dao.currency.Currency;
import com.dao.currency.CurrencyDAO;
import com.dao.currency.CurrencyDAOImpl;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {

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
            while (resultSet.next()) {
                allExchangeRates.add(createExchangeRate(resultSet));
            }
        }
        return allExchangeRates;
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByCodes(String baseCode, String targetCode) throws SQLException {
        String sql = "SELECT " +
                "rates.id, " +
                "base.id, base.code, base.full_name, base.sign, " +
                "target.id, target.code, target.full_name, target.sign, " +
                "rates.rate " +
                "from exchange_rates rates " +
                "join currencies base on base.code=? AND rates.base_currency_id=base.id " +
                "join currencies target on target.code=? AND rates.target_currency_id=target.id; ";
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, baseCode);
            stmt.setString(2, targetCode);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }

            Currency baseCurrency = new Currency(
                    resultSet.getInt(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5)
            );
            Currency targetCurrency = new Currency(
                    resultSet.getInt(6),
                    resultSet.getString(7),
                    resultSet.getString(8),
                    resultSet.getString(9)
            );

            return Optional.of(
                    new ExchangeRate(
                            resultSet.getInt(1),
                            baseCurrency,
                            targetCurrency,
                            resultSet.getBigDecimal(10)
                    )
            );
        }
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
