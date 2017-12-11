package by.kholodok.coursework.service_admin.exception;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class NoZkConnectionException extends Exception {

    public NoZkConnectionException() {
        super();
    }

    public NoZkConnectionException(String message) {
        super(message);
    }

    public NoZkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoZkConnectionException(Throwable cause) {
        super(cause);
    }

}
