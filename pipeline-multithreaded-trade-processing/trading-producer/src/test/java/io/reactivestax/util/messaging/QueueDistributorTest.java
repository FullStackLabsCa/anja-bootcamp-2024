package io.reactivestax.util.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueDistributorTest {

    QueueDistributor queueDistributor;

    @BeforeEach
    void setUp() {
        queueDistributor = QueueDistributor.getInstance();
    }

    @Test
    void testGetQueueNumberNumberUsingRoundRobinAlgorithm() {
        int queueNumber = queueDistributor.getQueueNumberNumberUsingAlgorithm("round-robin", 3);
        System.out.println(queueNumber);
        assertTrue(queueNumber >= 0 && queueNumber < 3);
    }

    @Test
    void testGetQueueNumberNumberUsingRandomAlgorithm() {
        int queueNumber = queueDistributor.getQueueNumberNumberUsingAlgorithm("random", 3);
        System.out.println(queueNumber);
        assertTrue(queueNumber >= 0 && queueNumber < 3);
    }

    @Test
    void testFigureOutTheNextQueueUsingRoundRobinAlgorithmUseMapTrue() {
        int queueNumber1 = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", 3);
        int queueNumber2 = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", 3);
        assertEquals(queueNumber1, queueNumber2);
    }

    @Test
    void testFigureOutTheNextQueueUsingRoundRobinAlgorithmUseMapFalse() {
        int queueNumber1 = queueDistributor.figureOutTheNextQueue("TDB_00000001", true, "round-robin", 3);
        assertTrue(queueNumber1 >= 0 && queueNumber1 < 3);
        int queueNumber2 = queueDistributor.figureOutTheNextQueue("TDB_00000002", true, "round-robin", 3);
        assertTrue(queueNumber2 >= 0 && queueNumber2 < 3);
        int queueNumber3 = queueDistributor.figureOutTheNextQueue("TDB_00000003", true, "round-robin", 3);
        assertTrue(queueNumber3 >= 0 && queueNumber3 < 3);
        int queueNumber4 = queueDistributor.figureOutTheNextQueue("TDB_00000004", true, "round-robin", 3);
        assertTrue(queueNumber4 >= 0 && queueNumber4 < 3);
    }

    @Test
    void testFigureOutTheNextQueueUsingRandomAlgorithm() {
        int queueNumber = queueDistributor.figureOutTheNextQueue("TDB_00000001", false, "random",
                3);
        assertTrue(queueNumber >= 0 && queueNumber < 3);
    }
}
