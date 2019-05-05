package controller.command;

import model.ClientListener;
import model.RelaySocket;

/**
 * Класс для команды из паттерна команд, нужен для обработки сообщения  от
 * клиента, которое мы не распознали.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Unknown implements ICommand {

    //код, когда команда не распознана, отправляется 
    //когда ClientParser не распознал команду
    private final static int UC = 500;
    
    /**
     * Функция - реакция на непонятную этому серверу строку от клиента
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        cl.sendMessage(UC, "unknown command: " + cl.getLastMessage());
    }
}