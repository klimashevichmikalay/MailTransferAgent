//дописать закрытие отдельного клиента
package modelsListeners;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ClientListener;

public class SMTPServer {

    private static ServerSocket serverSocket;
    private final int CURRENT_PORT;
    private final ClientParser clPrsr;

    public SMTPServer(String[] args) {
        clPrsr = new ClientParser();
        CURRENT_PORT = Integer.parseInt((args[0]));
        try {
            serverSocket = new ServerSocket(CURRENT_PORT);
        } catch (IOException ex) {
        }
        this.acceptClients(args);
    }

    private void acceptClients(String[] args) {

        while (!SMTPServer.serverSocket.isClosed()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException ex) {
                continue;
            }

            ClientListener clLstnr = new ClientListener(clientSocket, args);
            clLstnr.start();
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(SMTPServer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ServerSocket getServerSocket() {
        return SMTPServer.serverSocket;
    }

    public static int getServerPort() {
        return serverSocket.getLocalPort();
    }
}
