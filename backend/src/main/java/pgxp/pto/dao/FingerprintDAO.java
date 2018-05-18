package pgxp.pto.dao;

import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import pgxp.pto.entity.Fingerprint;
import org.demoiselle.jee.crud.AbstractDAO;

/**
 *
 * @author gladson
 */
public class FingerprintDAO extends AbstractDAO< Fingerprint, Long> {

    private static final Logger LOG = getLogger(FingerprintDAO.class.getName());

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
     * @param email
     * @return
     */
    public List<Fingerprint> findByUsuario(String email) {
        return em.createNamedQuery("Fingerprint.findByUsuario").setParameter("usuario", email).getResultList();
    }

    /**
     *
     * @param cod
     * @return
     */
    public List<Fingerprint> findByEndpoint(String cod) {
        return em.createNamedQuery("Fingerprint.findByEndpoint").setParameter("endpoint", cod).getResultList();
    }
}
