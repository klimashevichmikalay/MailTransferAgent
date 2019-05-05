package modelsListeners;

import model.ClientListener;
import model.ClientState;
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

public class ClientParserTest {

    ClientParser cp;
    private ClientListener cl;

    public ClientParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cp = new ClientParser();
        cl = Mockito.mock(ClientListener.class);
    }

    @After
    public void tearDown() {
        cp = null;
        cl = null;
    }

    @Test
    public void testParseClient1() {
        doReturn("vrfy from:<klimashevich.mikalay@mail.ru>").when(cl).getLastMessage();
        assertEquals("vrfy", cp.parseClient(cl));
    }

    @Test
    public void testParseClient2() {
        doReturn("QUIT").when(cl).getLastMessage();
        assertEquals("quit", cp.parseClient(cl));
    }

    @Test
    public void testParseClient3() {
        doReturn("NOOP").when(cl).getLastMessage();
        assertEquals("noop", cp.parseClient(cl));
    }

    @Test
    public void testParseClient4() {
        doReturn("rset").when(cl).getLastMessage();
        assertEquals("rset", cp.parseClient(cl));
    }

    @Test
    public void testParseClient5() {
        doReturn("rset").when(cl).getLastMessage();
        assertEquals("rset", cp.parseClient(cl));
    }

    @Test
    public void testParseClient6() {
        doReturn("gtgttgt").when(cl).getLastMessage();
        doReturn(ClientState.QUIT).when(cl).getClientState();
        assertEquals(null, cp.parseClient(cl));
    }

    @Test
    public void testParseClient7() {
        doReturn("").when(cl).getLastMessage();
        doReturn(ClientState.CONNECTION).when(cl).getClientState();
        assertEquals("connecting", cp.parseClient(cl));
    }

    @Test
    public void testParseClient8() {
        doReturn("rcpt to grgrgr").when(cl).getLastMessage();
        doReturn(ClientState.MAIL).when(cl).getClientState();
        assertEquals("rcpt", cp.parseClient(cl));
    }

    @Test
    public void testParseClient9() {
        doReturn("rrrrcpt to grgrgr").when(cl).getLastMessage();
        doReturn(ClientState.MAIL).when(cl).getClientState();
        assertEquals("unknown", cp.parseClient(cl));
    }

    @Test
    public void testParseClient10() {
        doReturn("data").when(cl).getLastMessage();
        doReturn(ClientState.RCPT).when(cl).getClientState();
        assertEquals("data", cp.parseClient(cl));
    }

    @Test
    public void testParseClient11() {
        doReturn("point").when(cl).getLastMessage();
        doReturn(ClientState.GET_MAIL_INFO).when(cl).getClientState();
        assertEquals("point", cp.parseClient(cl));
    }

    @Test
    public void testParseClient12() {
        doReturn("MAIL FROM: <frfrfr>").when(cl).getLastMessage();
        assertEquals("MAIL", cp.getCommandWord(cl));

    }
    
      @Test
    public void testParseClient13() {
        doReturn(".").when(cl).getLastMessage();
        assertEquals("point", cp.getCommandWord(cl));

    }
    
       @Test
    public void testParseClient14() {
        doReturn("DATA").when(cl).getLastMessage();
        assertEquals("DATA", cp.getCommandWord(cl));
        
        

    }
}
