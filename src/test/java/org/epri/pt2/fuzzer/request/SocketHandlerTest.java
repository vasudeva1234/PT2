//*********************************************************************************************************************
// SocketHandlerTest.java
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
//
//import junit.framework.TestCase;
//
//import org.epri.pt2.sniffer.EthernetDataPacket;
//
//public class SocketHandlerTest extends TestCase implements SendEventListener {
//	private static final String HOST = "localhost";
//	private static final int PORT = 8439;
//	private static final int READ_BUFFER_LEN = 1000;
//	private static final int READ_WAIT_TIME = 1000;
//
//	private static final String TEST_STRING = "This is a test";
//	private static final String RESPONSE = "Response";
//	private static final String TEST_STRING2 = "This is another test";
//	private static final String RESPONSE2 = "another Response";
//	private static final String TEST_STRING3 = "This is a third test";
//	private static final String RESPONSE3 = "third Response";
//
//	private SocketHandler handler;
//	private TestServer server;
//	private RequestResult result;
//
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		server = new TestServer(PORT);
//		new Thread(server).start();
//
//		handler = new SocketHandler(HOST, PORT);
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	public void testSend() throws UnsupportedEncodingException {
//
//		EthernetDataPacket packet = new EthernetDataPacket("Test1",
//				TEST_STRING.getBytes());
//
//		sendTest(packet, RESPONSE);
//
//		EthernetDataPacket packet2 = new EthernetDataPacket("Test2",
//				TEST_STRING2.getBytes());
//
//		sendTest(packet2, RESPONSE2);
//
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		EthernetDataPacket packet3 = new EthernetDataPacket("Test3",
//				TEST_STRING3.getBytes());
//
//		sendTest(packet3, RESPONSE3);
//	}
//
//	private void sendTest(EthernetDataPacket packet, String response) {
//		server.setResponse(response);
//
//		handler.sendRequest(packet, this);
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
