//*********************************************************************************************************************
// DNP3ProxyController.java
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
import java.net.BindException;
import java.net.InetSocketAddress;

import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.daemon.Server;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.tcp.InterceptingConnectionHandler;

public class DNP3ProxyController {
	private String listenAddress;
	private int listenPort;

	private DNP3ProxyInterceptorController interceptorController;

	private TargetedConnectionHandler interceptHandler;
	ConnectionHandler socksHandler;

	private boolean isEnabled;
	private boolean isInterceptEnabled;

	private Server proxy;
	private final static DNP3ProxyController controller;

	static {
		controller = new DNP3ProxyController();
	}

	private DNP3ProxyController() {
		listenAddress = "0.0.0.0";
		listenPort = 8081;

		setEnabled(false);
		setInterceptEnabled(false);

		// Instantiate the interceptors
		interceptorController = new DNP3ProxyInterceptorController(this);

	}

	public static DNP3ProxyController getInstance() {
		return controller;
	}

	public void start() throws Exception {
		InetSocketAddress listen = new InetSocketAddress(listenAddress,
				listenPort);

		interceptHandler = new InterceptingConnectionHandler(
				interceptorController);

		ConnectionHandler socksHandler = new SocksConnectionHandler(
				interceptHandler, true); // true
		// ->
		// autodetect
		// SOCKS

		try {
			proxy = new Server(listen, socksHandler);
			proxy.start();
			setEnabled(true);
		} catch(BindException e) {
			throw new BindException(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		// TODO
		if (proxy != null) {
			proxy.stop();
			setEnabled(!proxy.isStopped());
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isInterceptEnabled() {
		return isInterceptEnabled;
	}

	public void setInterceptEnabled(boolean isInterceptEnabled) {
		this.isInterceptEnabled = isInterceptEnabled;
	}

	public String getListenAddress() {
		return listenAddress;
	}

	public void setListenAddress(String listenAddress) {
		this.listenAddress = listenAddress;
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public DNP3ProxyInterceptorController getInterceptorController() {
		return interceptorController;
	}

	public void setInterceptorController(
			DNP3ProxyInterceptorController interceptorController) {
		this.interceptorController = interceptorController;
	}

}
