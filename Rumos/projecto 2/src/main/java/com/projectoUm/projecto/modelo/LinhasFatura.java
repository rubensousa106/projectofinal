/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ruben
 */
@Entity
@Table(name = "Linhas_Fatura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LinhasFatura.findAll", query = "SELECT l FROM LinhasFatura l"),
    @NamedQuery(name = "LinhasFatura.findById", query = "SELECT l FROM LinhasFatura l WHERE l.id = :id"),
    @NamedQuery(name = "LinhasFatura.findByIdProduto", query = "SELECT l FROM LinhasFatura l WHERE l.idProduto = :idProduto"),
    @NamedQuery(name = "LinhasFatura.findByIdFatura", query = "SELECT l FROM LinhasFatura l WHERE l.idFatura = :idFatura")})
public class LinhasFatura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "id_Produto")
    private Integer idProduto;
    @Column(name = "id_Fatura")
    private Integer idFatura;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Produto produto;

    public LinhasFatura() {
    }

    public LinhasFatura(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public Integer getIdFatura() {
        return idFatura;
    }

    public void setIdFatura(Integer idFatura) {
        this.idFatura = idFatura;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LinhasFatura)) {
            return false;
        }
        LinhasFatura other = (LinhasFatura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.projectoUm.projecto.modelo.LinhasFatura[ id=" + id + " ]";
    }
    
}
