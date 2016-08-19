//*********************************************************************************************************************
// WorkspaceWizard.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.WorkspaceDO;
import org.epri.pt2.controller.WorkspaceController;
import org.epri.pt2.gui.model.WorkspaceDOTableModel;
import org.epri.pt2.listeners.ViewCallbackInterface;

/**
 * A wizard for creating a new workspace.
 * 
 * @author Tam Do
 *
 */
public class WorkspaceWizard extends JDialog implements ViewCallbackInterface,
		ActionListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8767643032909791274L;

	private WorkspaceController controller = WorkspaceController.getInstance();
	private JTable workspaceTable;
	private WorkspaceDOTableModel model;
	private JButton createButton;
	private JButton removeButton;
	private JButton openButton;

	private SelectionState state = SelectionState.getInstance();

	public WorkspaceWizard(JFrame frame) {
		super(frame);

		controller.registerListener(this);

		initComponents();
		dataChanged();
	}

	private void initComponents() {
		setTitle("Select a Workspace");
		model = new WorkspaceDOTableModel(controller);

		workspaceTable = new JTable(model);
		workspaceTable.getSelectionModel().addListSelectionListener(this);
		workspaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workspaceTable
				.setToolTipText("Please select a project from the list. If no projects are available, please create a new project first.");

		TableColumn idColumn = workspaceTable.getColumnModel().getColumn(0);
		idColumn.setPreferredWidth(40);
		idColumn.setMaxWidth(40);

		JScrollPane scrollPane = new JScrollPane(workspaceTable);

		createButton = new JButton("Create");
		createButton.addActionListener(this);
		createButton.setToolTipText("Create a new workspace");
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		removeButton
				.setToolTipText("Remove the selected workspace. Please note that this also removes all associated projects and data from database.");
		openButton = new JButton("Open");
		openButton.addActionListener(this);
		openButton.setToolTipText("Open the selected workspace.");

		setLayout(new MigLayout("insets 5"));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("align right"));
		buttonPanel.add(createButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(openButton);

		add(scrollPane, "dock center, h 100");
		add(buttonPanel, "dock south");

		pack();
		setSize(300, 200);
		setLocationRelativeTo(null);
		setModal(true);
	}

	public void dataChanged() {
		if (state.getWorkspaceId() > 0) {
			openButton.setEnabled(true);
		} else {
			openButton.setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(createButton)) {
			String name = JOptionPane.showInputDialog(null, "Name: ",
					"Workspace");
			WorkspaceDO workspace = new WorkspaceDO(name);
			// controller.addWorkspace(workspace);
			model.addRow(workspace);
		} else if (e.getSource().equals(removeButton)) {
			int selectedRow = workspaceTable.getSelectedRow();
			if (selectedRow >= 0) {
				model.removeRow(model.getRow(selectedRow));
			}
		} else if (e.getSource().equals(openButton)) {
			// Update the workspace selected
			int selectedRow = workspaceTable.getSelectedRow();
			if(selectedRow > -1) {
				List<WorkspaceDO> workspaces = controller.getWorkspaces();
				state.setWorkspaceId(workspaces.get(selectedRow).getId());
				ProjectListPanel.getInstance().tableModel.fireTableDataChanged();
			}
			setVisible(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			//List<WorkspaceDO> workspaces = controller.getWorkspaces();
			int selectedRow = workspaceTable.getSelectedRow();

			if (selectedRow >= 0) {
				//state.setWorkspaceId(workspaces.get(selectedRow).getId());
				openButton.setEnabled(true);
			} else {
				openButton.setEnabled(false);
			}
		}
	}

	public Object getSelectionModel() {
		return workspaceTable.getSelectionModel();
	}

}
