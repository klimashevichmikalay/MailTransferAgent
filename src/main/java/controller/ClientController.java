package controller;

import controller.command.*;
import model.ClientListener;

import java.util.HashMap;
import java.util.Map;
import model.RelaySocket;

public class ClientController {

    //карта комманд
    private final Map<String, ICommand> commands;
   //добавление комманд, более подробно каждая команда 
   //описана в своем файле
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

    //сервер при подключении создает объект
    //класса ClientListener. В этом объекте
    //в отдельном потоке обрабатываются комманды клиента.
    //Этот объект содержит в себе объект
    //класса ClientController(он в текущем файле)
    //И чтобы обработать последнюю команду от клиента, мы выполняем  вот этот
    //метод execute(String command, ClientListener cl, RelaySocket rs).
    public void execute(String command, ClientListener cl, RelaySocket rs) {
        commands.get(command).execute(cl, rs);
    }
}