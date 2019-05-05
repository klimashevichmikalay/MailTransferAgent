package smtp.mail.server;

import java.io.*;
import log.MyLog;
import modelsListeners.SMTPServer;

/**
 * Класс инициализации лога и сервера.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class main {

    /**
     * Точка входа в программу
     *
     * @param args - первый элемент - порт этого компьютера, на котором запускаем сервер,
     * остальные - адрес сервера для ретрансляции
     * @exception IOException при исключении при создании MyLog и SMTPServer-объектов
     */
    public static void main(String[] args) throws IOException {      
        new MyLog();
        new SMTPServer(args);        
    }
}
