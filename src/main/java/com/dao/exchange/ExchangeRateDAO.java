package com.dao.exchange;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeRateDAO {
    List<ExchangeRate> getAllExchangeRates() throws SQLException;
}
