/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2015, Telestax Inc and individual contributors
 * by the @authors tag. 
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

package org.mobicents.media.server.impl.srtp;

import org.junit.Test;

import junit.framework.Assert;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class DtlsHandlerTest {

    private static final byte[] HELLO_CLIENT = { 0x16, (byte) 0xfe, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, (byte) 0x96, 0x01, 0x00, 0x00, (byte) 0x8a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x8a,
            (byte) 0xfe, (byte) 0xfd, (byte) 0xb0, 0x6a, 0x76, (byte) 0xb0, 0x46, (byte) 0xf3, 0x76, 0x1b, 0x43, 0x28,
            (byte) 0xa9, 0x77, (byte) 0xd3, (byte) 0xc8, 0x65, 0x56, 0x3d, (byte) 0x84, (byte) 0xf9, 0x60, (byte) 0x92,
            (byte) 0x8f, 0x73, 0x15, 0x4e, 0x5b, 0x46, 0x5a, (byte) 0x93, (byte) 0x9d, (byte) 0xfb, 0x0d, 0x00, 0x00, 0x00,
            0x1e, (byte) 0xc0, 0x2b, (byte) 0xc0, 0x2f, 0x00, (byte) 0x9e, (byte) 0xcc, 0x14, (byte) 0xcc, 0x13, (byte) 0xc0,
            0x0a, (byte) 0xc0, 0x14, 0x00, 0x39, (byte) 0xc0, 0x09, (byte) 0xc0, 0x13, 0x00, 0x33, 0x00, (byte) 0x9c, 0x00,
            0x35, 0x00, 0x2f, 0x00, 0x0a, 0x01, 0x00, 0x00, 0x42, (byte) 0xff, 0x01, 0x00, 0x01, 0x00, 0x00, 0x17, 0x00, 0x00,
            0x00, 0x23, 0x00, 0x00, 0x00, 0x0d, 0x00, 0x16, 0x00, 0x14, 0x06, 0x01, 0x06, 0x03, 0x05, 0x01, 0x05, 0x03, 0x04,
            0x01, 0x04, 0x03, 0x03, 0x01, 0x03, 0x03, 0x02, 0x01, 0x02, 0x03, 0x00, 0x0e, 0x00, 0x07, 0x00, 0x04, 0x00, 0x02,
            0x00, 0x01, 0x00, 0x00, 0x0b, 0x00, 0x02, 0x01, 0x00, 0x00, 0x0a, 0x00, 0x06, 0x00, 0x04, 0x00, 0x17, 0x00, 0x18 };

    private static final byte[] RTP_PACKET = new byte[] { (byte) 0x80, 0x08, 0x6a, 0x6c, (byte) 0xc1, (byte) 0xab, 0x74,
            (byte) 0x8d, (byte) 0xb2, (byte) 0xe2, (byte) 0x83, 0x69, 0x57, 0x57, 0x57, 0x57, 0x57, 0x57, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
            0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54 };

    @Test
    public void testCanHandle() {
        // given
        DtlsHandler dtlsHandler = new DtlsHandler();

        // when
        boolean canHandleHelloClient = dtlsHandler.canHandle(HELLO_CLIENT);
        boolean canHandleRtp = dtlsHandler.canHandle(RTP_PACKET);

        // then
        Assert.assertTrue(canHandleHelloClient);
        Assert.assertFalse(canHandleRtp);
    }

}
