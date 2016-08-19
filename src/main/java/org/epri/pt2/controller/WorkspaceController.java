//*********************************************************************************************************************
// WorkspaceController.java
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
package org.epri.pt2.controller;

import java.util.List;

import javax.persistence.EntityManager;

import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.DO.WorkspaceDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Controls the addition or removal of workspaces from the hibernate database.
 * 
 * @author tdo
 * 
 */

public class WorkspaceController extends ViewCallback {
	private final static WorkspaceController controller;

	static {
		controller = new WorkspaceController();
	}

	private WorkspaceController() {
	};

	public static WorkspaceController getInstance() {
		return controller;
	}

	/**
	 * @param workspace
	 *            the workspace to set
	 */
	public WorkspaceDO addWorkspace(WorkspaceDO workspace) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		workspace = manager.merge(workspace);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return workspace;
	}

	public List<WorkspaceDO> getWorkspaces() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<WorkspaceDO> workspaces = manager.createQuery("from WorkspaceDO",
				WorkspaceDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		return workspaces;
	}

	public WorkspaceDO getWorkspace(String name) {
		WorkspaceDO retval = new WorkspaceDO();

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<WorkspaceDO> workspace = manager.createQuery(
				"from WorkspaceDO where WORKSPACE_NAME='" + name + "'",
				WorkspaceDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		try {
			retval = workspace.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retval;
	}

	public WorkspaceDO getWorkspace(int id) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		WorkspaceDO retval = manager.find(WorkspaceDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		return retval;
	}

	public WorkspaceDO removeWorkspace(int workspaceid) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		WorkspaceDO workspace = manager.find(WorkspaceDO.class, new Integer(
				workspaceid));

		// get the list of projects within the workspace
		List<ProjectDO> projects = workspace.getProjects();

		// for each workspace drop the project.
		for (ProjectDO project : projects) {
			ProjectController.getInstance().removeProject(workspace.getId(),
					project);
		}

		manager.remove(workspace);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return workspace;
	}

	public WorkspaceDO removeWorkspace(WorkspaceDO workspace) {
		return removeWorkspace(workspace.getId());
	}
}