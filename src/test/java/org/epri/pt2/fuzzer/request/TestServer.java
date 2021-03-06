//*********************************************************************************************************************
// TestServer.java
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer implements Runnable {
	private static final int READ_BUFFER_LEN = 1000;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private String serverRecvd = "";
	private String response = "";
	private final int port;
	private Thread currentThread;

	public TestServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			currentThread = Thread.currentThread();
			System.out.format("%d: Server started\n", port);
			serverSocket = new ServerSocket(port);
			clientSocket = serverSocket.accept();
			System.out.format("%d: Got connection\n", port);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()));

			char[] buffer = new char[READ_BUFFER_LEN];
			while (in.read(buffer) != 0) {
				System.out.format("%d: Server recvd: %s\n", port, String.valueOf(buffer));
				serverRecvd += String.valueOf(buffer);
				serverRecvd = serverRecvd.trim();
				out.write(response);
				out.flush();
			}

			System.out.format("%d: Closing sockets", port);
			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't create server socket");
		}
	}

	public String getServerRcvd() {
		String resp = serverRecvd;
		serverRecvd = "";
		return resp;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
	
	public void killServer() {
		try {
			System.out.format("%d: Killing Server", port);
			serverSocket.close();
			clientSocket.close();
			currentThread.join();
			System.out.format("%d: Killed", port);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
