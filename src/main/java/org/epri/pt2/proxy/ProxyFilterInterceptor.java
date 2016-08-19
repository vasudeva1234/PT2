//*********************************************************************************************************************
// ProxyFilterInterceptor.java
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epri.pt2.gui.model.FilterTableModel;
import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;

/**
 * A class for implementing regular expression type filters on requests
 * and responses.
 * @author Tam Do
 *
 */
public class ProxyFilterInterceptor implements ProxyInterceptorInterface {
	private ProxyController controller;
	private FilterTableModel model;

	public ProxyFilterInterceptor(ProxyController proxyController) {
		controller = proxyController;
		model = FilterTableModel.getInstance();
	}

	public void processRequest(MutableBufferedRequest request) {
		if (controller.isFilteringEnabled() && !controller.isFuzzingEnabled()
				&& !controller.isInterceptEnabled()) {
			List<FilterDO> filters = model.getFilters();

			for (FilterDO filter : filters) {
				FilterType type = filter.getType();

				switch (type) {
				case Global:
					request.setHeader(processFilter(request.getHeader(), filter));
					try {
						request.setContent(processFilter(
								request.getDecodedContent(), filter));
					} catch (MessageFormatException e) {
						e.printStackTrace();
					}
					break;
				case GlobalBody:
					try {
						request.setContent(processFilter(
								request.getDecodedContent(), filter));
					} catch (MessageFormatException e) {
					}
					break;
				case GlobalHeader:
					request.setHeader(processFilter(request.getHeader(), filter));
					break;
				case RequestBody:
					try {
						request.setContent(processFilter(
								request.getDecodedContent(), filter));
					} catch (MessageFormatException e) {
					}
					break;
				case RequestHeader:
					request.setHeader(processFilter(request.getHeader(), filter));
					break;
				default:
					break;
				}
			}
		}
	}

	public void processResponse(BufferedRequest request,
			MutableBufferedResponse response) {
		if (controller.isFilteringEnabled() && !controller.isFuzzingEnabled()
				&& !controller.isInterceptEnabled()) {
			List<FilterDO> filters = model.getFilters();

			for (FilterDO filter : filters) {
				FilterType type = filter.getType();

				switch (type) {
				case Global:
					response.setHeader(processFilter(response.getHeader(),
							filter));
					try {
						response.setContent(processFilter(
								response.getDecodedContent(), filter));
					} catch (MessageFormatException e) {
						e.printStackTrace();
					}
					break;
				case GlobalBody:
					try {
						response.setContent(processFilter(
								response.getDecodedContent(), filter));
					} catch (MessageFormatException e) {
						e.printStackTrace();
					}
					break;
				case GlobalHeader:
					response.setHeader(processFilter(response.getHeader(),
							filter));
					break;
				case ResponseBody:
					try {
						response.setContent(processFilter(
								response.getDecodedContent(), filter));
						// String s = new String(response.getDecodedContent(),
						// "UTF-8");
					} catch (MessageFormatException e) {
						e.printStackTrace();
					}
					// } catch (UnsupportedEncodingException e) {
					// e.printStackTrace();
					// }
					break;
				case ResponseHeader:
					response.setHeader(processFilter(response.getHeader(),
							filter));
					break;
				default:
					break;
				}
			}
		}
	}

	private byte[] processFilter(byte[] bytes, FilterDO filter) {
		String s;
		try {
			s = new String(bytes, "UTF-8");

			Pattern p = Pattern.compile(filter.getReplaceRule());
			Matcher m = p.matcher(s);

			if (m.find()) {
				s = m.replaceAll(filter.getReplaceVal());
			}

			s.replaceAll(filter.getReplaceRule(), filter.getReplaceVal());

			return s.getBytes();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// if replacement fails, return original packet
		return bytes;
	}

}
