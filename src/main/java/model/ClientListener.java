package model;

import controller.ClientController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelsListeners.ClientParser;
import modelsListeners.ClientView;
import modelsListeners.SMTPServer;

public class ClientListener implements Runnable {

    private final Socket clientSocket;
    private final RelaySocket relaySocket;
    private BufferedReader in;
    private BufferedWriter out;
    private ClientView view;
    private final Thread t;
    private volatile String lastMessage;
    private volatile ClientState state;
    private final ClientController clCtrl;
    private final ClientParser clPrsr;
    private static final String POINT = ".";
    MailInfo mailInfo;
    private final static String NEW_LINE = System.getProperty("line.separator");

    public ClientListener(Socket clientSocketCopy, String[] args) {

        this.clientSocket = clientSocketCopy;
        clPrsr = new ClientParser();
        clCtrl = new ClientController();
        createView();
        try {
            createWriteRead();
        } catch (IOException ex) {
        }
        mailInfo = new MailInfo();
        state = ClientState.CONNECTION;
        lastMessage = "220 Sender OK";
        t = new Thread(this, "ClientListener");
        view.setMesage("SERVER_PORT: " + SMTPServer.getServerPort());

        if (args.length == 1) {
            relaySocket = null;
        } else {
            relaySocket = new RelaySocket(this.view, args);
        }
    }

    @Override
    public void run() {
        clCtrl.execute(clPrsr.parseClient(this), this, this.relaySocket);
        while (!(this.clientSocket.isClosed())) {
            try {
                lastMessage = in.readLine();
            } catch (IOException ex) {
                continue;
            }
            view.setMesage(NEW_LINE + "C: " + lastMessage);
            if (this.state == ClientState.GET_MAIL_INFO && !lastMessage.equals(POINT)) {
                mailInfo.add(lastMessage);
            } else {
                clCtrl.execute(clPrsr.parseClient(this), this, this.relaySocket);
            }
        }
    }

    public void sendMessage(int code, String msg) {

        if (clientSocket.isClosed()) {
            return;
        }

        msg = Integer.toString(code) + " " + msg;
        try {
            out.write(msg + NEW_LINE);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        view.setMesage(NEW_LINE + "S: " + msg + NEW_LINE);
    }

    public void close() {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        view.setMesage(NEW_LINE + "Client socket closed." + NEW_LINE);
    }

    private void createWriteRead() throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public MailInfo getMailInfo() {
        return this.mailInfo;
    }

    public void start() {
        this.t.start();
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    private void createView() {
        this.view = new ClientView();
    }

    public void setClientState(ClientState newState) {
        this.state = newState;
    }

    public ClientState getClientState() {
        return this.state;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
