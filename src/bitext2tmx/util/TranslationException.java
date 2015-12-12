package bitext2tmx.util;

/**
 * TranslationException may be thrown
 * while parsing/writing out the file.
 * <p>
 * Note that a filter may also throw IOException in case of any I/O errors.
 * 
 * @author Maxym Mykhalchuk
 */
@SuppressWarnings("serial")
public class TranslationException extends Exception {
    /**
     * Constructs an instance of <code>TranslationException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public TranslationException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>TranslationException</code> with the
     * specified detail message and cause.
     * 
     * @param msg
     *            the detail message.
     * @param cause
     *            cause the cause
     */
    public TranslationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

