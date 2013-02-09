package jeeves.services;

import jeeves.exceptions.ServiceNotAllowedEx;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Base jeeves service class for services that require CSRF (Cross Site Request Forgery) tokens.
 *
 * @author Jose García
 */
public abstract class CSRFTokenService implements Service {
    private final static String DEFAULT_PRNG = "SHA1PRNG"; //algorithm to generate key

    public final static String TOKEN_PARAMETER_NAME = "_tk";
    public final static String TOKEN_SESSION_KEY = "CSRF-TOKEN";


    public abstract void init(String appPath, ServiceConfig params) throws Exception;

    /** Services that require CSRF tokens must implement doExec instead of exec **/
    protected abstract Element doExec(Element params, ServiceContext context) throws Exception;

    /**
     * Checks the CSRF token, throwing an exception if it's not valid.
     * If it's valid call specific service implementation.
     *
     * @param params
     * @param context
     * @return
     * @throws ServiceNotAllowedEx
     */
    public final Element exec(Element params, ServiceContext context) throws Exception {
        if (!isValidToken(params, context)) {
            throw new ServiceNotAllowedEx("Service not allowed. CSRF Token is not valid");
        }

        return doExec(params, context);
    }

    /**
     * Generates a CSRF Token.
     *
     * @return  CSRF Token.
     */
    private String generateToken() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance(DEFAULT_PRNG);
        final byte[] bytes = new byte[32];
        sr.nextBytes(bytes);
        return Base64.encodeBase64URLSafeString(bytes);
    }

    /**
     * Gets the CSRF Token from user session. If not available, creates it.
     *
     * @param context
     * @return CSRF Token from user session.
     */
    private String retrieveOrCreateTokenFromSession(final ServiceContext context) throws NoSuchAlgorithmException {
        String token = null;

        UserSession session = context.getUserSession();

        if(session != null) {
            token = (String) session.getProperty(TOKEN_SESSION_KEY);
            if(StringUtils.isBlank(token))
                session.setProperty(TOKEN_SESSION_KEY, (token = generateToken()));
        }
        return token;
    }

    /**
     * Checks if a param
     * @param params
     * @param context
     * @return
     */
    public boolean isValidToken(Element params, final ServiceContext context)  throws NoSuchAlgorithmException {
        return this.retrieveOrCreateTokenFromSession(context).equals(Util.getParam(params, TOKEN_PARAMETER_NAME, ""));
    }
}
