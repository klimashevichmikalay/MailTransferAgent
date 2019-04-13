package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Ehlo implements ICommand {

    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int ERR = 550;
    private final long TIME_OUT = 300000;

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.COMMUNICATION) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.getMailInfo().clearInfo();
            cl.sendMessage(SUCCES, "OK"); 
        } else {
            cl.sendMessage(ERR, "Error in relay EHLO.");
        }
        timer.stop();
    }
}