/* 
 * polymap.org
 * Copyright 2009, Polymap GmbH, and individual contributors as indicated
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

package org.polymap.core.project.operations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qi4j.api.concern.ConcernOf;
import org.qi4j.api.injection.scope.This;

import org.eclipse.core.commands.operations.IUndoableOperation;

/**
 * This concern ensures that operations get a properly translated
 * label set after their init() method was called.
 * 
 * @author <a href="http://www.polymap.de">Falko Braeutigam</a>
 * @version POLYMAP3 ($Revision$)
 * @since 3.0
 */
public abstract class OperationLabelConcern
        extends ConcernOf<IUndoableOperation>
        implements IUndoableOperation {

    private static Log log = LogFactory.getLog( OperationLabelConcern.class );

    @This IUndoableOperation    composite;
    
    
    public String getLabel() {
        log.info( "composite= " + composite.getClass() );
        return next.getLabel();
    }

}
