package controller.command;

import java.util.ArrayList;
import model.ClientListener;
import model.ClientState;
import model.MailInfo;
import model.RelaySocket;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PointTest {

    private Point point;
    private final ClientListener cl;
    private final RelaySocket rs;
    private final MailInfo mailInfo;
    private final ArrayList<String> mailInfoList;
    private final int BSC = 503;
    private final int SUCCES = 250;
    private final int TRANCSACTION_FAILED = 554;
    private final int SYNTAX_ERROR = 500;

    public PointTest() {
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
        mailInfo = Mockito.mock(MailInfo.class);
        mailInfoList = Mockito.mock(ArrayList.class);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Mockito.doNothing().when(cl).setClientState(ClientState.COMMUNICATION);
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
        Mockito.doNothing().when(mailInfo).add(".");
        doReturn(mailInfoList).when(mailInfo).getMailInfoList();       
        point = new Point();
    }

    @After
    public void tearDown() {
        point = null;
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.GET_MAIL_INFO).when(cl).getClientState();
        doReturn(true).when(mailInfo).isContainsMinCommands();
        doReturn(true).when(mailInfo).checkSyntax();
        doReturn(true).when(rs).retransmit(mailInfoList, SUCCES);
        point.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "message accepted for delivery");
        verify(cl, times(1)).setClientState(ClientState.COMMUNICATION);
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        point.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.GET_MAIL_INFO).when(cl).getClientState();
        doReturn(false).when(mailInfo).isContainsMinCommands();
        point.execute(cl, rs);
        verify(cl, times(1)).sendMessage(TRANCSACTION_FAILED, "Little information in the letter.");
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.GET_MAIL_INFO).when(cl).getClientState();
        doReturn(true).when(mailInfo).isContainsMinCommands();
        doReturn(false).when(mailInfo).checkSyntax();
        point.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERROR, "Syntax error in the letter.");
    }

    @Test
    public void testExecute5() throws Exception {
        doReturn(ClientState.GET_MAIL_INFO).when(cl).getClientState();
        doReturn(true).when(mailInfo).isContainsMinCommands();
        doReturn(true).when(mailInfo).checkSyntax();
        doReturn(false).when(rs).retransmit(mailInfoList, SUCCES);
        point.execute(cl, rs);
        verify(cl, times(1)).sendMessage(TRANCSACTION_FAILED, "Transaction failed.");
    }
}
