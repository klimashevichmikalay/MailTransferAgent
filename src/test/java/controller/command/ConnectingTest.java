package controller.command;

import model.ClientListener;
import model.ClientState;
import model.MailInfo;
import model.RelaySocket;
import org.junit.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectingTest {

    private static Connecting connecting;
    private static ClientListener cl;
    private static RelaySocket rs;
    static MailInfo mailInfo;
    private final int SUCCES = 220;
    private final int BSC = 503;

    public ConnectingTest() {

    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
        mailInfo = Mockito.mock(MailInfo.class);
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
        connecting = new Connecting();
    }

    @After
    public void tearDown() {
        connecting = null;
        cl = null;
        rs = null;
        mailInfo = null;
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        rs = null;
        connecting.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Sender OK.");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        connecting.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "Bad sequence of commands.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn(0).when(rs).getCodeMsg();
        connecting.execute(cl, rs);
        verify(cl, never()).setClientState(ClientState.COMMUNICATION);
    }

    @Test
    public void testExecute4() throws Exception {
        rs = Mockito.mock(RelaySocket.class);
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        doReturn(220).when(rs).getCodeMsg();
        connecting.execute(cl, rs);
        verify(cl, times(1)).setClientState(ClientState.COMMUNICATION);
    }

    @Test
    public void testExecute5() throws Exception {
        rs = Mockito.mock(RelaySocket.class);
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        doReturn(0).when(rs).getCodeMsg();
        connecting.execute(cl, rs);
        verify(cl, times(1)).sendMessage(211, "Cannot connect to relay.");
        verify(cl, times(0)).setClientState(ClientState.COMMUNICATION);
    }
}
