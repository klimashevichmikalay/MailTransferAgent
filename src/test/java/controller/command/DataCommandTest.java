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
public class DataCommandTest {

    private Data data;
    private ClientListener cl = Mockito.mock(ClientListener.class);
    private RelaySocket rs = Mockito.mock(RelaySocket.class);
    private final int SUCCES = 354;
    private final int BSC = 503;
    private final int SYNTAX_ERR = 501;
    private final String regExpData = "\\A[dD]{1}[aA]{1}[tT]{1}[aA]{1}\\z";

    public DataCommandTest() {
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
        Mockito.doNothing().when(cl).close();
        Mockito.doNothing().when(cl).sendMessage(211, "Time is out. Server close connection.");
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        data = new Data();
    }

    @After
    public void tearDown() {
        data = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, data.isCorrectCommand("DATA", regExpData));
    }

    @Test
    public void testICorrectCommand2() throws Exception {
        assertEquals(true, data.isCorrectCommand("data", regExpData));
    }

    @Test
    public void testICorrectCommand3() throws Exception {
        assertEquals(true, data.isCorrectCommand("DaTa", regExpData));
    }

    @Test
    public void testICorrectCommand4() throws Exception {
        assertEquals(true, data.isCorrectCommand("Data", regExpData));
    }

    @Test
    public void testICorrectCommand5() throws Exception {
        assertEquals(false, data.isCorrectCommand("Dataa", regExpData));
    }

    @Test
    public void testICorrectCommand6() throws Exception {
        assertEquals(false, data.isCorrectCommand(" Data", regExpData));
    }

    @Test
    public void testICorrectCommand7() throws Exception {
        assertEquals(false, data.isCorrectCommand("DATA:", regExpData));
    }

    @Test
    public void testICorrectCommand8() throws Exception {
        assertEquals(false, data.isCorrectCommand("", regExpData));
    }

    @Test
    public void testICorrectCommand9() throws Exception {
        assertEquals(false, data.isCorrectCommand("qwerty", regExpData));
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("DATA", SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Enter mail, end with \".\" on a line by itself.");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("DATA", SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Enter mail, end with \".\" on a line by itself.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.DATA).when(cl).getClientState();
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "Bad sequence of commands.");
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("DATA", SUCCES);
        data.execute(cl, rs);
        verify(cl, times(0)).sendMessage(BSC, "Bad sequence of commands.");
    }

    @Test
    public void testExecute5() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATAaa").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("DATA", SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in DATA.");
    }
}
