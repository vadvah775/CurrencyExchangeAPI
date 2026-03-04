package com.servlets;

import com.dao.currency.CurrencyDAO;
import com.dao.currency.CurrencyDAOImpl;
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
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.getWriter().println("error in path");
            }
            String currency = pathInfo.substring(1);
            System.out.println(currency);
            CurrencyDAO currencyDAO = new CurrencyDAOImpl();
            mapper.writeValue(resp.getWriter(), currencyDAO.getCurrencyByCode(currency));
        } catch (SQLException e) {
            ErrorHandler.sendError(501, "Data base error", resp);
        } catch (IOException e) {
            ErrorHandler.sendError(401, "Fatal error", resp);
        }


    }
}
