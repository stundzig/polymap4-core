package org.polymap.service.openrefine;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import com.google.refine.RefineServlet;

public class OpenRefineServlet
        extends RefineServlet
        implements Servlet {

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        ServletConfig wrapper = new ServletConfig() {

            private ServletContext contextWrapper;


            @Override
            public String getServletName() {
                return config.getServletName();
            }


            @Override
            public ServletContext getServletContext() {
                if (contextWrapper == null) {
                    ServletContext context = config.getServletContext();
                    contextWrapper = new ServletContext() {

                        public String getRealPath( String path ) {
                            return "/Users/stundzig/Desktop/desk/projects/mapzone/ws/org.polymap.service.openrefine/webapp";//config.getInitParameter( "refine.data" ) + "/webapp";
                        }
                        
                        public String getContextPath() {
                            return context.getContextPath();
                        }


                        public ServletContext getContext( String uripath ) {
                            return context.getContext( uripath );
                        }


                        public int getMajorVersion() {
                            return context.getMajorVersion();
                        }


                        public int getMinorVersion() {
                            return context.getMinorVersion();
                        }


                        public int getEffectiveMajorVersion() {
                            return context.getEffectiveMajorVersion();
                        }


                        public int getEffectiveMinorVersion() {
                            return context.getEffectiveMinorVersion();
                        }


                        public String getMimeType( String file ) {
                            return context.getMimeType( file );
                        }


                        public Set<String> getResourcePaths( String path ) {
                            return context.getResourcePaths( path );
                        }


                        public URL getResource( String path ) throws MalformedURLException {
                            return context.getResource( path );
                        }


                        public InputStream getResourceAsStream( String path ) {
                            return context.getResourceAsStream( path );
                        }


                        public RequestDispatcher getRequestDispatcher( String path ) {
                            return context.getRequestDispatcher( path );
                        }


                        public RequestDispatcher getNamedDispatcher( String name ) {
                            return context.getNamedDispatcher( name );
                        }


                        public Servlet getServlet( String name ) throws ServletException {
                            return context.getServlet( name );
                        }


                        public Enumeration<Servlet> getServlets() {
                            return context.getServlets();
                        }


                        public Enumeration<String> getServletNames() {
                            return context.getServletNames();
                        }


                        public void log( String msg ) {
                            context.log( msg );
                        }


                        public void log( Exception exception, String msg ) {
                            context.log( exception, msg );
                        }


                        public void log( String message, Throwable throwable ) {
                            context.log( message, throwable );
                        }


                        


                        public String getServerInfo() {
                            return context.getServerInfo();
                        }


                        public boolean setInitParameter( String name, String value ) {
                            return context.setInitParameter( name, value );
                        }


                        public Object getAttribute( String name ) {
                            return context.getAttribute( name );
                        }


                        public Enumeration<String> getAttributeNames() {
                            return context.getAttributeNames();
                        }


                        public void setAttribute( String name, Object object ) {
                            context.setAttribute( name, object );
                        }


                        public void removeAttribute( String name ) {
                            context.removeAttribute( name );
                        }


                        public String getServletContextName() {
                            return context.getServletContextName();
                        }


                        public Dynamic addServlet( String servletName, String className ) {
                            return context.addServlet( servletName, className );
                        }


                        public Dynamic addServlet( String servletName, Servlet servlet ) {
                            return context.addServlet( servletName, servlet );
                        }


                        public Dynamic addServlet( String servletName,
                                Class<? extends Servlet> servletClass ) {
                            return context.addServlet( servletName, servletClass );
                        }


                        public <T extends Servlet> T createServlet( Class<T> clazz )
                                throws ServletException {
                            return context.createServlet( clazz );
                        }


                        public ServletRegistration getServletRegistration( String servletName ) {
                            return context.getServletRegistration( servletName );
                        }


                        public Map<String,? extends ServletRegistration> getServletRegistrations() {
                            return context.getServletRegistrations();
                        }


                        public javax.servlet.FilterRegistration.Dynamic addFilter(
                                String filterName, String className ) {
                            return context.addFilter( filterName, className );
                        }


                        public javax.servlet.FilterRegistration.Dynamic addFilter(
                                String filterName, Filter filter ) {
                            return context.addFilter( filterName, filter );
                        }


                        public javax.servlet.FilterRegistration.Dynamic addFilter(
                                String filterName, Class<? extends Filter> filterClass ) {
                            return context.addFilter( filterName, filterClass );
                        }


                        public <T extends Filter> T createFilter( Class<T> clazz )
                                throws ServletException {
                            return context.createFilter( clazz );
                        }


                        public FilterRegistration getFilterRegistration( String filterName ) {
                            return context.getFilterRegistration( filterName );
                        }


                        public Map<String,? extends FilterRegistration> getFilterRegistrations() {
                            return context.getFilterRegistrations();
                        }


                        public SessionCookieConfig getSessionCookieConfig() {
                            return context.getSessionCookieConfig();
                        }


                        public void setSessionTrackingModes(
                                Set<SessionTrackingMode> sessionTrackingModes ) {
                            context.setSessionTrackingModes( sessionTrackingModes );
                        }


                        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
                            return context.getDefaultSessionTrackingModes();
                        }


                        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
                            return context.getEffectiveSessionTrackingModes();
                        }


                        public void addListener( String className ) {
                            context.addListener( className );
                        }


                        public <T extends EventListener> void addListener( T t ) {
                            context.addListener( t );
                        }


                        public void addListener( Class<? extends EventListener> listenerClass ) {
                            context.addListener( listenerClass );
                        }


                        public <T extends EventListener> T createListener( Class<T> clazz )
                                throws ServletException {
                            return context.createListener( clazz );
                        }


                        public JspConfigDescriptor getJspConfigDescriptor() {
                            return context.getJspConfigDescriptor();
                        }


                        public ClassLoader getClassLoader() {
                            return context.getClassLoader();
                        }


                        public void declareRoles( String... roleNames ) {
                            context.declareRoles( roleNames );
                        }


                        public String getVirtualServerName() {
                            return context.getVirtualServerName();
                        }


                        @Override
                        public String getInitParameter( String name ) {
                            return context.getInitParameter( name );
                        }


                        @Override
                        public Enumeration<String> getInitParameterNames() {
                            return context.getInitParameterNames();
                        }

                    };
                }
                return contextWrapper;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return config.getInitParameterNames();
            }


            @Override
            public String getInitParameter( String name ) {
                return config.getInitParameter( name );
            }
        };
        super.init( wrapper );
    }


    @Override
    public ServletConfig getServletConfig() {
        return super.getServletConfig();
    }
}
