package com.recnar.aws.s3.domain;

public class GeneralErrorException extends RuntimeException{

    private static final long serialVersionUID = -5531978952908290297L;
    private final String message;

    public GeneralErrorException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
