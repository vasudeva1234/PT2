//*********************************************************************************************************************
// RequestSenderTest.java
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
//package org.epri.pt2.fuzzer.request;
//
//import java.io.UnsupportedEncodingException;
//import java.net.ConnectException;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import junit.framework.TestCase;
//
//import org.epri.pt2.sniffer.EthernetDataPacket;
//
//public class RequestSenderTest extends TestCase implements SendEventListener {
//	private static final int BAD_PORT = 48820;
//	private static final String HOST = "localhost";
//	private static final int PORT = 8439;
//	private static final int PORT2 = 8441;
//	private static final int READ_WAIT_TIME = 1000;
//
//	private static final String TEST_STRING = "This is a test";
//	private static final String RESPONSE = "Response";
//	private static final String TEST_STRING2 = "This is another test";
//	private static final String RESPONSE2 = "another Response";
//	private static final String TEST_STRING3 = "This is a third test";
//	private static final String RESPONSE3 = "third Response";
//
//	private RequestResult result;
//	private Map<Integer, TestServer> servers;
//	
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		
//		servers = new HashMap<Integer, TestServer>();
//
//		addServer(PORT);
//		addServer(PORT2);
//		
//	}
//	
//	private void addServer(int port) {
//		TestServer server = new TestServer(port);
//		new Thread(server).start();
//		servers.put(port, server);
//	}
//	
//	private void killServer(int port) {
//		TestServer server = servers.get(port);
//		if (server != null) {
//			server.killServer();
//			servers.remove(server);
//		}
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		
//	}
//
//	public void testSend() throws UnsupportedEncodingException {
//		//Send packet to server 1
//		EthernetDataPacket packet = new EthernetDataPacket("Test1",
//				TEST_STRING.getBytes());
//		packet.setDestination(HOST);
//		packet.setDstPort(PORT);
//
//		sendTest(packet, RESPONSE);
//
//		//Send packet to server 2
//		EthernetDataPacket packet2 = new EthernetDataPacket("Test2",
//				TEST_STRING2.getBytes());
//		packet2.setDestination(HOST);
//		packet2.setDstPort(PORT2);
//
//		sendTest(packet2, RESPONSE2);
//		
//		//Try sending packet to non-existent server
//		EthernetDataPacket packet3 = new EthernetDataPacket("TestBad", TEST_STRING.getBytes());
//		packet3.setDestination(HOST);
//		packet3.setDstPort(BAD_PORT);
//		
//		RequestSenderImpl.sendPacket(packet3, this);
//		try {
//			Thread.sleep(READ_WAIT_TIME);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		assertNull(result.data);
//		assertNotNull(result.exception);
//		assertEquals(new ConnectException().getClass().toString(), result.exception.getClass().toString());
//		System.out.println(result.exception);
//		
//		//shutdown a server and try sending packet to it
//		killServer(PORT);
//		EthernetDataPacket packet4 = new EthernetDataPacket("TestBad", TEST_STRING.getBytes());
//		packet4.setDestination(HOST);
//		packet4.setDstPort(PORT);
//		
//		RequestSenderImpl.sendPacket(packet4, this);
//		try {
//			Thread.sleep(READ_WAIT_TIME);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		assertNull(result.data);
//		assertNotNull(result.exception);
//		assertEquals(new ConnectException().getClass().toString(), result.exception.getClass().toString());
//		System.out.println(result.exception);
//		
//		//Restart the server and verify connection can be reestablished
//		addServer(PORT);
//		EthernetDataPacket packet5 = new EthernetDataPacket("Test1",
//				TEST_STRING.getBytes());
//		packet5.setDestination(HOST);
//		packet5.setDstPort(PORT);
//
//		sendTest(packet5, RESPONSE);
//	}
//
//
//	private void sendTest(EthernetDataPacket packet, String response) {
//		TestServer server = servers.get(packet.getDstPort());
//		if (server == null) {
//			System.err.println("Couldn't find server");
//			fail();
//		}
//		server.setResponse(response);
//
//		RequestSenderImpl.sendPacket(packet, this);
//
//		System.out.println("Waiting " + READ_WAIT_TIME / 1000 + " seconds");
//		try {
//			Thread.sleep(READ_WAIT_TIME);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		assertEquals(new String(packet.getData()), server.getServerRcvd());
//		assertEquals(server.getResponse(), new String(result.data));
//	}
//
//	public void sendCompleted(EthernetDataPacket packet, RequestResult result) {
//		this.result = result;
//
//	}
//
//}
