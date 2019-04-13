package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Mail implements ICommand {

    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int SYNTAX_ERR = 501;
    private final static int FAIL_IN_RELAY = 211;
    private final String regExpMail = "\\A[mM]{1}[aA]{1}[iI]{1}[lL]{1}\\s{1}"
            + "[fF]{1}[rR]{1}[oO]{1}[mM]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}"
            + "[a-z]{2,6}.{1}[a-z]{2,3}>{1}\\z";
    private final long TIME_OUT = 300000;

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.COMMUNICATION) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpMail)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in MAIL FROM");
            return;
        }
        cl.getMailInfo().clearInfo();

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.setClientState(ClientState.MAIL);
            cl.sendMessage(SUCCES, "OK");
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "ERROR in RELAY MAIL.");
        }
        timer.stop();
    }
}
