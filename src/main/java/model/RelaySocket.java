package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import modelsListeners.ClientView;

public class RelaySocket {

    private boolean isCreateSucces;
    private Socket relaySocket;
    private BufferedReader relayIn;
    private BufferedWriter relayOut;
    private String lastMessage;
    private final String HOST = "smtp.gmail.com.";
    private final static int PORT = 465;
    private final static int SUCCES_AUTH = 235;
    private final static int SUCCES = 250;
    private final static int ERR = 211;
    private final static String NEW_LINE = System.getProperty("line.separator");
    ClientView view;
    private final static String LOGIN = "a2xpbWFzaGV2aWNoLm1pa2FsYXlAZ21haWwuY29t";
    private String PASSWORD = "cmprejEyMzQ=";

    RelaySocket(ClientView viewCopy) {
        this.view = viewCopy;
        isCreateSucces = true;
        createRelaySocket();
    }

    private void createRelaySocket() {
        try {
            SslRMIClientSocketFactory sslf = new SslRMIClientSocketFactory();
            relaySocket = sslf.createSocket(HOST, PORT);
        } catch (Exception e) {
            isCreateSucces = false;
        }
        try {
            relayIn = new BufferedReader(new InputStreamReader(relaySocket.getInputStream()));
            relayOut = new BufferedWriter(new OutputStreamWriter(relaySocket.getOutputStream()));
        } catch (IOException ex) {
            isCreateSucces = false;
        }
    }

    private Boolean sendMessage(String msg) {
        try {
            relayOut.write(msg + NEW_LINE);
            relayOut.flush();
        } catch (IOException ex) {
            return false;
        }
        view.setMesage(NEW_LINE + "RC: " + msg);
        return true;
    }

    private String getMessage() {
        if ((this.relaySocket.isClosed())) {
            return null;
        }
        String msg = null;
        while (msg == null) {
            try {
                if (((msg = relayIn.readLine()) == null)) {
                    continue;
                } else {
                    break;
                }
            } catch (IOException ex) {
            }
        }
        this.lastMessage = msg;
        view.setMesage(NEW_LINE + "RS: " + msg);
        return msg;
    }

    public int authorization() {

        if (sendMessage("helo 127.0.0.1")) {
            if (getCodeMsg() != RelaySocket.SUCCES) {
                return ERR;
            }
        }

        if (sendMessage("auth login")) {
            if (getCodeMsg() != 334) {
                return ERR;
            }
        }

        if (sendMessage(LOGIN)) {
            if (getCodeMsg() != 334) {
                return ERR;
            }
        }
        if (sendMessage(PASSWORD)) {
            if (getCodeMsg() != SUCCES_AUTH) {
                return ERR;
            }
        }
        return SUCCES;
    }

    public boolean retransmit(ClientListener cl, int SUCCES) {
        if (!isCreateSucces) {
            cl.sendMessage(ERR, "Fail in connect to relay.");
        }
        sendMessage(cl.getLastMessage());

        int relayResult;
        if (((relayResult = getCodeMsg()) != SUCCES)) {
            cl.sendMessage(relayResult, getLastMessage());
            return false;
        }
        return true;
    }

    public boolean retransmit(String msg, int SUCCES) {
        sendMessage(msg);
        int relayResult;
        if (((relayResult = getCodeMsg()) != SUCCES)) {
            return false;
        }
        return true;
    }

    public boolean retransmit(ArrayList<String> mailInfoList, int SUCCES) {
        for (String s : mailInfoList) {
            sendMessage(s);
        }
        if ((getCodeMsg() != SUCCES)) {
            return false;
        }
        return true;
    }

    public void closeRelay() {
        try {
            relaySocket.close();
        } catch (IOException ex) {
        }
        view.setMesage(NEW_LINE + "relaySocket closed.");
    }

    private int getCodeMsg() {
        String msg = getMessage();
        if (msg != null) {
            return Integer.parseInt((msg.substring(0, 3)));
        }
        return ERR;
    }

    public String getLastMessage() {
        return this.lastMessage;
    }
}