/* 
 * polymap.org
 * Copyright 2011-2013, Polymap GmbH. All rights reserved.
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
package org.polymap.service.fs.spi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Default implementation of an content node. 
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public abstract class DefaultContentNode
        implements IContentNode {

    private static Log log = LogFactory.getLog( DefaultContentNode.class );

    private String                  name;
    
    private IPath                   parentPath;
    
    private IContentProvider        provider;
    
    private Object                  source;

    private Map<String,Object>      data = new HashMap();
    

    /**
     * 
     * @param name
     * @param parentPath
     * @param provider
     * @param source The backend object this content node represents. Might be null
     *        if no such mapping to a backend objekt exists. {@link #getSource()}
     */
    public DefaultContentNode( String name, IPath parentPath, IContentProvider provider, Object source ) {
        assert name != null;
//        assert parentPath != null;
//        assert provider != null;
        this.name = name;
        this.parentPath = parentPath;
        this.provider = provider;
        this.source = source;
    }

    
    /**
     * This default implementation does nothing. Override this to
     * perform any folder/file specific cleanup.
     */
    @Override
    public void dispose() {
    }


    /**
     * This method is called by the engine before this node is returned from the
     * cache.
     * <p/>
     * This default implementation of the method always returns <code>true</code>.
     * 
     * @return True if this (currently cached) instance is still valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }


    /**
     * Default implementation: 1kB
     */
    @Override
    public int getSizeInMemory() {
        return 1024;
    }

    
    @Override
    public String getName() {
        return name;
    }

    
    public IPath getParentPath() {
        return parentPath;
    }

    
    @Override
    public IPath getPath() {
        return parentPath != null ? parentPath.append( getName() ) : new Path( getName() );
    }


    @Override
    public IContentProvider getProvider() {
        return provider;
    }

    /**
     * Convenient for <code>getProvider().getSite()</code>.
     */
    public IContentSite getSite() {
        return getProvider().getSite();
    }

    
    /**
     * The backend object this content node represents. Might be null if no such
     * mapping to a backend objekt exists.
     */
    @Override
    public Object getSource() {
        return source;
    }


    @Override
    public Object getData( String key ) {
        return data.get( key );
    }


    @Override
    public Object putData( String key, Object value ) {
        return data.put( key, value );
    }
    
}
