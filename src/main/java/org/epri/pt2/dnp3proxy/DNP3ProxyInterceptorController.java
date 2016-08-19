//*********************************************************************************************************************
// DNP3ProxyInterceptorController.java
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
package org.epri.pt2.dnp3proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.DNP3LinkHeader;
import org.owasp.proxy.tcp.StreamHandle;
import org.owasp.proxy.tcp.StreamInterceptor;

public class DNP3ProxyInterceptorController implements
		StreamInterceptor<InetSocketAddress, InetSocketAddress> {

	private List<DNP3ProxyInterceptorInterface> listeners;

	private static Logger logger = Logger.getAnonymousLogger();

	private HashSet<StreamHandle> handlers = new HashSet<StreamHandle>();

	private HashMap<DNP3Flow, DNP3ApplicationPacketDO> flowMap;

	private DNP3ProxyController controller;

	public DNP3ProxyInterceptorController(DNP3ProxyController controller) {
		this.controller = controller;

		flowMap = new HashMap<DNP3Flow, DNP3ApplicationPacketDO>();

		listeners = new ArrayList<DNP3ProxyInterceptorInterface>();
	}

	public void addListener(DNP3ProxyInterceptorInterface listener) {
		listeners.add(listener);
	}

	public void removeListener(DNP3ProxyInterceptorInterface listener) {
		listeners.remove(listener);
	}

	public void connected(StreamHandle clientServer, StreamHandle serverClient,
			InetSocketAddress clientLabel, InetSocketAddress serverLabel) {
		logger.info("Connected " + clientLabel + " to " + serverLabel);
		if (!handlers.contains(clientServer)) {
			handlers.add(clientServer);
		} else {
			logger.info("Connect called twice for the same handler");
		}
		if (!handlers.contains(serverClient)) {
			handlers.add(serverClient);
		} else {
			logger.info("Connect called twice for the same handler");
		}
	}

	public void received(StreamHandle handle, byte[] b, int off, int len) {
		logger.info("Receved a packet");
		
		/**
		 * Parse initial packet information to determine what to do with it
		 */
		DNP3LinkHeader linkHeader = new DNP3LinkHeader(Arrays.copyOfRange(b,
				off, len));

		/*
		 * Have we seen this conversation stream before? If so, lets add to it
		 */
		DNP3Flow flow = new DNP3Flow(linkHeader.getSource(),
				linkHeader.getDestination());

		if (!flowMap.containsKey(flow)) {
			flowMap.put(flow, new DNP3ApplicationPacketDO());
		}

		DNP3ApplicationPacketDO appPacket = flowMap.get(flow);

		if (appPacket.addData(Arrays.copyOfRange(b, off, len))) {
			if (appPacket.isAppPacket()) {
				byte[] data;

				if (controller.isInterceptEnabled()) {
					for (DNP3ProxyInterceptorInterface listener : listeners) {
						listener.processAppPacket(appPacket);
					}
				}

				data = appPacket.updatePacket();

				try {
					handle.write(data, 0, data.length);
				} catch (IOException ioe) {
					logger.info(handle + ": error writing to the output: "
							+ ioe);
				}

				appPacket.initialize();

				/*
				 * Sometimes multiple DNP3 packets are packed into one, this
				 * makes sure that we process all of these as well.
				 */
				while (appPacket.getRemainingData().length > 0) {
					if (appPacket.addData(appPacket.getRemainingData())) {
						/*
						 * Our criteria for replacing the packet is it is
						 * greater than or equal to 1077 bytes
						 */
						
						if (controller.isInterceptEnabled()) {
							for (DNP3ProxyInterceptorInterface listener : listeners) {
								listener.processAppPacket(appPacket);
							}
						}

						data = appPacket.updatePacket();

						try {
							handle.write(data, 0, data.length);
						} catch (IOException ioe) {
							logger.info(handle
									+ ": error writing to the output: " + ioe);
						}

						/* reset the application packet */
						appPacket.initialize();
					}
				}
			} else {
				byte[] data;

				if (controller.isInterceptEnabled()) {
					for (DNP3ProxyInterceptorInterface listener : listeners) {
						listener.processAppPacket(appPacket);
					}
				}

				data = appPacket.updatePacket();

				try {
					handle.write(data, 0, data.length);
				} catch (IOException ioe) {
					logger.info(handle + ": error writing to the output: "
							+ ioe);
				}

				// Reset the packet
				appPacket.initialize();
			}
		}
	}

	public void readException(StreamHandle handle, IOException ioe) {
		logger.info(handle + ": error reading: " + ioe);
		if (!handlers.contains(handle))
			logger.info("readException called for nonexistent handler");
	}

	public void inputClosed(StreamHandle handle) {
		logger.info(handle + " : input closed, closing output");
		handle.close();
		if (handlers.contains(handle)) {
			handlers.remove(handle);
		} else {
			logger.info("Closed called twice for the same handler");
		}
	}

}
