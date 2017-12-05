package by.kholodok.coursework.exception;

/**
 * Created by dmitrykholodok on 12/5/17
 */
public class NoServiceFoundException extends Exception {

    public NoServiceFoundException() {
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
