/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.dao;

import java.util.List;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.demoiselle.jee.crud.AbstractDAO;
import pgxp.pto.entity.Mensagem;

/**
 *
 * @author gladson
 */
public class MensagemDAO extends AbstractDAO< Mensagem, Long> {

    private static final Logger LOG = getLogger(MensagemDAO.class.getName());

    /**
     *
     */
    @PersistenceContext
    protected EntityManager em;

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @return
     */
    public List<Mensagem> listaEmail() {
        return getEntityManager().createQuery("Select m from Mensagem m where m.tipo = 'MAIL' and m.enviada = false").getResultList();
    }

    /**
     *
     * @return
     */
    public List<Mensagem> listaPush() {
        return getEntityManager().createQuery("Select m from Mensagem m where m.tipo = 'PUSH' and m.enviada = false").getResultList();
    }

    /**
     *
     * @return
     */
    public List<Mensagem> listaNotificacao() {
        return getEntityManager().createQuery("Select m from Mensagem m where m.tipo = 'NOTI' and m.enviada = false").getResultList();
    }

}
