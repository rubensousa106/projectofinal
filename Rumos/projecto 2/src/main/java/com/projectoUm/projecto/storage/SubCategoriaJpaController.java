/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.storage;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.projectoUm.projecto.modelo.Categoria;
import com.projectoUm.projecto.modelo.SubCategoria;
import com.projectoUm.projecto.storage.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.stereotype.Service;

/**
 *
 * @author ruben
 */
@Service
public class SubCategoriaJpaController implements Serializable {

    public SubCategoriaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("com.projectoUm_projecto_jar_0.0.1-SNAPSHOTPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubCategoria subCategoria) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria idCategoria = subCategoria.getIdCategoria();
            if (idCategoria != null) {
                idCategoria = em.getReference(idCategoria.getClass(), idCategoria.getId());
                subCategoria.setIdCategoria(idCategoria);
            }
            em.persist(subCategoria);
            if (idCategoria != null) {
                idCategoria.getSubCategoriaList().add(subCategoria);
                idCategoria = em.merge(idCategoria);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubCategoria subCategoria) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubCategoria persistentSubCategoria = em.find(SubCategoria.class, subCategoria.getId());
            Categoria idCategoriaOld = persistentSubCategoria.getIdCategoria();
            Categoria idCategoriaNew = subCategoria.getIdCategoria();
            if (idCategoriaNew != null) {
                idCategoriaNew = em.getReference(idCategoriaNew.getClass(), idCategoriaNew.getId());
                subCategoria.setIdCategoria(idCategoriaNew);
            }
            subCategoria = em.merge(subCategoria);
            if (idCategoriaOld != null && !idCategoriaOld.equals(idCategoriaNew)) {
                idCategoriaOld.getSubCategoriaList().remove(subCategoria);
                idCategoriaOld = em.merge(idCategoriaOld);
            }
            if (idCategoriaNew != null && !idCategoriaNew.equals(idCategoriaOld)) {
                idCategoriaNew.getSubCategoriaList().add(subCategoria);
                idCategoriaNew = em.merge(idCategoriaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subCategoria.getId();
                if (findSubCategoria(id) == null) {
                    throw new NonexistentEntityException("The subCategoria with id " + id + " no longer exists.");
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
            SubCategoria subCategoria;
            try {
                subCategoria = em.getReference(SubCategoria.class, id);
                subCategoria.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subCategoria with id " + id + " no longer exists.", enfe);
            }
            Categoria idCategoria = subCategoria.getIdCategoria();
            if (idCategoria != null) {
                idCategoria.getSubCategoriaList().remove(subCategoria);
                idCategoria = em.merge(idCategoria);
            }
            em.remove(subCategoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubCategoria> findSubCategoriaEntities() {
        return findSubCategoriaEntities(true, -1, -1);
    }

    public List<SubCategoria> findSubCategoriaEntities(int maxResults, int firstResult) {
        return findSubCategoriaEntities(false, maxResults, firstResult);
    }

    private List<SubCategoria> findSubCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubCategoria.class));
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

    public SubCategoria findSubCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubCategoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubCategoria> rt = cq.from(SubCategoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
