package com.example.Ecommerce.exception;

public class UserNotVerifiedException extends Exception{
    private Boolean newEmailSent;

    public UserNotVerifiedException(Boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }
    public Boolean isEmailSent(){
        return newEmailSent;
    }
}
