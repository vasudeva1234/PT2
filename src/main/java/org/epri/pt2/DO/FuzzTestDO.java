//*********************************************************************************************************************
// FuzzTestDO.java
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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * Implements the fuzz test data object.
 * @author Southwest Research Institute
 *
 */

@Entity
@Table(name = "FUZZTESTS")
public class FuzzTestDO implements Comparable<FuzzTestDO> {

	// Setup the column values for the FuzzTest Table.
	@Id
	@GeneratedValue
	@Column(name = "FUZZTEST_ID")
	private int id;
	
	@Column(name = "FUZZTEST_NAME")
	private String testName;

	@Column(name = "FUZZTEST_FUZZVALUE")
	private String fuzzValue;
	
	// Setup the relationships for the FuzzTest Table.
	@ManyToMany(mappedBy = "fuzzTestSet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<AbstractPacketDO> owners = new HashSet<AbstractPacketDO>();
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "FUZZTEST_DATATYPE", joinColumns = { @JoinColumn(name = "FUZZTEST_ID") }, inverseJoinColumns = { @JoinColumn(name = "DATATYPE_ID") })
	private Set<DataTypeDO> dataTypeSet = new HashSet<DataTypeDO>();

	private boolean selected = false;
	
	protected FuzzTestDO() {
	}

	public FuzzTestDO(String name) {
		this.testName = name;
	}

	public FuzzTestDO(String name, String fuzzValue) {
		this.testName = name;
		this.fuzzValue = fuzzValue;
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
	 * @return the fuzz test name
	 */
	public String getFuzzTestName() {
		return testName;
	}

	/**
	 * @param name
	 *            the fuzz name to set
	 */
	public void setFuzzTestName(String name) {
		this.testName = name;
	}
	
	/**
	 * @return the fuzz data
	 */
	public String getFuzzValue() {
		return fuzzValue;
	}

	/**
	 * @param data
	 *            the fuzz data to set
	 */
	public void setFuzzValue(String fuzzValue) {
		this.fuzzValue = fuzzValue;
	}
	
	/**
	 * @return the fuzzTestSet
	 */
	public Set<DataTypeDO> getDataTypeSet() {
		return dataTypeSet;
	}

	/**
	 * @param fuzzTestSet
	 *            the fuzzTestSet to set
	 */
	public void setDataTypeSet(Set<DataTypeDO> dataTypeSet) {
		this.dataTypeSet = dataTypeSet;
	}

	public void addDataType(DataTypeDO dataType) {
		dataTypeSet.add(dataType);
	}

	public void removeDataType(DataTypeDO dataType) {
		dataTypeSet.remove(dataType);
	}

	public void removeDataType(int dataTypeId) {
		for (DataTypeDO dataType : dataTypeSet) {
			if (dataType.getId() == dataTypeId) {
				dataTypeSet.remove(dataType);
			}
		}
	}
	
	public void removeDataTypes() {
		dataTypeSet.clear();
	}
	
	/**
	 * @return the ownerSet
	 */
	public Set<AbstractPacketDO> getOwners() {
		return owners;
	}

	/**
	 * @param ownerSet
	 *            the ownerSet to set
	 */
	public void setOwners(Set<AbstractPacketDO> ownerSet) {
		this.owners = ownerSet;
	}

	public void addOwner(AbstractPacketDO packet) {
		owners.add(packet);
	}

	public void removeOwner(AbstractPacketDO packet) {
		owners.remove(packet);
	}

	public void removeOwner(int packetId) {
		for (AbstractPacketDO packet : owners) {
			if (packet.getId() == packetId) {
				owners.remove(packet);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FuzzTestDO o) {
		// TODO Auto-generated method stub
		return getId() - o.getId();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
