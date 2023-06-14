/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.controller.exceptions;

/**
 *
 * @author ka
 */
public enum ErrorEnum {

    INTERNAL_SERVER_ERROR(501, "Error interno en el servidor"),
    // DATABASE ============
    DATABASE_GENERAL_ERROR(4002, "Error desconocido en la base de datos"),
    UNIQUE_CONSTRAINT(-268, "El registro ya existe"),
    PK_NULL_FIELD(-703, "Registro con campos nulos");

    private final int code; // Error code
    private final String Message; // Message by code

    private ErrorEnum(int code, String Message) {
        this.code = code;
        this.Message = Message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return Message;
    }

    public static String getMessageBy(int code) {

        switch (code) {
            case -703:
                return PK_NULL_FIELD.Message;
            case -268:
                return UNIQUE_CONSTRAINT.Message;
            case -239:
                return UNIQUE_CONSTRAINT.Message;
            case 4002:
                return DATABASE_GENERAL_ERROR.Message;
            case 501:
                return INTERNAL_SERVER_ERROR.Message;
            default:
                return INTERNAL_SERVER_ERROR.Message;

        }
    }

}
