package controller.command;

import controller.command.ICommand;
import controller.command.ICommand;
import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Mail implements ICommand {

    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int SYNTAX_ERR = 501;
    private final String regExpMail = "\\A[mM]{1}[aA]{1}[iI]{1}[lL]{1}\\s{1}"
            + "[fF]{1}[rR]{1}[oO]{1}[mM]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}"
            + "[a-z]{2,6}.{1}[a-z]{2,3}>{1}\\z";

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

        //время ответа - 5 мин   
        if (rs.retransmit(cl, SUCCES)) {
            cl.setClientState(ClientState.MAIL);
            cl.sendMessage(SUCCES, "OK");
        }
    }

    public boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}
