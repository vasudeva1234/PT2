//*********************************************************************************************************************
// SelectionState.java
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
package org.epri.pt2.gui;

/**
 * Contains a singleton object which contains the state of selected projects,
 * workspaces, tests, and packets.
 * 
 * @author Tam Do
 * 
 */
public class SelectionState {
	public final static SelectionState state;

	protected int workspaceId;
	protected int projectId;
	protected int testCaseId;
	protected int contentDataId;

	static {
		state = new SelectionState();
	}

	/**
	 * 
	 */
	private SelectionState() {
		workspaceId = -1;
		projectId = -1;
		testCaseId = -1;
		contentDataId = -1;
	}

	public static SelectionState getInstance() {
		return state;
	}

	/**
	 * @return the workspaceId
	 */
	public int getWorkspaceId() {
		return workspaceId;
	}

	/**
	 * @param workspaceId
	 *            the workspaceId to set
	 */
	public void setWorkspaceId(int workspaceId) {
		this.workspaceId = workspaceId;
	}

	/**
	 * @return the projectId
	 */
	public int getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the testCaseId
	 */
	public int getTestCaseId() {
		return testCaseId;
	}

	/**
	 * @param testCaseId
	 *            the testCaseId to set
	 */
	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	/**
	 * @return the contentDataId
	 */
	public int getContentDataId() {
		return contentDataId;
	}

	/**
	 * @param contentDataId
	 *            the contentDataId to set
	 */
	public void setContentDataId(int contentDataId) {
		this.contentDataId = contentDataId;
	}
}
