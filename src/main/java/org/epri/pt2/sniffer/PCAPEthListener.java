//*********************************************************************************************************************
// PCAPEthListener.java
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
package org.epri.pt2.sniffer;

import java.io.File;

import org.epri.pt2.listeners.PacketListenerInterface;
import org.epri.pt2.reassembler.TcpReassembler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapClosedException;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;

/**
 * An implementation of an ethernet sniffer class using JNetPcap and libpcap.
 * 
 * @author Tam Do
 * 
 */
public class PCAPEthListener extends AbstractSniffer {
	private String name;
	private StringBuilder errBuf;
	private Pcap pcap;
	private PcapBpfProgram program;
	private String filter;
	private Thread thread;

	private String fileName;

	private TcpReassembler reassembler;
	
	private PCAPEthListener() {
		errBuf = new StringBuilder();
		program = new PcapBpfProgram();
		// filter =
		// "tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0)";
		filter = "tcp port 80 or tcp port 8080 or tcp port 20000";
		types.add(SnifferInterface.MAPPABLE);
	}

	public void setFilter(String filter) {
		if (pcap != null) {
			// try new filter
			if (pcap.compile(program, filter, 0, 0xFFFFFF00) == Pcap.OK) {
				this.filter = filter;
			} else {
				// restore filter
				pcap.compile(program, this.filter, 0, 0xFFFFFF00);
			}
		}
	}

	public String getFilter() {
		return filter;
	}

	public PCAPEthListener(String name) {
		this();
		this.name = name;

	}

	public PCAPEthListener(File f) {
		this();
		fileName = f.getAbsolutePath();
	}

	public boolean isOpen() {
		if (pcap == null) {
			return false;
		}
		return true;
	}

	public void connectOffline() {
		if (fileName != null) {
			
		}
	}

	public void connect() {

		// if a file is found open offline
		if (fileName != null) {
			pcap = Pcap.openOffline(fileName, errBuf);

			if (pcap == null) {
				System.err.printf("Error while opening device for capture: "
						+ errBuf.toString());
				return;
			}
		}
		// else open live
		else {
			pcap = Pcap.openLive(name, 64 * 1024, Pcap.MODE_PROMISCUOUS,
					10 * 1000, errBuf);
		}

		if (pcap == null) {
			System.err.printf(errBuf.toString());
		}

		if (pcap.compile(program, filter, 0, 0xFFFFFF00) != Pcap.OK) {
			System.err.println(pcap.getErr());
			pcap = null;
			return;
		}

		if (pcap.setFilter(program) != Pcap.OK) {
			System.err.println(pcap.getErr());
			pcap = null;
			return;
		}

		thread = new Thread(new PCAPRunner());
		thread.start();
	}

	public void disconnect() {
		reassembler.setRunning(false);
		pcap.breakloop();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pcap.close();
		pcap = null;
	}

	public String getName() {
		return name;
	}

	public boolean isSnifferType(int type) {
		return types.contains(type);
	}

	private class PCAPRunner implements Runnable {

		public void run() {
			reassembler = new TcpReassembler();
			
			// transfer registration of listeners to reassembler
			for (PacketListenerInterface listener : listeners) {
				reassembler.registerListener(listener);
			}

			reassembler.setRunning(true);
			if(fileName != null) {
				pcap.loop(Pcap.LOOP_INFINITE, reassembler, "Packet Dump..");
			}
			else
			{
				PcapPacket packet = new PcapPacket(JMemory.POINTER);
				
				try
				{
					while(pcap.nextEx(packet) == Pcap.NEXT_EX_OK) {
						PcapPacket copy = new PcapPacket(packet);
						reassembler.nextPacket(copy, null);	

					}
				}
				catch (PcapClosedException e) {
					// The pcap stream has closed
					return;
				}
			}
		}
		
	}
}
