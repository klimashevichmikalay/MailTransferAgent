package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения RSET от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Rset implements ICommand {

    private final static int BSC = 503;
    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final String regExpRset = "\\A[rR]{1}[sS]{1}[eE]{1}[tT]{1}\\z";

    
    /**
     * Функция - реакция на  строку RSET от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if ((!cl.getLastMessage().equals(CommandsNames.rset))) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpRset)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in RSET");
            return;
        }
        //сброс всей информации о прошлом пересылаемом сообщении
        cl.getMailInfo().clearInfo();
        cl.sendMessage(SUCCES, "OK");
        cl.setClientState(ClientState.COMMUNICATION);
    }
}