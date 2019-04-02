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
public class EhloTest {//

    private Ehlo ehlo;
    private final ClientListener cl;
    private final RelaySocket rs;
    MailInfo mailInfo;
    private final int SUCCES = 250;
    private final int BSC = 503;
    private final int ERR = 550;

    public EhloTest() {
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
        ehlo = new Ehlo();
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
    }

    @After
    public void tearDown() {
        ehlo = null;
    }

    @Test
    public void testExecute1() throws Exception {

        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn(SUCCES).when(rs).authorization();
        ehlo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn(SUCCES).when(rs).authorization();
        ehlo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.MAIL).when(cl).getClientState();
        doReturn(SUCCES).when(rs).authorization();
        ehlo.execute(cl, rs);
        verify(rs, never()).authorization();
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn(-99).when(rs).authorization();
        ehlo.execute(cl, rs);
        verify(cl, times(1)).sendMessage(ERR, "Unable to pass authorization on the relay server.");
    }
}
