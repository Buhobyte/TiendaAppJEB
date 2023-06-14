/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.appback.controller;

import com.example.appback.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.example.appback.entity.Categorias;
import com.example.appback.entity.Productos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author ka
 */
public class ProductosJpaController implements Serializable {

    public ProductosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productos productos) {
        if (productos.getCategoriasCollection() == null) {
            productos.setCategoriasCollection(new ArrayList<Categorias>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Categorias> attachedCategoriasCollection = new ArrayList<Categorias>();
            for (Categorias categoriasCollectionCategoriasToAttach : productos.getCategoriasCollection()) {
                categoriasCollectionCategoriasToAttach = em.getReference(categoriasCollectionCategoriasToAttach.getClass(), categoriasCollectionCategoriasToAttach.getId());
                attachedCategoriasCollection.add(categoriasCollectionCategoriasToAttach);
            }
            productos.setCategoriasCollection(attachedCategoriasCollection);
            em.persist(productos);
            for (Categorias categoriasCollectionCategorias : productos.getCategoriasCollection()) {
                categoriasCollectionCategorias.getProductosCollection().add(productos);
                categoriasCollectionCategorias = em.merge(categoriasCollectionCategorias);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Productos productos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos persistentProductos = em.find(Productos.class, productos.getId());
            Collection<Categorias> categoriasCollectionOld = persistentProductos.getCategoriasCollection();
            Collection<Categorias> categoriasCollectionNew = productos.getCategoriasCollection();
            Collection<Categorias> attachedCategoriasCollectionNew = new ArrayList<Categorias>();
            for (Categorias categoriasCollectionNewCategoriasToAttach : categoriasCollectionNew) {
                categoriasCollectionNewCategoriasToAttach = em.getReference(categoriasCollectionNewCategoriasToAttach.getClass(), categoriasCollectionNewCategoriasToAttach.getId());
                attachedCategoriasCollectionNew.add(categoriasCollectionNewCategoriasToAttach);
            }
            categoriasCollectionNew = attachedCategoriasCollectionNew;
            productos.setCategoriasCollection(categoriasCollectionNew);
            productos = em.merge(productos);
            for (Categorias categoriasCollectionOldCategorias : categoriasCollectionOld) {
                if (!categoriasCollectionNew.contains(categoriasCollectionOldCategorias)) {
                    categoriasCollectionOldCategorias.getProductosCollection().remove(productos);
                    categoriasCollectionOldCategorias = em.merge(categoriasCollectionOldCategorias);
                }
            }
            for (Categorias categoriasCollectionNewCategorias : categoriasCollectionNew) {
                if (!categoriasCollectionOld.contains(categoriasCollectionNewCategorias)) {
                    categoriasCollectionNewCategorias.getProductosCollection().add(productos);
                    categoriasCollectionNewCategorias = em.merge(categoriasCollectionNewCategorias);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productos.getId();
                if (findProductos(id) == null) {
                    throw new NonexistentEntityException("The productos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos productos;
            try {
                productos = em.getReference(Productos.class, id);
                productos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productos with id " + id + " no longer exists.", enfe);
            }
            Collection<Categorias> categoriasCollection = productos.getCategoriasCollection();
            for (Categorias categoriasCollectionCategorias : categoriasCollection) {
                categoriasCollectionCategorias.getProductosCollection().remove(productos);
                categoriasCollectionCategorias = em.merge(categoriasCollectionCategorias);
            }
            em.remove(productos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Productos> findProductosEntities() {
        return findProductosEntities(true, -1, -1);
    }

    public List<Productos> findProductosEntities(int maxResults, int firstResult) {
        return findProductosEntities(false, maxResults, firstResult);
    }

    private List<Productos> findProductosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Productos findProductos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productos> rt = cq.from(Productos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
