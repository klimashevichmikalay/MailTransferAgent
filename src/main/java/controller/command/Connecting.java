package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Connecting implements ICommand {

    private final int BSC = 503;
    private final long TIME_OUT = 120000;
    private final int FAIL_IN_RELAY = 211;
    private final int SUCCES = 220;

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if (cl.getClientState() != ClientState.CONNECTION) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        cl.sendMessage(SUCCES, "Sender OK.");

        if (rs != null && rs.getCodeMsg() == SUCCES) {
            cl.setClientState(ClientState.COMMUNICATION);
        } else if (rs != null) {
            cl.sendMessage(FAIL_IN_RELAY, "Cannot connect to relay.");
        }

        if (rs == null) {
            cl.setClientState(ClientState.COMMUNICATION);
        }
        timer.stop();
    }
}
