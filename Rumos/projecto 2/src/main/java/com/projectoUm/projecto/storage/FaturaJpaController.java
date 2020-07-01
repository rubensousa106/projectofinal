/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.storage;

import com.projectoUm.projecto.modelo.Fatura;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.projectoUm.projecto.modelo.Funcionario;
import com.projectoUm.projecto.storage.exceptions.IllegalOrphanException;
import com.projectoUm.projecto.storage.exceptions.NonexistentEntityException;
import java.util.ArrayList;
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
public class FaturaJpaController implements Serializable {

   public FaturaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("com.projectoUm_projecto_jar_0.0.1-SNAPSHOTPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Fatura fatura) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        Funcionario funcionarioOrphanCheck = fatura.getFuncionario();
        if (funcionarioOrphanCheck != null) {
            Fatura oldFaturaOfFuncionario = funcionarioOrphanCheck.getFatura();
            if (oldFaturaOfFuncionario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Funcionario " + funcionarioOrphanCheck + " already has an item of type Fatura whose funcionario column cannot be null. Please make another selection for the funcionario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Funcionario funcionario = fatura.getFuncionario();
            if (funcionario != null) {
                funcionario = em.getReference(funcionario.getClass(), funcionario.getId());
                fatura.setFuncionario(funcionario);
            }
            em.persist(fatura);
            if (funcionario != null) {
                funcionario.setFatura(fatura);
                funcionario = em.merge(funcionario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fatura fatura) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Fatura persistentFatura = em.find(Fatura.class, fatura.getId());
            Funcionario funcionarioOld = persistentFatura.getFuncionario();
            Funcionario funcionarioNew = fatura.getFuncionario();
            List<String> illegalOrphanMessages = null;
            if (funcionarioNew != null && !funcionarioNew.equals(funcionarioOld)) {
                Fatura oldFaturaOfFuncionario = funcionarioNew.getFatura();
                if (oldFaturaOfFuncionario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Funcionario " + funcionarioNew + " already has an item of type Fatura whose funcionario column cannot be null. Please make another selection for the funcionario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (funcionarioNew != null) {
                funcionarioNew = em.getReference(funcionarioNew.getClass(), funcionarioNew.getId());
                fatura.setFuncionario(funcionarioNew);
            }
            fatura = em.merge(fatura);
            if (funcionarioOld != null && !funcionarioOld.equals(funcionarioNew)) {
                funcionarioOld.setFatura(null);
                funcionarioOld = em.merge(funcionarioOld);
            }
            if (funcionarioNew != null && !funcionarioNew.equals(funcionarioOld)) {
                funcionarioNew.setFatura(fatura);
                funcionarioNew = em.merge(funcionarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = fatura.getId();
                if (findFatura(id) == null) {
                    throw new NonexistentEntityException("The fatura with id " + id + " no longer exists.");
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
            Fatura fatura;
            try {
                fatura = em.getReference(Fatura.class, id);
                fatura.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fatura with id " + id + " no longer exists.", enfe);
            }
            Funcionario funcionario = fatura.getFuncionario();
            if (funcionario != null) {
                funcionario.setFatura(null);
                funcionario = em.merge(funcionario);
            }
            em.remove(fatura);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fatura> findFaturaEntities() {
        return findFaturaEntities(true, -1, -1);
    }

    public List<Fatura> findFaturaEntities(int maxResults, int firstResult) {
        return findFaturaEntities(false, maxResults, firstResult);
    }

    private List<Fatura> findFaturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Fatura.class));
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

    public Fatura findFatura(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fatura.class, id);
        } finally {
            em.close();
        }
    }

    public int getFaturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Fatura> rt = cq.from(Fatura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
