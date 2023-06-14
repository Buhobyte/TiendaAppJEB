/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.dao;

import com.example.appback.controller.constant.FilterTypes;

/**
 *
 * @author ka
 */

public class Property {

    private String nameParameter;
    private FilterTypes type;
    private Object value;

    public Property(String nameParameter, FilterTypes type, Object value) {
        this.nameParameter = nameParameter;
        this.type = type;
        this.value = value;
    }

    public String getNameParameter() {
        return nameParameter;
    }

    public void setNameParameter(String nameParameter) {
        this.nameParameter = nameParameter;
    }

    public FilterTypes getType() {
        return type;
    }

    public void setType(FilterTypes type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}