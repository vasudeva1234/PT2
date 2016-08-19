//*********************************************************************************************************************
// FuzzerHelper.java
//
// Copyright 2014 ELECTRIC POWER RESEARCH INSTITUTE, INC. All rights reserved.
//
// PT2 ("this software") is licensed under BSD 3-Clause license.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
// following conditions are met:
//
// •    Redistributions of source code must retain the above copyright  notice, this list of conditions and
//      the following disclaimer.
//
// •    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
//      the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// •    Neither the name of the Electric Power Research Institute, Inc. (“EPRI”) nor the names of its contributors
//      may be used to endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL EPRI BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
// OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
//
//
//*********************************************************************************************************************
//
//  Code Modification History:
//  -------------------------------------------------------------------------------------------------------------------
//  11/20/2012 - Tam T. Do, Southwest Research Institute (SwRI)
//       Generated original version of source code.
//  10/22/2014 - Tam T. Do, Southwest Research Institute (SwRI)
//       Added DNP3 software capabilities.
//*********************************************************************************************************************
//
package org.epri.pt2.fuzzer;

import java.util.List;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.fuzzer.request.RequestResult;
import org.epri.pt2.fuzzer.request.RequestSenderImpl;
import org.epri.pt2.fuzzer.request.SendEventListener;
import org.epri.pt2.proxy.FilterDO;
import org.epri.pt2.proxy.ProxyController;
import org.epri.pt2.proxy.ProxyFuzzerInterceptor;
//import org.epri.pt2.sniffer.EthernetDataPacket;

/**
 * Provides helper methods for sending request and response fuzz packets.
 * 
 * @author Tam Do
 * 
 */
public class FuzzerHelper implements SendEventListener {
	private final Object lock = new Object();

	// private EthernetDataPacket packet;
	private RequestResult result;

	private enum MessageState {
		REQUEST_SENT, RESPONSE_RECEIVED
	}

	private ProxyFuzzerInterceptor responseInterceptor;

	private MessageState state;

	public FuzzerHelper() {
		responseInterceptor = ProxyController.getInstance()
				.getFuzzerInterceptor();
	}

	public RequestResult sendRequest(AbstractPacketDO packet) {
		synchronized (lock) {
			state = MessageState.REQUEST_SENT;
			RequestSenderImpl.sendPacket(packet, this);
			while (state != MessageState.RESPONSE_RECEIVED) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			return result;
		}
	}

	public void queueResponse(FilterDO filter) {
		responseInterceptor.queueResponse(filter);
	}

	public void queueResponses(List<FilterDO> filters) {
		responseInterceptor.queueResponses(filters);
	}

	public int getRemainingResponses() {
		return responseInterceptor.getRemainingTests();
	}

	public void sendCompleted(AbstractPacketDO packet, RequestResult result) {
		synchronized (lock) {
			// this.packet = packet;
			this.result = result;
			state = MessageState.RESPONSE_RECEIVED;
			lock.notify();
		}
	}
}
