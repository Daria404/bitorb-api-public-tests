package com.bitorb.api.pub.tests;

public class ResponseExceptions extends RuntimeException {

    public ResponseExceptions(String message) {
        super(message);
    }

    public static class InvalidParameterException extends ResponseExceptions {

        public InvalidParameterException(String message) {
            super(message);
            System.out.print(message);
        }

    }

    public static class UnauthorizedException extends ResponseExceptions {

        public UnauthorizedException(String message) {
            super(message);
            System.out.print(message);
        }
    }

    public static class WebserverInternalErrorException extends ResponseExceptions {

        public WebserverInternalErrorException(String message) {

            super(message);
            System.out.print(message);
        }
    }

    public static class EmptyBodyException extends ResponseExceptions {
        public EmptyBodyException(String message) {

            super(message);
            System.out.print(message);
        }
    }
}
