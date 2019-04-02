package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

public class Ehlo implements ICommand {

    private final static int SUCCES = 250;
    private final static int BSC = 503;
    private final static int ERR = 550;   

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.COMMUNICATION) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }      

        //время ответа - 5 мин            
        if (rs.authorization() != SUCCES) {
            cl.sendMessage(ERR, "Unable to pass authorization on the relay server.");
        } else {
            cl.getMailInfo().clearInfo();
            cl.sendMessage(SUCCES, "OK");
        }
    }
}