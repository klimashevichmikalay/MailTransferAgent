/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.command;

import model.ClientListener;
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

public class UnknownTest {

    private final static int UC = 500;
    Unknown unknwn;
    private ClientListener cl;
    private RelaySocket rs;

    public UnknownTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        unknwn = new Unknown();
        cl = Mockito.mock(ClientListener.class);
        rs = Mockito.mock(RelaySocket.class);
    }

    @After
    public void tearDown() {
        unknwn = null;
        cl = null;
        rs = null;
    }

    /**
     * Test of execute method, of class Unknown.
     */
    @Test
    public void testExecute1() {
        doReturn("").when(cl).getLastMessage();
        Mockito.doNothing().when(cl).sendMessage(UC, "unknown command: ");
        unknwn.execute(cl, rs);
        verify(cl, times(1)).sendMessage(UC, "unknown command: ");
    }

}
