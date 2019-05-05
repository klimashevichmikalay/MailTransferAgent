package modelsListeners;

import model.ClientListener;
import controller.command.CommandsNames;

/**
 * Класс обрабатывает строки, полученные от клиента для последующего вызова с
 * помощью обработанной строки нужной команды из ClientController для обработки
 * сообщения.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class ClientParser {

    private String msg;

    /**
     * Обработка последнего сообщения от клиента
     *
     * @param cl - класс общения с клиентом, содержит String lastMessage -
     * последнее сообщение, с которым работает данная функция
     * @return имя команды для вызова команды обработки сообщения из
     * ClientController
     */
    public String parseClient(ClientListener cl) {

        msg = getCommandWord(cl).toLowerCase();

        //если это команда, которая может вызываться
        //в любой время сессии, то возвращаем сроку с ее названием.
        String qnhr;
        if ((qnhr = checkQNHRV(msg)) != null) {
            return qnhr;
        }

        //если нет, то смотри команды по состоянию сесии
        //если ничего не нашли, возвращаем unknown - неизвестную команду
        switch (cl.getClientState()) {

            case QUIT: {
                return null;
            }

            case CONNECTION: {
                return CommandsNames.connecting;
            }
            case MAIL: {
                return parseMail(msg);
            }
            case COMMUNICATION: {
                return parseCommunication(msg);
            }

            case GET_MAIL_INFO: {
                if (msg.equals(CommandsNames.point)) {
                    return CommandsNames.point;
                }
            }

            case RCPT: {
                if (msg.equals(CommandsNames.data)) {
                    return CommandsNames.data;
                }

                if (msg.equals(CommandsNames.rcpt)) {
                    return CommandsNames.rcpt;
                }
            }
            default:
                return CommandsNames.unknown;
        }
    }

    /**
     * Проверка на команды, которые клиент может писать в любое время, после
     * любой команды
     *
     * @param msg - строка от клиента
     * @return null при неизвестной команде, строка-команда, если известна
     * обработчика в паттерке команда в ClientListener
     */
    private String checkQNHRV(String msg) {

        if (msg.equals(CommandsNames.vrfy)) {
            return CommandsNames.vrfy;
        }

        if (msg.equals(CommandsNames.quit)) {
            return CommandsNames.quit;
        }

        if (msg.equals(CommandsNames.noop)) {
            return CommandsNames.noop;
        }

        if (msg.equals(CommandsNames.rset)) {
            return CommandsNames.rset;
        }
        return null;
    }

    public String getCommandWord(ClientListener cl) {

        if (".".equals(cl.getLastMessage())) {
            return CommandsNames.point;
        }

        if (cl.getLastMessage().split(" ", 2) != null) {
            return cl.getLastMessage().split(" ", 2)[0];
        }
        return cl.getLastMessage().substring(1);
    }

    /**
     * Определение команды клиента при состоянии COMMUNICATION(пользовательский
     * тип)
     *
     * @param msg - строка от клиента
     * @return null, и нет, иначе строку для вызова обработчика в паттерке
     * команда в ClientListener
     */
    String parseCommunication(String msg) {//нужно дописать комманды      

        if (msg.equals(CommandsNames.ehlo)) {
            return CommandsNames.ehlo;
        }
        if (msg.equals(CommandsNames.helo)) {
            return CommandsNames.helo;
        }
        if (msg.equals(CommandsNames.mail)) {
            return CommandsNames.mail;
        }

        if (msg.equals(CommandsNames.quit)) {
            return CommandsNames.quit;
        }
        return CommandsNames.unknown;
    }

    String parseMail(String msg) {

        if (msg.equals(CommandsNames.rcpt)) {
            return CommandsNames.rcpt;
        }
        return CommandsNames.unknown;
    }
}
