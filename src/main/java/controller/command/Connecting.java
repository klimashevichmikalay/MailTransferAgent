package controller.command;

import model.ClientListener;
import model.ClientState;
import model.RelaySocket;

/**
 * Команда, вызываемая один раз, как только клиент подключился.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Connecting implements ICommand {

    /**
     * код ошибки, когда эта команда не может вызываться - плохая
     * последовательность команд
     */
    private final int BSC = 503;
    /**
     * максимальное время ожидания подключения - 2 минуты
     */
    private final long TIME_OUT = 120000;
    /**
     * код ошибки, когда есть сервер, на который нужно пересылать сообщение, и к
     * нему не удалось подключиться за время 2 минуты
     */
    private final int FAIL_IN_RELAY = 211;
    /**
     * код успеха
     */
    private final int SUCCES = 220;

    /**
     * Функция - реакция на подключение клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        //если состояние сессии при работе с клиентом (Client.state)
        //не совпадает с нужным для этой команды, значит 
        //нарушена последовательность команд или клиент отправил новую команду, не
        //дождавшись ответа нашего сервера
        if (cl.getClientState() != ClientState.CONNECTION) {
            cl.sendMessage(BSC, "Bad sequence of commands.");
            return;
        }

        //создаем объект CommandTimer, который сразу засекает время TIME_OUT
        //если время выйдет, то он отправит ошибку о истечении времени ответа
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        //подключились на этот сервер - раз подклчились,
        //то ответ 220 всегда
        cl.sendMessage(SUCCES, "Sender OK.");

        //если RelayServer rs != null, то наш сервер - это реле
        //тогда мы ждем от сервера ответ 220
        if (rs != null && rs.getCodeMsg() == SUCCES) {
            //установили соединение - теперь устанавливаем состояние  COMMUNICATION
            //для дальнейшего общения
            cl.setClientState(ClientState.COMMUNICATION);
        } else if (rs != null) {
            //если ответ от rs-сервера, куда пересылаем не 220,
            //то сообщаем об ошибке
            cl.sendMessage(FAIL_IN_RELAY, "Cannot connect to relay.");
        }
        
        //если rs-реле - равно null, то в этот сервер - конечный путь
        //тогда сразу устанавливаем состояние COMMUNICATION
        if (rs == null) {
            cl.setClientState(ClientState.COMMUNICATION);
        }
        //после всего мы останавливаем таймер,
        //если до сюда не успели дойти, а время вышло, отправлено сообщение об ошибке
        timer.stop();
    }
}
