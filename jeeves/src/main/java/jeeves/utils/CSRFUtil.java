package jeeves.utils;

import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class with methods for CSRF (Cross Site Request Forgery) tokens.
 *
 * @author Jose García
 */
public final class CSRFUtil {
    private final static String DEFAULT_PRNG = "SHA1PRNG"; //algorithm to generate key

    public final static String TOKEN_PARAMETER_NAME = "_tk";
    public final static String TOKEN_SESSION_KEY = "CSRF-TOKEN";

    /**
     * Generates a CSRF Token.
     *
     * @return  CSRF Token.
     */
    public static String generateToken() throws NoSuchAlgorithmException {
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
    public static String retrieveOrCreateTokenFromSession(final ServiceContext context)
            throws NoSuchAlgorithmException {
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
     * Checks if a CSRF Token provided in a request params is valid.
     *
     * @param params
     * @param context
     * @return
     */
    public static boolean isValidToken(Element params, final ServiceContext context)
            throws NoSuchAlgorithmException {
        return retrieveOrCreateTokenFromSession(context).equals(Util.getParam(params, TOKEN_PARAMETER_NAME, ""));
    }

    /**
     * Adds CSRF token to a jeeves service response.
     *
     * Used in services that create a maintenance form.
     *
     * @param response
     */
    public static void addTokenToServiceResponse(Element response, final ServiceContext context)
            throws NoSuchAlgorithmException {
        response.addContent(new Element(TOKEN_PARAMETER_NAME).setText(retrieveOrCreateTokenFromSession(context)));
    }
}