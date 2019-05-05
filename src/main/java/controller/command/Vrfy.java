package controller.command;

import model.ClientListener;
import model.RelaySocket;


/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения VRFY от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Vrfy implements ICommand {

    private final static int SUCCES = 250;
    private final static int SYNTAX_ERR = 501;
    private final static int FAIL_IN_RELAY = 211;
    private final long TIME_OUT = 300000;
    private final String regExpVrfy = "\\A[vV]{1}[rR]{1}[fF]{1}[yY]{1}\\s{1}[fF]"
            + "{1}[rR]{1}[oO]{1}[mM]{1}:{1}(<?([a-zA-Z0-9._]{2,63}[@]{1}){1}[a-z]{2,6}"
            + ".{1}[a-z]{2,3}>?,?){1,}";

    
    /**
     * Функция - реакция на  строку VRFY от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpVrfy)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in VRFY");
            return;
        }
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.sendMessage(SUCCES, "OK");
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "VRFY RELAY RCPT.");
        }
        timer.stop();
    }
}
