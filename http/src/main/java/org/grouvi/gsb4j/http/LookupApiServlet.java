
package org.grouvi.gsb4j.http;


import org.grouvi.gsb4j.api.SafeBrowsingApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;


/**
 * Servlet for Safe Browsing Lookup API.
 *
 * @author azilet
 */
class LookupApiServlet extends ServletBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LookupApiServlet.class );

    @Inject
    @Named( SafeBrowsingApi.LOOKUP_API )
    Provider<SafeBrowsingApi> lookupApiProvider;


    @Override
    Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    SafeBrowsingApi getSafeBrowsingApi()
    {
        return lookupApiProvider.get();
    }

}

