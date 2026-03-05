package com.servlets;

import com.dao.exchange.ExchangeRate;
import com.dao.exchange.ExchangeRateDAO;
import com.dao.exchange.ExchangeRateDAOImpl;
import com.utils.ErrorHandler;
import com.utils.Validator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "exchange-rates-servlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        try{
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();

            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), exchangeRateDAO.getAllExchangeRates());
            resp.setStatus(201);
        } catch (SQLException e) {
            ErrorHandler.sendError(501, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try{
            Optional<String> code1 = Validator.validateCurrencyCode(req.getParameter("baseCurrencyCode"));
            if(code1.isEmpty()){
                ErrorHandler.sendError(400, "Invalid or missing base currency code", resp);
                return;
            }
            Optional<String> code2 = Validator.validateCurrencyCode(req.getParameter("targetCurrencyCode"));
            if(code2.isEmpty()){
                ErrorHandler.sendError(400, "Invalid or missing target currency code", resp);
                return;
            }
            Optional<BigDecimal> rate = Validator.validateBigDecimal(req.getParameter("rate"));
            if(rate.isEmpty()){
                ErrorHandler.sendError(400, "Invalid or missing rate", resp);
                return;
            }

            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();

            Optional<ExchangeRate> newExchangeRate = exchangeRateDAO.addExchangeRate(code1.get(), code2.get(), rate.get());
            if(newExchangeRate.isEmpty()){
                ErrorHandler.sendError(500, "Failed to get exchange rate", resp);
                return;
            }

            mapper.writeValue(resp.getWriter(), newExchangeRate.get());
            resp.setStatus(201);

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                ErrorHandler.sendError(409, "The exchange rate with this codes already exists.", resp);
                return;
            }
            if ("23502".equals(e.getSQLState())) {
                ErrorHandler.sendError(404, "One (or both) of the currencies in the currency pair does not exist in the database.", resp);
                return;
            }
            ErrorHandler.sendError(500, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }
}
