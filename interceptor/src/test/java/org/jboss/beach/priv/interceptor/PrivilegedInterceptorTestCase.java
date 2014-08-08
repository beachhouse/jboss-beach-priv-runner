/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
 */
package org.jboss.beach.priv.interceptor;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
//@RunWith(Interceptors.class)
@RunWith(CdiRunner.class)
// per default only the test class is scanned
@AdditionalClasses({ DoPrivilegedInterceptor.class })
public class PrivilegedInterceptorTestCase {
    @After @DoPrivileged
    public void after() {
        System.getProperties();
    }

    /* Interceptors on a static method won't do
    @AfterClass @DoPrivileged
    public static void afterClass() {
        System.getProperties();
    }
    */

    @Before @DoPrivileged
    public void before() {
        System.getProperties();
    }

    /* Interceptors on a static method won't do
    @BeforeClass @DoPrivileged
    public static void beforeClass() {
        System.getProperties();
    }
    */

    @Test(expected = SecurityException.class)
    public void testGetProperties() {
        System.getProperties();
    }

    @Test @DoPrivileged
    public void testGetPropertiesPrivileged() {
        System.getProperties();
    }
}
