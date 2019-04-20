package controller.command;

import model.ClientListener;
import model.ClientState;
import model.MailInfo;
import model.RelaySocket;

public class Point implements ICommand {

    private final long TIME_OUT = 600000;
    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int TRANCSACTION_FAILED = 554; 

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if ((cl.getClientState() != ClientState.GET_MAIL_INFO)) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        MailInfo mi = cl.getMailInfo();
        mi.add(".");
        if (!mi.isContainsMinCommands()) {
            cl.sendMessage(TRANCSACTION_FAILED, "Little information in the letter.");
            return;
        }

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getMailInfo().getMailInfoList(), SUCCES)) {
            cl.setClientState(ClientState.COMMUNICATION);
            cl.sendMessage(SUCCES, "message accepted for delivery");
        } else {
            cl.sendMessage(TRANCSACTION_FAILED, "Transaction failed.");
        }
        timer.stop();
    }
}
