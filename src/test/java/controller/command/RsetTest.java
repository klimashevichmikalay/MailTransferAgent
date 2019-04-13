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

public class RsetTest {

    private final static int BSC = 503;
    private final static int SUCCES = 250;  
    private final String regExpRset = "\\A[rR]{1}[sS]{1}[eE]{1}[tT]{1}\\z";
    private Rset rset;
    private RelaySocket rs = Mockito.mock(RelaySocket.class);
    private ClientListener cl = Mockito.mock(ClientListener.class);
    MailInfo mailInfo;

    public RsetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        rset = new Rset();
        cl = Mockito.mock(ClientListener.class);
        mailInfo = Mockito.mock(MailInfo.class);
        doReturn(mailInfo).when(cl).getMailInfo();
        Mockito.doNothing().when(mailInfo).clearInfo();
    }

    @After
    public void tearDown() {
        cl = null;
        rset = null;
        mailInfo = null;
    }

    @Test
    public void testICorrectCommand1() throws Exception {
        assertEquals(true, rset.isCorrectCommand("RSET", regExpRset));
    }

    @Test
    public void testICorrectCommand2() throws Exception {
        assertEquals(true, rset.isCorrectCommand("rset", regExpRset));
    }

    @Test
    public void testICorrectCommand3() throws Exception {
        assertEquals(false, rset.isCorrectCommand(" rset", regExpRset));
    }

    @Test
    public void testICorrectCommand4() throws Exception {
        assertEquals(false, rset.isCorrectCommand("RCPT", regExpRset));
    }

    @Test
    public void testExecute1() throws Exception {        
        doReturn("rset").when(cl).getLastMessage();
        rset.execute(cl, rs);
        verify(cl, times(1)).sendMessage(SUCCES, "OK");
    }

    @Test
    public void testExecute2() throws Exception {
        doReturn("rsetgg").when(cl).getLastMessage();
        rset.execute(cl, rs);
        verify(cl, times(1)).sendMessage(BSC, "bad sequence of commands.");
    }
}
