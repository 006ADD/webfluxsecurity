package net.proselyte.webfluxsecurity.exception;

public class ApiException extends RuntimeException{

    protected String errorCode;

    ApiException(String message, String errorCode){
        super(message);
        this.errorCode=errorCode;
    }
}
