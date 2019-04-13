package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Data implements ICommand {

    private final long TIME_OUT = 120000;
    private final static int SUCCES = 354;
    private final static int BSC = 503;
    private final static int SYNTAX_ERR = 501;
    private final static int FAIL_IN_RELAY = 211;
    private final String regExpData = "\\A[dD]{1}[aA]{1}[tT]{1}[aA]{1}\\z";
    private final String succesMessage = "Enter mail, end with \".\" on a line by itself.";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if (cl.getClientState() != ClientState.MAIL && cl.getClientState() != ClientState.RCPT) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpData)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in DATA.");
            return;
        }

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.setClientState(ClientState.GET_MAIL_INFO);
            cl.sendMessage(SUCCES, succesMessage);
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "ERROR in RELAY DATA.");
        }
        timer.stop();
    }
}
