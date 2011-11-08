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
package com.netbout.rest;

import com.netbout.rest.page.JaxbBundle;
import com.netbout.rest.page.PageBuilder;
import com.netbout.spi.Identity;
import com.netbout.spi.User;
import com.rexsl.core.Manifests;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * RESTful front of login functions.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Path("/g")
public final class LoginRs extends AbstractRs {

    /**
     * Login page.
     * @return The page, convertable to XML
     * @see <a href="http://developers.facebook.com/docs/authentication/">facebook.com</a>
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Page login() {
        final URI facebookUri = UriBuilder
            .fromPath("https://www.facebook.com/dialog/oauth")
            .queryParam("client_id", Manifests.INSTANCE.read("Netbout-FbId"))
            .queryParam(
                "redirect_uri",
                this.uriInfo().getAbsolutePathBuilder()
                    .replacePath("/g/fb")
                    .build()
            )
            .queryParam("scope", "email")
            .build();
        return new PageBuilder()
            .stylesheet("login")
            .build(AbstractPage.class)
            .init(this)
            .append(
                new JaxbBundle("facebook").attr(Page.HATEOAS_HREF, facebookUri)
            );
    }

    /**
     * Facebook authentication page (callback hits it).
     * @param code Facebook "authorization code"
     * @return The response
     */
    @Path("/fb")
    @GET
    public Response fbauth(@PathParam("code") final String code) {
        return new PageBuilder()
            .build(AbstractPage.class)
            .authenticated(this.authenticate(code))
            .entity("fbauth")
            .status(Response.Status.TEMPORARY_REDIRECT)
            .location(UriBuilder.fromPath("/").build())
            .build();
    }

    /**
     * Authenticate the user through facebook.
     * @param code Facebook "authorization code"
     * @return The user found
     */
    private Identity authenticate(final String code) {
        // let's get user name from FB
        final String name = "test";
        // let's get this user from Entry
        final User user = this.entry().user(name);
        // let's create default identity
        return user.identity(name);
    }

}
