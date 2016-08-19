//*********************************************************************************************************************
// DataTypeDO.java
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Implements the data type data object.
 * @author Southwest Research Institute
 *
 */

@Entity
@Table(name = "DATATYPES")
public class DataTypeDO implements Comparable<DataTypeDO> {

	@Id
	@GeneratedValue
	@Column(name = "DATATYPE_ID")
	private int id;

	@Column(name = "DATATYPE_NAME")
	private String name;

	// Setup the relationships for the DataTypes Table.
	@ManyToMany(mappedBy = "dataTypeSet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<FuzzTestDO> owners = new HashSet<FuzzTestDO>();
	
	public DataTypeDO() {		
	}
	
	public DataTypeDO(String name) {
		this.name = name;
	}
	
	private boolean selected = false;

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
	 * @return the data type name
	 */
	public String getDataTypeName() {
		return name;
	}

	/**
	 * @param name
	 *            the data type name to set
	 */
	public void setDataTypeName(String name) {
		this.name = name;
	}

	/**
	 * @return the ownerSet
	 */
	public Set<FuzzTestDO> getOwnerSet() {
		return owners;
	}

	/**
	 * @param ownerSet
	 *            the ownerSet to set
	 */
	public void setOwners(Set<FuzzTestDO> ownerSet) {
		this.owners = ownerSet;
	}

	public void addOwner(FuzzTestDO fuzzTest) {
		owners.add(fuzzTest);
	}

	public void removeOwner(FuzzTestDO fuzzTest) {
		owners.remove(fuzzTest);
	}

	public void removeOwner(int fuzzTestId) {
		for (FuzzTestDO fuzzTest : owners) {
			if (fuzzTest.getId() == fuzzTestId) {
				owners.remove(fuzzTest);
			}
		}
	}
	
	public void removeOwners() {
		owners.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(DataTypeDO o) {
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
