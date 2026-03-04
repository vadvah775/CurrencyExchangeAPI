package com.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {
    private String url;
    private String username;
    private String password;
    private String driver;
    private static ConnectionPool instance;
    private Connection connection;

    private ConnectionPool() throws SQLException {
        loadProperties();
        initPool();
    }

    public static ConnectionPool getInstance() throws SQLException {
        if(instance == null){
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void loadProperties(){
        try(InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(stream);

            this.url = prop.getProperty("db.url");
            this.username = prop.getProperty("db.username");
            this.password = prop.getProperty("db.password");
            this.driver = prop.getProperty("db.driver");

            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initPool() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public Connection getConnection(){
        try {
            if(connection == null || connection.isClosed()){
                initPool();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
