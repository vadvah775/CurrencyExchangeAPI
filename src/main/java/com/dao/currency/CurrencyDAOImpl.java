package com.dao.currency;

import com.dao.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO{

    ConnectionPool connectionPool;

    public CurrencyDAOImpl() throws SQLException {
        connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> allCurrencies = new ArrayList<>();
        try(Connection connection = connectionPool.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM currencies;");
            while(resultSet.next()){
                allCurrencies.add(createCurrencies(resultSet));
            }
        }
        return allCurrencies;
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
        String sqlQuerry = "SELECT * FROM currencies WHERE code=?";
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sqlQuerry);
            stmt.setString(1, code);
            ResultSet resultSet = stmt.executeQuery();
            if(!resultSet.next()){
                return Optional.empty();
            }
            return Optional.of(createCurrencies(resultSet));
        }
    }

    private Currency createCurrencies(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt(1);
        String code = resultSet.getString(2);
        String fullName = resultSet.getString(3);
        String sign = resultSet.getString(4);
        return new Currency(id, code, fullName, sign);

    }
}
