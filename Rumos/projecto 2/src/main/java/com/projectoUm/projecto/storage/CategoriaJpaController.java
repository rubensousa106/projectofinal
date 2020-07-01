/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.storage;

import com.projectoUm.projecto.modelo.Categoria;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.projectoUm.projecto.modelo.SubCategoria;
import com.projectoUm.projecto.storage.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author ruben
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) {
        if (categoria.getSubCategoriaList() == null) {
            categoria.setSubCategoriaList(new ArrayList<SubCategoria>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<SubCategoria> attachedSubCategoriaList = new ArrayList<SubCategoria>();
            for (SubCategoria subCategoriaListSubCategoriaToAttach : categoria.getSubCategoriaList()) {
                subCategoriaListSubCategoriaToAttach = em.getReference(subCategoriaListSubCategoriaToAttach.getClass(), subCategoriaListSubCategoriaToAttach.getId());
                attachedSubCategoriaList.add(subCategoriaListSubCategoriaToAttach);
            }
            categoria.setSubCategoriaList(attachedSubCategoriaList);
            em.persist(categoria);
            for (SubCategoria subCategoriaListSubCategoria : categoria.getSubCategoriaList()) {
                Categoria oldIdCategoriaOfSubCategoriaListSubCategoria = subCategoriaListSubCategoria.getIdCategoria();
                subCategoriaListSubCategoria.setIdCategoria(categoria);
                subCategoriaListSubCategoria = em.merge(subCategoriaListSubCategoria);
                if (oldIdCategoriaOfSubCategoriaListSubCategoria != null) {
                    oldIdCategoriaOfSubCategoriaListSubCategoria.getSubCategoriaList().remove(subCategoriaListSubCategoria);
                    oldIdCategoriaOfSubCategoriaListSubCategoria = em.merge(oldIdCategoriaOfSubCategoriaListSubCategoria);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getId());
            List<SubCategoria> subCategoriaListOld = persistentCategoria.getSubCategoriaList();
            List<SubCategoria> subCategoriaListNew = categoria.getSubCategoriaList();
            List<SubCategoria> attachedSubCategoriaListNew = new ArrayList<SubCategoria>();
            for (SubCategoria subCategoriaListNewSubCategoriaToAttach : subCategoriaListNew) {
                subCategoriaListNewSubCategoriaToAttach = em.getReference(subCategoriaListNewSubCategoriaToAttach.getClass(), subCategoriaListNewSubCategoriaToAttach.getId());
                attachedSubCategoriaListNew.add(subCategoriaListNewSubCategoriaToAttach);
            }
            subCategoriaListNew = attachedSubCategoriaListNew;
            categoria.setSubCategoriaList(subCategoriaListNew);
            categoria = em.merge(categoria);
            for (SubCategoria subCategoriaListOldSubCategoria : subCategoriaListOld) {
                if (!subCategoriaListNew.contains(subCategoriaListOldSubCategoria)) {
                    subCategoriaListOldSubCategoria.setIdCategoria(null);
                    subCategoriaListOldSubCategoria = em.merge(subCategoriaListOldSubCategoria);
                }
            }
            for (SubCategoria subCategoriaListNewSubCategoria : subCategoriaListNew) {
                if (!subCategoriaListOld.contains(subCategoriaListNewSubCategoria)) {
                    Categoria oldIdCategoriaOfSubCategoriaListNewSubCategoria = subCategoriaListNewSubCategoria.getIdCategoria();
                    subCategoriaListNewSubCategoria.setIdCategoria(categoria);
                    subCategoriaListNewSubCategoria = em.merge(subCategoriaListNewSubCategoria);
                    if (oldIdCategoriaOfSubCategoriaListNewSubCategoria != null && !oldIdCategoriaOfSubCategoriaListNewSubCategoria.equals(categoria)) {
                        oldIdCategoriaOfSubCategoriaListNewSubCategoria.getSubCategoriaList().remove(subCategoriaListNewSubCategoria);
                        oldIdCategoriaOfSubCategoriaListNewSubCategoria = em.merge(oldIdCategoriaOfSubCategoriaListNewSubCategoria);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getId();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
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
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            List<SubCategoria> subCategoriaList = categoria.getSubCategoriaList();
            for (SubCategoria subCategoriaListSubCategoria : subCategoriaList) {
                subCategoriaListSubCategoria.setIdCategoria(null);
                subCategoriaListSubCategoria = em.merge(subCategoriaListSubCategoria);
            }
            em.remove(categoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
