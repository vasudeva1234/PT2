//*********************************************************************************************************************
// ProxyController.java
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
package org.epri.pt2.proxy;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;

import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.daemon.Server;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;
import org.owasp.proxy.http.server.BufferingHttpRequestHandler;
import org.owasp.proxy.http.server.DefaultHttpRequestHandler;
import org.owasp.proxy.http.server.HttpProxyConnectionHandler;
import org.owasp.proxy.http.server.HttpRequestHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;

/**
 * A class for controlling the state of the proxy.
 * 
 * @author root
 *
 */
public class ProxyController {
	private final static ProxyController controller;
	private static int MAX_CONTENT_SIZE = 10240;

	private ProxyInterceptorController interceptorController;

	private Server proxy;
	private boolean isEnabled = false;
	private boolean isSSLEnabled = false;
	private boolean isFilteringEnabled = false;
	private boolean isFuzzingEnabled = false;
	private boolean isInterceptEnabled = false;

	private String listenAddress;
	private int listenPort;

	private MessageInterceptorImpl messageInterceptor;
	private ProxyFilterInterceptor filterInterceptor;
	private ProxyFuzzerInterceptor fuzzerInterceptor;

	static {
		controller = new ProxyController();
	}

	private ProxyController() {
		listenAddress = "localhost";
		listenPort = 8080;
		setEnabled(false);
		setSSLEnabled(false);
		setFilteringEnabled(false);
		setFuzzingEnabled(false);

		filterInterceptor = new ProxyFilterInterceptor(this);
		fuzzerInterceptor = new ProxyFuzzerInterceptor();

		interceptorController = ProxyInterceptorController.getInstance();
		interceptorController.addListener(filterInterceptor);
		interceptorController.addListener(fuzzerInterceptor);

		messageInterceptor = new MessageInterceptorImpl();

	}

	public static ProxyController getInstance() {
		return controller;
	}

	public ProxyInterceptorController getInterceptorController() {
		return interceptorController;
	}

	public ProxyFuzzerInterceptor getFuzzerInterceptor() {
		return fuzzerInterceptor;
	}

	public void start() throws Exception {

		// Start the proxy

		HttpRequestHandler httpRequestHandler = new DefaultHttpRequestHandler();
		httpRequestHandler = new BufferingHttpRequestHandler(
				httpRequestHandler,
				(BufferedMessageInterceptor) messageInterceptor,
				MAX_CONTENT_SIZE);

		InetSocketAddress listen = new InetSocketAddress(listenAddress,
				listenPort);

		if (!isSSLEnabled) {
			TargetedConnectionHandler httpProxy = new HttpProxyConnectionHandler(
					httpRequestHandler);

			ConnectionHandler socks = new SocksConnectionHandler(httpProxy,
					true);

			try {
				proxy = new Server(listen, socks);
				proxy.start();
				setEnabled(true);

			} catch (BindException e)
			{
				throw new BindException(e.getMessage());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				HttpProxyConnectionHandler httpProxy = new HttpProxyConnectionHandler(
						httpRequestHandler);
				SSLContextSelector contextSelector = new SSLContextSelectorImpl(
						"ca.p12", "swri", "swri");
				TargetedConnectionHandler ssl = new SSLConnectionHandler(
						contextSelector, true, httpProxy);
				httpProxy.setConnectHandler(ssl);
				ConnectionHandler socks = new SocksConnectionHandler(ssl, true);

				proxy = new Server(listen, socks);
				proxy.start();
				setEnabled(true);

			} catch (BindException e) {
				
				// TODO
				throw new BindException(e.getMessage());
			} catch (GeneralSecurityException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		if (proxy != null) {
			proxy.stop();
			setEnabled(!proxy.isStopped());
		}
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the isSSLEnabled
	 */
	public boolean isSSLEnabled() {
		return isSSLEnabled;
	}

	/**
	 * @param isSSLEnabled
	 *            the isSSLEnabled to set
	 */
	public void setSSLEnabled(boolean isSSLEnabled) {
		this.isSSLEnabled = isSSLEnabled;
	}

	/**
	 * @return the listenAddress
	 */
	public String getListenAddress() {
		return listenAddress;
	}

	/**
	 * @param listenAddress
	 *            the listenAddress to set
	 */
	public void setListenAddress(String listenAddress) {
		this.listenAddress = listenAddress;
	}

	/**
	 * @return the listenPort
	 */
	public int getListenPort() {
		return listenPort;
	}

	/**
	 * @param listenPort
	 *            the listenPort to set
	 */
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * @return the isFilteringEnabled
	 */
	public boolean isFilteringEnabled() {
		return isFilteringEnabled;
	}

	/**
	 * @param isFilteringEnabled
	 *            the isFilteringEnabled to set
	 */
	public void setFilteringEnabled(boolean isFilteringEnabled) {
		this.isFilteringEnabled = isFilteringEnabled;
	}

	/**
	 * @return the isFuzzingEnabled
	 */
	public boolean isFuzzingEnabled() {
		return isFuzzingEnabled;
	}

	/**
	 * @param isFuzzingEnabled
	 *            the isFuzzingEnabled to set
	 */
	public void setFuzzingEnabled(boolean isFuzzingEnabled) {
		this.isFuzzingEnabled = isFuzzingEnabled;
	}

	/**
	 * @return the isInterceptEnabled
	 */
	public boolean isInterceptEnabled() {
		return isInterceptEnabled;
	}

	/**
	 * @param isInterceptEnabled
	 *            the isInterceptEnabled to set
	 */
	public void setInterceptEnabled(boolean isInterceptEnabled) {
		this.isInterceptEnabled = isInterceptEnabled;
	}
}
