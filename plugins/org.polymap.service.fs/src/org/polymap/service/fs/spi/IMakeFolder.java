/* 
 * polymap.org
 * Copyright 2012, Polymap GmbH. All rights reserved.
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

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public interface IMakeFolder
        extends IContentFolder {

    /**
     * Creates a new folder with the given name.
     * <p/>
     * The method is responsible of calling
     * {@link IContentSite#invalidateFolder(IContentFolder)} on folders that content
     * has changed during this method.
     * 
     * @param newName
     * @return Newly created folder.
     */
    public IContentFolder createFolder( String newName );

}
