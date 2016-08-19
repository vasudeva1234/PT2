//*********************************************************************************************************************
// AbstractIfaceDO.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.epri.pt2.InterfaceType;

/**
 * Implements the abstract interface data object.
 * 
 * @author tdo
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "INTERFACES")
public abstract class AbstractIfaceDO implements Comparable<AbstractIfaceDO> {
	@Id
	@GeneratedValue
	@Column(name = "INTERFACE_ID")
	private int id;

	@ManyToMany(mappedBy = "interfaces", fetch = FetchType.EAGER)
	private Set<DeviceDO> owners = new HashSet<DeviceDO>();

	@Enumerated
	@Column(name = "name")
	private InterfaceType name;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER_ID")
	private AbstractProtocolDO protocol;

	private String physicalInterface;
	private boolean isConnected = false;

	/**
	 * @return the physicalInterface
	 */
	public String getPhysicalInterface() {
		return physicalInterface;
	}

	/**
	 * @param physicalInterface
	 *            the physicalInterface to set
	 */
	public void setPhysicalInterface(String physicalInterface) {
		this.physicalInterface = physicalInterface;
	}

	/**
	 * @return the isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * @param isConnected
	 *            the isConnected to set
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	protected AbstractIfaceDO() {
	}

	public AbstractIfaceDO(InterfaceType myName) {
		setName(myName);
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
	 * @return the name
	 */
	public InterfaceType getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(InterfaceType name) {
		this.name = name;
	}

	public void addOwner(DeviceDO device) {
		owners.add(device);
	}

	public void removeOwner(DeviceDO device) {
		owners.remove(device);
	}

	/**
	 * @return the protocol
	 */
	public AbstractProtocolDO getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(AbstractProtocolDO protocol) {
		this.protocol = protocol;
	}

}
