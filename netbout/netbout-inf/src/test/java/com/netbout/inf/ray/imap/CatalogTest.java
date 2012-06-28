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
package com.netbout.inf.ray.imap;

import java.util.Collection;
import java.util.Random;
import java.util.TreeSet;
import org.apache.commons.collections.IteratorUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test case of {@link Catalog}.
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class CatalogTest {

    /**
     * Temporary folder.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * Catalog can register value and find it laters.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void registersValueAndFindsItThen() throws Exception {
        final Catalog catalog = new Catalog(this.temp.newFile("catalog.txt"));
        final String value = "some value to use, \u0433";
        final long pos = Math.max(Math.abs(new Random().nextLong()), 1L);
        final Collection<Catalog.Item> items = new TreeSet<Catalog.Item>();
        items.add(new Catalog.Item(value, pos));
        // @checkstyle MagicNumber (1 line)
        for (int num = 0; num < 1000; ++num) {
            items.add(new Catalog.Item(String.format("foo-%s", num), num));
        }
        catalog.create(items.iterator());
        MatcherAssert.assertThat(catalog.seek(value), Matchers.equalTo(pos));
        MatcherAssert.assertThat(catalog.seek("absent"), Matchers.equalTo(-1L));
    }

    /**
     * Catalog can register value and find it in the iterator.
     * @throws Exception If there is some problem inside
     */
    @Test
    public void registersValueAndFindsItInIterator() throws Exception {
        final Catalog catalog = new Catalog(this.temp.newFile("catalog-2.txt"));
        final String value = "some value to use, \u0433";
        final long pos = Math.max(Math.abs(new Random().nextLong()), 1L);
        final Collection<Catalog.Item> items = new TreeSet<Catalog.Item>();
        final Catalog.Item item = new Catalog.Item(value, pos);
        items.add(item);
        // @checkstyle MagicNumber (1 line)
        for (int num = 0; num < 100; ++num) {
            items.add(new Catalog.Item(String.format("foo-%s", num), num));
        }
        catalog.create(items.iterator());
        MatcherAssert.assertThat(
            IteratorUtils.toArray(catalog.iterator()),
            Matchers.<Object>hasItemInArray(item)
        );
    }

}
