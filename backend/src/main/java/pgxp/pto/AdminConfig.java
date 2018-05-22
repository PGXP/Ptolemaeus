/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto;

import org.demoiselle.jee.configuration.annotation.Configuration;

/**
 *
 * @author PauloGladson
 */
@Configuration(prefix = "pgxp.pto")
public class AdminConfig {

    private String vappub;
    private String vapprv;
    private String pathfile;
    private String pathia;

    public String getVappub() {
        return vappub;
    }

    public void setVappub(String vappub) {
        this.vappub = vappub;
    }

    public String getVapprv() {
        return vapprv;
    }

    public void setVapprv(String vapprv) {
        this.vapprv = vapprv;
    }

    public String getPathfile() {
        return pathfile;
    }

    public void setPathfile(String pathfile) {
        this.pathfile = pathfile;
    }

    public String getPathia() {
        return pathia;
    }

    public void setPathia(String pathia) {
        this.pathia = pathia;
    }

}
