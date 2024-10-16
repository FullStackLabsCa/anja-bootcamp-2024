package io.reactivestax.repository.jdbc;

import io.reactivestax.entity.TradePayload;
import io.reactivestax.repository.TradePayloadRepository;
import org.hibernate.Session;

public class JDBCTradePayloadRepository implements TradePayloadRepository {
    private static final String INSERT_TRADE_PAYLOAD = "Insert into trade_payloads (trade_id, validity_status, payload) values(?, ?, ?)";
    private static final String READ_RAW_PAYLOAD_QUERY = "Select payload from trade_payloads where trade_id = ?";
    private static final String UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY = "Update trade_payloads set lookup_status = ? where trade_id = ?";
    private static final String UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY = "Update trade_payloads set je_status = ? where trade_id = ?";
    private static JDBCTradePayloadRepository instance;

    private JDBCTradePayloadRepository() {
        // private constructor to prevent instantiation
    }

    public static synchronized JDBCTradePayloadRepository getInstance() {
        if (instance == null) {
            instance = new JDBCTradePayloadRepository();
        }
        return instance;
    }

    @Override
    public void insertTradeRawPayload(TradePayload tradePayload) {

    }

    @Override
    public TradePayload readRawPayload(String tradeNumber) {
        return null;
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, int tradeId) {

    }

    @Override
    public void updateTradePayloadPostedStatus(int tradeId) {

    }


//
//    @Override
//    public void insertTradeRawPayload(RawPayload rawPayload, Connection connection) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRADE_PAYLOAD)) {
//            connection.setAutoCommit(false);
//            preparedStatement.setString(1, rawPayload.getTradeId());
//            preparedStatement.setString(2, rawPayload.getValidityStatus());
//            preparedStatement.setString(3, rawPayload.getPayload());
//            preparedStatement.execute();
//            connection.commit();
//            connection.setAutoCommit(true);
//        }
//    }
//
//    @Override
//    public String readRawPayload(String tradeId, Connection connection) throws SQLException {
//        String payload = "";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(READ_RAW_PAYLOAD_QUERY)) {
//            preparedStatement.setString(1, tradeId);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                payload = resultSet.getString("payload");
//            }
//        }
//
//        return payload;
//    }
//
//    @Override
//    public void updateTradePayloadLookupStatus(boolean lookupStatus, String tradeId, Connection connection) throws SQLException {
//        connection.setAutoCommit(false);
//        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_LOOKUP_STATUS_QUERY)){
//            preparedStatement.setString(1, lookupStatus ? "pass": "fail");
//            preparedStatement.setString(2, tradeId);
//            preparedStatement.execute();
//        }
//        if(!lookupStatus) connection.commit();
//    }
//
//    @Override
//    public void updateTradePayloadPostedStatus(String postedStatus, String tradeId, Connection connection) throws SQLException {
//        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRADE_PAYLOAD_POSTED_STATUS_QUERY)){
//            preparedStatement.setString(1, postedStatus);
//            preparedStatement.setString(2, tradeId);
//            preparedStatement.execute();
//        }
//    }
}