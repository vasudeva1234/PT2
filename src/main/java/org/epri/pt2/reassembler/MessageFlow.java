//*********************************************************************************************************************
// MessageFlow.java
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
package org.epri.pt2.reassembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.epri.pt2.DO.AbstractPacketDO;
//import org.epri.pt2.sniffer.EthernetDataPacket;

/**
 * Reassemble message flows from a list of conversations.
 * 
 * @author tdo
 * 
 */
public class MessageFlow {

	/**
	 * TODO Check connInfo for the port, and call the appropriate reassembler.
	 * @param connInfo
	 * @param flow
	 * @return
	 */
	public static List<AbstractPacketDO> processTcpFlow(
			TcpConnectionInfo connInfo, TcpFlow flow) {
		List<TcpLink> inFlow = flow.getRequestFlow();
		List<TcpLink> outFlow = flow.getResponseFlow();

		AbstractReassembler reassembler;

		List<AbstractPacketDO> requestPackets = new ArrayList<AbstractPacketDO>();
		List<AbstractPacketDO> responsePackets = new ArrayList<AbstractPacketDO>();

		/* 
		 * If either source or destination ports for this connection is
		 * DNP3, reassemble it as so.
		 */
		if( (connInfo.getDstPort() == 20000) || 
				(connInfo.getSrcPort() == 20000))
		{
			reassembler = new DNP3Reassembler();
		}
		/* Default to HTTP / HTTPS reassembly */
		else
		{
			reassembler = new HttpReassembler();
		}
		
		for (TcpLink link : inFlow) {

			List<AbstractPacketDO> packets = reassembler.processLink(link,
					connInfo, true);
			requestPackets.addAll(packets);

			TcpLink copy = link;

			do {
				if (!copy.isProcessed()) {
					copy.setFirst(copy);
					break;
				}

			} while ((copy = copy.getNext()) != null);

			if (copy == null) {
				inFlow.remove(link);
			} else {
				link = copy;
			}
		}

		for (TcpLink link : outFlow) {
			List<AbstractPacketDO> packets = reassembler.processLink(link,
					connInfo, false);
			responsePackets.addAll(packets);

			TcpLink copy = link;

			do {
				if (!copy.isProcessed()) {
					copy.setFirst(copy);
					break;
				}

			} while ((copy = copy.getNext()) != null);

			if (copy == null) {
				outFlow.remove(link);
			} else {
				link = copy;
			}
		}

		List<AbstractPacketDO> packets = new ArrayList<AbstractPacketDO>();
		packets.addAll(requestPackets);
		packets.addAll(responsePackets);

		// remove null packets

		packets.removeAll(Collections.singleton(null));

		// reorder and return
		Collections.sort(packets);

		return packets;
	}

}
