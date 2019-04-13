package smtp.mail.server;
//план:
//лог

import java.io.*;
import modelsListeners.SMTPServer;

public class main {

    public static void main(String[] args) throws IOException {
        new SMTPServer(args);
    }
}
