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
package org.jboss.beach.priv.runner;

import static java.security.AccessController.doPrivileged;
import static java.security.AccessController.getContext;

import java.security.AccessControlContext;
import java.security.DomainCombiner;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class RunBefores extends Statement {
    private final Statement fNext;

    private final Object fTarget;

    private final List<FrameworkMethod> fBefores;

    public RunBefores(Statement next, List<FrameworkMethod> befores, Object target) {
        fNext = next;
        fBefores = befores;
        fTarget = target;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    for (FrameworkMethod before : fBefores) {
                        try {
                            before.invokeExplosively(fTarget);
                        } catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                    }
                    return null;
                }
            }, new AccessControlContext(getContext(), new DomainCombiner() {
                @Override
                public ProtectionDomain[] combine(final ProtectionDomain[] currentDomains, final ProtectionDomain[] assignedDomains) {
                    return assignedDomains;
                }
            }));
        } catch (RuntimeException e) {
            throw e.getCause();
        }
        fNext.evaluate();
    }
}
