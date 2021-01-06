package com.pichs.xhttp.exception;


public class HttpError extends Exception {

    private int errorCode = -1000;

    public HttpError() {
        super();
    }

    public HttpError(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public HttpError(String message) {
        super(message);
    }

    public HttpError(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpError(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }


    @Override
    public String toString() {
        return (errorCode != -1000 ? "errorCode:'" + errorCode + "'" : "") + super.toString();
    }
}
