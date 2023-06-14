/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.controller.exceptions;

/**
 *
 * @author ka
 */

import javax.faces.application.FacesMessage;
import java.sql.SQLException;
import javax.persistence.PersistenceException;
import org.eclipse.persistence.exceptions.DatabaseException;

public class ErrorMessage {

    private String detail;
    private String summary;
    private int code;

    private FacesMessage facesMsg;

    public ErrorMessage() {
    }

    public ErrorMessage(Throwable errorThrow) {
        generateErrorMessage(errorThrow);
        configureFacesMsg();
    }

    public ErrorMessage(Throwable errorThrow, String detail, String summary, int code) {

        generateErrorMessage(errorThrow);

        if (!detail.isEmpty()) {
            this.detail = detail;
        }
        if (!summary.isEmpty()) {
            this.summary = summary;
        }
        if (code != 0) {
            this.code = code;
        }
        configureFacesMsg();
    }

    public ErrorMessage(String detail, String summary, int code) {
        this.detail = detail;
        this.summary = summary;
        if (code != 0) {
            this.code = code;
            this.summary = ErrorEnum.getMessageBy(code);
        }
        configureFacesMsg();
    }

    public FacesMessage getFacesMsg() {
        return facesMsg;
    }

    public void setFacesMsg(FacesMessage facesMsg) {
        this.facesMsg = facesMsg;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private void configureFacesMsg() {
        this.facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                this.summary,
                "[" + this.code + "]: " + this.detail.substring(0, this.detail.length() > 200 ? 200 : this.detail.length()));
    }

    private void generateErrorMessage(Throwable errorThrowable) {

        // Extraer el error de Persistencia Eclipselink
        if (errorThrowable instanceof PersistenceException) {
            PersistenceException pse = (PersistenceException) errorThrowable;

            // Extraer el error de base de datos
            if (pse.getCause() instanceof DatabaseException) {
                DatabaseException dbe = (DatabaseException) pse.getCause();

                if (dbe.getCause() instanceof SQLException) {
                    SQLException sqlE = (SQLException) dbe.getCause();
                    // Configurar Mensaje
                    this.code = sqlE.getErrorCode();
                    this.detail = sqlE.getMessage();
                    this.summary = ErrorEnum.getMessageBy(sqlE.getErrorCode());
                    return;
                }
                // Configurar Mensaje
                this.code = dbe.getDatabaseErrorCode();
                this.detail = dbe.getMessage();
                this.summary = ErrorEnum.getMessageBy(dbe.getDatabaseErrorCode());
                return;
            }
            this.code = ErrorEnum.DATABASE_GENERAL_ERROR.getCode();
            this.detail = pse.getMessage();
            this.summary = ErrorEnum.DATABASE_GENERAL_ERROR.getMessage();
            return;
        }

        this.code = ErrorEnum.INTERNAL_SERVER_ERROR.getCode();
        this.detail = errorThrowable.getMessage();
        this.summary = ErrorEnum.INTERNAL_SERVER_ERROR.getMessage();;
    }

}