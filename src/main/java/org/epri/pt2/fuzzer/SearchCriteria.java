//*********************************************************************************************************************
// SearchCriteria.java
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
package org.epri.pt2.fuzzer;


/**
 * @author Southwest Research Institute
 *
 */
public class SearchCriteria {
	private final static SearchCriteria searchCriteria;
	
	public String searchTag;
	public String searchData;
	public String searchFuzzValue;

	static {
		searchCriteria = new SearchCriteria();
	}

	private SearchCriteria() {
	};

	/**
	 * Get an instance of the searchCriteria.
	 * @return
	 */
	public static SearchCriteria getInstance() {
		return searchCriteria;
	}
	
	/**
	 * Get the tag to search on.
	 * @return searchTag
	 */
	public String getSearchTag() {
		return searchTag;
	}
	
	/**
	 * Set the tag to search on.
	 * @param searchTag
	 */
	public void setSearchTag(String searchTag) {
		this.searchTag = searchTag;
	}
	
	/**
	 * Get the content data to search on.
	 * @return searchData
	 */
	public String getSearchData() {
		return searchData;
	}
	
	/**
	 * Set the content data to search on.
	 * @param searchData
	 */
	public void setSearchData(String searchData) {
		this.searchData = searchData;
	}
	
	/**
	 * Get the fuzz value to search on.
	 * @return searchFuzzValue
	 */
	public String getSearchFuzzValue() {
		return searchFuzzValue;
	}
	/**
	 * Set the fuzz value to search on.
	 * @param searchFuzzValue
	 */
	
	public void setSearchFuzzValue(String searchFuzzValue) {
		this.searchFuzzValue = searchFuzzValue;
	}

}
