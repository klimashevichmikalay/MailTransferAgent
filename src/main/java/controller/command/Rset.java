package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Rset implements ICommand {

    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final String regExpRset = "\\A[rR]{1}[sS]{1}[eE]{1}[tT]{1}\\z";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if ((!cl.getLastMessage().equals(CommandsNames.rset))) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpRset)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in RSET");
            return;
        }
        cl.getMailInfo().clearInfo();
        cl.sendMessage(SUCCES, "OK");
        cl.setClientState(ClientState.COMMUNICATION);
    }
}