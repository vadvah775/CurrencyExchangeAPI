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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "exchange-rate-servlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try{
            String pathInfo = req.getPathInfo();

            Optional<List<String>> listCodes = Validator.validateTwoCurrencyCodes(pathInfo);
            if(listCodes.isEmpty()){
                ErrorHandler.sendError(400, "Invalid currency code", resp);
                return;
            }

            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();
            Optional<ExchangeRate> exchangeRate = exchangeRateDAO.getExchangeRateByCodes(listCodes.get().get(0), listCodes.get().get(1));
            if(exchangeRate.isEmpty()){
                ErrorHandler.sendError(404, "Currency pair not found", resp);
                return;
            }
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), exchangeRate.get());

        } catch (SQLException e) {
            ErrorHandler.sendError(500, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }
}
