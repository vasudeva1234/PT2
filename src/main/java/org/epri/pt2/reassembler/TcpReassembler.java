//*********************************************************************************************************************
// TcpReassembler.java
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.listeners.PacketListener;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 * Reassembles tcp packages into directional port/ip pair flows. Currently
 * packages are segmented using time-based detection.
 * 
 * @author Tam Do
 */
public class TcpReassembler extends PacketListener implements
		PcapPacketHandler<Object> {

//	static Logger logger = Logger
	private volatile Map<TcpConnectionInfo, TcpFlow> flowMap = new ConcurrentHashMap<TcpConnectionInfo, TcpFlow>();

	/* timeout in ms */

	private Thread packetThread;
	
	private boolean isRunning;

	/**
	 * Creates a tcp reassembler instance. The reassembler starts a packetThread
	 * instance which periodically reconstructs http packets.
	 */
	public TcpReassembler() {
		createPacketThread();
		packetThread.start();
	}

	private void createPacketThread() {
		packetThread = new Thread(new Runnable() {

			public void run() {
				while (isRunning) {
					// TODO: remove stale packets

					for (TcpConnectionInfo connectionInfo : flowMap.keySet()) {

						TcpFlow flow = flowMap.get(connectionInfo);

						/**
						 * Processes the packets...
						 */
						List<AbstractPacketDO> packets = MessageFlow
								.processTcpFlow(connectionInfo, flow);

						for (AbstractPacketDO packet : packets) {
							if(packet != null) {
								notifyListeners(packet);
								// TODO: convert to logger
//								System.out.println(packet.getSource() + " ---> "
//										+ packet.getDestination());
								
//								if(packet instanceof DNP3ApplicationPacketDO) {
//									DNP3ApplicationPacketDO appPacket = (DNP3ApplicationPacketDO)packet;
//									
//									if(appPacket.isAppPacket()) {
////										System.out.println("->AppPacket");
//									}
//								}
								
//								System.out.println(FormatUtils.hexdump(packet
//										.getData()));
							}
//							else
//							{
////								System.out.println("Null Packet Received");
//							}


						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Given a fragmented packet, attempt to reassemble the packet stream.
	 * 
	 * @param packet
	 * @throws Exception
	 */
	public void reassemblePacket(JPacket packet) throws Exception {
		Ip4 ip4 = new Ip4();
		Ip6 ip6 = new Ip6();
		Tcp tcp = new Tcp();

		long length = 0;
		if (packet.hasHeader(ip4) && packet.hasHeader(tcp)) {
			length = ip4.getPayloadLength() - tcp.getLength();

		} else if (packet.hasHeader(ip6)) {
			length = ip4.getPayloadLength() - tcp.getLength();

		} else {
			return;
		}

		if (length == 0) {
			return;
		}

		reassembleTcp(packet, tcp.seq(), tcp.ack(), length, tcp.getPayload(),
				tcp.getPayloadLength(), tcp.flags_SYN());
	}

	/**
	 * Reassemble a fragmented tcp packet
	 * 
	 * @param packet
	 * @param sequence
	 * @param ack_num
	 * @param length
	 * @param data
	 * @param dataLength
	 * @param synflag
	 * @throws Exception
	 */
	public void reassembleTcp(JPacket packet, long sequence, long ack_num,
			long length, byte[] data, int dataLength, boolean synflag)
			throws Exception {

		Ip4 ip4 = new Ip4();
		Ip6 ip6 = new Ip6();

		InetAddress srcIp = null;
		InetAddress dstIp = null;

		int srcPort = -1;
		int dstPort = -1;

		String srcMac = "";
		String dstMac = "";

		Ethernet ethernet = new Ethernet();

		if (packet.hasHeader(ethernet)) {
			srcMac = FormatUtils.mac(ethernet.source());
			dstMac = FormatUtils.mac(ethernet.destination());
		}

		/* if we have an incomplete tcp packet then discard the packet... */
		if (dataLength < length) {
			return;
		}

		/* check if the packet has a ip header and get the source ip */
		if (packet.hasHeader(ip4)) {
			srcIp = InetAddress.getByAddress(ip4.source());
			dstIp = InetAddress.getByAddress(ip4.destination());

		} else if (packet.hasHeader(ip6)) {
			srcIp = InetAddress.getByAddress(ip6.source());
			dstIp = InetAddress.getByAddress(ip6.destination());
		}

		/* if the packet does not have an ip header then discard the packet */
		if (srcIp == null || dstIp == null) {
			return;
		}

		Tcp tcp = new Tcp();

		if (packet.hasHeader(tcp)) {
			srcPort = tcp.source();
			dstPort = tcp.destination();

			TcpConnectionInfo connectionInfo;

			boolean isRequest = false;

			/**
			 * HTTP:80, HTTP:8080, HTTPS:443, DNP3:20000
			 */
			if (dstPort == 80 || dstPort == 8080 ||  dstPort == 443 || dstPort == 20000) {
				isRequest = true;
				//connectionInfo = new TcpConnectionInfo(srcIp, dstIp, srcPort,
				//		dstPort, srcMac, dstMac);
			} //else {
//				connectionInfo = new TcpConnectionInfo(dstIp, srcIp, dstPort,
//						srcPort, dstMac, srcMac);
//			}
			
			connectionInfo = new TcpConnectionInfo(srcIp, dstIp, srcPort,
					dstPort, srcMac, dstMac);

			// construct a fragment
			TcpFragment f = new TcpFragment();
			f.setSeq(sequence);
			f.setLen(dataLength);
			f.setAck(tcp.ack());
			f.setData(tcp.getPayload());
			f.setTimestamp(System.currentTimeMillis());

			/* check if we have seen this IP/Port pair before */
			if (flowMap.containsKey(connectionInfo)) {
				TcpFlow flow = flowMap.get(connectionInfo);

				List<TcpLink> links;
				if (isRequest) {
					links = flow.getRequestFlow();
				} else {
					links = flow.getResponseFlow();
				}

				for (TcpLink link : links) {
					link = link.getFirst();

					if (link.add(f)) {
						return;
					}
				}

				// if unable to add link to any existing chain, create a new one
				TcpLink link = new TcpLink(f);
				links.add(link);

				// merge(links);
				return;
			}
			// if not create and add
			else {
				List<TcpLink> links = new CopyOnWriteArrayList<TcpLink>();

				TcpLink link = new TcpLink(f);
				links.add(link);

				TcpFlow flow = new TcpFlow();

				if (isRequest) {
					flow.setRequestFlow(links);
				} else {
					flow.setResponseFlow(links);
				}

				flowMap.put(connectionInfo, flow);
				return;
			}
		}
	}

	// TODO: check if this algorithm works..
	private void merge(List<TcpLink> links) {

		// O(n^2) ouch...
		for (TcpLink link : links) {

			if (link == null) {
				continue;
			}

			for (int i = 0; i < links.size(); i++) {
				if (links.get(i) == null) {
					continue;
				} else if (link.equals(links.get(i))) {
					continue;
				} else {
					if (link.add(links.get(i))) {
						links.set(i, null);
					}
				}
			}
		}

		// remove all null links
		links.removeAll(Collections.singleton(null));
	}

	/**
	 * This method is automatically called when we receive a packet
	 */
	public void nextPacket(PcapPacket packet, Object arg1) {
		try {
			reassemblePacket(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param isRunning the isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
