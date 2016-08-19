//*********************************************************************************************************************
// ProjectController.java
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
 * Add, removes, or lists projects from a workspace.
 * 
 * @author tdo
 */

public class ProjectController extends ViewCallback {
	private final static ProjectController controller;

	static {
		controller = new ProjectController();
	}

	private ProjectController() {
	};

	/**
	 * Get an instance of the controller.
	 * 
	 * @return
	 */
	public static ProjectController getInstance() {
		return controller;
	}

	/**
	 * Add a project to the workspace.
	 * 
	 * @param workspace
	 * @param project
	 * @return
	 */
	public ProjectDO addProject(WorkspaceDO workspace, ProjectDO project) {
		return addProject(workspace.getId(), project);
	}

	/**
	 * Add a project to the workspace.
	 * 
	 * @param workspaceId
	 * @param project
	 * @return
	 */
	public ProjectDO addProject(int workspaceId, ProjectDO project) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		WorkspaceDO workspace = manager.find(WorkspaceDO.class, new Integer(
				workspaceId));
		project.setOwner(workspace);
		workspace.addProject(project);

		project = manager.merge(project);
		manager.merge(workspace);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return project;
	}

	/**
	 * Gets a list of projects associated with the workspace.
	 * 
	 * @param workspace
	 * @return
	 */
	public List<ProjectDO> getProjects(int workspaceId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<ProjectDO> projects = manager.createQuery(
				"from ProjectDO where OWNER_ID = " + workspaceId,
				ProjectDO.class).getResultList();
		manager.getTransaction().commit();
		manager.close();
		return projects;
	}

	/**
	 * Get a project by the given name.
	 * 
	 * @param name
	 * @return
	 */
	public ProjectDO getProjectByName(String name) {
		ProjectDO retval = new ProjectDO();

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<ProjectDO> projects = manager.createQuery(
				"from ProjectDO where PROJECT_NAME = '" + name + "'",
				ProjectDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		try {
			retval = projects.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retval;
	}

	/**
	 * Get a project by the given id.
	 * 
	 * @param id
	 * @return
	 */
	public ProjectDO getProject(int id) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		ProjectDO project = manager.find(ProjectDO.class, new Integer(id));
		manager.getTransaction().commit();
		manager.close();

		return project;
	}

	public ProjectDO getProject(ProjectDO project) {
		return getProject(project.getId());
	}
	
	/**
	 * Remove a project from the workspace.
	 * 
	 * @param workspaceId
	 * @param projectId
	 * @return
	 */
	public ProjectDO removeProject(int workspaceId, int projectId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		WorkspaceDO workspace = manager.find(WorkspaceDO.class, new Integer(
				workspaceId));
		ProjectDO project = manager.find(ProjectDO.class,
				new Integer(projectId));
		workspace.removeProject(project);
		project.setOwner(null);
		manager.merge(workspace);
		project = manager.merge(project);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();
		return project;
	}

	/**
	 * Remove a project from the workspace.
	 * 
	 * @param workspaceId
	 * @param project
	 * @return
	 */
	public ProjectDO removeProject(int workspaceId, ProjectDO project) {
		return removeProject(workspaceId, project.getId());
	}
}
