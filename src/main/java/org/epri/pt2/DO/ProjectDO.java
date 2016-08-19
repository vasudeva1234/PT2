//*********************************************************************************************************************
// ProjectDO.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Implements the project data object.
 * 
 * @author tdo
 * 
 */
@Entity
@Table(name = "PROJECTS")
public class ProjectDO {

	@Id
	@GeneratedValue
	@Column(name = "PROJECT_ID")
	private int id;

	@Column(name = "PROJECT_NAME")
	private String name;

	@Column(name = "PROJECT_DESC")
	private String desc;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER_ID")
	private WorkspaceDO owner;

	@OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
	private List<DeviceDO> devices = new ArrayList<DeviceDO>();
	
	@OneToMany(mappedBy = "owner")
	private List<AbstractPacketDO> packets = new ArrayList<AbstractPacketDO>();
	
	@OneToMany(mappedBy = "project")
	private List<TestScriptDO> scripts = new ArrayList<TestScriptDO>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "PROJECT_TESTCASE", joinColumns = { @JoinColumn(name = "PROJECT_ID") }, inverseJoinColumns = { @JoinColumn(name = "TESTCASE_ID") })
	private Set<TestCaseDO> testCaseSet = new HashSet<TestCaseDO>();

	public ProjectDO() {
	}

	public ProjectDO(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the deviceList
	 */
	public List<DeviceDO> getDeviceList() {
		return devices;
	}

	/**
	 * @param deviceList
	 *            the deviceList to set
	 */
	public void setDeviceList(List<DeviceDO> deviceList) {
		this.devices = deviceList;
	}
	
	/**
	 * @return the packetList
	 */
	public List<AbstractPacketDO> getPacketList() {
		return packets;
	}

	/**
	 * @param packetList
	 *            the packetList to set
	 */
	public void setPacketList(List<AbstractPacketDO> packetList) {
		this.packets = packetList;
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
	 * @return the owner
	 */
	public WorkspaceDO getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(WorkspaceDO owner) {
		this.owner = owner;
	}

	/**
	 * @return the testCaseSet
	 */
	public Set<TestCaseDO> getTestCaseSet() {
		return testCaseSet;
	}

	/**
	 * @param testCaseSet
	 *            the testCaseSet to set
	 */
	public void setTestCaseSet(Set<TestCaseDO> testCaseSet) {
		this.testCaseSet = testCaseSet;
	}

	public void addTestCase(TestCaseDO testCase) {
		testCaseSet.add(testCase);
	}

	public void removeTestCase(TestCaseDO testCase) {
		testCaseSet.remove(testCase);
	}

	public void removeTestCase(int testCaseId) {
		for (TestCaseDO testcase : testCaseSet) {
			if (testcase.getId() == testCaseId) {
				testCaseSet.remove(testcase);
			}
		}
	}

	public void addDevice(DeviceDO device) {
		devices.add(device);
	}

	public void removeDevice(DeviceDO device) {
		devices.remove(device);
	}
	
}
