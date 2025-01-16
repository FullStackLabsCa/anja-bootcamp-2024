package io.reactivestax.tradingproducer.repository;

import io.reactivestax.tradingproducer.type.entity.TradePayload;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateTradePayloadRepository implements TradePayloadRepository {

    @Override
    public void insertTradeRawPayload(TradePayload tradePayload) {
//        Session session = HibernateTransactionUtil.getInstance().getConnection();
//        io.reactivestax.tradingproducer.repository.entity.TradePayload tradePayloadEntity =
//                prepareTradePayloadEntity(tradePayload);
//        session.persist(tradePayloadEntity);
    }

    private io.reactivestax.tradingproducer.type.entity.TradePayload prepareTradePayloadEntity(TradePayload tradePayload) {
        io.reactivestax.tradingproducer.type.entity.TradePayload tradePayloadEntity = new io.reactivestax.tradingproducer.type.entity.TradePayload();
//        tradePayloadEntity.setTradeNumber(tradePayload.getTradeNumber());
//        tradePayloadEntity.setPayload(tradePayload.getPayload());
//        tradePayloadEntity.setValidityStatus(ValidityStatus.valueOf(tradePayload.getValidityStatus()));
        return tradePayloadEntity;
    }
}
