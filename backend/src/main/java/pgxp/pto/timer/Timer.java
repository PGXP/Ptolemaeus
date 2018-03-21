package pgxp.pto.timer;

import java.util.List;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.inject.Inject;
import org.demoiselle.jee.core.lifecycle.annotation.Startup;
import pgxp.pto.cloud.CloudSender;
import pgxp.pto.dao.ArquivoDAO;
import pgxp.pto.dao.FingerprintDAO;
import pgxp.pto.entity.Fingerprint;

/**
 *
 * @author SERPRO
 */
@Stateless
public class Timer {

    private static final Logger LOG = getLogger(Timer.class.getName());

    @Inject
    private FingerprintDAO fingerprintDAO;

    @Inject
    private ArquivoDAO arquivoDAO;

    @Inject
    private CloudSender sender;

    /**
     *
     */
    @Transactional
    @Schedule(second = "0", minute = "0", hour = "*/1", persistent = false)
    public void atSchedule1h() {
        arquivoDAO.reindex();
    }

    /**
     *
     */
    @Transactional
    @Schedule(second = "33", minute = "*/5", hour = "*", persistent = false)
    public void atSchedule5m() {
        //LOG.info("atSchedule1m");
        
    }

    /**
     *
     */
    @Startup
    public void atNow() {

    }

    /**
     *
     */
    @Transactional
    @Schedule(second = "0", minute = "0", hour = "9", persistent = false)
    public void atScheduleOneInDay() {
        // LOG.info("atScheduleOneInDay");
    }

}
