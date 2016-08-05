/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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

package org.mobicents.media.control.mgcp.transaction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mobicents.media.control.mgcp.command.MgcpCommand;
import org.mobicents.media.control.mgcp.exception.DuplicateMgcpTransactionException;
import org.mobicents.media.control.mgcp.exception.MgcpTransactionNotFoundException;
import org.mobicents.media.control.mgcp.message.MgcpRequest;
import org.mobicents.media.control.mgcp.message.MgcpRequestType;
import org.mobicents.media.control.mgcp.message.MgcpResponse;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class MgcpTransactionManagerTest {

    private static final String REQUEST = "CRCX 147483653 mobicents/bridge/$@127.0.0.1:2427 MGCP 1.0";
    private static final String RESPONSE = "200 147483653 Successful Transaction";

    @Test
    public void testProcessRemoteTransaction() throws DuplicateMgcpTransactionException, MgcpTransactionNotFoundException {
        // given
        final int transactionId = 147483653;
        final MgcpRequest request = mock(MgcpRequest.class);
        final MgcpResponse response = mock(MgcpResponse.class);
        final MgcpCommand command = mock(MgcpCommand.class);
        final MgcpTransaction transaction = new MgcpTransaction(transactionId);
        final MgcpTransactionProvider txProvider = mock(MgcpTransactionProvider.class);
        final MgcpTransactionManager txManager = new MgcpTransactionManager(txProvider);

        // when - request
        when(request.toString()).thenReturn(REQUEST);
        when(request.isRequest()).thenReturn(true);
        when(request.getRequestType()).thenReturn(MgcpRequestType.CRCX);
        when(request.getTransactionId()).thenReturn(transactionId);
        when(txProvider.provideRemote(transactionId)).thenReturn(transaction);
        txManager.process(request, command);

        // then
        assertTrue(txManager.contains(transactionId));
        verify(txProvider, times(1)).provideRemote(transactionId);
        verify(command, times(1)).execute(request);

        // when - response
        when(response.toString()).thenReturn(RESPONSE);
        when(response.isRequest()).thenReturn(false);
        when(response.getTransactionId()).thenReturn(transactionId);

        txManager.process(response);

        // then
        assertFalse(txManager.contains(transactionId));
    }

    @Test
    public void testProcessLocalTransaction() throws DuplicateMgcpTransactionException, MgcpTransactionNotFoundException {
        // given
        final int initialTransactionId = 0;
        final int finalTransactionId = 147483653;
        final MgcpRequest request = mock(MgcpRequest.class);
        final MgcpResponse response = mock(MgcpResponse.class);
        final MgcpTransaction transaction = new MgcpTransaction(147483653);
        final MgcpTransactionProvider txProvider = mock(MgcpTransactionProvider.class);
        final MgcpTransactionManager txManager = new MgcpTransactionManager(txProvider);

        // when - request
        when(request.toString()).thenReturn(REQUEST);
        when(request.isRequest()).thenReturn(true);
        when(request.getRequestType()).thenReturn(MgcpRequestType.CRCX);
        when(request.getTransactionId()).thenReturn(initialTransactionId, finalTransactionId);
        when(txProvider.provideLocal()).thenReturn(transaction);

        txManager.process(request, null);

        // then
        assertTrue(txManager.contains(finalTransactionId));
        verify(txProvider, times(1)).provideLocal();

        // when - response
        when(response.toString()).thenReturn(RESPONSE);
        when(response.isRequest()).thenReturn(false);
        when(response.getTransactionId()).thenReturn(finalTransactionId);

        txManager.process(response);

        // then
        assertFalse(txManager.contains(finalTransactionId));
    }

    @Test(expected = DuplicateMgcpTransactionException.class)
    public void testProcessRetransmission() throws DuplicateMgcpTransactionException, MgcpTransactionNotFoundException {
        // given
        final int transactionId = 147483653;
        final MgcpRequest request = mock(MgcpRequest.class);
        final MgcpCommand command = mock(MgcpCommand.class);
        final MgcpTransaction transaction = new MgcpTransaction(transactionId);
        final MgcpTransactionProvider txProvider = mock(MgcpTransactionProvider.class);
        final MgcpTransactionManager txManager = new MgcpTransactionManager(txProvider);

        // when - request
        when(request.toString()).thenReturn(REQUEST);
        when(request.isRequest()).thenReturn(true);
        when(request.getRequestType()).thenReturn(MgcpRequestType.CRCX);
        when(request.getTransactionId()).thenReturn(transactionId);
        when(txProvider.provideRemote(transactionId)).thenReturn(transaction);

        txManager.process(request, command);
        txManager.process(request, command);
    }

}