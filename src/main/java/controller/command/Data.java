package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Команда, вызываемая каждый раз, когда хочет начать ввод сообщения.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Data implements ICommand {

    /**
     * макс время ожидания - 2 минуты
     */
    private final long TIME_OUT = 120000;
    /**
     * код готовности принять сообщение
     */
    private final static int SUCCES = 354;
    /**
     * плохая последовательность команд, например если перед этой командой не
     * было MAIL и/или RCPT
     */
    private final static int BSC = 503;
    /**
     * синтаксическая ошибка в команде, например как в "DATA;"
     */
    private final static int SYNTAX_ERR = 501;
    /**
     * релэй сервер не вернул 354
     */
    private final static int FAIL_IN_RELAY = 211;
    /**
     * регулярка для проверки синтаксиса
     */
    private final String regExpData = "\\A[dD]{1}[aA]{1}[tT]{1}[aA]{1}\\z";
    /**
     * указание действий клиенту, если сервер готов принять сообщение для
     * пересылки
     */
    private final String succesMessage = "Enter mail, end with \".\" on a line by itself.";

    /**
     * Функция - реакция на сообщение DATA от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пересылаем на rs, а потом в зависимости от
     * результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        //проверка состояний
        if (cl.getClientState() != ClientState.MAIL && cl.getClientState() != ClientState.RCPT) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        //проверка синтаксиса команды, isCorrectCommand(cl, regex) 
        //возвращает ложь, если строка от клиента не подходит под шаблон этот команды
        if (!isCorrectCommand(cl.getLastMessage(), this.regExpData)) {
            cl.sendMessage(SYNTAX_ERR, "Syntax error in DATA.");
            return;
        }

        //теперь засекаем время TIME_OUT
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);        
        
        //если rs-класс с сокетом для реле не равен null,
        //то этот сервер промежуточный
        //пересылаем собщение клиента, а также код SUCCES - код ууспешного
        //выполнения команды
        //если следующий сервер вернет не SUCCES, то SUCCES retransmit(cl.getLastMessage(), SUCCES)
        //вернет ложь
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            
            cl.setClientState(ClientState.GET_MAIL_INFO);
            cl.sendMessage(SUCCES, succesMessage);
        } else {
            cl.sendMessage(FAIL_IN_RELAY, "ERROR in RELAY DATA.");
        }
        timer.stop();
    }
}
