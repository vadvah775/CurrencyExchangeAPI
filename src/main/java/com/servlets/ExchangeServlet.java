package com.servlets;

import com.service.ExchangeService;
import com.utils.ErrorHandler;
import com.utils.Validator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Optional<String> from = Validator.validateCurrencyCode(req.getParameter("from"));
            Optional<String> to = Validator.validateCurrencyCode(req.getParameter("to"));
            if (from.isEmpty() || to.isEmpty()) {
                ErrorHandler.sendError(400, "Invalid currency code", resp);
                return;
            }

            Optional<BigDecimal> amount = Validator.validateBigDecimal(req.getParameter("amount"));
            if (amount.isEmpty()) {
                ErrorHandler.sendError(400, "Invalid amount", resp);
                return;
            }

            ExchangeService service = new ExchangeService(mapper);

            Optional<ObjectNode> objectNode = service.exchange(from.get(), to.get(), amount.get());
            if (objectNode.isEmpty()) {
                ErrorHandler.sendError(404, "The exchange rate was not found in the database", resp);
                return;
            }

            mapper.writeValue(resp.getWriter(), objectNode.get());
            resp.setStatus(200);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            ErrorHandler.sendError(501, "Fatal error", resp);
        }
    }
}
