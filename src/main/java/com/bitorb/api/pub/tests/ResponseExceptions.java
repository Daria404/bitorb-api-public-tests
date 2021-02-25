package com.bitorb.api.pub.tests;

public class ResponseExceptions extends RuntimeException {
    public int responseCode;
    String message;

    public ResponseExceptions(String message, int code) {
        this.message = message;
        this.responseCode = code;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public static class InvalidParameterException extends ResponseExceptions {

        public InvalidParameterException(String message, int code) {
            super(message, code);
        }

    }

    public static class UnauthorizedException extends ResponseExceptions {

        public UnauthorizedException(String message, int code) {
            super(message, code);
        }
    }

    public static class WebserverInternalErrorException extends ResponseExceptions {

        public WebserverInternalErrorException(String message, int code) {

            super(message, code);
        }
    }

    public static class EmptyBodyException extends ResponseExceptions {
        public EmptyBodyException(String message, int code) {
            super(message, code);
        }
    }
}
