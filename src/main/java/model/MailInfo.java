package model;

import java.util.ArrayList;


/**
 * Класс для хранения данных сообщения, после того как клиент вводит сообщение для отправления.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class MailInfo {

    //все строки сообщения для отправки
    private final ArrayList<String> mailInfoList;
    
    //минимальный набор команд, который долен быть: to, from , subject, content-type и точка
    private ArrayList<String> requiredCommands;
    
    //первые слова строк от клиета, например от "from:gggt"
    //сюда попадет "from"
    //потом проверим в isContainsMinCommands() содержит ли этот массив
    //в себе массив минимальных команд - requiredCommands
    private final ArrayList<String> commandsMailInfoList;
    private final String to = "To";
    private final String from = "From";
    private final String subject = "Subject";
    private final String contentType = "Content-Type";
    private final String empty = "";
    private final String point = ".";

    /**
     * Конструктор, инициализация массивов для хранения и необходимых минимальных команд
     *     
     */
    public MailInfo() {
        this.mailInfoList = new ArrayList<>();
        commandsMailInfoList = new ArrayList<>();
        createRequiredCommands();
    }
    
     /**
     * Добавить минимальный набор команд, которое должно содержать пересылаемой сообщение: 
     * to, from, subject, content-type, и точка. 
     */
    private void createRequiredCommands() {
        this.requiredCommands = new ArrayList<>();
        requiredCommands.add(to);
        requiredCommands.add(from);
        requiredCommands.add(subject);
        requiredCommands.add(contentType);
        requiredCommands.add(empty);
        requiredCommands.add(point);
    }

    
    /**
     * Проверка на то, содержит ли сообщение для отправки минимальный набор команд  
     * @return возвращает истину, если содержит, ложь в противном случае
     */
    public boolean isContainsMinCommands() {
        if (commandsMailInfoList.isEmpty()) {
            return false;
        }
        return commandsMailInfoList.containsAll(requiredCommands);
    }

    /**
     * Взять первое слово от команды, пересылаемой клиентов при описании отправляемого письма для
     * добавления в массив информации о сообщении.
     * 
     * @param str - строка от клиента
     * @return первое слово до двоеточия, например для Subject: tema это будет Subject
     */
    public String getFirstWord(String str) {
        try {
            return str.split(":", 2)[0];
        } catch (RuntimeException e) {
            return null;
        }
    }

    public ArrayList<String> getMailInfoList() {
        return this.mailInfoList;
    }

    /**
     * Добавление данных сообщения
     * 
     * @param info - строка от клиента, добавляется певое слово в массив commandsMailInfoList для его проверки на включение минимума команд,
     * и строка целиком добавляется в mailInfoList для пересылания. 
     */
    public void add(String info) {
        this.mailInfoList.add(info);
        this.commandsMailInfoList.add(getFirstWord(info));
    }

    public void clearInfo() {
        this.mailInfoList.clear();
        this.commandsMailInfoList.clear();
    }
}
