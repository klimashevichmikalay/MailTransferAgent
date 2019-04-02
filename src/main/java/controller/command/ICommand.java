package controller.command;

import model.ClientListener;
import model.RelaySocket;

public interface ICommand {

    void execute(ClientListener cl, RelaySocket rs);
}
