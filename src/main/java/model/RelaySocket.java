package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import log.MyLog;
import modelsListeners.ClientView;


/**
 * Класс для ретрансляции сообщений на другой сервер.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class RelaySocket {

    //сокет для общения со следующим сервером
    private Socket relaySocket;
    private BufferedReader relayIn;
    private BufferedWriter relayOut;
    
    //ошибка при неудачной ретрансляции
    private final int ERR = 211;
    private final String NEW_LINE = System.getProperty("line.separator");
    
    //преставление-окно сессии
    private final ClientView view;
    
    //адрес сервера следующего в пути реле
    private final String RELAY_HOST;
    private final int RELAY_PORT;
    private String name;

    /**
     * Конструктор
     *
     * @param viewCopy - окно для написания в нем результатов общения с сервером, на который пересылаются сообщения
     * @param args - агрументы, переданные программе при запуске, 1й и 2й элементы содержат
     * адрес сервера, куда пересылаем сообщения 
     */
    public RelaySocket(ClientView viewCopy, String[] args) {
        this.view = viewCopy;
        RELAY_HOST = args[1];
        RELAY_PORT = Integer.parseInt((args[2]));
        name = "relayInServer" + args[0];
        createRelaySocket();
    }

    public String getRelayHost() {
        return RELAY_HOST;
    }

    public String getName() {
        return name;
    }

     /**
     * Отправка сообщения через сокет relaySocket серверу
     *
     * @param msg - пересылаемое сообщение
     * @return ложь, если возникло исключение при записи в сокет, иначе истина      
     */
    private Boolean sendMessage(String msg) {
        try {
            relayOut.write(msg + NEW_LINE);
            relayOut.flush();
            MyLog.logMsg(this, "send msg: " + msg);
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in retransmit");
            return false;
        }
        view.setMesage(NEW_LINE + "RELAY MSG: " + msg);
        return true;
    }

    //возвращает полученное сообщение от сервера,
    //на который ретранслирует
    private String getMessage() {
        if ((this.relaySocket.isClosed())) {
            MyLog.logMsg(this, "can not get msg: socket is closed");
            return null;
        }
        String msg = null;
        try {
            msg = relayIn.readLine();
            MyLog.logMsg(this, "get msg: " + msg);
        } catch (IOException ex) {
        }
        view.setMesage(NEW_LINE + "RELAY ANSWR: " + msg);
        return msg;
    }

      /**
     * Получает код сообщения, которое вернул сервер, на который ретранслируем
     * 
     * @return код ответа  
     */
    public int getCodeMsg() {
        String msg = getMessage();
        if (msg != null) {
            return Integer.parseInt((msg.substring(0, 3)));
        }
        return ERR;
    }

    //String msg, int SUCCES - это сообщение для
    //ретрансляции и код, который след сервер должен дать
    //в случае успешного выполнения команды
    //елси все успешно - возвращаем истину
    public boolean retransmit(String msg, int SUCCES) {
        sendMessage(msg);
        int code = 0;
        while (code != ERR && code != SUCCES) {
            code = getCodeMsg();
        }
        return code == SUCCES;
    }

    //String mailInfoList, int SUCCES - это строки с информацией о сообщении после DATA 
    //для отправления на другую почту
    //ретрансляции и код, который след сервер должен дать
    //в случае успешного выполнения команды
    //елси все успешно - возвращаем истину
    public boolean retransmit(ArrayList<String> mailInfoList, int SUCCES) {
        mailInfoList.stream().forEach((s) -> {
            sendMessage(s);
        });
        return getCodeMsg() == SUCCES;
    }

    private void createRelaySocket() {

        try {
            relaySocket = new Socket(RELAY_HOST, RELAY_PORT);
            relayIn = new BufferedReader(new InputStreamReader(relaySocket.getInputStream()));
            relayOut = new BufferedWriter(new OutputStreamWriter(relaySocket.getOutputStream()));
            MyLog.logMsg(this, "create succes");
            MyLog.logMsg(this, "send msg relay to " + RELAY_HOST + " " + RELAY_PORT);
            MyLog.logMsg(this, "get msg " + name);
            view.setMesage(NEW_LINE + "relaySocket closed. retransmit to" + RELAY_HOST + " " + RELAY_PORT);
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in create socket or BufferedReader");
        }
    }

    public void closeRelay() {
        try {
            relaySocket.close();
            MyLog.logMsg(this, "closed socket");
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in closet");
        }
        view.setMesage(NEW_LINE + "relaySocket closed");
    }
}
