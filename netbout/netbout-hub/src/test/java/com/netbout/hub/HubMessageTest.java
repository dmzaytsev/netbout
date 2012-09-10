/**
 * Copyright (c) 2009-2012, Netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code accidentally and without intent to use it, please report this
 * incident to the author by email.
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
package com.netbout.hub;

import com.netbout.spi.Bout;
import com.netbout.spi.BoutMocker;
import com.netbout.spi.Identity;
import com.netbout.spi.IdentityMocker;
import com.netbout.spi.Message;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case of {@link HubMessage}.
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class HubMessageTest {

    /**
     * HubMessage can "wrap" MessageDt class.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void wrapsMessageDtDataProperties() throws Exception {
        final PowerHub hub = new PowerHubMocker().mock();
        final Identity viewer = new IdentityMocker().mock();
        final Bout bout = new BoutMocker().mock();
        final MessageDt data = Mockito.mock(MessageDt.class);
        final Message msg = new HubMessage(hub, viewer, bout, data);
        msg.number();
        Mockito.verify(data).getNumber();
        msg.author();
        Mockito.verify(data).getAuthor();
        msg.text();
        Mockito.verify(data).getText();
        msg.date();
        Mockito.verify(data).getDate();
    }

    /**
     * HubMessage can be equal to the same message.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void exposesEqualityFeature() throws Exception {
        final PowerHub hub = new PowerHubMocker().mock();
        final Identity viewer = new IdentityMocker().mock();
        final Bout bout = new BoutMocker().mock();
        final MessageDt data = Mockito.mock(MessageDt.class);
        MatcherAssert.assertThat(
            new HubMessage(hub, viewer, bout, data),
            Matchers.equalTo(new HubMessage(hub, viewer, bout, data))
        );
    }

}
