package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Rcpt implements ICommand {

    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int UUA = 550;
    private final static int SYNTAX_ERR = 501;
    private final String regExpRcpt = "\\A[rR]{1}[cC]{1}[pP]{1}[tT]{1}\\s{1}"
            + "[tT]{1}[oO]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}[a-z]{2,6}.{1}"
            + "[a-z]{2,3}>{1}\\z";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.MAIL && cl.getClientState() != ClientState.RCPT) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }
        if (!isCorrectCommand(cl.getLastMessage(), this.regExpRcpt)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in RCPT");
            return;
        }

        //время ответа - 5 мин  
        if (rs.retransmit(cl, SUCCES)) {
            cl.setClientState(ClientState.RCPT);
            cl.sendMessage(SUCCES, "OK");
        }
    }

    public boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}