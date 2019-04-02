package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Data implements ICommand {

    private final static int SUCCES = 354;
    private final static int BSC = 503;
    private final static int SYNTAX_ERR = 501;
    private final String regExpData = "\\A[dD]{1}[aA]{1}[tT]{1}[aA]{1}\\z";

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.MAIL && cl.getClientState() != ClientState.RCPT) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpData)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in DATA");
            return;
        }

        //время ответа - 2 мин
        if (rs.retransmit(cl, SUCCES)) {
            cl.setClientState(ClientState.GET_MAIL_INFO);
            cl.sendMessage(SUCCES, "Enter mail, end with \".\" on a line by itself");
        }
    }  
  
    boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}