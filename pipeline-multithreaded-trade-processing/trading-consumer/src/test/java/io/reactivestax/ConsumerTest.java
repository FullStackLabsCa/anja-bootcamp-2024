//package io.reactivestax;
//
//import io.reactivestax.service.TradeService;
//import io.reactivestax.util.ApplicationPropertiesUtils;
//import io.reactivestax.util.database.TransactionUtil;
//import io.reactivestax.util.factory.BeanFactory;
//import org.junit.Before;
//
//public class ConsumerTest {
//    ApplicationPropertiesUtils applicationPropertiesUtils;
//    TradeService tradeService;
//    TransactionUtil transactionUtil;
//
//    @Before
//    public void setUp() {
//        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");
//        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
//        tradeService = TradeService.getInstance();
//        transactionUtil = BeanFactory.getTransactionUtil();
//    }
//}
