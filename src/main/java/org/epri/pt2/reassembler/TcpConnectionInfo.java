//*********************************************************************************************************************
// TcpConnectionInfo.java
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

import java.net.InetAddress;

/**
 * A class which represents a tcp/ip connection.
 * 
 * @author Tam Do
 *
 */
public class TcpConnectionInfo {
	private InetAddress srcIp;
	private InetAddress dstIp;
	private int srcPort;
	private int dstPort;
	private String srcMac;
	private String dstMac;

	public TcpConnectionInfo(InetAddress srcIp, InetAddress dstIp, int srcPort,
			int dstPort, String srcMac, String dstMac) {
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcMac = srcMac;
		this.dstMac = dstMac;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + srcPort;
		hash = 31 * hash + dstPort;
		hash = 31 * hash + (null == srcIp ? 0 : srcIp.hashCode());
		hash = 31 * hash + (null == dstIp ? 0 : srcIp.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (o.getClass() != this.getClass())) {
			return false;
		}

		TcpConnectionInfo keyPair = (TcpConnectionInfo) o;

		if (srcIp.equals(keyPair.getSrcIp())
				&& dstIp.equals(keyPair.getDstIp())
				&& (srcPort == keyPair.getSrcPort())
				&& (dstPort == keyPair.getDstPort())) {
			return true;
		}

		return false;
	}

	public String getSrcMac() {
		return srcMac;
	}

	public String getDstMac() {
		return dstMac;
	}

	public void setSrcMac(String mac) {
		this.srcMac = mac;
	}

	public void setDstMac(String mac) {
		this.dstMac = mac;
	}

	/**
	 * @return the srcIp
	 */
	public InetAddress getSrcIp() {
		return srcIp;
	}

	/**
	 * @param srcIp
	 *            the srcIp to set
	 */
	public void setSrcIp(InetAddress srcIp) {
		this.srcIp = srcIp;
	}

	/**
	 * @return the dstIp
	 */
	public InetAddress getDstIp() {
		return dstIp;
	}

	/**
	 * @param dstIp
	 *            the dstIp to set
	 */
	public void setDstIp(InetAddress dstIp) {
		this.dstIp = dstIp;
	}

	/**
	 * @return the srcPort
	 */
	public int getSrcPort() {
		return srcPort;
	}

	/**
	 * @param srcPort
	 *            the srcPort to set
	 */
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	/**
	 * @return the dstPort
	 */
	public int getDstPort() {
		return dstPort;
	}

	/**
	 * @param dstPort
	 *            the dstPort to set
	 */
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
}
