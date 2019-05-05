package controller;

import controller.command.*;
import model.ClientListener;

import java.util.HashMap;
import java.util.Map;
import model.RelaySocket;

/**
 * Класс для хранения комманд и запуска нужной. Реализация шаблона комманда.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class ClientController {

    /**
     * Карта комманд - класов для обработки команд сервера
     */
    private final Map<String, ICommand> commands;

     /**
         * Конструктор - создание нового объекта и 
         * добавление объектов для обработки сообщений клиента 
         */
    public ClientController() {       
        commands = new HashMap<>();
        //CommandsNames.****  -  дублируют названия класов 
        //соответствующего класса обработчика команды от клиента
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

    /**
     * @param command - имя команды, получается путем обработки строки от клиента
     * @param cl - класс обработки сообщений от клиента, работает в отдельном потоке
     * @param rs - релэй класс, содержит сокет, который пересылает сообщения другому серверу
     */
    public void execute(String command, ClientListener cl, RelaySocket rs) {
       //класс ClientListener для работы с клиентом содержит поле - текущий класс
        // и вызывает нужную команду после того, как обработает стороку-сообщение от клиента
        //в классе ParseMail
        commands.get(command).execute(cl, rs);
    }
}
