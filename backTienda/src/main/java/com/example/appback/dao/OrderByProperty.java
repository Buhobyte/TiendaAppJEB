/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.dao;

/**
 *
 * @author ka
 */
public class OrderByProperty {

    private String nameParameter;
    private Boolean asc;

    public OrderByProperty(String nameParameter, Boolean asc) {
        this.nameParameter = nameParameter;
        this.asc = asc;
    }

    /**
     * @return the nameParameter
     */
    public String getNameParameter() {
        return nameParameter;
    }

    /**
     * @param nameParameter the nameParameter to set
     */
    public void setNameParameter(String nameParameter) {
        this.nameParameter = nameParameter;
    }

    /**
     * @return the asc
     */
    public Boolean getAsc() {
        return asc;
    }

    /**
     * @param asc the asc to set
     */
    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

}
