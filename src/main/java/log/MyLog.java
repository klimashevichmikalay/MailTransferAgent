package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import model.ClientListener;
import model.RelaySocket;
import modelsListeners.SMTPServer;


/**
 * Класс-реализация логирования
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class MyLog {  
     /**
     * Конструктор, создает, если их нет, папки с лог-файлами    
     */
    public MyLog() {        
        new File("log").mkdirs();
        //сесии общения с клиентами и другими серверами, если на них пересылаем.
        //в последнем случае наш сервер выступает в роли клиента и его общение
        //с другим сервером будет хранится в папке для файлов-сессии клиентов, 
        //ведь он клиент по сути в этой ситуации
        new File(logClients).mkdirs();
        
        //файлы серверов с разными портами: когда какой клиент подключился и тд.
        new File(logServers).mkdirs();
    }

    /**
     * Путь к файлам с лог-файлами лога каждой сессии с каким-либо клиентом  
     */
    public static final String logClients = "log/clients/";
     /**
     * Путь к файлам с лог-файлами для серверов: какие клиенты и когда подключились, ход работы 
     */
    public static final String logServers = "log/servers/";

    
     /**
     * Запись в лог события, связанного с ретрансляцией
     *
     * @param msg - сообщение, записываемое в лог файл
     * @param rs - объект с сокетом для реле
     */
    public static void logMsg(RelaySocket rs, String msg) {
        String path = logClients + rs.getName();
        writeMsg(path, msg);
    }

    /**
     * Запись в лог события, связанного с добавлением клиентов
     *
     * @param msg - сообщение, записываемое в лог файл
     * @param server - объект с сообщением для реле
     */
    public static void logMsg(SMTPServer server, String msg) {
        String path = logServers + server.getName();
        writeMsg(path, msg);
    }

    public static void logMsg(ClientListener cl, String msg) {
        String path = logClients + cl.getName();
        writeMsg(path, msg);
    }

    
    /**
     * Непосредственно записть сообщения в файл по определенному пути
     *
     * @param msg - сообщение, записываемое в лог файл
     * @param path - путь лог-файла
     */
    private static void writeMsg(String path, String msg) {
        FileWriter fStream;
        try {
            fStream = new FileWriter(path, true);
            fStream.append(new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(Calendar.getInstance().getTime())
                    + " --- " + msg);
            fStream.append("\n");
            fStream.flush();
            fStream.close();
        } catch (IOException ex) {
        }
    }
}
