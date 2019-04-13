package controller.command;

import java.util.Timer;
import java.util.TimerTask;
import model.ClientListener;

public class CommandTimer {

    private final static int FAIL_IN_RELAY = 421;
    private final Timer timer;
    private final TimerTask task;

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

    public void stop() {
        task.cancel();
        timer.cancel();
        timer.purge();
    }

    void timeOut(ClientListener cl) {
        cl.sendMessage(FAIL_IN_RELAY, "Time is out. Server close connection.");
        cl.close();
    }
}
