package jeeves.services;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.CSRFUtil;
import org.jdom.Element;

/**
 * Jeeves service class to retrieve CSRF token, to be use in jeeves services that requires it.
 *
 * An example of usage can be a script using http invocations to jeeves services that require the CSRF token.
 * This service should be call before to obtain the token and send to the jeeves service that requires it.
 *
 * @author Jose García
 */
public class GetSecureToken implements Service
{
    public void init(String appPath, ServiceConfig params) throws Exception {}

    //--------------------------------------------------------------------------
    //---
    //--- Service
    //---
    //--------------------------------------------------------------------------

    public Element exec(Element params, ServiceContext context) throws Exception
    {
        String token = CSRFUtil.retrieveOrCreateTokenFromSession(context);

        return new Element("token").setText(token);
    }
}