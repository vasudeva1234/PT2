//*********************************************************************************************************************
// WorkspaceDOTableModel.java
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

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.WorkspaceDO;
import org.epri.pt2.controller.WorkspaceController;
import org.epri.pt2.listeners.ViewCallbackInterface;

/**
 * @author Tam Do
 * 
 */
public class WorkspaceDOTableModel extends AbstractTableModel implements
		ViewCallbackInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5098084711632147397L;
	private String[] columnNames = { "ID", "Name" };

	protected WorkspaceController controller;

	/**
	 * @param controller
	 */
	public WorkspaceDOTableModel(WorkspaceController controller) {
		this.controller = controller;
		controller.registerListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return controller.getWorkspaces().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return controller.getWorkspaces().get(rowIndex).getId();
		} else if (columnIndex == 1) {
			return controller.getWorkspaces().get(rowIndex).getName();
		}

		return null;
	}

	/**
	 * @param workspace
	 */
	public void addRow(WorkspaceDO workspace) {
		controller.addWorkspace(workspace);
		fireTableDataChanged();
	}

	/**
	 * @param workspace
	 */
	public void removeRow(WorkspaceDO workspace) {
		controller.removeWorkspace(workspace.getId());
		fireTableDataChanged();
	}

	/**
	 * @param rowSelected
	 * @return
	 */
	public WorkspaceDO getRow(int rowSelected) {
		return controller.getWorkspaces().get(rowSelected);
	}

	public void dataChanged() {
		fireTableDataChanged();
	}
}
