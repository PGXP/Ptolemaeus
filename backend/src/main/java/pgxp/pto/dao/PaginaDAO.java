package pgxp.pto.dao;

import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import pgxp.pto.entity.Fingerprint;
import org.demoiselle.jee.crud.AbstractDAO;
import pgxp.pto.entity.Pagina;

/**
 *
 * @author SERPRO
 */
public class PaginaDAO extends AbstractDAO< Pagina, UUID> {

    private static final Logger LOG = getLogger(PaginaDAO.class.getName());

    @PersistenceContext
    protected EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
