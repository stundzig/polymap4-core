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
package org.polymap.core.style.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.polymap.core.style.model.ConstantNumber;

/**
 * Editor of a {@link ConstantNumber}. 
 *
 * @author Falko Br�utigam
 */
class ConstantNumberEditor
        extends StylePropertyEditor<ConstantNumber> {

    private static Log log = LogFactory.getLog( ConstantNumberEditor.class );

    
    @Override
    public String label() {
        return "Constant number";
    }


    @Override
    public Composite createValueContents( Composite parent ) {
        Composite contents = super.createValueContents( parent );
        Label l = new Label( parent, SWT.NONE );
        l.setText( "" + spv.value.get() );
        return contents;
    }
    
}