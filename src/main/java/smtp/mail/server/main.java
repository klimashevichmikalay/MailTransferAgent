package smtp.mail.server;
//план:
//дописать большую вью
//дописать проверки!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//лог
//объект с инфой - дописать регулярные выражения
//возможно,  свои исключения
//Executors мб добавить?
//добавить секундомеры?
import java.io.*;
import modelsListeners.SMTPServer;


/*

<dependency>
            <groupId>com.mkyong.hashing</groupId>
            <artifactId>MailTransferAgent</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>


*/
public class main {

    public static void main (String[] args) throws IOException {
        SMTPServer smtpSrvr = new SMTPServer();
        smtpSrvr.startServer();
    }
}