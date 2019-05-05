package controller.command;

import model.ClientListener;
import model.RelaySocket;

/**
 * Интерфейс для команд из паттерна команд.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public interface ICommand {    
     /**
     * Функция - реакция на какую либо команду, которую отправил клиент
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    void execute(ClientListener cl, RelaySocket rs);

     /**
     * Функция - проверка синтаксиса для команд
     *
     * @param str - строка от клиента
     * @param regexp - регулярное выражение для правильной строки
     * @return возвращает результат соответствия регулярному выраению сообщения от клиента
     */
    default boolean isCorrectCommand(String str, String regexp) {
        return str.matches(regexp);
    }
}
