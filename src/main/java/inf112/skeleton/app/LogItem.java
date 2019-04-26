package inf112.skeleton.app;

public class LogItem {
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
                //+ "@"
                //+ fmtTime()
                //+ ": "
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

