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

    private Connecting connecting;
    private final ClientListener cl;
    private final RelaySocket rs;
    MailInfo mailInfo;
    private final int SUCCES = 220;
    private final int BSC = 503;
    private final int ERR = 211;

    public ConnectingTest() {
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
        mailInfo = Mockito.mock(MailInfo.class);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
        connecting = new Connecting();
    }

    @After
    public void tearDown() {
        connecting = null;
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        doReturn(true).when(rs).retransmit("", SUCCES);
        connecting.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "Sender OK");
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
        connecting.execute(cl, rs);
        verify(rs, never()).retransmit("", SUCCES);
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        doReturn(false).when(rs).retransmit("", SUCCES);
        connecting.execute(cl, rs);
        verify(cl, times(1)).sendMessage(ERR, "Could not connect to relay server.");
    }
}
