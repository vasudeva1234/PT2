//*********************************************************************************************************************
// AbstractReassembler.java
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
import java.util.Date;
import java.util.List;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.reassembler.HttpUtils.MessageType;

public abstract class AbstractReassembler {

	public List<AbstractPacketDO> processLink(TcpLink link, 
			TcpConnectionInfo connInfo, boolean isRequest)
			{
				List<AbstractPacketDO> packets = new ArrayList<AbstractPacketDO>();
				
				return packets;
			}
	
	public void addConnectionInfo(AbstractPacketDO packet,
			TcpConnectionInfo info, boolean isRequest) {
		packet.setTimestamp(new Date(packet.getFragment().getTimestamp()));

		if (isRequest) {
			packet.setMacDst(info.getDstMac());
			packet.setMacSrc(info.getSrcMac());
			packet.setDestination(info.getDstIp().toString().substring(1));
			packet.setSource(info.getSrcIp().toString().substring(1));
			packet.setSrcPort(info.getSrcPort());
			packet.setDstPort(info.getDstPort());
			packet.setType(MessageType.Request);
		} else {
			packet.setMacDst(info.getDstMac());
			packet.setMacSrc(info.getSrcMac());
			packet.setDestination(info.getDstIp().toString().substring(1));
			packet.setSource(info.getSrcIp().toString().substring(1));
			packet.setSrcPort(info.getDstPort());
			packet.setDstPort(info.getSrcPort());
			packet.setType(MessageType.Response);
		}
	}
}
