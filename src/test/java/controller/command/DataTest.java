package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataTest {

    private Data data;
    private ClientListener cl = Mockito.mock(ClientListener.class);
    private RelaySocket rs = Mockito.mock(RelaySocket.class);
    private final int SUCCES = 354;
    private final int BSC = 503;
    private final int SYNTAX_ERR = 501;
    private final String regExpData = "\\A[dD]{1}[aA]{1}[tT]{1}[aA]{1}\\z";

    public DataTest() {
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
        doReturn(true).when(rs).retransmit(cl, SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Enter mail, end with \".\" on a line by itself");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit(cl, SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Enter mail, end with \".\" on a line by itself");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.DATA).when(cl).getClientState();
        doReturn(true).when(rs).retransmit(cl, SUCCES);
        doReturn("DATA").when(cl).getLastMessage();
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit(cl, SUCCES);
        data.execute(cl, rs);
        verify(cl, times(0)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute5() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATAaa").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit(cl, SUCCES);
        data.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in DATA");
    }

    @Test
    public void testExecute6() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit(cl, SUCCES);
        data.execute(cl, rs);
        verify(rs, never()).retransmit(cl, SUCCES);
    }
}
