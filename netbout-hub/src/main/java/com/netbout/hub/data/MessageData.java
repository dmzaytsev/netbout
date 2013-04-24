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
package com.netbout.hub.data;

import com.jcabi.log.Logger;
import com.jcabi.urn.URN;
import com.netbout.hub.BoutDt;
import com.netbout.hub.MessageDt;
import com.netbout.hub.PowerHub;
import com.netbout.hub.inf.InfBout;
import com.netbout.hub.inf.InfIdentity;
import com.netbout.hub.inf.InfMessage;
import com.netbout.inf.notices.MessagePostedNotice;
import com.netbout.inf.notices.MessageSeenNotice;
import com.netbout.spi.Bout;
import com.netbout.spi.Identity;
import com.netbout.spi.Message;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * One message in a bout.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
final class MessageData implements MessageDt {

    /**
     * Hub to work with.
     */
    private final transient PowerHub hub;

    /**
     * Number of the message.
     */
    private final transient Long number;

    /**
     * Bout data.
     */
    private final transient BoutDt bdata;

    /**
     * The date.
     */
    private transient Date date;

    /**
     * The author.
     */
    private transient URN author;

    /**
     * The text.
     */
    private transient String text;

    /**
     * Who already have seen this message, and who haven't?
     */
    private final transient ConcurrentMap<URN, Boolean> seenBy =
        new ConcurrentHashMap<URN, Boolean>();

    /**
     * Public ctor.
     * @param ihub The hub
     * @param num The number of this message
     * @param bout Bout data
     */
    public MessageData(final PowerHub ihub, final Long num, final BoutDt bout) {
        this.hub = ihub;
        assert num != null;
        this.number = num;
        this.bdata = bout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final MessageDt data) {
        return this.getDate().compareTo(data.getDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getNumber() {
        return this.number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(final Date dte) {
        synchronized (this.number) {
            this.date = dte;
            this.hub.make("changed-message-date")
                .synchronously()
                .arg(this.number)
                .arg(this.date)
                .asDefault(true)
                .exec();
        }
        Logger.debug(
            this,
            "#setDate('%s'): set",
            this.date
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate() {
        synchronized (this.number) {
            if (this.date == null) {
                this.date = this.hub.make("get-message-date")
                    .synchronously()
                    .arg(this.number)
                    .exec();
            }
        }
        return this.date;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthor(final URN idnt) {
        synchronized (this.number) {
            this.author = idnt;
            this.hub.make("changed-message-author")
                .synchronously()
                .arg(this.number)
                .arg(this.author)
                .asDefault(true)
                .exec();
        }
        Logger.debug(
            this,
            "#setAuthor('%s'): set for msg #%d",
            this.author,
            this.number
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URN getAuthor() {
        synchronized (this.number) {
            if (this.author == null) {
                this.author = this.hub.make("get-message-author")
                    .synchronously()
                    .arg(this.number)
                    .exec();
            }
        }
        return this.author;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(final String txt) {
        synchronized (this.number) {
            this.text = txt;
            this.hub.make("changed-message-text")
                .synchronously()
                .arg(this.number)
                .arg(this.text)
                .asDefault(true)
                .exec();
        }
        this.hub.infinity().see(
            new MessagePostedNotice() {
                @Override
                public Message message() {
                    return new InfMessage(MessageData.this);
                }
                @Override
                public Bout bout() {
                    return new InfBout(MessageData.this.bdata);
                }
            }
        );
        Logger.debug(
            this,
            "#setText('%s'): set for msg #%d",
            this.text,
            this.number
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        synchronized (this.number) {
            if (this.text == null) {
                this.text = this.hub.make("get-message-text")
                    .synchronously()
                    .arg(this.number)
                    .exec();
            }
        }
        return this.text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSeenBy(final URN identity) {
        synchronized (this.number) {
            if (!this.isSeenBy(identity)) {
                this.hub.make("message-was-seen")
                    .asap()
                    .arg(this.number)
                    .arg(identity)
                    .asDefault(true)
                    .exec();
                this.seenBy.put(identity, true);
                this.hub.infinity().see(
                    new MessageSeenNotice() {
                        @Override
                        public Message message() {
                            return new InfMessage(MessageData.this);
                        }
                        @Override
                        public Identity identity() {
                            return new InfIdentity(identity);
                        }
                    }
                );
            }
        }
        Logger.debug(
            this,
            "#addSeenBy('%s'): set for msg #%d",
            identity,
            this.number
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isSeenBy(final URN identity) {
        synchronized (this.number) {
            if (!this.seenBy.containsKey(identity)) {
                final Boolean status = this.hub.make("was-message-seen")
                    .synchronously()
                    .arg(this.number)
                    .arg(identity)
                    .asDefault(false)
                    .exec();
                this.seenBy.put(identity, status);
            }
        }
        return this.seenBy.get(identity);
    }

}