package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения RCPT TO от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Rcpt implements ICommand {

    //успешное выполнение команды
    private final static int SUCCES = 250;
    
    //плохая последовательность команд
    private final static int BSC = 503;

    //систаксическая ошибка в команде, но она распознана
    private final static int SYNTAX_ERR = 501;
    
    //не получилось реле
    private final static int FAIL_IN_RELAY = 211;
    
    //время ожидания ответа от след сервера, если этот промежточный
    private final long TIME_OUT = 300000;
    
    //для проверки синтаксиса
    private final String regExpRcpt = "\\A[rR]{1}[cC]{1}[pP]{1}[tT]{1}\\s{1}"
            + "[tT]{1}[oO]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}[a-z]{2,6}.{1}"
            + "[a-z]{2,3}>{1}\\z";

    
    /**
     * Функция - реакция на  строку RCPT от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.MAIL && cl.getClientState() != ClientState.RCPT) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }
        if (!isCorrectCommand(cl.getLastMessage(), this.regExpRcpt)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in RCPT");
            return;
        }

        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.setClientState(ClientState.RCPT);
            cl.sendMessage(SUCCES, "OK");
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "ERROR in RELAY RCPT.");
        }
        timer.stop();
    }
}
