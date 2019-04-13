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

    private Socket relaySocket;
    private BufferedReader relayIn;
    private BufferedWriter relayOut;
    private final int ERR = 211;
    private final String NEW_LINE = System.getProperty("line.separator");
    private final ClientView view;
    private final String RELAY_HOST;
    private final int RELAY_PORT;

    RelaySocket(ClientView viewCopy, String[] args) {
        this.view = viewCopy;
        RELAY_HOST = args[1];
        RELAY_PORT = Integer.parseInt((args[2]));
        createRelaySocket();
    }

    public String getRelayHost() {
        return RELAY_HOST;
    }

    private Boolean sendMessage(String msg) {
        try {
            relayOut.write(msg + NEW_LINE);
            relayOut.flush();
        } catch (IOException ex) {
            return false;
        }
        view.setMesage(NEW_LINE + "RELAY MSG: " + msg);
        return true;
    }

    private String getMessage() {
        if ((this.relaySocket.isClosed())) {
            return null;
        }
        String msg = null;
        try {
            msg = relayIn.readLine();
        } catch (IOException ex) {
        }
        view.setMesage(NEW_LINE + "RELAY ANSWR: " + msg);
        return msg;
    }

    public int getCodeMsg() {
        String msg = getMessage();
        if (msg != null) {
            return Integer.parseInt((msg.substring(0, 3)));
        }
        return ERR;
    }

    public boolean retransmit(String msg, int SUCCES) {
        sendMessage(msg);
        int code = 0;
        while (code != ERR && code != SUCCES) {
            code = getCodeMsg();
        }
        return code == SUCCES;
    }

    public boolean retransmit(ArrayList<String> mailInfoList, int SUCCES) {
        mailInfoList.stream().forEach((s) -> {
            sendMessage(s);
        });
        return getCodeMsg() == SUCCES;
    }

    private void createRelaySocket() {

        try {
            relaySocket = new Socket(RELAY_HOST, RELAY_PORT);
        } catch (IOException ex) {
        }

        try {
            relayIn = new BufferedReader(new InputStreamReader(relaySocket.getInputStream()));
            relayOut = new BufferedWriter(new OutputStreamWriter(relaySocket.getOutputStream()));
        } catch (IOException ex) {
        }
    }

    public void closeRelay() {
        try {
            relaySocket.close();
        } catch (IOException ex) {
        }
        view.setMesage(NEW_LINE + "RelaySocket closed.");
    }
}
