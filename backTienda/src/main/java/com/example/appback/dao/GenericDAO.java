/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.dao;

/**
 *
 * @author ka
 */
import com.example.appback.controller.exceptions.AppException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class GenericDAO<T> {

    @PersistenceContext(unitName = "com.example_backTienda_jar_1.0PU")
    protected EntityManager em;

    private final Class<T> instance;

    public GenericDAO(Class<T> instance) {
        this.instance = instance;
    }

    public void insert(T instance) throws AppException {
        try {
            em.persist(instance);
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void insertList(List<T> instanceList) throws AppException {
        try {
            instanceList.forEach(item -> {
                em.persist(item);
            });
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void remove(T instance) throws AppException {
        try {
            em.remove(em.merge(instance));
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void update(T instance) throws AppException {
        try {
            em.merge(instance);
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void updateList(List<T> instanceList) throws AppException {
        try {
            instanceList.forEach(item -> {
                em.merge(item);
            });
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void deleteList(List<T> instanceList) throws AppException {
        try {
            instanceList.forEach(item -> {
                em.remove(em.merge(item));
            });
            em.flush();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public void flush() {
        em.flush();
    }

    public T searchOne(SearchObject searchObject) {
        List<T> auxSearch = this.search(searchObject);
        if (!auxSearch.isEmpty()) {
            return auxSearch.get(0);
        }
        return null;
    }

    public List searchAndJoin(SearchObject searchObjectT, String atributeT, Class<?> clsReturn) {
        return searchAndJoin(searchObjectT, atributeT, clsReturn, JoinType.INNER);
    }

    public List searchAndJoin(SearchObject searchObjectT, String atributeT, Class<?> clsReturn, JoinType joinType) {
        //Se crea una consulta base
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq;
        // Retornar la clase T por defecto
        if (instance.equals(clsReturn)) {
            cq = cb.createQuery(instance);
        } else {
            cq = cb.createQuery(clsReturn.getClass());
        }
        Root<T> root = cq.from(instance); // objeto raiz de la clase local
        Join< T, ?> join = root.join(atributeT, joinType);

        if (instance.equals(clsReturn)) {
            cq.select(root);
        } else {
            cq.select(join);
        }

        cq.where(filterWhere(cb, root, searchObjectT.getProperties()));

        return em.createQuery(cq).getResultList();
    }

    public List searchAndJoinGeneric(SearchObject soA, SearchObject soB, Class<?> clsConstruct) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> predicates;

        CriteriaQuery cq = cb.createQuery();

        Root rootA = cq.from(soA.getCls());
        Root rootB = cq.from(soB.getCls());

        // Multiselected columns
        cq = orderSelection(cb, cq, rootA, rootB, soA, soB, clsConstruct);

        // Construir predicates para where
        predicates = applyFilters(cb, rootA, soA.getProperties());
        predicates.addAll(applyFilters(cb, rootB, soB.getProperties()));
        predicates.add(cb.equal(getParamsExpression(rootA, soA.getOnJoin()), getParamsExpression(rootB, soB.getOnJoin())));

        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        return em.createQuery(cq).getResultList();
    }

    public List<T> search(SearchObject searchObject) {
        //Se crea una consulta base
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(instance);

        //Entity
        Root<T> providerRoot = cq.from(instance);
        //select
        cq.select(providerRoot);

        //Fetchs, traer columnas relacionadas con eager
        searchObject.getFetchs().forEach((column) -> {
            providerRoot.fetch(column);
        });

        // Filtros
        cq.where(filterWhere(cb, providerRoot, searchObject.getProperties()));

        //Order
        cq.orderBy(applyOrderByProperty(cb, providerRoot, searchObject.getOrderByProperties()));

        return findByRange(cq, searchObject.getFrom(), searchObject.getTo());

    }

    private CriteriaQuery orderSelection(CriteriaBuilder cb, CriteriaQuery cq, Root rootA, Root rootB, SearchObject soA, SearchObject soB, Class<?> clsConstruct) {
        List<Path> pathsSelects = new ArrayList<>();
        if (clsConstruct == null || clsConstruct.equals(Object[].class)) {
            soA.getSelects().forEach(column -> {
                pathsSelects.add(getParamsExpression(rootA, column));
            });
            soB.getSelects().forEach(column -> {
                pathsSelects.add(getParamsExpression(rootB, column));
            });
            cq.select(cb.array(pathsSelects.toArray(new Path[pathsSelects.size()])));
        } else if (clsConstruct.equals(soA.getCls())) {
            cq.select(rootA);
        } else if (clsConstruct.equals(soB.getCls())) {
            cq.select(rootB);
        } else {
            Field[] fields = clsConstruct.getDeclaredFields();
            boolean next;
            for (Field field : fields) {
                field.setAccessible(true);
                next = false;

                for (String column : soA.getSelects()) {
                    if (field.getName().equals(column)) {
                        pathsSelects.add(getParamsExpression(rootA, column));
                        next = true;
                        break;
                    }
                }
                if (next) {
                    continue;
                }
                for (String column : soB.getSelects()) {
                    if (field.getName().equals(column)) {
                        pathsSelects.add(getParamsExpression(rootB, column));
                        break;
                    }
                }
            }

            cq.select(cb.construct(clsConstruct, pathsSelects.toArray(new Path[pathsSelects.size()])));
        } //Fin multiselect
        return cq;
    }

    public Long count(SearchObject searchObject) {
        //Se crea una consulta base
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(instance);
        //FROM - Entidad
        Root<T> root = cq.from(instance);
        cq.select(root);

        cq.where(filterWhere(cb, root, searchObject.getProperties()));

        return queryCount(root, cq);
    }

    public Predicate[] filterWhere(CriteriaBuilder cb, Root root, List<Property> properties) {
        List<Predicate> predicates = applyFilters(cb, root, properties);
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    public List<Predicate> applyFilters(CriteriaBuilder cb, Root root, List<Property> properties) {

        List<Predicate> predicates = new ArrayList<>();
        String property;
        Object value;
        Path pathRootProperties;

        for (Property prop : properties) {

            property = prop.getNameParameter();
            value = prop.getValue();

            if (value == null || isInstanceStringEmpty(value) || isInstanceStringEmpty(property)) {
                continue;
            }
            pathRootProperties = getParamsExpression(root, property);

            switch (prop.getType()) {
                case EQUAL:
                    predicates.add(cb.equal(pathRootProperties, value));
                    break;
                case LIKE:
                    predicates.add(cb.like(pathRootProperties, "%" + value + "%"));
                    break;
                case LIKE_LEFT:
                    predicates.add(cb.like(pathRootProperties, "%" + value));
                    break;
                case LIKE_RIGHT:
                    predicates.add(cb.like(pathRootProperties, value + "%"));
                    break;
                case GREATER_THAN:
                    predicates.add(addFilterGreaterThan(cb, pathRootProperties, value));
                    break;
                case LESS_THAN:
                    predicates.add(addFilterLessThan(cb, pathRootProperties, value));
                    break;
                case GREATER_OR_EQUAL_THAN:
                    predicates.add(addFilterGreaterOrEqualThan(cb, pathRootProperties, value));
                    break;
                case LESS_OR_EQUAL_THAN:
                    predicates.add(addFilterLessOrEqualThan(cb, pathRootProperties, value));
                    break;
                case IS_NULL:
                    predicates.add(cb.isNull(pathRootProperties));
                    break;
                case NOT_EQUAL:
                    predicates.add(cb.notEqual(pathRootProperties, value));
                    break;
                case IS_NOT_NULL:
                    predicates.add(cb.isNotNull(pathRootProperties));
                    break;
            }
        }
        // Se elimina cualquier filtro null
        predicates.removeAll(Collections.singleton(null));
        return predicates;
    }

    private boolean isInstanceStringEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            if (((String) value).trim().length() == 0) {
                return true;
            }
        }
        return false;
    }

    private Predicate addFilterGreaterThan(CriteriaBuilder cb, Path filterPropertyPath, Object value) {

        if (value instanceof Long) {
            return cb.greaterThan(filterPropertyPath, (Long) value);
        }
        if (value instanceof Integer) {
            return cb.greaterThan(filterPropertyPath, (Integer) value);
        }
        if (value instanceof Date) {
            return cb.greaterThan(filterPropertyPath, (Date) value);
        }
        if (value instanceof BigDecimal) {
            return cb.greaterThan(filterPropertyPath.as(BigDecimal.class), (BigDecimal) value);
        }
        if (value instanceof Double) {
            return cb.greaterThan(filterPropertyPath.as(Double.class), (Double) value);
        }
        return null;
    }

    private Predicate addFilterGreaterOrEqualThan(CriteriaBuilder cb, Path filterPropertyPath, Object value) {

        if (value instanceof Long) {
            return cb.greaterThanOrEqualTo(filterPropertyPath, (Long) value);
        }
        if (value instanceof Integer) {
            return cb.greaterThanOrEqualTo(filterPropertyPath, (Integer) value);
        }
        if (value instanceof Date) {
            return cb.greaterThanOrEqualTo(filterPropertyPath, (Date) value);
        }
        if (value instanceof BigDecimal) {
            return cb.greaterThanOrEqualTo(filterPropertyPath.as(BigDecimal.class), (BigDecimal) value);
        }
        if (value instanceof Double) {
            return cb.greaterThanOrEqualTo(filterPropertyPath.as(Double.class), (Double) value);
        }

        return null;
    }

    private Predicate addFilterLessThan(CriteriaBuilder cb, Path filterPropertyPath, Object value) {

        if (value instanceof Long) {
            return cb.lessThan(filterPropertyPath, (Long) value);
        }
        if (value instanceof Integer) {
            return cb.lessThan(filterPropertyPath, (Integer) value);
        }
        if (value instanceof Date) {
            return cb.lessThan(filterPropertyPath, (Date) value);
        }
        if (value instanceof BigDecimal) {
            return cb.lessThan(filterPropertyPath.as(BigDecimal.class), (BigDecimal) value);
        }
        if (value instanceof Double) {
            return cb.lessThan(filterPropertyPath.as(Double.class), (Double) value);
        }

        return null;
    }

    private Predicate addFilterLessOrEqualThan(CriteriaBuilder cb, Path filterPropertyPath, Object value) {

        if (value instanceof Long) {
            return cb.lessThanOrEqualTo(filterPropertyPath, (Long) value);
        }
        if (value instanceof Integer) {
            return cb.lessThanOrEqualTo(filterPropertyPath, (Integer) value);
        }
        if (value instanceof Date) {
            return cb.lessThanOrEqualTo(filterPropertyPath, (Date) value);
        }
        if (value instanceof BigDecimal) {
            return cb.lessThanOrEqualTo(filterPropertyPath.as(BigDecimal.class), (BigDecimal) value);
        }
        if (value instanceof Double) {
            return cb.lessThanOrEqualTo(filterPropertyPath.as(Double.class), (Double) value);
        }

        return null;
    }

    public List<Order> applyOrderByProperty(CriteriaBuilder cb, Root root, List<OrderByProperty> orderByList) {

        List<Order> orderList = new ArrayList();

        orderByList.stream().forEachOrdered(i -> {
            if (i.getNameParameter() == null || i.getNameParameter().isEmpty()) {
                return;
            }

            Path path = getParamsExpression(root, i.getNameParameter());
            //ORDER BY -Consulta para ordenamiento
            Order queryOrder = (i.getAsc()) ? cb.asc(path) : cb.desc(path);
            orderList.add(queryOrder);
        });

        return orderList;
    }

    public Path getParamsExpression(Root root, String property) {
        if (property != null) {
            String[] innerProps = property.split(Pattern.quote("."));
            Path<?> path = null;
            if (innerProps.length == 0) {
                path = root.get(property);
            } else {
                for (String innerProp : innerProps) {
                    path = (path == null) ? root.get(innerProp) : path.get(innerProp);
                }
            }
            return path;
        }
        return null;
    }

    public T find(Object id) {
        T ret = em.find(instance, id);
        em.flush();
        return ret;
    }

    public List<T> findAll() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(instance));
        return em.createQuery(cq).getResultList();
    }

    public List<T> findByRange(Integer from, Integer to) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(instance));
        Query q = em.createQuery(cq);
        if (from != null && to != null) {
            q.setFirstResult(from);
            q.setMaxResults(to);
        }
        return q.getResultList();
    }

    public List findByRange(CriteriaQuery cq, Integer from, Integer to) {
        if (from != null && to != null) {
            return em.createQuery(cq).setFirstResult(from).setMaxResults(to).getResultList();
        } else {
            return em.createQuery(cq).getResultList();
        }
    }

    public List<T> findAll(CriteriaQuery<T> cq) {
        return em.createQuery(cq).getResultList();
    }

    public List<T> findRangeSort(int from, int to, String orderBy, Boolean isAsc) {
        SearchObject so = new SearchObject(from, to, orderBy, isAsc);
        return search(so);
    }

    public long queryCount(Root<T> root, CriteriaQuery<T> cq) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(root));
        if (cq.getRestriction() != null) {
            countQuery.where(cq.getRestriction());
        }
        if (cq.getGroupRestriction() != null) {
            countQuery.where(cq.getGroupRestriction());
        }
        return em.createQuery(countQuery).getSingleResult();
    }
}
