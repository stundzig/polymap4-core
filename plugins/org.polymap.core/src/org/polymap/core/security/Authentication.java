/* 
 * polymap.org
 * Copyright (C) 2009-2013, Polymap GmbH. All rights reserved.
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
package org.polymap.core.security;

import java.security.Principal;

import sun.security.acl.PrincipalImpl;

/**
 *
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @since 3.0
 */
public class Authentication {

    /** The role/group every user is in. */
    public static final Principal       ALL = new PrincipalImpl( "ALL" );
    
}
