package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Quit implements ICommand {

    private final long TIME_OUT = 120000;
    private final static int SUCCES = 221;
    private final static int SYNTAX_ERR = 501;
    private final String regExpQuit = "\\A[qQ]{1}[uU]{1}[iI]{1}[tT]{1}\\z";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpQuit)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in QUIT");
            return;
        }

        cl.setClientState(ClientState.QUIT);
        cl.sendMessage(SUCCES, "SMTPServer closing connection.");
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs != null && rs.retransmit("QUIT", SUCCES)) {
            rs.closeRelay();
        }
        cl.close();
        timer.stop();
    }
}
