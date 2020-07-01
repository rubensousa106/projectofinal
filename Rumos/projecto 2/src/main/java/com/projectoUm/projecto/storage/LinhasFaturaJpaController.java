/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.storage;

import com.projectoUm.projecto.modelo.LinhasFatura;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.projectoUm.projecto.modelo.Produto;
import com.projectoUm.projecto.storage.exceptions.IllegalOrphanException;
import com.projectoUm.projecto.storage.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author ruben
 */
public class LinhasFaturaJpaController implements Serializable {

    public LinhasFaturaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LinhasFatura linhasFatura) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        Produto produtoOrphanCheck = linhasFatura.getProduto();
        if (produtoOrphanCheck != null) {
            LinhasFatura oldLinhasFaturaOfProduto = produtoOrphanCheck.getLinhasFatura();
            if (oldLinhasFaturaOfProduto != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Produto " + produtoOrphanCheck + " already has an item of type LinhasFatura whose produto column cannot be null. Please make another selection for the produto field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto = linhasFatura.getProduto();
            if (produto != null) {
                produto = em.getReference(produto.getClass(), produto.getId());
                linhasFatura.setProduto(produto);
            }
            em.persist(linhasFatura);
            if (produto != null) {
                produto.setLinhasFatura(linhasFatura);
                produto = em.merge(produto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LinhasFatura linhasFatura) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LinhasFatura persistentLinhasFatura = em.find(LinhasFatura.class, linhasFatura.getId());
            Produto produtoOld = persistentLinhasFatura.getProduto();
            Produto produtoNew = linhasFatura.getProduto();
            List<String> illegalOrphanMessages = null;
            if (produtoNew != null && !produtoNew.equals(produtoOld)) {
                LinhasFatura oldLinhasFaturaOfProduto = produtoNew.getLinhasFatura();
                if (oldLinhasFaturaOfProduto != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Produto " + produtoNew + " already has an item of type LinhasFatura whose produto column cannot be null. Please make another selection for the produto field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (produtoNew != null) {
                produtoNew = em.getReference(produtoNew.getClass(), produtoNew.getId());
                linhasFatura.setProduto(produtoNew);
            }
            linhasFatura = em.merge(linhasFatura);
            if (produtoOld != null && !produtoOld.equals(produtoNew)) {
                produtoOld.setLinhasFatura(null);
                produtoOld = em.merge(produtoOld);
            }
            if (produtoNew != null && !produtoNew.equals(produtoOld)) {
                produtoNew.setLinhasFatura(linhasFatura);
                produtoNew = em.merge(produtoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = linhasFatura.getId();
                if (findLinhasFatura(id) == null) {
                    throw new NonexistentEntityException("The linhasFatura with id " + id + " no longer exists.");
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
            LinhasFatura linhasFatura;
            try {
                linhasFatura = em.getReference(LinhasFatura.class, id);
                linhasFatura.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The linhasFatura with id " + id + " no longer exists.", enfe);
            }
            Produto produto = linhasFatura.getProduto();
            if (produto != null) {
                produto.setLinhasFatura(null);
                produto = em.merge(produto);
            }
            em.remove(linhasFatura);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LinhasFatura> findLinhasFaturaEntities() {
        return findLinhasFaturaEntities(true, -1, -1);
    }

    public List<LinhasFatura> findLinhasFaturaEntities(int maxResults, int firstResult) {
        return findLinhasFaturaEntities(false, maxResults, firstResult);
    }

    private List<LinhasFatura> findLinhasFaturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LinhasFatura.class));
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

    public LinhasFatura findLinhasFatura(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LinhasFatura.class, id);
        } finally {
            em.close();
        }
    }

    public int getLinhasFaturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LinhasFatura> rt = cq.from(LinhasFatura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
