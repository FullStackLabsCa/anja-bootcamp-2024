package io.reactivestax.ems;

import io.reactivestax.ems.enums.OtpLock;

import java.sql.*;
import java.util.UUID;

public class DataInserter {

    private static final String URL = "jdbc:postgresql://localhost:5432/ems";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, "postgres", "postgres")) {
            connection.setAutoCommit(false); // Start a transaction

            // Insert 20 customers
            for (int i = 1; i <= 20; i++) {
                UUID customerId = insertCustomer(connection, "FirstName" + i, "LastName" + i);
                insertContacts(connection, customerId, "FirstName" + i);
            }

            connection.commit(); // Commit the transaction
            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a customer
    private static UUID insertCustomer(Connection connection, String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO customer (customer_id, first_name, last_name, created_at, otp_lock) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?) RETURNING customer_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            UUID customerId = UUID.randomUUID();
            statement.setObject(1, customerId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, OtpLock.NOT_LOCKED.name());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return (UUID) resultSet.getObject("customer_id");
        }
    }

    // Method to insert contacts for a customer
    private static void insertContacts(Connection connection, UUID customerId, String firstName) throws SQLException {
        String[] phones = { "123-456-789" + firstName.charAt(firstName.length() - 1), "987-654-321" + firstName.charAt(firstName.length() - 1) };
        String[] emails = { firstName.toLowerCase() + "1@example.com", "alt" + firstName.toLowerCase() + "@example.com" };

        for (String phone : phones) {
            insertContact(connection, customerId, "PHONE", phone);
        }
        for (String email : emails) {
            insertContact(connection, customerId, "EMAIL", email);
        }
    }

    // Method to insert a contact record
    private static void insertContact(Connection connection, UUID customerId, String contactType, String contactValue) throws SQLException {
        String sql = "INSERT INTO contact (contact_id, customer_id, contact_type, contact_value, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            UUID contactId = UUID.randomUUID();
            statement.setObject(1, contactId);
            statement.setObject(2, customerId);
            statement.setString(3, contactType);
            statement.setString(4, contactValue);

            statement.executeUpdate();
        }
    }
}
