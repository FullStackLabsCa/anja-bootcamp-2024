package io.reactivestax.repository.hibernate;

import java.util.Optional;

import org.hibernate.Session;

import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.repository.hibernate.entity.TradePayload;
import io.reactivestax.type.dto.TradePayloadDTO;
import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;
import io.reactivestax.util.database.hibernate.HibernateTransactionUtil;

public class HibernateTradePayloadRepository implements TradePayloadRepository {
    private static HibernateTradePayloadRepository instance;

    private HibernateTradePayloadRepository() {
    }

    public static synchronized HibernateTradePayloadRepository getInstance() {
        if (instance == null) {
            instance = new HibernateTradePayloadRepository();
        }

        return instance;
    }

    @Override
    public Optional<io.reactivestax.type.dto.TradePayloadDTO> readRawPayload(String tradeNumber) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        Optional<TradePayload> first = session
                .createQuery("from TradePayload tp where tp.tradeNumber = :tradeNumber",
                        io.reactivestax.repository.hibernate.entity.TradePayload.class)
                .setParameter("tradeNumber", tradeNumber)
                .stream()
                .findFirst();

        return getTradePayloadDTO(first);
    }

    @Override
    public void updateTradePayloadLookupStatus(boolean lookupStatus, Long tradeId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setLookupStatus(lookupStatus ? LookupStatus.PASS : LookupStatus.FAIL);
    }

    @Override
    public void updateTradePayloadPostedStatus(Long tradeId) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        TradePayload tradePayload = session.get(TradePayload.class, tradeId);
        tradePayload.setJournalEntryStatus(PostedStatus.POSTED);
    }

    private Optional<io.reactivestax.type.dto.TradePayloadDTO> getTradePayloadDTO(
            Optional<TradePayload> optionalTradePayloadEntity) {
        return optionalTradePayloadEntity.map(tradePayloadEntity -> {
            io.reactivestax.type.dto.TradePayloadDTO tradePayload = new io.reactivestax.type.dto.TradePayloadDTO();
            tradePayload.setId(tradePayloadEntity.getId());
            tradePayload.setTradeNumber(tradePayloadEntity.getTradeNumber());
            tradePayload.setPayload(tradePayloadEntity.getPayload());
            tradePayload.setLookupStatus(String.valueOf(tradePayloadEntity.getLookupStatus()));
            tradePayload.setValidityStatus(String.valueOf(tradePayloadEntity.getValidityStatus()));
            tradePayload.setJournalEntryStatus(String.valueOf(tradePayloadEntity.getJournalEntryStatus()));
            return tradePayload;
        });
    }

    @Override
    public void saveTradePayload(TradePayloadDTO tradePayloadDTO) {
        Session session = HibernateTransactionUtil.getInstance().getConnection();
        session.persist(
                Optional.of(tradePayloadDTO)
                        .map(trdPayloadDTO -> TradePayload.builder()
                                .tradeNumber(trdPayloadDTO.getTradeNumber())
                                .payload(trdPayloadDTO.getPayload())
                                .lookupStatus(LookupStatus.valueOf(trdPayloadDTO.getLookupStatus()))
                                .validityStatus(ValidityStatus.valueOf(trdPayloadDTO.getValidityStatus()))
                                .journalEntryStatus(PostedStatus.valueOf(trdPayloadDTO.getJournalEntryStatus()))
                                .build())
                        .get());

    }

}
