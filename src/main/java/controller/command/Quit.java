package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;


/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения QUIT от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Quit implements ICommand {

    /** время отжидания ответа от реле - 2 минтуы  */
    private final long TIME_OUT = 120000;
    /** сервер готов закрыть соединение */
    private final static int SUCCES = 221;
    private final static int SYNTAX_ERR = 501;
    private final String regExpQuit = "\\A[qQ]{1}[uU]{1}[iI]{1}[tT]{1}\\z";

    
    
    /**
     * Функция - реакция на  строку QUIT от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        //проверка синтаксиса
        if (!isCorrectCommand(cl.getLastMessage(), this.regExpQuit)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in QUIT");
            return;
        }

        //установка состояния, сообщения клиенту о прощании
        cl.setClientState(ClientState.QUIT);
        cl.sendMessage(SUCCES, "SMTPServer closing connection.");
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs != null && rs.retransmit("QUIT", SUCCES)) {
            rs.closeRelay();
        }
        cl.close();
        timer.stop();
    }
}
