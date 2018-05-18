package pgxp.pto.dao;

import java.io.IOException;
import pgxp.pto.security.Credentials;
import pgxp.pto.security.Social;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import static java.security.MessageDigest.getInstance;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import pgxp.pto.constants.Perfil;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import org.demoiselle.jee.core.api.security.DemoiselleUser;
import org.demoiselle.jee.core.api.security.SecurityContext;
import org.demoiselle.jee.core.api.security.Token;
import org.demoiselle.jee.crud.AbstractDAO;
import org.demoiselle.jee.security.exception.DemoiselleSecurityException;
import org.demoiselle.jee.security.message.DemoiselleSecurityMessages;
import pgxp.pto.AdminConfig;
import pgxp.pto.entity.Fingerprint;
import pgxp.pto.entity.Mensagem;
import pgxp.pto.entity.User;
import pgxp.pto.security.Subscription;

/**
 *
 * @author gladson
 */
public class UserDAO extends AbstractDAO<User, UUID> {

    private static final Logger LOG = getLogger(UserDAO.class.getName());

    @Inject
    private SecurityContext securityContext;

    @Inject
    private DemoiselleUser loggedUser;

    @Inject
    private Token token;

    @Inject
    private DemoiselleSecurityMessages bundle;

    @Inject
    private MensagemDAO mensagemDAO;

    @Inject
    private FingerprintDAO fingerprintDAO;

    @Inject
    private AdminConfig config;

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
     * @param password
     * @return
     */
    public User verifyEmail(String email, String password) {

        User usu = verifyEmail(email);

        if (usu == null) {
            throw new DemoiselleSecurityException("Usuário não existe, solicite acesso ao Síndico", UNAUTHORIZED.getStatusCode());
        }

        if (usu.getPass() == null) {
            throw new DemoiselleSecurityException("Para sua segurança altere sua senha", UNAUTHORIZED.getStatusCode());
        }

        if (!usu.getPass().equalsIgnoreCase(md5(password))) {
            throw new DemoiselleSecurityException("Senha incorreta", UNAUTHORIZED.getStatusCode());
        }

        return usu;
    }

    /**
     *
     * @param email
     * @return
     */
    public User verifyEmail(String email) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> from = query.from(User.class);
        TypedQuery<User> typedQuery = getEntityManager().createQuery(
                query.select(from)
                        .where(builder.equal(from.get("email"), email))
        );

        return typedQuery.getResultList().isEmpty() ? null : typedQuery.getResultList().get(0);
    }

    /**
     *
     * @param email
     * @return
     */
    public User searchByEmail(String email) {

        User user = verifyEmail(email);

        if (user == null) {
            throw new DemoiselleSecurityException("Não existe Usuário para ser importado, crie um novo cadastrado", PRECONDITION_FAILED.getStatusCode());
        }

        return user;

    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public User persist(User entity) {
        entity.setPass(md5(entity.getPass() == null ? UUID.randomUUID().toString() : entity.getPass()));
        return super.persist(entity);
    }

    /**
     *
     * @param id
     * @return
     */
    public String valida(String id) {
        return "{ \"mensagem\":\"Email Validado\"}";
    }

    /**
     *
     * @param credentials
     * @return
     */
    public Token login(Credentials credentials) {

        if (credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            throw new DemoiselleSecurityException("Pass não informada", UNAUTHORIZED.getStatusCode());
        }

        if (credentials.getUsername() == null || credentials.getUsername().isEmpty()) {
            throw new DemoiselleSecurityException("Email não informado", UNAUTHORIZED.getStatusCode());
        }

        User usu = verifyEmail(credentials.getUsername(), credentials.getPassword());

        loggedUser.setName(usu.getDescription());
        loggedUser.setIdentity(usu.getId().toString());
        loggedUser.addRole(usu.getPerfil().toString());

        loggedUser.addParam("Email", usu.getEmail());
        loggedUser.addParam("Foto", usu.getFoto() == null ? "img/user.png" : usu.getFoto());
        loggedUser.addParam("Vapid", config.getVappub());
        securityContext.setUser(loggedUser);

//        if (usu.getEmail().equalsIgnoreCase("admin@demoiselle.org")) {
//            Mensagem mensagem = new Mensagem();
//            mensagem.setAddresses(usu.getEmail());
//            mensagem.setEnviada(Boolean.FALSE);
//            mensagem.setTopic("Oi, " + usu.getDescription() + ", meu nome é Cloudia, sou a Zeladora do CondomínioFácil, Seja bem vindo!!!");
//            mensagem.setTipo("PUSH");
//            mensagem.setTextmessage(null);
//            mensagemDAO.persist(mensagem);
//        }
        return token;
    }

//    /**
//     *
//     * @param credentials
//     * @return
//     */
//    public String amnesia(Credentials credentials) {
//
//        User usu = verifyEmail(credentials.getUsername());
//
//        Mensagem mensagem = new Mensagem();
//        mensagem.setTipo("AMIN");
//        mensagem.setEnviada(Boolean.FALSE);
//        mensagem.setTopic(" [Login]  Alteração de Pass");
//        mensagem.setTextmessage(marcar(usu));
//        mensagem.setExtra(null);
//        mensagem.setAddresses(usu.getEmail());
//        mensagemDAO.persist(mensagem);
//
//        return "{ \"mensagem\":\"Enviamos um link para seu email para alterar sua senha\"}";
//    }
    /**
     *
     * @param credentials
     * @return
     */
    public String resenha(Credentials credentials) {

        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,10})");
        Matcher matcher = pattern.matcher(credentials.getPassword());
        if (!matcher.matches()) {
            throw new DemoiselleSecurityException("Pass fraca, tente outra, com 8 caracteres que tenha pelo menos 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial", PRECONDITION_FAILED.getStatusCode());
        }

        User usu = find(UUID.fromString(credentials.getUsername()));

        if (usu != null) {
            usu.setPass(md5(credentials.getPassword()));
            mergeFull(usu);
        }

        return "{ \"mensagem\":\"Sua senha foi alterada\"}";

    }

    /**
     *
     * @param credentials
     */
    public void register(Credentials credentials) {
        // envia email
        LOG.log(Level.INFO, "Enviando lembran\u00e7a para : {0}", credentials.getUsername());
        //return login(credentials);
    }

    /**
     *
     * @param credentials
     * @return
     */
    public String amnesia(Credentials credentials) {
        User usu = verifyEmail(credentials.getUsername());

        if (usu != null) {
            Mensagem mensagem = new Mensagem();
            mensagem.setTipo("MAIL");
            mensagem.setEnviada(Boolean.FALSE);
            mensagem.setTopic(" [Ptolomeaus]  " + usu.getDescription());
            mensagem.setTextmessage("");
            mensagem.setExtra(null);
            mensagem.setAddresses(usu.getEmail());
            mensagemDAO.persist(mensagem);
        }

        return "{ \"mensagem\":\"Enviamos um link no seu email para alterar sua senha\"}";
    }

    /**
     *
     * @return
     */
    public Token retoken() {
        loggedUser = securityContext.getUser();
        securityContext.setUser(loggedUser);
        return token;
    }

    private String md5(String senha) {
        MessageDigest md = null;
        try {
            md = getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOG.severe(e.getMessage());
        }
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
        return hash.toString(16);
    }

    /**
     *
     * @param social
     * @return
     */
    public Token social(Social social) {

        if (social.getProvider().equalsIgnoreCase("google") && !validateGoogle(social.getIdToken())) {
            throw new DemoiselleSecurityException("Não validado pelo Google", Response.Status.PRECONDITION_FAILED.getStatusCode());
        }

        if (social.getProvider().equalsIgnoreCase("facebook") && !validateFacebook(social.getToken())) {
            throw new DemoiselleSecurityException("Não validado pelo Facebook", Response.Status.PRECONDITION_FAILED.getStatusCode());
        }

        User usu = verifyEmail(social.getEmail());

        if (usu == null) {
            usu = new User();
            usu.setEmail(social.getEmail());
            usu.setFoto(social.getImageUrl());
            usu.setDescription(social.getName());
            usu.setPass(UUID.randomUUID().toString());
            usu.setPerfil(Perfil.VISITANTE);
            usu = persist(usu);

            Mensagem mensagem = new Mensagem();
            mensagem.setAddresses(social.getEmail());
            mensagem.setEnviada(Boolean.FALSE);
            mensagem.setTopic(" [CondomínioFácil]  Bem vindo!");
            mensagem.setTipo("MAIL");
            mensagem.setTextmessage("");
            mensagemDAO.persist(mensagem);

        } else if (!social.getImageUrl().equalsIgnoreCase(usu.getFoto())) {
            usu.setFoto(social.getImageUrl());
            mergeFull(usu);
        }

        loggedUser.setName(usu.getDescription());
        loggedUser.setIdentity(usu.getId().toString());
        loggedUser.addRole(usu.getPerfil().toString());

        loggedUser.addParam("Email", usu.getEmail());
        loggedUser.addParam("Foto", usu.getFoto() == null ? "img/user.png" : usu.getFoto());
        loggedUser.addParam("Vapid", config.getVappub());
        securityContext.setUser(loggedUser);

        return token;
    }

    private boolean validateGoogle(String token) {

        try {
            String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + token;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            return con.getResponseCode() == 200;
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean validateFacebook(String token) {

        try {
            String url = "https://graph.facebook.com/app?access_token=" + token;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            return con.getResponseCode() == 200;
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     *
     * @param firebase
     */
    public void setFirebase(Subscription firebase) {

        if (firebase != null) {

            List<Fingerprint> fps = fingerprintDAO.findByEndpoint(firebase.getEndpoint());

            if (fps == null || fps.isEmpty()) {
                Fingerprint fp = new Fingerprint();
                fp.setEndpoint(firebase.getEndpoint());
                fp.setAuth(firebase.getKeys().getAuth());
                fp.setP256dh(firebase.getKeys().getP256dh());
                fp.setUsuario(securityContext.getUser().getParams("Email"));
                fingerprintDAO.persist(fp);
            } else {
                fps.stream().map((fp) -> {
                    fp.setEndpoint(firebase.getEndpoint());
                    fp.setAuth(firebase.getKeys().getAuth());
                    fp.setP256dh(firebase.getKeys().getP256dh());
                    fp.setUsuario(securityContext.getUser().getParams("Email"));
                    return fp;
                }).forEachOrdered((fp) -> {
                    fingerprintDAO.mergeFull(fp);
                });

            }

        }
    }

}
