package controller;

import controller.command.*;
import model.ClientListener;

import java.util.HashMap;
import java.util.Map;
import model.RelaySocket;

public class ClientController {

    private final Map<String, ICommand> commands;

    public ClientController() {
        commands = new HashMap<>();
        commands.put(CommandsNames.connecting, new Connecting());
        commands.put(CommandsNames.ehlo, new Ehlo());
        commands.put(CommandsNames.helo, new Helo());
        commands.put(CommandsNames.mail, new Mail());
        commands.put(CommandsNames.rcpt, new Rcpt());
        commands.put(CommandsNames.data, new Data());
        commands.put(CommandsNames.unknown, new Unknown());
        commands.put(CommandsNames.point, new Point());
        commands.put(CommandsNames.quit, new Quit());
        commands.put(CommandsNames.noop, new Noop());
        commands.put(CommandsNames.rset, new Rset());
        commands.put(CommandsNames.vrfy, new Vrfy());
    }

    public void execute(String command, ClientListener cl, RelaySocket rs) {
        commands.get(command).execute(cl, rs);
    }
}