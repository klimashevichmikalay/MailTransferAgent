package model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MailInfoTest {

    MailInfo mi;

    public MailInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mi = new MailInfo();
    }

    @After
    public void tearDown() {
        mi = null;
    }

    @Test
    public void testIsContainsMinCommands1() {
        mi.add("To");
        assertEquals(false, mi.isContainsMinCommands());
    }

    @Test
    public void testIsContainsMinCommands2() {
        mi.add("To");
        mi.add("From");
        mi.add("Subject");
        mi.add("Content-Type");
        mi.add("");
        mi.add(".");
        assertEquals(true, mi.isContainsMinCommands());
    }

    @Test
    public void testGetFirstWord1() {
        assertEquals("Subject", mi.getFirstWord("Subject: text"));
    }

    @Test
    public void testAdd1() {
        mi.add("To");
        assertEquals(true, mi.getMailInfoList().contains("To"));
    }

    @Test
    public void testClearInfo() {
        mi.add("To");
        mi.clearInfo();
        assertEquals(true, mi.getMailInfoList().isEmpty());
    }
}
