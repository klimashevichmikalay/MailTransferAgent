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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HeloTest {//

    private Helo helo;
    private final ClientListener cl;
    private RelaySocket rs;
    MailInfo mailInfo;
    private final int SUCCES = 250;
    private final int BSC = 503;
    private final int ERR = 550;

    public HeloTest() {
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
        helo = new Helo();
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
        rs = Mockito.mock(RelaySocket.class);
    }

    @After
    public void tearDown() {
        helo = null;
    }

    @Test
    public void testExecute0() throws Exception {

        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        rs = null;
        helo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute1() throws Exception {

        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn("").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("", SUCCES);
        helo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        helo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn("").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("", SUCCES);
        helo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(ERR, "Error in relay HELO.");
    }
}
