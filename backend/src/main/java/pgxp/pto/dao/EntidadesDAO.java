/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.dao;

import java.util.UUID;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.demoiselle.jee.crud.AbstractDAO;
import pgxp.pto.entity.Entidades;

/**
 *
 * @author SERPRO
 */
public class EntidadesDAO extends AbstractDAO< Entidades, UUID> {

    private static final Logger LOG = getLogger(EntidadesDAO.class.getName());

    @PersistenceContext
    protected EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
