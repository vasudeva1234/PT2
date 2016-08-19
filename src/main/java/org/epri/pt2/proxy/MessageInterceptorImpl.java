//*********************************************************************************************************************
// MessageInterceptorImpl.java
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

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;

/**
 * A class for intercepting proxy packets.  This class passes handling of the packets
 * off to the ProxyInterceptor controller.
 * 
 * @author Tam Do
 * 
 */
public class MessageInterceptorImpl extends BufferedMessageInterceptor {
	ProxyInterceptorController controller = ProxyInterceptorController
			.getInstance();

	public Action directRequest(final MutableRequestHeader request) {
		return Action.BUFFER;
	}

	public Action directResponse(final RequestHeader request,
			final MutableResponseHeader response) {
		return Action.BUFFER;
	}

	public void processRequest(final MutableBufferedRequest request) {
		controller.processRequest(request);

		// Fix content length
		String contentLength;
		try {
			contentLength = request.getHeader("Content-Length");

			if (contentLength != null) {
				request.setHeader("Content-Length",
						Integer.toString(request.getContent().length));
			}
		} catch (MessageFormatException e) {
			e.printStackTrace();
		}
	}

	public void processResponse(final BufferedRequest request,
			final MutableBufferedResponse response) {
		controller.processResponse(request, response);
		// Fix content length
		String contentLength;
		try {
			contentLength = request.getHeader("Content-Length");

			if (contentLength != null) {
				response.setHeader("Content-Length",
						Integer.toString(response.getContent().length));
			}
		} catch (MessageFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
