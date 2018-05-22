package pgxp.pto.timer;

import java.util.Map;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.inject.Inject;
import org.demoiselle.jee.core.lifecycle.annotation.Startup;
import pgxp.pto.AdminConfig;
import pgxp.pto.cloud.CloudSender;
import pgxp.pto.dao.ArquivoDAO;
import pgxp.pto.dao.FingerprintDAO;
import pgxp.pto.dao.PaginaDAO;
import pgxp.pto.entity.Pagina;
import pgxp.pto.ia.NLPtools;

/**
 *
 * @author SERPRO
 */
@Stateless
public class Timer {

    private static final Logger LOG = getLogger(Timer.class.getName());

    @Inject
    private AdminConfig config;

    @Inject
    private FingerprintDAO fingerprintDAO;

    @Inject
    private ArquivoDAO arquivoDAO;

    @Inject
    private PaginaDAO paginaDAO;

    @Inject
    private NLPtools nlp;

    @Inject
    private CloudSender sender;

    /**
     *
     */
    @Transactional
    @Schedule(second = "0", minute = "5", hour = "*/5", persistent = false)
    public void atSchedule1h() {

    }

    /**
     *
     */
    @Transactional
    @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
    public void atSchedule5m() {
        if (!paginaDAO.listaNaoValidos().isEmpty()) {
            Pagina pag = paginaDAO.listaNaoValidos().get(0);
            Map<String, String> nlpResult = nlp.nameFinder(pag.getTexto());
            pag.setValidado(Boolean.TRUE);
            pag.carregar(nlpResult);
            paginaDAO.mergeFull(pag);
        }
    }

    /**
     *
     */
    @Startup
    @Transactional
    public void atNow() {
        arquivoDAO.scan();
    }

    /**
     *
     */
    @Transactional
    @Schedule(second = "7", minute = "7", hour = "7", persistent = false)
    public void atScheduleOneInDay() {
        arquivoDAO.reindex();
    }

}
