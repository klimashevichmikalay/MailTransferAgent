/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelsListeners;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Garant
 */
public class ClientViewTest {
    
    public ClientViewTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setMesage method, of class ClientView.
     */
    @Test
    public void testSetMesage() {
        System.out.println("setMesage");
        String message = "ggg";
        ClientView instance = new ClientView();
        instance.setMesage(message);     
    }
    
}
