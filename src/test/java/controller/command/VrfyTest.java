package controller.command;

import model.ClientListener;
import model.MailInfo;
import model.RelaySocket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VrfyTest {

    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final static int FAIL_IN_RELAY = 211;
    private final long TIME_OUT = 300000;
    private final String regExpVrfy = "\\A[vV]{1}[rR]{1}[fF]{1}[yY]{1}\\s{1}[fF]"
            + "{1}[rR]{1}[oO]{1}[mM]{1}:{1}(<?([a-zA-Z0-9._]{2,63}[@]{1}){1}[a-z]{2,6}"
            + ".{1}[a-z]{2,3}>?,?){1,}";

    Vrfy vrfy;
    private RelaySocket rs;
    private ClientListener cl;
    MailInfo mailInfo;

    public VrfyTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        vrfy = new Vrfy();
        cl = Mockito.mock(ClientListener.class);
        mailInfo = Mockito.mock(MailInfo.class);
        rs = Mockito.mock(RelaySocket.class);
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
    }

    @After
    public void tearDown() {
        cl = null;
        vrfy = null;
        mailInfo = null;
        rs = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, vrfy.isCorrectCommand("VRFY fROM:<klimashevich.mikalay@mail.ru>", regExpVrfy));
    }

    @Test
    public void testICorrectCommand2() throws Exception {
        assertEquals(true, vrfy.isCorrectCommand("vrfy from:<klimashevich.mikalay@mail.ru>", regExpVrfy));
    }

    @Test
    public void testICorrectCommand3() throws Exception {
        assertEquals(false, vrfy.isCorrectCommand("RCPT TO:<<klimashevich.mikalay@mail.ru>", regExpVrfy));
    }

    @Test
    public void testICorrectCommand4() throws Exception {
        assertEquals(false, vrfy.isCorrectCommand("vrfy from::<klimashevich.mikalay@mail.ru>", regExpVrfy));
    }

    @Test
    public void testExecute1() {
        doReturn("RCPT TO:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("RCPT TO:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(cl, times(0)).sendMessage(SUCCES, "OK");
        verify(rs, times(0)).retransmit("RCPT TO:<klimashevich.mikalay@mail.ru>", SUCCES);
    }

    public void testExecute2() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    public void testExecute3() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(true).when(rs).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(rs, times(1)).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
    }

    public void testExecute4() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        rs = null;
        vrfy.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
        verify(rs, times(0)).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
    }

    public void testExecute5() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(cl, times(0)).sendMessage(SUCCES, "OK");
        verify(rs, times(1)).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
    }

    public void testExecute6() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, rs);
        verify(cl, times(0)).sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
        verify(cl, times(0)).sendMessage(SUCCES, "OK");
        verify(cl, times(1)).sendMessage(FAIL_IN_RELAY, "VRFY RELAY RCPT.");
    }

    public void testExecute7() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        doReturn(false).when(rs).retransmit("vrfy from:<klimashevich.mikalay@mail.ru>", SUCCES);
        vrfy.execute(cl, null);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");     
    }
}
