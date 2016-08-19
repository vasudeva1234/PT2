//*********************************************************************************************************************
// HttpUtils.java
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
package org.epri.pt2.reassembler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A set of utility methods for parsing an HTTP packet.
 * 
 * @author Tam Do
 *
 */
public class HttpUtils {
	public static enum MessageType {
		Request, Response
	}

	/**
	 * Given a byte array of a content stream and an offset return all headers.
	 * 
	 * @param content
	 * @param offset
	 * @return
	 */
	public static List<String> getHeaders(String content) {
		boolean isValid = false;

		String lines[] = content.split("\\n");

		List<String> headers = new ArrayList<String>();

		for (String line : lines) {
			if (line.contentEquals("\r")) {
				isValid = true;
				break;
			}

			headers.add(line.trim());
		}

		if (isValid) {
			return headers;
		} else {
			return null;
		}
	}

	public static int getContentSize(String content) {

		String[] chunk = content.split("\r\n\r\n");
		if (chunk.length < 2) {
			return -1;
		}

		return chunk[1].length();
	}

	/**
	 * For a given string containing the Content-Length header return the
	 * content-length field.
	 * 
	 * @param header
	 * @return
	 */
	public static int getHeaderContentLength(String header) {
		header = header.toLowerCase();

		String[] content = header.split(":\\s");
		return Integer.parseInt(content[1].replaceAll("[\n\r]", ""));
	}

	/**
	 * Given a byte array containing a content stream find the location of the
	 * first HTTP Request header.
	 * 
	 * @param content
	 * @return
	 */
	public static int findRequestHeader(String content) {
		String regex = "(OPTIONS|GET|HEAD|POST|PUT|DELETE|TRACE|CONNECT)\\s\\S+\\s(HTTP)/1\\.(1|0)";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.start();
		} else {
			return -1;
		}
	}

	/**
	 * Given a byte array containing a content stream find the location of the
	 * first HTTP Response header.
	 * 
	 * @param content
	 * @return
	 */
	public static int findResponseHeader(String content) {
		String regex = "HTTP/1\\.(0|1)\\s\\d{3}\\s(\\w+)";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.start();
		} else {
			return -1;
		}
	}
}
