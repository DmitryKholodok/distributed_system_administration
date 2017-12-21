package by.kholodok.coursework.service_a.exception;

/**
 * Created by dmitrykholodok on 12/12/17
 */

public class ServiceConnectionException extends Exception {

    public ServiceConnectionException() {
        super();
    }

    public ServiceConnectionException(String message) {
        super(message);
    }

    public ServiceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceConnectionException(Throwable cause) {
        super(cause);
    }
}
