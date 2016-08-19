//*********************************************************************************************************************
// TestCaseDO.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Implements the test case data object.
 * 
 * @author tdo
 * 
 */
@Entity
@Table(name = "TESTCASES")
public class TestCaseDO implements Comparable<TestCaseDO> {
	@Id
	@GeneratedValue
	@Column(name = "TESTCASE_ID")
	private int id;

	@ManyToMany(mappedBy = "testCaseSet")
	private Set<ProjectDO> owners = new HashSet<ProjectDO>();

	@Column(name = "TESTCASE_NAME")
	private String name;

	@Column(name = "TESTCASE_DESC")
	private String desc;

	@Column(name = "TESTCASE_RESULT")
	private String result;

	@OneToMany(mappedBy = "owner")
	private List<TestScriptDO> testScripts;

	@Column(name = "TESTSCRIPT_COMMAND")
	private String command;

	@Column(name = "TESTSCRIPT_DIRECTORY")
	private String directory;

	private boolean selected = false;

	private String protocol;

	private String fileName;

	protected TestCaseDO() {
	}

	public TestCaseDO(String name) {
		this.name = name;
	}

	public TestCaseDO(String name, String desc, String result) {
		this.name = name;
		this.desc = desc;
		this.result = result;
		this.testScripts = new ArrayList<TestScriptDO>();
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

	public void addTestScript(TestScriptDO script) {
		testScripts.add(script);
	}

	public void removeTestScript(TestScriptDO script) {
		testScripts.remove(script);
	}

	/**
	 * @return the script
	 */
	public List<TestScriptDO> getScript() {
		return testScripts;
	}

	/**
	 * @param script
	 *            the script to set
	 */
	public void setScript(List<TestScriptDO> script) {
		this.testScripts = script;
	}

	/**
	 * @return the ownerSet
	 */
	public Set<ProjectDO> getOwners() {
		return owners;
	}

	/**
	 * @param ownerSet
	 *            the ownerSet to set
	 */
	public void setOwners(Set<ProjectDO> ownerSet) {
		this.owners = ownerSet;
	}

	public void addOwner(ProjectDO project) {
		owners.add(project);
	}

	public void removeOwner(ProjectDO project) {
		owners.remove(project);
	}

	public void removeOwner(int projectId) {
		for (ProjectDO project : owners) {
			if (project.getId() == projectId) {
				owners.remove(project);
			}
		}
	}

	public int compareTo(TestCaseDO o) {
		return getId() - o.getId();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return the testScripts
	 */
	public List<TestScriptDO> getTestScripts() {
		return testScripts;
	}

	/**
	 * @param testScripts the testScripts to set
	 */
	public void setTestScripts(List<TestScriptDO> testScripts) {
		this.testScripts = testScripts;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
}
