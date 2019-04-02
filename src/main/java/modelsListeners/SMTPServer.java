//дописать закрытие отдельного клиента
package modelsListeners;

import controller.ClientController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ClientList;
import controller.ClientListController;
import controller.ClientListControllerOperations;
import model.ClientListener;

public class SMTPServer {

    private ServerSocket serverSocket;
    private int PORT = 25;
    private static ClientList clList;
    private static ClientListController clListCtrl;
    private static ClientParser clPrsr;
    private static ClientController clControl;

    public SMTPServer() {
        clListCtrl = new ClientListController();
        clPrsr = new ClientParser();
        clList = new ClientList();
        clList.addClientListView(new ClientListView());
    }

    public void startServer() {

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.err.println("Fail in created server.");
        }
        this.acceptClients();
    }

    private void acceptClients() {
        while (!this.serverSocket.isClosed()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException ex) {
                continue;
            }
            ClientListener clLstnr = new ClientListener(clientSocket);
            clListCtrl.execute(clList, ClientListControllerOperations.ADD, clLstnr, 0);
            clLstnr.start();
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SMTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        clListCtrl.execute(clList, ClientListControllerOperations.CLOSE_ALL, null, 0);
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }
}
