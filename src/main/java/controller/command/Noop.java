package controller.command;

import model.ClientListener;
import model.RelaySocket;

public class Noop implements ICommand {

    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final  String regExpNoop = "\\A[nN]{1}[oO]{2}[pP]{1}\\z";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if ((!cl.getLastMessage().equals(CommandsNames.noop))) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }
        cl.sendMessage(SUCCES, "OK");
    } 
}