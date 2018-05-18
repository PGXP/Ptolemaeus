package pgxp.pto.timer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
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
    private CloudSender sender;

    /**
     *
     */
    @Transactional
    @Schedule(second = "0", minute = "0", hour = "*/3", persistent = false)
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
    @Transactional
    public void atNow() {

        File folder = new File(config.getPath());
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                try {
                    arquivoDAO.ler(new FileInputStream(listOfFiles[i]), listOfFiles[i].getName());
                    System.out.println("File " + listOfFiles[i].getName());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        arquivoDAO.reindex();
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
