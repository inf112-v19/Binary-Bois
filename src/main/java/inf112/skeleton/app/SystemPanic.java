package inf112.skeleton.app;

/**
 * This is an exception that is not meant to ever be cought.
 *
 * Throw a panic with SystemPanic.panic("message")
 */
public class SystemPanic extends Exception {
    public SystemPanic(String msg) {
        super(msg);
    }

    /**
     * A panic should be thrown for errors that the system cannot recover from.
     *
     * @param msg Message explaining the unfortunate situation.
     */
    public static void panic(String msg) throws RuntimeException {
        try {
            // Throwing and catching SystemPanic is a cheap way to to get a stacktrace.
            throw new SystemPanic(msg);
        } catch (SystemPanic e) {
            System.out.println("PANIC: " + msg);
            System.out.println("Displaying stack trace: ");
            for (StackTraceElement elem : e.getStackTrace())
                System.out.println(elem);
            throw new RuntimeException();
        }
    }
}
