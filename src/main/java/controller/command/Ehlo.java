package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Команда, вызываемая каждый раз, когда клиент отправил EHLO.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Ehlo implements ICommand {

    /** код успеха */
    private final static int SUCCES = 250;
     /** плохая последовательность команд */
    private final static int BSC = 503;
     /** код ошибки при пересылании */
    private final static int ERR = 550;
     /** макс время ожидания ответа от реле сервера - 5 мин*/
    private final long TIME_OUT = 300000;

    
    
    /**
     * Функция - реакция на EHLO от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        if (cl.getClientState() != ClientState.COMMUNICATION) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        //засекаем время
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        
        //если rs-класс с сокетом для реле не равен null,
        //то этот сервер промежуточный
        //пересылаем собщение клиента, а также код SUCCES - код ууспешного
        //выполнения команды
        //если следующий сервер вернет не SUCCES, то SUCCES retransmit(cl.getLastMessage(), SUCCES)
        //вернет ложь
        if (rs == null || rs.retransmit(cl.getLastMessage(), SUCCES)) {
            cl.getMailInfo().clearInfo();
            cl.sendMessage(SUCCES, "OK"); 
        } else {
            cl.sendMessage(ERR, "Error in relay EHLO.");
        }
        //после всего останавливаем секудномер
        timer.stop();
    }
}