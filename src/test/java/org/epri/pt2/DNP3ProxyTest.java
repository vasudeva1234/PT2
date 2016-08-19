//*********************************************************************************************************************
// DNP3ProxyTest.java
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
package org.epri.pt2;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.DNP3LinkHeader;
import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.daemon.Server;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.tcp.InterceptingConnectionHandler;
import org.owasp.proxy.tcp.StreamHandle;
import org.owasp.proxy.tcp.StreamInterceptor;

public class DNP3ProxyTest extends TestCase {
	public class DNP3Flow {
		private int source;
		private int destination;

		public DNP3Flow(int source, int destination) {
			this.setSource(source);
			this.setDestination(destination);
		}

		public int getSource() {
			return source;
		}

		public void setSource(int source) {
			this.source = source;
		}

		public int getDestination() {
			return destination;
		}

		public void setDestination(int destination) {
			this.destination = destination;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}

			if (other instanceof DNP3Flow) {
				DNP3Flow flow_other = (DNP3Flow) other;
				if ((this.getSource() == flow_other.getSource())
						&& (this.getDestination() == flow_other
								.getDestination())) {
					return true;
				}
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 31 + source;
			hash = hash * 31 + destination;
			return hash;
		}
	}

	private static Logger logger = Logger.getAnonymousLogger();

	private InetSocketAddress listen;

	private TargetedConnectionHandler ch;

	private HashSet<StreamHandle> handlers = new HashSet<StreamHandle>();

	private HashMap<DNP3Flow, DNP3ApplicationPacketDO> flowMap = new HashMap<DNP3Flow, DNP3ApplicationPacketDO>();

	boolean spoofResponse = false;

	public DNP3ProxyTest(String name) {
		super(name);
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private byte[] spoofData;

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	protected void setUp() throws Exception {
		super.setUp();
		File dnp3File = new File(ClassLoader.getSystemClassLoader().getResource("packets/dnp3_11_byte_packet.bin").getPath());

		spoofData = FileUtils.readFileToByteArray(dnp3File);

		listen = new InetSocketAddress("localhost", 8080);
		StreamInterceptor<InetSocketAddress, InetSocketAddress> si = new StreamInterceptor<InetSocketAddress, InetSocketAddress>() {
			public void connected(StreamHandle cs, StreamHandle sc,
					InetSocketAddress cl, InetSocketAddress sl) {
				logger.info("Connected " + cl + " to " + sl);
				if (!handlers.contains(cs)) {
					handlers.add(cs);
				} else {
					fail("Connect called twice for the same handler");
				}
				if (!handlers.contains(sc)) {
					handlers.add(sc);
				} else {
					fail("Connect called twice for the same handler");
				}
			}

			public void inputClosed(StreamHandle handle) {
				logger.info(handle + " : input closed, closing output");
				handle.close();
				if (handlers.contains(handle)) {
					handlers.remove(handle);
				} else {
					fail("Closed called twice for the same handler");
				}
			}

			public void readException(StreamHandle handle, IOException ioe) {
				logger.info(handle + ": error reading: " + ioe);
				if (!handlers.contains(handle))
					fail("readException called for nonexistent handler");
			}

			/**
			 * StreamHandle handle - An instance of RelayInterceptor This can be
			 * used to identify the direction of the connection
			 */
			public void received(StreamHandle handle, byte[] b, int off, int len) {

				// logger.info(handle + ": received '"
				// + bytesToHex(Arrays.copyOfRange(b, off, len)));
				//
				// try {
				// handle.write(b, off, len);
				// } catch (IOException ioe) {
				// logger.info(handle + ": error writing to the output: "
				// + ioe);
				// }

				/**
				 * Parse initial packet information to determine what to do with
				 * it
				 */
				DNP3LinkHeader linkHeader = new DNP3LinkHeader(
						Arrays.copyOfRange(b, off, len));

				/*
				 * Have we seen this conversation stream before? If so, lets add
				 * to it
				 */
				DNP3Flow flow = new DNP3Flow(linkHeader.getSource(),
						linkHeader.getDestination());

				if (!flowMap.containsKey(flow)) {
					flowMap.put(flow, new DNP3ApplicationPacketDO());
				}

				DNP3ApplicationPacketDO appPacket = flowMap.get(flow);

				if (appPacket.addData(Arrays.copyOfRange(b, off, len))) {
					logger.info("We received a DNP3 transport packet or app packet");
					if (appPacket.isAppPacket()) {
						logger.info("This is an app packet");

						byte[] appPacketData;

						/*
						 * Our criteria for replacing the packet is it is
						 * greater than or equal to 1077 bytes
						 */
						if (appPacket.getData().length >= 1077) {
							/*
							 * Perform a little packet reconstruction
							 */
							logger.info("spoofing");
							appPacket.setData(spoofData);

							appPacketData = appPacket.updatePacket();

						}
						/* Otherwise this is the packet we want to spoof */
						else {
							appPacketData = appPacket.updatePacket();

							logger.info("Spoofing response");
						}

						try {
							handle.write(appPacketData, 0, appPacketData.length);
						} catch (IOException ioe) {
							logger.info(handle
									+ ": error writing to the output: " + ioe);
						}

						appPacket.initialize();

						/*
						 * Sometimes multiple DNP3 packets are packed into one,
						 * this makes sure that we process all of these as well.
						 */
						while (appPacket.getRemainingData().length > 0) {
							if (appPacket.addData(appPacket.getRemainingData())) {

								/*
								 * add the completed packet to the list of
								 * packets
								 */
								/*
								 * Our criteria for replacing the packet is it
								 * is greater than or equal to 1077 bytes
								 */
								if (appPacket.getData().length >= 1077) {
									/*
									 * Perform a little packet reconstruction
									 */
									logger.info("spoofing");
									appPacket.setData(spoofData);

									appPacketData = appPacket.updatePacket();

								}
								/* Otherwise this is the packet we want to spoof */
								else {
									appPacketData = appPacket.updatePacket();

									logger.info("Spoofing response");
								}

								try {
									handle.write(appPacketData, 0,
											appPacketData.length);
								} catch (IOException ioe) {
									logger.info(handle
											+ ": error writing to the output: "
											+ ioe);
								}

								/* reset the application packet */
								appPacket.initialize();
							}
						}
					} else {
						logger.info("Plain message");
						try {
							handle.write(appPacket.getData(), 0,
									appPacket.getData().length);
						} catch (IOException ioe) {
							logger.info(handle
									+ ": error writing to the output: " + ioe);
						}

						// Reset the packet
						appPacket.initialize();
					}
				}

				if (!handlers.contains(handle))
					fail("receive called for nonexistent handler");

			}

		};
		ch = new InterceptingConnectionHandler(si);

		ConnectionHandler socks = new SocksConnectionHandler(ch, true); // true
																		// ->
																		// autodetect
																		// SOCKS
		Server proxy = new Server(listen, socks);
		
		proxy.start();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAll() {
		//while (true)
		//	;
		// TODO
	}
}
