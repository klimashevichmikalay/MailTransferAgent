package controller.command;

import model.ClientListener;
import model.RelaySocket;

/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения NOOP от
 * клиента.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Noop implements ICommand {

    /** плохая посл команд, например, если клиент не дожидался ответа, а уже прислал другую команду и последнее сообщение
    * от клиента изменилось(lastMessage в классе ClientListener)
     */
    private final static int BSC = 503;
    private final static int SUCCES = 250;    
    
    /**
     * Функция - реакция на строку NOOP от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {

        if ((!cl.getLastMessage().equals(CommandsNames.noop))) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }       
        cl.sendMessage(SUCCES, "OK");
    } 
}