//*********************************************************************************************************************
// ProxyFuzzerInterceptor.java
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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;

/**
 * A class for implementing an HTTP response fuzzer on the proxy.
 * 
 * @author Tam Do
 * 
 */
public class ProxyFuzzerInterceptor implements ProxyInterceptorInterface {
	// list of fuzzed packets
	Queue<FilterDO> filters;

	public ProxyFuzzerInterceptor() {
		filters = new ConcurrentLinkedQueue<FilterDO>();
	}

	public void processRequest(MutableBufferedRequest request) {
		// do nothing
		return;
	}

	public void queueResponse(FilterDO filter) {
		filters.add(filter);
	}

	public void queueResponses(List<FilterDO> requests) {
		filters.addAll(requests);
	}

	public int getRemainingTests() {
		return filters.size();
	}

	public void processResponse(BufferedRequest request,
			MutableBufferedResponse response) {
		if (ProxyController.getInstance().isFuzzingEnabled()) {
			// check if the filter rule matches the packet we are looking for
			if (filters.size() > 0) {
				FilterDO filter = filters.peek();

				Pattern pattern = Pattern.compile(filter.getReplaceRule());

				try {
					Matcher matcher = pattern.matcher(new String(response
							.getDecodedContent(), "UTF-8"));

					if (matcher.find()) {
						filter = filters.remove();
						response.setContent(filter.getReplaceVal().getBytes());
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (MessageFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
