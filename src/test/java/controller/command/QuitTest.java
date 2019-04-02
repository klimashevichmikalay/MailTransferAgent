package controller.command;

import java.util.ArrayList;
import model.ClientListener;
import model.ClientState;
import model.MailInfo;
import model.RelaySocket;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuitTest {

    private Quit quit;
    private final ClientListener cl;
    private final RelaySocket rs;
    private final static int SUCCES = 221;
    private final static int SYNTAX_ERR = 501;
    private final String regExpQuit = "\\A[qQ]{1}[uU]{1}[iI]{1}[tT]{1}\\z";

    public QuitTest() {
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
        quit = new Quit();
        Mockito.doNothing().when(cl).setClientState(ClientState.QUIT);
        Mockito.doNothing().when(cl).close();
        Mockito.doNothing().when(rs).closeRelay();
    }

    @After
    public void tearDown() {
        quit = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, quit.isCorrectCommand("QUIT", regExpQuit));
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn("QUIT").when(cl).getLastMessage();
        quit.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "SMTPServer closing connection.");
        verify(cl, times(1)).setClientState(ClientState.QUIT);
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn("QUIT;").when(cl).getLastMessage();
        quit.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in QUIT");
        verify(cl, times(0)).setClientState(ClientState.QUIT);
    }
}
