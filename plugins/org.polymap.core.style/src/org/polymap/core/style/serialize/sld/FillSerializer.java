/* 
 * polymap.org
 * Copyright (C) 2016, the @authors. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.core.style.serialize.sld;

import java.awt.Color;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.polymap.core.style.model.Fill;

/**
 * @author Steffen Stundzig
 */
public class FillSerializer
        extends StyleCompositeSerializer<Fill,FillDescriptor> {

    private static Log log = LogFactory.getLog( FillSerializer.class );


    @Override
    protected FillDescriptor createDescriptor() {
        return new FillDescriptor();
    }


    @Override
    public void doSerialize( Fill style ) {
        setValue( style.color.get(), ( FillDescriptor sd, Color value ) -> sd.color.set( value ) );
        setValue( style.opacity.get(), ( FillDescriptor sd, Double value ) -> sd.opacity.set( value ) );
    }

}