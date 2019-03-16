package inf112.skeleton.app;

import java.util.Vector;

class LogItem {
    /** Time relative to start of game log in milliseconds. */
    private long time;
    /** Counts number of occurrences. */
    private int num;
    /** The entity sending the message. */
    private String from;
    /** The displayed message text. */
    private String msg;

    LogItem(String from, String msg, long time) {
        this.from = from;
        this.msg = msg;
        this.time = time;
        num = 1;
    }

    private String fmtTime() {
        // Time in seconds
        int ts = (int) (time / 1000.0);
        int secs = ts % 60;
        int mins = (ts / 60) % 60;
        int hr = ts / 3600;
        return String.format("%02d:%02d:%02d", hr, mins, secs);
    }

    @Override
    public String toString() {
        return (from
                + "@"
                + fmtTime()
                + ": "
                + msg
                + (num <= 1 ? "" : String.format(" (x%02d)", num)));
    }

    String getMessage() {
        return msg;
    }

    void repeat() {
        num++;
    }
}

public class GameLog {
    private long start_time;
    private Vector<LogItem> logs;
    private int max_len;

    public GameLog(int max_len) {
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

        // This is obviously inefficient, but the game log doesn't exactly have
        // to be high-performance.
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
