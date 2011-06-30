/**
 * Copyright (c) 2009-2011, netBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email: privacy@netbout.com.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.rest;

import com.netbout.engine.Bout;
import com.netbout.engine.BoutFactory;
import com.netbout.rest.jaxb.PageWithBouts;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class ListRsTest {

    private static final Long BOUT_ID = 123L;

    private static final String QUERY = "";

    @Test
    public void testListsBouts() throws Exception {
        final FactoryBuilder builder = mock(FactoryBuilder.class);
        final ListRs svc = new ListRs(builder);
        assertThat(svc.list(this.QUERY), instanceOf(PageWithBouts.class));
    }

    @Test
    public void testSingleBoutRendering() throws Exception {
        final FactoryBuilder builder = mock(FactoryBuilder.class);
        final BoutFactory bfactory = mock(BoutFactory.class);
        final Bout bout = mock(Bout.class);
        doReturn(bout).when(bfactory).find(this.BOUT_ID);
        doReturn(this.BOUT_ID).when(bout).number();
        doReturn(bfactory).when(builder).getBoutFactory();
        final ListRs svc = new ListRs(builder);
        assertThat(svc.bout(this.BOUT_ID), instanceOf(BoutRs.class));
    }

}