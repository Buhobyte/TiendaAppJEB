/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.controller.exceptions;

/**
 *
 * @author ka
 */

public class AppException extends Exception {

    private static final long serialVersionUID = 1L;

    private final ErrorMessage errorMessage;

    public AppException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AppException(Throwable throwError) {
        super(throwError);
        this.errorMessage = new ErrorMessage(throwError);
        System.out.println(throwError);
    }

    public AppException(Throwable cause, ErrorMessage errorMessage) {
        super(cause);
        this.errorMessage = errorMessage;
    }

    public AppException(Throwable throwError, String datailMessage, String summaryMessage, int codeMessage) {
        super(summaryMessage, throwError);
        this.errorMessage = new ErrorMessage(throwError, datailMessage, summaryMessage, codeMessage);
    }

    public ErrorMessage getErrorMessage() {
        return this.errorMessage;
    }

}
