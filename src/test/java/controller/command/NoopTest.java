package controller.command;

import model.ClientListener;
import model.RelaySocket;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoopTest {

    private Noop noop;
    private final ClientListener cl;
    private final RelaySocket rs;
    private final int BSC = 503;
    private final int SUCCES = 250;
    private final int SYNTAX_ERR = 501;
    private final String regExpNoop = "\\A[nN]{1}[oO]{2}[pP]{1}\\z";
    int msgCode = 0;

    public NoopTest() {
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        noop = new Noop();
    }

    @After
    public void tearDown() {
        noop = null;
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn("noop").when(cl).getLastMessage();
        noop.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn("noopqwerty").when(cl).getLastMessage();
        noop.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }
}
