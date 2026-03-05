package com.servlets;

import com.dao.exchange.ExchangeRateDAO;
import com.dao.exchange.ExchangeRateDAOImpl;
import com.utils.ErrorHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;

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
}
