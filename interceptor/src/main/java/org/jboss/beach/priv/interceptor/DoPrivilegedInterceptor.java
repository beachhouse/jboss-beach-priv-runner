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

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@DoPrivileged @Interceptor
public class DoPrivilegedInterceptor {
    @AroundInvoke
    public Object aroundInvoke(final InvocationContext context) throws Exception {
        // TODO: should I create a new AccessControlContext in a privileged block?
        // That would be assuming the responsibilities of the code that uses this interceptor.
        final AccessControlContext acc = AccessController.doPrivileged(new PrivilegedExceptionAction<AccessControlContext>() {
            @Override
            public AccessControlContext run() throws Exception {
                return new AccessControlContext(AccessController.getContext(), new DomainCombiner() {
                    @Override
                    public ProtectionDomain[] combine(final ProtectionDomain[] currentDomains, final ProtectionDomain[] assignedDomains) {
                        return assignedDomains;
                    }
                });
            }
        });
        return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                return context.proceed();
            }
        }, acc);
    }
}
