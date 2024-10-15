package io.reactivestax.utility;

import java.sql.Connection;

import jakarta.transaction.Transaction;

public class JDBCServiceUtil implements ServiceUtil<Connection, Transaction> {
    @Override
    public Connection getConnection() {
        
        return null;
    }

    @Override
    public Transaction getTransaction() {
        //no need to return a separate transaction object
        return null;
    }

}
