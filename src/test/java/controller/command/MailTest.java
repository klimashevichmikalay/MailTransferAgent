package controller.command;

import model.ClientListener;
import model.ClientState;
import model.MailInfo;
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
public class MailTest {

    private Mail mail;
    private final ClientListener cl;
    private final RelaySocket rs;
    MailInfo mailInfo;
    private final int SUCCES = 250;
    private final int BSC = 503;
    private final int SYNTAX_ERR = 501;
    private final String regExpMail = "\\A[mM]{1}[aA]{1}[iI]{1}[lL]{1}\\s{1}"
            + "[fF]{1}[rR]{1}[oO]{1}[mM]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}"
            + "[a-z]{2,6}.{1}[a-z]{2,3}>{1}\\z";

    public MailTest() {
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
        Mockito.doNothing().when(cl).setClientState(ClientState.MAIL);
        mail = new Mail();
    }

    @After
    public void tearDown() {
        mail = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, mail.isCorrectCommand("MAIL FROM:<klimashevich.mikalay@mail.ru>", regExpMail));
    }

    @Test
    public void testICorrectCommand3() throws Exception {
        assertEquals(false, mail.isCorrectCommand(" MAIL FROM:<klimashevich.mikalay@mail.ru", regExpMail));
    }

    @Test
    public void testICorrectCommand4() throws Exception {
        assertEquals(false, mail.isCorrectCommand("MAIL:<klimashevich.mikalay@mail.ru", regExpMail));
    }

    @Test
    public void testICorrectCommand5() throws Exception {
        assertEquals(false, mail.isCorrectCommand("MAIL FROM:<klimashevich.mikalay@@mail.ru", regExpMail));
    }

    @Test
    public void testICorrectCommand6() throws Exception {
        assertEquals(false, mail.isCorrectCommand("MAIL FROM:<klimashevich.mikalay@mail", regExpMail));
    }

    @Test
    public void testICorrectCommand7() throws Exception {
        assertEquals(false, mail.isCorrectCommand("MAIL FROM:<klimashevich.mikalay@mail.ruMAIL FROM:<klimashevich.mikalay@mail.ru", regExpMail));
    }

    @Test
    public void testExecute1() throws Exception {
        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn("MAIL FROM:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit(cl, SUCCES);
        mail.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        mail.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "Bad sequence of commands.");
    }

    @Test
    public void testExecute3() throws Exception {
        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn("qwerty").when(cl).getLastMessage();
        mail.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in MAIL FROM");
    }

    @Test
    public void testExecute4() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("DATA").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit(cl, SUCCES);
        mail.execute(cl, rs);
        verify(cl, never()).sendMessage(BSC, "bad sequence of commands.");
    }

    @Test
    public void testExecute5() throws Exception {
        doReturn(ClientState.COMMUNICATION).when(cl).getClientState();
        doReturn("MAIL :<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        mail.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in MAIL FROM");
    }

    @Test
    public void testExecute6() throws Exception {
        doReturn(ClientState.RCPT).when(cl).getClientState();
        doReturn("").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit(cl, SUCCES);
        mail.execute(cl, rs);
        verify(rs, never()).retransmit(cl, SUCCES);
    }
}
