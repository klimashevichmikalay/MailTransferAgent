package modelsListeners;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.MyLog;
import model.ClientListener;

/**
 * Класс для добавления клиентов и создания для них объекта-потока-обработки.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class SMTPServer {

    private static ServerSocket serverSocket;
    
    //текущий порт сервера на этом компе
    private final int CURRENT_PORT;
    
    //парсер для строк клиента, после его обработки выдается значение
    //команды, чтобы паттерн "команда" в ClientController обработал собощение клиента
    private final ClientParser clPrsr;
    
    //имя сервера
    private final String name;

    public SMTPServer(String[] args) {
        CURRENT_PORT = Integer.parseInt((args[0]));
        name = "serverPort" + args[0];
        clPrsr = new ClientParser();

        try {
            serverSocket = new ServerSocket(CURRENT_PORT);
            
            //лог создали в майне,его статические методы вызываются всюду, 
            //один класс лога на всю программу
            MyLog.logMsg(this, "create succes server");
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in create ServerSocket in port" + args[0]);
        }

        if (args.length < 3) {
            MyLog.logMsg(this, "server port " + args[0] + " .Without relay");
        } else {
            MyLog.logMsg(this, "server port " + args[0] + ". with relay to " + args[1] + " " + args[2]);
            MyLog.logMsg(this, "check relay log in " + MyLog.logClients + "relay" + args[0]);
        }

        this.acceptClients(args);
    }

    /**
     * Цикл подключения клиентов
     *
     * @param args - аргументы при запуске команд, если есть реле, то аргументы
     * содержат адрес для пересылки сбщния
     */
    void acceptClients(String[] args) {
        while (!SMTPServer.serverSocket.isClosed()) {
            MyLog.logMsg(this, "wait client...");
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();

            } catch (IOException ex) {
                MyLog.logMsg(this, "ERR in accept client");
                continue;
            }

            ClientListener clLstnr = new ClientListener(clientSocket, args);
            MyLog.logMsg(this, "accept client " + clLstnr.getName());
            MyLog.logMsg(this, "check log in " + MyLog.logClients + clLstnr.getName());
            clLstnr.start();
        }
        MyLog.logMsg(this, "server is closed...");
    }

    public void closeServer() {
        try {
            serverSocket.close();
            MyLog.logMsg(this, "server is closed succes...");

        } catch (IOException ex) {
            Logger.getLogger(SMTPServer.class
                    .getName()).log(Level.SEVERE, null, ex);
            MyLog.logMsg(this, "ERR in in closing server");
        }
    }

    /**
     * Получить сокетСервера
     *
     * @return сокет сервера serverSocket
     */
    public ServerSocket getServerSocket() {
        return SMTPServer.serverSocket;
    }

    /**
     * Получить порт сервера
     *
     * @return потр сервера, который он занимает на этом компьютере
     */
    public static int getServerPort() {
        return serverSocket.getLocalPort();
    }

    public String getName() {
        return name;
    }
}
