/**
 * Copyright (c) 2009-2011, NetBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the NetBout.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.netbout.spi;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;

/**
 * Universal resource locator (URN).
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 * @see <a href="http://tools.ietf.org/html/rfc2141">RFC2141</a>
 */
public final class Urn {

    /**
     * The prefix.
     */
    private static final String PREFIX = "urn";

    /**
     * Encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The separator.
     */
    private static final String SEP = ":";

    /**
     * Validating regular expr.
     */
    private static final String REGEX =
        "^urn:[a-z]{0,31}:([a-z0-9()+,\\-.:=@;$_!*']|%[0-9a-fA-F]{2})+$";

    /**
     * The URI.
     */
    private final transient URI uri;

    /**
     * Public ctor.
     * @param text The text of the URN
     * @throws URISyntaxException If syntax is not correct
     */
    public Urn(final String text) throws URISyntaxException {
        if (!text.matches(this.REGEX)) {
            throw new URISyntaxException(text, "Invalid format of URN");
        }
        this.uri = new URI(text);
    }

    /**
     * Public ctor.
     * @param nid The namespace ID
     * @param nss The namespace specific string
     */
    public Urn(final String nid, final String nss) {
        if (!nid.matches("^[a-z]{0,31}$")) {
            throw new IllegalArgumentException("NID can contain only letters");
        }
        try {
            this.uri = URI.create(
                String.format(
                    "%s%s%s%2$s%s",
                    this.PREFIX,
                    this.SEP,
                    nid,
                    URLEncoder.encode(nss, this.ENCODING)
                )
            );
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Static ctor.
     * @param text The text of the URN
     * @return The URN
     */
    public static Urn create(final String text) {
        try {
            return new Urn(text);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.uri.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        boolean equals = false;
        if (obj instanceof Urn) {
            equals = this.uri.equals(((Urn) obj).uri);
        } else if (obj instanceof URI) {
            equals = this.uri.equals((URI) obj);
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }

    /**
     * Convert it to URI.
     * @return The URI
     */
    public URI toURI() {
        return URI.create(this.uri.toString());
    }

    /**
     * Get namespace ID.
     * @return Namespace ID
     */
    public String nid() {
        return this.segment(1);
    }

    /**
     * Get namespace specific string.
     * @return Namespace specific string
     */
    public String nss() {
        try {
            return URLDecoder.decode(this.segment(2), this.ENCODING);
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get segment by position.
     * @param pos Its position
     * @return The segment
     */
    private String segment(final int pos) {
        return StringUtils.splitPreserveAllTokens(
            this.uri.toString(),
            this.SEP,
            // @checkstyle MagicNumber (1 line)
            3
        )[pos];
    }

}
