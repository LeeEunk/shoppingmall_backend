package org.eunkk.apiserver.utill;

public class CustomJWTException extends RuntimeException{

    public CustomJWTException(String msg) {
        super(msg);
    }
}
