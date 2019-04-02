package controller.command;

import model.ClientListener;
import model.RelaySocket;

public class Vrfy implements ICommand {

    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final String regExpVrfy = "\\A[vV]{1}[rR]{1}[fF]{1}[yY]{1}\\s{1}[fF]"
            + "{1}[rR]{1}[oO]{1}[mM]{1}:{1}(<?([a-zA-Z0-9._]{2,63}[@]{1}){1}[a-z]{2,6}"
            + ".{1}[a-z]{2,3}>?,?){1,}";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if ((!cl.getLastMessage().equals(CommandsNames.vrfy))) {
            cl.sendMessage(BSC, "bad sequence of commands in Vrfy.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpVrfy)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
            return;
        }

        if (rs.retransmit(cl, SUCCES)) {
            cl.sendMessage(SUCCES, "OK");
        }
    }

    private boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}