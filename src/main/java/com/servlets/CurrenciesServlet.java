package com.servlets;

import com.dao.currency.Currency;
import com.dao.currency.CurrencyDAO;
import com.dao.currency.CurrencyDAOImpl;
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
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "currencies-servlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            CurrencyDAO currencyDAO = new CurrencyDAOImpl();

            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), currencyDAO.getAllCurrencies());
            resp.setStatus(200);
        } catch (SQLException e){
            ErrorHandler.sendError(501, "Data base error", resp);
        } catch (IOException e){
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try{
            Optional<String> validCode = Validator.validateCurrencyCode(req.getParameter("code"));
            if(validCode.isEmpty()){
                ErrorHandler.sendError(400, "Invalid currency code", resp);
                return;
            }
            String code = validCode.get();

            String name = req.getParameter("name");
            if (name == null || name.trim().isEmpty()) {
                ErrorHandler.sendError(400, "Currency name is required", resp);
                return;
            }

            String sign = req.getParameter("sign");

            Currency currency = new Currency(code, name, sign);
            CurrencyDAO currencyDAO = new CurrencyDAOImpl();

            Optional<Currency> newCurrency = currencyDAO.addCurrency(currency);
            if(newCurrency.isEmpty()){
                ErrorHandler.sendError(500, "Failed to get currency", resp);
                return;
            }
            mapper.writeValue(resp.getWriter(), newCurrency.get());
            resp.setStatus(201);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                ErrorHandler.sendError(409, "The currency with this code already exists.", resp);
                return;
            }
            ErrorHandler.sendError(500, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }
}
