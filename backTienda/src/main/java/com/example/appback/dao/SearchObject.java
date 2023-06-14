/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.dao;

/**
 *
 * @author ka
 */
import com.example.appback.controller.constant.FilterTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchObject {

    private Class<?> cls;
    private String onJoin;
    private Integer from;
    private Integer to;
    private List<Property> properties;
    private List<OrderByProperty> orderByProperties;
    private List<String> fetchs;
    private List<String> selects;

    public SearchObject() {
        this.fetchs = new ArrayList<>();
        this.selects = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.orderByProperties = new ArrayList<>();
    }

    public SearchObject(Integer from, Integer to) {
        this.from = from;
        this.to = to;
        this.fetchs = new ArrayList<>();
        this.selects = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.orderByProperties = new ArrayList<>();
    }

    public SearchObject(Integer from, Integer to, String orderProperyName, Boolean isAscending) {
        this.from = from;
        this.to = to;
        this.fetchs = new ArrayList<>();
        this.selects = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.orderByProperties = new ArrayList<>();
        this.orderByProperties.add(new OrderByProperty(orderProperyName, isAscending));
    }

    public SearchObject(Class<?> cls, String onJoin) {
        this.cls = cls;
        this.onJoin = onJoin;
        this.fetchs = new ArrayList<>();
        this.selects = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.orderByProperties = new ArrayList<>();
    }

    // getters and setters
    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public String getOnJoin() {
        return onJoin;
    }

    public void setOnJoin(String onJoin) {
        this.onJoin = onJoin;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<OrderByProperty> getOrderByProperties() {
        return orderByProperties;
    }

    public void setOrderByProperties(List<OrderByProperty> orderByProperties) {
        this.orderByProperties = orderByProperties;
    }

    public List<String> getFetchs() {
        return fetchs;
    }

    public void setFetchs(List<String> fetchs) {
        this.fetchs = fetchs;
    }

    public List<String> getSelects() {
        return selects;
    }

    public void setSelects(List<String> select) {
        this.selects = select;
    }

    // functions
    public void addSelect(String selectColumn) {
        this.selects.add(selectColumn);
    }

    public void addFetch(String columnFetch) {
        this.fetchs.add(columnFetch);
    }

    public SearchObject addParameter(String parameter, FilterTypes filter, Object value) {
        if (value != null) {
            properties.add(new Property(parameter, filter, value));
        }
        return this;
    }

    public SearchObject addOrderBy(String parameter, Boolean asc) {
        if (parameter != null) {
            orderByProperties.add(new OrderByProperty(parameter, asc));
        }
        return this;
    }

    public SearchObject isNull(String parameter) {
        properties.add(new Property(parameter, FilterTypes.IS_NULL, "IS_NULL"));
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.cls);
        hash = 47 * hash + Objects.hashCode(this.onJoin);
        hash = 47 * hash + Objects.hashCode(this.from);
        hash = 47 * hash + Objects.hashCode(this.to);
        hash = 47 * hash + Objects.hashCode(this.properties);
        hash = 47 * hash + Objects.hashCode(this.orderByProperties);
        hash = 47 * hash + Objects.hashCode(this.fetchs);
        hash = 47 * hash + Objects.hashCode(this.selects);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchObject other = (SearchObject) obj;
        if (!Objects.equals(this.onJoin, other.onJoin)) {
            return false;
        }
        if (!Objects.equals(this.cls, other.cls)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        if (!Objects.equals(this.orderByProperties, other.orderByProperties)) {
            return false;
        }
        if (!Objects.equals(this.fetchs, other.fetchs)) {
            return false;
        }
        return Objects.equals(this.selects, other.selects);
    }

}
