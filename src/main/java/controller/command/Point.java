package controller.command;

import model.ClientListener;
import model.ClientState;
import model.MailInfo;
import model.RelaySocket;

/**
 * Класс - команда для доставки сообщения. Вызывается, когда клиент ввел сообщение после DATA и ввел точку.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class Point implements ICommand {

    /** маскимальное время ожидания - 10 минут */
    private final long TIME_OUT = 600000;
    /** плохая последовательность команд */
    private final static int BSC = 503;
    /** код об успешном отправлении */
    private final static int SUCCES = 250;
    /** мало информации в сообщении или ретрансляция на другой сервер неудачна */
    private final static int TRANCSACTION_FAILED = 554; 

    
    /**
     * Функция - реакция на завершения сообщения для пересылания
     *
     * @param cl - подключаемый клиент
     * @param rs - объект с сокетом. Если равен null, то текущий сервер - это
     * конечный пункт пути, иначе пытаемся подключится на rs, а потом в
     * зависимости от результата отправляем определенной сообщения
     */
    @Override
    public void execute(ClientListener cl, RelaySocket rs) {
        //проверка соответставия состоянию выполнению соответств команды
        if ((cl.getClientState() != ClientState.GET_MAIL_INFO)) {
            cl.sendMessage(BSC, "bad sequence of commands.");
            return;
        }

        //была введена точка- завершение ввода сообщения для отпраки,
        //добавляем эту точчку
        MailInfo mi = cl.getMailInfo();
        mi.add(".");
        
        //содержит ли сообщение от клиента минимальный набор команд?
        //Это: точка, from, to, subject, content-type
        if (!mi.isContainsMinCommands()) {
            cl.sendMessage(TRANCSACTION_FAILED, "Little information in the letter.");
            return;
        }

        //засекаем время
        CommandTimer timer = new CommandTimer(cl, TIME_OUT);
        
        //если это реле, то пытаемся переслать, если нет - то сразу говорим, что доставили,
        //ведь нам дошло
        if (rs == null || rs.retransmit(cl.getMailInfo().getMailInfoList(), SUCCES)) {
            cl.setClientState(ClientState.COMMUNICATION);
            cl.sendMessage(SUCCES, "message accepted for delivery");
        } else {            
            cl.sendMessage(TRANCSACTION_FAILED, "Transaction failed.");
        }
        //стоп таймер
        timer.stop();
    }
}
