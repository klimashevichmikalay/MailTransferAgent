package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Connecting implements ICommand {

    private final static int SUCCES = 220;
    private final static int BSC = 503;
    private final static int ERR = 211;

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.CONNECTION) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        if (rs.retransmit("", SUCCES)) {
            cl.setClientState(ClientState.COMMUNICATION);
            cl.sendMessage(SUCCES, "Sender OK");
        } else {
            cl.sendMessage(ERR, "Could not connect to relay server.");
        }
    }   
}