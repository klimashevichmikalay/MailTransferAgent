package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения MAIL FROM от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Mail implements ICommand {

    /**
     * код успеха, если сервер конечный, возвращается сразу, если промежуточный,
     * то после ответа 250 от сервера
     */
    private final static int SUCCES = 250;
    /**
     * плохая последовательность команд
     */
    private final static int BSC = 503;
    /**
     * синтаксическая ошибка
     */
    private final static int SYNTAX_ERR = 501;
    /**
     * какая - то неудача при ретранслировании
     */
    private final static int FAIL_IN_RELAY = 211;
    /**
     * регулярное выражения для строк, соответствующих правильному синтаксису
     */
    private final String regExpMail = "\\A[mM]{1}[aA]{1}[iI]{1}[lL]{1}\\s{1}"
            + "[fF]{1}[rR]{1}[oO]{1}[mM]{1}:{1}<([a-zA-Z0-9._]{1,63}[@]{1}){1}"
            + "[a-z]{2,6}.{1}[a-z]{2,3}>{1}\\z";
    /**
     * максм время ожидания ответа от сервера-релэя - 5 минут
     */
    private final long TIME_OUT = 300000;

    /**
     * Функция - реакция на какую либо строку MAIL от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.COMMUNICATION) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        if (!isCorrectCommand(cl.getLastMessage(), this.regExpMail)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in MAIL FROM");
            return;
        }
        cl.getMailInfo().clearInfo();

        //теперь засекаем время TIME_OUT, по истечении которого
        //клиент получит собщение об ошибке времени
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        
        //если rs-класс с сокетом для реле не равен null,
        //то этот сервер промежуточный
        //пересылаем собщение клиента, а также код SUCCES - код ууспешного
        //выполнения команды
        //если следующий сервер вернет не SUCCES, то SUCCES retransmit(cl.getLastMessage(), SUCCES)
        //вернет ложь
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.setClientState(ClientState.MAIL);
            cl.sendMessage(SUCCES, "OK");
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "ERROR in RELAY MAIL.");
        }
        //обработали сообщение клиента, стоп таймер
        timer.stop();
    }
}
