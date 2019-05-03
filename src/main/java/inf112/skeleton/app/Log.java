package inf112.skeleton.app;

import java.util.Vector;

public class Log {
    private long start_time;
    private Vector<LogItem> logs;
    private int max_len;

    public Log(int max_len) {
        this.max_len = max_len;
        start_time = System.currentTimeMillis();
        logs = new Vector<>();
    }

    public void append(String player, String msg) {
        if (logs.size() >= 1)
            if (logs.get(0).getMessage().equals(msg)) {
                logs.get(0).repeat();
                return;
            }

        if (logs.size() >= max_len)
            logs.removeElementAt(logs.size()-1);

        long time = System.currentTimeMillis() - start_time;
        logs.insertElementAt(new LogItem(player, msg, time), 0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (LogItem log : logs) {
            builder.append(log.toString() + "\n");
        }
        return builder.toString();
    }
}
