/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgxp.pto.security;

/**
 *
 * @author paulo
 */
public class Keys {

    private String auth;
    private String p256dh;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getP256dh() {
        return p256dh;
    }

    public void setP256dh(String p256dh) {
        this.p256dh = p256dh;
    }

    @Override
    public String toString() {
        return "Keys{" + "auth=" + auth + ", p256dh=" + p256dh + '}';
    }

}
