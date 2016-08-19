//*********************************************************************************************************************
// SocketHandler.java
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
package org.epri.pt2.fuzzer.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.epri.pt2.DO.AbstractPacketDO;


/**
 * This class handles the process of sending a request and receiving a response
 * over a socket interface.
 * 
 * @author njeirath
 * 
 */
public class SocketHandler extends Thread {
	private static final int SOCKET_READ_TIMEOUT = 5000;
	private static final int READ_BUFFER_SIZE = 10000;
	
	private static ExecutorService executor;

	public enum Status {
		WAITING, RTS, PROCESSING, RESULT_READY, ERROR, CLOSED
	};

	private final Object lock = new Object();
	private Status status;
	private String host;
	private int port;
	private Exception e = null;
	private boolean running;
	private AbstractPacketDO sendPacket;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private char[] results = new char[READ_BUFFER_SIZE];

	private SendEventListener listener;
	
	static {
		executor = Executors.newSingleThreadExecutor();
	}

	public SocketHandler(String host, int port) {
		this.host = host;
		this.port = port;

		running = true;
		status = Status.WAITING;

		this.start();
	}

	public boolean isReadyToSend() {
		synchronized (lock) {
			return (status == Status.WAITING);
		}
	}
	
	public boolean isErroredOrClosed() {
		if ((status == Status.ERROR) || (status == Status.CLOSED)) {
			return true;
		} else {
			return false;
		}
	}

	public void sendRequest(AbstractPacketDO packet,
			SendEventListener listener) {
		synchronized (lock) {
			if (status == Status.WAITING) {
				sendPacket = packet;
				status = Status.RTS;
				this.listener = listener;
			}
		}
	}
	
	public void notifyRequestComplete(AbstractPacketDO packet, SendEventListener listener, RequestResult result) {
		NotifierThread n = new NotifierThread(packet, listener, result);
		executor.execute(n);
	}

	@Override
	public void run() {
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(SOCKET_READ_TIMEOUT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			while (running) {
				try {
					Status s;
					synchronized (lock) {
						s = status;
					}

					if (s == Status.RTS) {
						// Send out packet
						synchronized (lock) {
							status = Status.PROCESSING;
						}

						if (!socket.isConnected()) {
							status = Status.CLOSED;
							e = new Exception("Socket is not connected");
							break;
						}

						results = new char[READ_BUFFER_SIZE];

						String sendVal = new String(sendPacket.getData());
						// System.out.println("Handler sending: " + sendVal);
						out.print(sendVal);
						out.flush();
						in.read(results);

						RequestResult result = new RequestResult();
						result.data = new String(results).trim().toCharArray();
						result.exception = e;
						synchronized (lock) {
							notifyRequestComplete(sendPacket, listener, result);
							status = Status.WAITING;
						}
					}

					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					RequestResult result = new RequestResult();
					result.exception = e;
					synchronized (lock) {
						notifyRequestComplete(sendPacket, listener, result);
						status = Status.WAITING;
					}
				}
			}

			in.close();
			out.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			this.e = e;
			status = Status.ERROR;
			RequestResult result = new RequestResult();
			result.exception = e;
			synchronized (lock) {
				notifyRequestComplete(sendPacket, listener, result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.e = e;
			status = Status.ERROR;
			RequestResult result = new RequestResult();
			result.exception = e;
			synchronized (lock) {
				notifyRequestComplete(sendPacket, listener, result);
			}
		}
	}
}
