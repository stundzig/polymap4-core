package org.polymap.service.openrefine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.polymap.core.CorePlugin;

import bsh.This;

public class OpenRefinePlugin extends Plugin {


    private static Log log = LogFactory.getLog( OpenRefinePlugin.class );


    public void start( BundleContext context ) throws Exception {
        
        File dataDir = new File(CorePlugin.getDataLocation( getBundle() ), "openrefine" );
        
        // TODO copy all ressources into the dataDir
        URL resource = this.getClass().getResource( "/webapp" );
        System.out.println( "res: " + resource );
//        if (dataDir.exists()) {
//            log.info( "Cleaning cache dir: " + dataDir );
//            FileUtils.deleteDirectory( dataDir );
//            dataDir.mkdir();
//        }
//        else {
            log.info( "Creating cache dir: " + dataDir );
            dataDir.mkdir();            
//        }
        
//        ServletHandler handler = new ServletHandler();
//        handler.addServletWithMapping(RefineServlet.class, "/*");
//        ServletContextHandler ch= new ServletContextHandler();
//
//        ch.setContextPath("/openrefine");
//        ch.setServletHandler(handler);
//        
//        Hashtable<String,String> initParams = new Hashtable<String, String>();
//        initParams.put( "refine.version", "2.6" );
//        initParams.put( "refine.revision", "polymap 1" );
//        initParams.put( "refine.data", dataDir.getAbsolutePath() );
//        
//        context.registerService(ContextHandler.class.getName(), ch, initParams);

        System.out.println("Registration Complete");
        httpServiceTracker = new HttpServiceTracker( context, dataDir );
        httpServiceTracker.open();
    }


    public void stop( BundleContext context ) throws Exception {
        httpServiceTracker.close();
        httpServiceTracker = null;
    }

    private ServiceTracker httpServiceTracker;


    private class HttpServiceTracker
            extends ServiceTracker {

        private File dataDir;


        public HttpServiceTracker( BundleContext context, File dataDir ) {
            super( context, HttpService.class.getName(), null );
            this.dataDir = dataDir;
        }


        public Object addingService( ServiceReference reference ) {
            HttpService httpService = (HttpService)context.getService( reference );
            try {
                Hashtable<String,String> initParams = new Hashtable<String, String>();
                initParams.put( "refine.version", "2.6" );
                initParams.put( "refine.revision", "polymap 1" );
                initParams.put( "refine.data", dataDir.getAbsolutePath() );
                
                httpService.registerServlet( "/openrefine", new OpenRefineServlet(), initParams, null ); //$NON-NLS-1$
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return httpService;
        }


        public void removedService( ServiceReference reference, Object service ) {
            HttpService httpService = (HttpService)service;
            httpService.unregister( "/openrefine" ); //$NON-NLS-1$
            super.removedService( reference, service );
        }
    }
}
