//*********************************************************************************************************************
// ProjectListPanel.java
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.epri.pt2.controller.ProjectController;
import org.epri.pt2.gui.model.ProjectDOTableModel;
import org.epri.pt2.sniffer.SnifferPacketPanel;

/**
 * Provides a panel for displaying projects in the workspace.
 * 
 * @author Tam Do
 * 
 */
public class ProjectListPanel extends JTabbedPane implements ActionListener,
		ListSelectionListener {
	private static final long serialVersionUID = -8245086107817862476L;

	private static final ProjectListPanel projectListPanel;
	
	private JScrollPane scrollPane;
	protected JTable table;
	protected ProjectDOTableModel tableModel;
	private SelectionState state = SelectionState.getInstance();
	private TestCaseListPanel listPanel;

	private JMenuItem removeProjectMenu;

	static {
		projectListPanel = new ProjectListPanel();
	}

	public static ProjectListPanel getInstance() {
		return projectListPanel;
	}

	private ProjectListPanel() {
		super();
		final JPopupMenu popupMenu = new JPopupMenu();
		removeProjectMenu = new JMenuItem("Remove Project");
		removeProjectMenu.addActionListener(this);
		popupMenu.add(removeProjectMenu);

		listPanel = TestCaseListPanel.getInstance();

		tableModel = new ProjectDOTableModel();

		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(listPanel);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				if (r > -1 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}

				int rowIndex = table.getSelectedRow();
				if (rowIndex < 0) {
					return;
				}
				if (e.isPopupTrigger() && (e.getComponent() instanceof JTable)) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				if (r >= 0 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}

				int rowindex = table.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setPreferredWidth(40);
		idColumn.setMaxWidth(40);
		
		TableColumnModel colModel = table.getColumnModel();
		
		for(int i = 0; i < colModel.getColumnCount(); i++) {
			colModel.getColumn(i).setCellRenderer(new RowRender());
		}

		scrollPane = new JScrollPane(table);
		addTab("Projects", scrollPane);
	}

	public ListSelectionModel getListSelectionModel() {
		return table.getSelectionModel();
	}

	public void valueChanged(ListSelectionEvent arg0) {

		if (arg0.getSource().equals(table.getSelectionModel())) {
			if (!arg0.getValueIsAdjusting()) {
				int selectedRow = table.getSelectedRow();

				if (selectedRow >= 0) {
					state.setProjectId((Integer) tableModel.getValueAt(
							selectedRow, 0));

					TestCaseToolbarPanel.getInstance().updateButtonState();
					SnifferPacketPanel.getInstance().loadPackets();
				}
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(removeProjectMenu)) {
			ProjectController.getInstance().removeProject(
					state.getWorkspaceId(), state.getProjectId());
		}
	}
	
	protected class RowRender extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3078560644944494958L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			setToolTipText(tableModel.getDescriptionAt(row));
			return this;
		}
	}
}
