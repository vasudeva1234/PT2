//*********************************************************************************************************************
// ProjectDOTableModel.java
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
package org.epri.pt2.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.controller.ProjectController;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.listeners.ViewCallbackInterface;

/**
 * Contains a project list model for a JTable containing a list of tables.
 * 
 * @author Tam Do
 * 
 */
public class ProjectDOTableModel extends AbstractTableModel implements
		ViewCallbackInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6827044617076095376L;
	private ProjectController controller = ProjectController.getInstance();
	private SelectionState state = SelectionState.getInstance();
	private String[] columnNames = { "ID", "Name" }; // , "Description"};

	private List<ProjectDO> projects;
	private boolean dirty = false;
	private int lastWorkspace = -1;

	public ProjectDOTableModel() {
		super();
		projects = new ArrayList<ProjectDO>();
		this.controller.registerListener(this);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getRowCount() {
		updateProjects();
		
		if (state.getWorkspaceId() > 0) {
			if(projects != null) {
				return projects.size();
			} else {
				return 0;
			}
		}
		return 0;
	}

	private void updateProjects() {
		int workspaceId = state.getWorkspaceId();

		if (workspaceId > 0) {
			if (projects == null) {
				lastWorkspace = workspaceId;
				projects = controller.getProjects(workspaceId);
			} else if (workspaceId > 0 && (workspaceId != lastWorkspace)) {
				lastWorkspace = workspaceId;
				projects = controller.getProjects(workspaceId);
			} else if (dirty) {
				projects = controller.getProjects(workspaceId);
			}
		}
	}

	public Object getValueAt(int row, int col) {
		updateProjects();

		if (col == 0) {
			return projects.get(row).getId();
		} else if (col == 1) {
			return projects.get(row).getName();
		} else if (col == 2) {
			return projects.get(row).getDesc();
		}
		return null;
	}

	public void dataChanged() {
		dirty = true;
		fireTableDataChanged();
	}

	public String getDescriptionAt(int row) {
		if (row > -1) {
			return projects.get(row).getDesc();
		}
		return "";
	}
}
