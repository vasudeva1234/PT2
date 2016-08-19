//*********************************************************************************************************************
// FilterDO.java
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

/**
 * A class describing the type of filter, associated replacement rules, and replacement value.
 * This class is used by the ProxyFuzzerInterceptor and the ProxyFilterInterceptor.
 * 
 * @author Tam Do
 *
 */
public class FilterDO {
	private FilterType type;
	private String replaceRule;
	private String replaceVal;

	/**
	 * @return the type
	 */
	public FilterType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FilterType type) {
		this.type = type;
	}

	/**
	 * @return the replaceRule
	 */
	public String getReplaceRule() {
		return replaceRule;
	}

	/**
	 * @param replaceRule
	 *            the replaceRule to set
	 */
	public void setReplaceRule(String replaceRule) {
		this.replaceRule = replaceRule;
	}

	/**
	 * @return the replaceVal
	 */
	public String getReplaceVal() {
		return replaceVal;
	}

	/**
	 * @param replaceVal
	 *            the replaceVal to set
	 */
	public void setReplaceVal(String replaceVal) {
		this.replaceVal = replaceVal;
	}
}
