package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RcptTest {

    private Rcpt rcpt;
    private ClientListener cl = Mockito.mock(ClientListener.class);
    private RelaySocket rs = Mockito.mock(RelaySocket.class);
    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int UUA = 550;
    private final static int SYNTAX_ERR = 501;
    private final String regExpRcpt = "\\A[rR]{1}[cC]{1}[pP]{1}[tT]{1}\\s{1}"
            + "[tT]{1}[oO]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}[a-z]{2,6}.{1}"
            + "[a-z]{2,3}>{1}\\z";

    public RcptTest() {
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
        rcpt = new Rcpt();
        Mockito.doNothing().when(cl).setClientState(ClientState.RCPT);
    }

    @After
    public void tearDown() {
        rcpt = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, rcpt.isCorrectCommand("RCPT TO:<klimashevich.mikalay@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand2() throws Exception {
        assertEquals(true, rcpt.isCorrectCommand("rcpt to:<klimashevich.mikalay@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand3() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("RCPT TO:<<klimashevich.mikalay@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand4() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("RCPT TO::<klimashevich.mikalay@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand5() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("RCPT TO:<klimashevich.mikalay@@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand6() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("RCPT TO:<klimashevich.mikalay@>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand7() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("RCPT TO<klimashevich.mikalay@mail.ru>", regExpRcpt));
    }

    @Test
    public void testICorrectCommand8() throws Exception {
        assertEquals(false, rcpt.isCorrectCommand("", regExpRcpt));
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn("RCPT TO:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("RCPT TO:<klimashevich.mikalay@mail.ru>", SUCCES);
        rcpt.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("RCPT TO:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("RCPT TO:<klimashevich.mikalay@mail.ru>", SUCCES);
        rcpt.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.QUIT).when(cl).getClientState();
        rcpt.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("RCPT:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        rcpt.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in RCPT");
    }

    @Test
    public void testExecute5() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn("RCPT TO:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("RCPT TO:<klimashevich.mikalay@mail.ru>", SUCCES);
        rcpt.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SUCCES, "OK");
    }
}
