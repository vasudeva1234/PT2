//*********************************************************************************************************************
// TestCaseListPanel.java
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.gui.model.TestCaseDOTableModel;
import org.epri.pt2.listeners.ViewCallbackInterface;

/**
 * A class for displaying the different test cases available to the user.
 * 
 * @author Tam Do
 * 
 */
public class TestCaseListPanel extends JPanel implements ViewCallbackInterface,
		ListSelectionListener {
	private final static TestCaseListPanel testCasePanel;

	private static final long serialVersionUID = 3563785127190921774L;
	private SelectionState state = SelectionState.getInstance();
	private JScrollPane scrollPane;

	protected JTable table;
	protected TestCaseDOTableModel tableModel;

	static {
		testCasePanel = new TestCaseListPanel();
	}

	/**
	 * @param controller
	 * @param state
	 */
	private TestCaseListPanel() {
		super();

		tableModel = new TestCaseDOTableModel();

		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.removeColumn(table.getColumnModel().getColumn(0));

		// TableColumn selectColumn = table.getColumnModel().getColumn(0);
		// selectColumn.setPreferredWidth(1);

		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setPreferredWidth(40);
		idColumn.setMaxWidth(40);

		scrollPane = new JScrollPane(table);

		setLayout(new MigLayout());

		add(scrollPane, "dock center");
	}

	public static TestCaseListPanel getInstance() {
		return testCasePanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epri.pt2.listeners.ViewCallbackInterface#dataChanged()
	 */
	public void dataChanged() {
		try {
			updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public ListSelectionModel getListSelectionModel() {
		return table.getSelectionModel();
	}

	public void valueChanged(ListSelectionEvent e) {

		if (e.getSource().equals(table.getSelectionModel())) {
			// if (!e.getValueIsAdjusting()) {
			int selectedRow = table.getSelectedRow();

			if (selectedRow >= 0) {
				state.setTestCaseId(tableModel.getRow(selectedRow).getId());
			}
			// }

		} else {
			if (!e.getValueIsAdjusting()) {
				tableModel.fireTableDataChanged();
			}
		}

	}
}
