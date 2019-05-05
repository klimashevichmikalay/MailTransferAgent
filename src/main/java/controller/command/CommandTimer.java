package controller.command;

import java.util.Timer;
import java.util.TimerTask;
import model.ClientListener;

/**
 * Класс таймера, нужен для сообщения об истечении времени ожидания ответа от
 * сервера.
 *
 * @author Климашевич Н. А., 621702
 * @version 1.0
 */
public class CommandTimer {

    /** Код ошибки, если истекло время */
    private final static int FAIL_IN_RELAY = 421;
    private final Timer timer;
    private final TimerTask task;

    /**
     * Конструктор и запуск таймера
     *
     * @param cl - класс, для которого засекается время, ему отправляется
     * сообщение о таймауте
     * @param timeOut - максимальное время ожидания
     */
    CommandTimer(ClientListener cl, long timeOut) {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                timeOut(cl);
            }
        };
        timer.schedule(task, timeOut);
    }

    /**
     * Остановка таймера и снятие задач из очереди, вызывается из ClientListener    
     */
    public void stop() {
        task.cancel();
        timer.cancel();
        timer.purge();
    }

     /**
     * Метод, отправляющий сообщение о истечении времени ClientListener-ру и прекращающий соединение.
     *
     * @param cl - класс, для которого засекается время, ему отправляется
     * сообщение о таймауте    
     */
    void timeOut(ClientListener cl) {
        cl.sendMessage(FAIL_IN_RELAY, "Time is out. Server close connection.");
        cl.close();
    }
}
