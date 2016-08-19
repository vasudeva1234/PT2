//*********************************************************************************************************************
// FuzzTestResultDO.java
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
package org.epri.pt2.DO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Southwest Research Institute
 *
 */

@Entity
@Table(name = "FUZZTESTRESULTS")
public class FuzzTestResultDO {
	// Setup the column values for the Packet Table.
	@Id
	@GeneratedValue
	@Column(name = "FUZZTESTRESULT_ID")
	private int id;
	
	@Column(name = "FUZZTESTRESULT_RESULT")
	private String result;
	
	@Column(name = "PACKET_FUZZTESTID")
	private int fuzzTestId;
	
	@Column(name = "FUZZTESTRESULT_DATATYPE")
	private String dataType;
	
	// Setup the relationships for the Content Data Table.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID")
	private AbstractPacketDO owner;
	
	protected FuzzTestResultDO() {
	}

	public FuzzTestResultDO(int fuzzTestId, String dataType) {		
		this.fuzzTestId = fuzzTestId;
		this.dataType = dataType;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the fuzz test id
	 */
	public int getFuzzTestId() {
		return fuzzTestId;
	}

	/**
	 * @param fuzzTestId
	 *            the fuzz test id to set
	 */
	public void setFuzzTestId(int fuzzTestId) {
		this.fuzzTestId = fuzzTestId;
	}
	
	/**
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param data type
	 *            the data type to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	/**
	 * @return the result 
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	
	/**
	 * @return the owner
	 */
	public AbstractPacketDO getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(AbstractPacketDO owner) {
		this.owner = owner;
	}
}
