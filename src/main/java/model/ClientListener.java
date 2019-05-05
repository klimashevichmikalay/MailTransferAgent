package model;

import controller.ClientController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.MyLog;
import modelsListeners.ClientParser;
import modelsListeners.ClientView;
import modelsListeners.SMTPServer;

/**
 * Класс для работы с клиентом: обработка сообщений, закрытие соединения,
 * организация ввода вывода в сокет.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class ClientListener implements Runnable {

    //сокет для общения с клиентом
    private final Socket clientSocket;

    //для ретрансляции, если это поле == null,
    //то этот сервер - конечный пункт назначения
    private final RelaySocket relaySocket;

    //ввод и вывод для сокета клиента
    private BufferedReader in;
    private BufferedWriter out;

    //представление
    private ClientView view;

    //поток работы с клиентом
    private final Thread t;

    //последне сообщение от клиента
    private volatile String lastMessage;

    //состояние клиента, описано в классе ClientState
    private volatile ClientState state;

    //этот класс для вызова команд обработки
    //сообщений клиента, реализация шаблона "команда"
    private final ClientController clCtrl;

    //парсер для сообщения клиента, 
    //для каждого сообщение от клиента
    //от обрабатывает и выдает название нужной команды из паттерна команд
    //затем в ClientController clCtrl вызывается эта команда
    //и в ней решается, что ответить клиенту
    private final ClientParser clPrsr;
    private static final String POINT = ".";

    //после сообщения DATA от клиента
    //сюда сохраняются все полученные от него строки до пустой строки с точкой
    MailInfo mailInfo;
    private final static String NEW_LINE = System.getProperty("line.separator");

    //имя класса, нужно для лога, для кадой сессии уникально, привязывается ко времени
    private String name;

    //clientSocketCopy - сокет, созданный в SMTPServer 
    //для общения с новым клиентом, String[] args - входные
    //параметры программы
    public ClientListener(Socket clientSocketCopy, String[] args) {

        //даем имя клиету
        setName(args);
        this.clientSocket = clientSocketCopy;
        clPrsr = new ClientParser();
        clCtrl = new ClientController();
        createView();
        try {
            createWriteRead();
            MyLog.logMsg(this, "create succes client. " + "check log in" + MyLog.logClients + getName());
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in create client");
        }
        
        MyLog.logMsg(this, "creating mailinfo...");
        mailInfo = new MailInfo();
        state = ClientState.CONNECTION;
        lastMessage = "220 Sender OK";
        MyLog.logMsg(this, "creating thread...");
        t = new Thread(this, "ClientListener");
        view.setMesage("SERVER_PORT: " + SMTPServer.getServerPort());

        //если входной параметр содержит одну строку
        //то это конечный сервер, иначе промежуточный
        //и остальные 2 параметры содержат адркс ретрансляции
        if (args.length == 1) {
            MyLog.logMsg(this, "messages will not retransmit");
            relaySocket = null;
        } else {
            relaySocket = new RelaySocket(this.view, args);
            MyLog.logMsg(this, "create relay for this client. check log in "
                    + MyLog.logClients + relaySocket.getName());
        }
    }

    /**
     * Запуск нити для обработки сообщений от клиента.
     */
    @Override
    public void run() {
        MyLog.logMsg(this, "run thread");

        //сразу выполням команду Connecting, сообщаем "220 ОК" клиенту
        clCtrl.execute(clPrsr.parseClient(this), this, this.relaySocket);
        while (!(this.clientSocket.isClosed())) {
            try {
                lastMessage = in.readLine();
            } catch (IOException ex) {
                MyLog.logMsg(this, "ERR in read socket");
                continue;
            }

            //устанавливаем последнее сообщения в окно сессии
            view.setMesage(NEW_LINE + "C: " + lastMessage);

            //потом записываем его в лог
            MyLog.logMsg(this, "get: " + NEW_LINE + "C: " + lastMessage);

            //если клиент вводит данные сообщения для отправления,
            //то сохраняем каждую строку в  mailInfo
            //как только он ввел одну точку в строке, то начинается отправка
            if (this.state == ClientState.GET_MAIL_INFO && !lastMessage.equals(POINT)) {
                mailInfo.add(lastMessage);
            } else {
                clCtrl.execute(clPrsr.parseClient(this), this, this.relaySocket);
            }
        }
        MyLog.logMsg(this, "client closed");
    }

    /**
     * Отправить сообщение клиенту
     *
     * @param code - код для клиента
     * @param msg - сообщение, которое идет за кодом ошибки или успеха, в
     * зависимости от результата отправляем определенной сообщения
     */
    public void sendMessage(int code, String msg) {
        
        if (clientSocket.isClosed()) {
            MyLog.logMsg(this, "ERR in send msg: client closed");
            return;
        }
        
        msg = Integer.toString(code) + " " + msg;
        try {
            out.write(msg + NEW_LINE);
            out.flush();
            MyLog.logMsg(this, "send: " + NEW_LINE + "C: " + msg);
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            MyLog.logMsg(this, "ERR in send msg");
            return;
        }
        view.setMesage(NEW_LINE + "S: " + msg + NEW_LINE);
    }
    
    public void close() {
        try {
            clientSocket.close();
            MyLog.logMsg(this, "client closed");
        } catch (IOException ex) {
            MyLog.logMsg(this, "ERR in closed");
        }
        view.setMesage(NEW_LINE + "Client socket closed." + NEW_LINE);
    }

    /**
     * Отправить сообщение клиенту
     *
     * @exception IOException если не удалось создать ввод или вывоз с сокетом
     */
    private void createWriteRead() throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    //имя привязывается к дате вплоть до миллисекунд
    private void setName(String[] args) {
        name = "client" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime()) + 
                "_SPORT_" + args[0];      
    }
    
    public String getName() {
        return name;
    }
    
    public MailInfo getMailInfo() {
        return this.mailInfo;
    }
    
    public void start() {
        this.t.start();
    }
    
    public boolean isClosed() {
        return clientSocket.isClosed();
    }
    
    private void createView() {
        this.view = new ClientView();
    }
    
    public void setClientState(ClientState newState) {
        this.state = newState;
    }
    
    public ClientState getClientState() {
        return this.state;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
}
