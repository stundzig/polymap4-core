/* 
 * polymap.org
 * Copyright (C) 2009-2015, Polymap GmbH. All rights reserved.
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
package org.polymap.core.project;

import org.polymap.model2.Concerns;
import org.polymap.model2.Defaults;
import org.polymap.model2.ManyAssociation;
import org.polymap.model2.Mixins;
import org.polymap.model2.Property;
import org.polymap.model2.runtime.event.PropertyChangeSupport;

/**
 * A Map contains other maps and/or {@link ILayer}s. It holds information about the
 * rendering of the Services of the Layers.
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
@Concerns({
    PropertyChangeSupport.class 
//    ACLCheckConcern.class
})
@Mixins({
//    ACL.class
})
public class IMap
        extends ProjectNode { 

    @Defaults
    public ManyAssociation<ILayer>      layers;

    public ManyAssociation<IMap>        children;

    public Property<String>             srsCode;

//    @ModelProperty(PROP_CRSCODE)
//    public void setCRSCode( String code )
//            throws NoSuchAuthorityCodeException, FactoryException, TransformException;
//    
//    public CoordinateReferenceSystem getCRS();


    /**
     * The max extent of the map. 
     */
    public Property<EnvelopeComposite>  maxExtent;

    
//    public <T> T visit( LayerVisitor<T> visitor );

}
