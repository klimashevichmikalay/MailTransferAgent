package controller.command;

import model.ClientListener;
import model.RelaySocket;

public class Unknown implements ICommand {

    private final static int UC = 500;

    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        cl.sendMessage(UC, "unknown command: " + cl.getLastMessage());
    }
}