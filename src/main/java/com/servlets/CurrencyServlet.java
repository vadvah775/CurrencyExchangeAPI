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

@WebServlet(name = "currency-servlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo();

            Optional<String> code = Validator.validateCurrencyCode(pathInfo);
            if(code.isEmpty()){
                ErrorHandler.sendError(400, "Invalid currency code", resp);
                return;
            }
            CurrencyDAO currencyDAO = new CurrencyDAOImpl();
            Optional<Currency> currency = currencyDAO.getCurrencyByCode(code.get());
            if(currency.isEmpty()){
                ErrorHandler.sendError(404, "Currency not found", resp);
                return;
            }
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), currency.get());
            resp.setStatus(200);
        } catch (SQLException e) {
            ErrorHandler.sendError(500, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(500, "Fatal error", resp);
        }
    }
}
