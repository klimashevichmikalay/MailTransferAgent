package model;

import modelsListeners.ClientView;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RelaySocketTest {

    RelaySocket rs;
    ClientView cv;
    String[] arr = {"", "127.0.0.1", "25"};
    private final String NEW_LINE = System.getProperty("line.separator");

    public RelaySocketTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cv = Mockito.mock(ClientView.class);
        rs = spy(new RelaySocket(cv, arr));
    }

    @After
    public void tearDown() {
        cv = null;
        rs = null;
    }

    @Test
    public void testGetRelayHost() {
        assertEquals(arr[1], rs.getRelayHost());
    }
}
