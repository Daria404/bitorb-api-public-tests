package com.bitorb.api.pub.tests;

public class ResponseExceptions extends RuntimeException {
    public int responseCode;

    public ResponseExceptions(String message, int code) {
        super(message);
        responseCode = code;
    }

    public static class InvalidParameterException extends ResponseExceptions {

        public InvalidParameterException(String message, int code) {
            super(message, code);
            System.out.print(message);
        }

    }

    public static class UnauthorizedException extends ResponseExceptions {

        public UnauthorizedException(String message, int code) {
            super(message, code);
            System.out.print(message);
        }
    }

    public static class WebserverInternalErrorException extends ResponseExceptions {

        public WebserverInternalErrorException(String message, int code) {

            super(message, code);
            System.out.print(message);
        }
    }

    public static class EmptyBodyException extends ResponseExceptions {
        public EmptyBodyException(String message, int code) {

            super(message, code);
            System.out.print(message);
        }
    }
}
