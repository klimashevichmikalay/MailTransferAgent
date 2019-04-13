package controller.command;

import model.ClientListener;
import model.RelaySocket;

public interface ICommand {

    void execute(ClientListener cl, RelaySocket rs);

    default boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}
