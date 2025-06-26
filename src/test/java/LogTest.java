import Logger.ServerLog;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

public class LogTest {
    private static ServerLog serverLog;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Start Test !\n");
        serverLog = Mockito.mock(ServerLog.class);

    }

    @AfterEach
    void tearDown() {
        System.out.println("END Test !\n");
    }

    @DisplayName("Test for IOException: ")
    @Test
    void writeLogTestIOException() {
        serverLog.writeLog(Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> serverLog.writeLog(Mockito.anyString()));
    }

    @DisplayName("Test run 1 time: ")
    @Test
    void writeLogTest() {
        serverLog.writeLog("A");

        Mockito.verify(serverLog, Mockito.times(1)).writeLog("A");
    }
}