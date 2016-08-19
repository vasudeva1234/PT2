//*********************************************************************************************************************
// SSLContextSelectorImpl.java
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.owasp.proxy.ssl.SSLContextSelector;

/**
 * A class implementing an SSL connection for the proxy.
 * 
 * @author Tam Do
 *
 */
public class SSLContextSelectorImpl implements SSLContextSelector {
	private SSLContext sslContext = null;

	public SSLContextSelectorImpl(String resource, String storePassword,
			String keyPassword) throws GeneralSecurityException, IOException {

		if (resource == null) {
			resource = "ca.p12";
		}

		KeyStore ks = KeyStore.getInstance("PKCS12");
		InputStream is = new FileInputStream(resource);

		if (is != null) {
			char[] ksp = storePassword.toCharArray();
			ks.load(is, ksp);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			char[] kp = keyPassword.toCharArray();
			kmf.init(ks, kp);
			sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(kmf.getKeyManagers(), null, null);
		}
	}

	/**
	 * This default implementation uses the same certificate for all hosts.
	 * 
	 * @return an SSLSocketFactory generated from the relevant server key
	 *         material
	 */
	public SSLContext select(InetSocketAddress target) {
		if (sslContext == null) {
			throw new NullPointerException("sslContext is null!");
		}
		return sslContext;
	}

}
