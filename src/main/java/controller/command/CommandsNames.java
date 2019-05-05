package controller.command;

/**
 * Класс констант-имен для команд, используемых в паттерне команд для обработки
 * сообщений при общении
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class CommandsNames {

    /** Имя команды, используемой как только клиент подключился */
    public final static String connecting = "connecting";
    /** Имя команды, вызываюейся при необходимости ответить на приветствие EHLO*/
    public final static String ehlo = "ehlo";
     /** Имя команды, вызываюейся при необходимости ответить на приветствие HELO*/
    public final static String helo = "helo";
      /** Имя команды, вызываюейся при необходимости ответить на MAIL*/
    public final static String mail = "mail";
     /** Имя команды, вызываюейся при необходимости ответить на RCPT */
    public final static String rcpt = "rcpt";
    /** Имя команды, вызываюейся при необходимости ответить на DATA */
    public final static String data = "data";
    /** Имя команды, вызываюейся при необходимости ответить на команду, которая не известна */
    public final static String unknown = "unknown";
    /** Имя команды, вызываюейся когда клиент написал точку и необходимо переслать сообщение */
    public final static String point = "point";
    /** Имя команды, вызываюейся желании клиента соединения */
    public final static String quit = "quit";
    /** Имя команды, вызываюейся при необходимости ответить на NOOP */
    public final static String noop = "noop";
    /** Имя команды, вызываюейся при необходимости ответить на RSET */
    public final static String rset = "rset";
    /** Имя команды, вызываюейся при необходимости ответить на VRFY */
    public final static String vrfy = "vrfy";
}