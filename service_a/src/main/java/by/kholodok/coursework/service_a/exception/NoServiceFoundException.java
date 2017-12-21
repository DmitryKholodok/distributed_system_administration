package by.kholodok.coursework.service_a.exception;

/**
 * Created by dmitrykholodok on 12/13/17
 */

public class NoServiceFoundException extends Exception {

    public NoServiceFoundException() {
        super();
    }

    public NoServiceFoundException(String message) {
        super(message);
    }

    public NoServiceFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoServiceFoundException(Throwable cause) {
        super(cause);
    }
}
