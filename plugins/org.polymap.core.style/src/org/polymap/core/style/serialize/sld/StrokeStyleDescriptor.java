/*
 * polymap.org Copyright (C) 2016, the @authors. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3.0 of the License, or (at your option) any later
 * version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.core.style.serialize.sld;

import org.opengis.filter.expression.Expression;

import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.Immutable;

/**
 * @author Steffen Stundzig
 */
public class StrokeStyleDescriptor
        extends SymbolizerDescriptor {

    @Immutable
    // StrokeCapStyle
    public Config<Expression> capStyle;

    @Immutable
    // StrokeJoinStyle
    public Config<Expression> joinStyle;

    @Immutable
    public Config<float[]> dashStyle;


    @Override
    protected StrokeStyleDescriptor clone() {
        return (StrokeStyleDescriptor)super.clone();
    }
}