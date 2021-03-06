/* 
 * polymap.org
 * Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.service.geoserver;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
/* 
 * polymap.org
 * Copyright 2010, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * $Id$
 */
import org.apache.commons.logging.LogFactory;

import org.eclipse.core.runtime.Plugin;

import org.polymap.core.CorePlugin;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class GeoServerPlugin 
        extends Plugin {

    private static Log log = LogFactory.getLog( GeoServerPlugin.class );

    public static final String ID = "org.polymap.service.geoserver";

    private static GeoServerPlugin      instance;
	
	
    /**
     * Returns the shared instance
     */
    public static GeoServerPlugin instance() {
    	return instance;
    }


    // instance *******************************************

    private File                        cacheDir;

    private ServiceTracker              httpServiceTracker;
    
    /** The base URL on the local machine (without proxy). */
    private String                      localBaseUrl;
    
    /** The base URL explicitly set by the user via {@link GeneralPreferencePage}. */
    private String                      proxyBaseUrl;

    
    /**
     * Returns the cache directory of this plugin.
     */
    public File getCacheDir() {
        return cacheDir;
    }

    /**
     * The configured base URL of this instance.
     *
     * @return The configured URL or somethong like http://localhost...
     */
    public String getBaseUrl() {
        return proxyBaseUrl != null && proxyBaseUrl.length() > 0 ? proxyBaseUrl : localBaseUrl;
    }

    
	public void start( BundleContext context ) throws Exception {
		super.start( context );
		instance = this;

		cacheDir = new File( CorePlugin.getDataLocation( getBundle() ), "cache" );
        if (cacheDir.exists()) {
            log.info( "Cleaning cache dir: " + cacheDir );
            FileUtils.deleteDirectory( cacheDir );
            cacheDir.mkdir();
        }
        else {
            log.info( "Creating cache dir: " + cacheDir );
            cacheDir.mkdir();            
        }
        
        // start test servlet
        httpServiceTracker = new ServiceTracker( context, HttpService.class.getName(), null ) {
            public Object addingService( ServiceReference reference ) {
                HttpService httpService = (HttpService)super.addingService( reference );                
                if (httpService != null) {
                    String protocol = "http";
                    String port = context.getProperty( "org.osgi.service.http.port" );
                    String hostname = "localhost";
                    try {
                        InetAddress.getLocalHost().getHostAddress();
                    }
                    catch (UnknownHostException e) {
                        // ignore; use "localhost" then
                    }

                    // get baseUrl
                    localBaseUrl = protocol + "://" + hostname + ":" + port;
                    log.info( "HTTP service found on: " + localBaseUrl );

//                    try {
//                        // auto test
//                        httpService.registerServlet( "/wms", new GeoServerServlet(), null, null );
//                    }
//                    catch (Exception e) {
//                        throw new RuntimeException( e );
//                    }
                }
                return httpService;
            }
        };
        httpServiceTracker.open();
	}

	
	public void stop( BundleContext context ) throws Exception {
	    if (httpServiceTracker != null) {
	        httpServiceTracker.close();
	        httpServiceTracker = null;
	    }
	    
		instance = null;
		super.stop(context);

		if (cacheDir.exists()) {
            log.info( "Cleaning cache dir: " + cacheDir );
            FileUtils.deleteDirectory( cacheDir );
        }
	}

}
