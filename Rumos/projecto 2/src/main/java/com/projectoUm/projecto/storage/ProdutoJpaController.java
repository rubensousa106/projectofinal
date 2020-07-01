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
import com.projectoUm.projecto.modelo.LinhasFatura;
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
public class ProdutoJpaController implements Serializable {

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LinhasFatura linhasFatura = produto.getLinhasFatura();
            if (linhasFatura != null) {
                linhasFatura = em.getReference(linhasFatura.getClass(), linhasFatura.getId());
                produto.setLinhasFatura(linhasFatura);
            }
            em.persist(produto);
            if (linhasFatura != null) {
                Produto oldProdutoOfLinhasFatura = linhasFatura.getProduto();
                if (oldProdutoOfLinhasFatura != null) {
                    oldProdutoOfLinhasFatura.setLinhasFatura(null);
                    oldProdutoOfLinhasFatura = em.merge(oldProdutoOfLinhasFatura);
                }
                linhasFatura.setProduto(produto);
                linhasFatura = em.merge(linhasFatura);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produto produto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto persistentProduto = em.find(Produto.class, produto.getId());
            LinhasFatura linhasFaturaOld = persistentProduto.getLinhasFatura();
            LinhasFatura linhasFaturaNew = produto.getLinhasFatura();
            List<String> illegalOrphanMessages = null;
            if (linhasFaturaOld != null && !linhasFaturaOld.equals(linhasFaturaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain LinhasFatura " + linhasFaturaOld + " since its produto field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (linhasFaturaNew != null) {
                linhasFaturaNew = em.getReference(linhasFaturaNew.getClass(), linhasFaturaNew.getId());
                produto.setLinhasFatura(linhasFaturaNew);
            }
            produto = em.merge(produto);
            if (linhasFaturaNew != null && !linhasFaturaNew.equals(linhasFaturaOld)) {
                Produto oldProdutoOfLinhasFatura = linhasFaturaNew.getProduto();
                if (oldProdutoOfLinhasFatura != null) {
                    oldProdutoOfLinhasFatura.setLinhasFatura(null);
                    oldProdutoOfLinhasFatura = em.merge(oldProdutoOfLinhasFatura);
                }
                linhasFaturaNew.setProduto(produto);
                linhasFaturaNew = em.merge(linhasFaturaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produto.getId();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto;
            try {
                produto = em.getReference(Produto.class, id);
                produto.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            LinhasFatura linhasFaturaOrphanCheck = produto.getLinhasFatura();
            if (linhasFaturaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produto (" + produto + ") cannot be destroyed since the LinhasFatura " + linhasFaturaOrphanCheck + " in its linhasFatura field has a non-nullable produto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
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

    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
