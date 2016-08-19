//*********************************************************************************************************************
// TestCaseDOTableModel.java
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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.controller.TestCaseController;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.listeners.ViewCallbackInterface;

public class TestCaseDOTableModel extends AbstractTableModel implements
		ViewCallbackInterface {
	/**
	 * 
	 */
	// private String[] columnNames = { "", "ID", "Name", "Description",
	// "Result" };
	public static enum ColumnNames {
		Selected, ID, Name, Description, Comments
	};

	private static final long serialVersionUID = 1L;
	private SelectionState state = SelectionState.getInstance();
	protected TestCaseController controller = TestCaseController.getInstance();

	private List<TestCaseDO> testCases;
	private int lastProject = -1;

	private boolean dirty = false;

	public TestCaseDOTableModel() {
		super();
	}

	public boolean isCellEditable(int row, int column) {
		return(column == 4);
	}

	@Override
	public String getColumnName(int col) {
		if (col == ColumnNames.Selected.ordinal()) {
			return "";
		}
		return ColumnNames.values()[col].toString();
	}

	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	public int getRowCount() {
		updateTestCases();

		if (testCases != null) {
			return testCases.size();
		}
		return 0;
	}

	private void updateTestCases() {
		int projectId = state.getProjectId();

		if (projectId > 0) {
			if (testCases == null) {
				lastProject = projectId;
				testCases = controller.getTestCases(projectId);
			} else if (projectId > 0 && (projectId != lastProject)) {
				lastProject = projectId;
				testCases = controller.getTestCases(projectId);
			} else if (dirty) {
				testCases = controller.getTestCases(projectId);
			}
		}

	}

	public Object getValueAt(int arg0, int arg1) {
		updateTestCases();

		if (arg1 == 0) {
			return testCases.get(arg0).isSelected();
		} else if (arg1 == 1) {
			return testCases.get(arg0).getId();
		} else if (arg1 == 2) {

			return testCases.get(arg0).getName();
		} else if (arg1 == 3) {

			return testCases.get(arg0).getDesc();
		} else if (arg1 == 4) {

			return testCases.get(arg0).getResult();
		}

		return null;

	}

	public void setValueAt(Object a, int row, int col) {
		if (row >= 0) {
			TestCaseDO testCase = testCases.get(row);

			if (col == 0) {
				testCase.setSelected((Boolean) a);
			} else if (col == 2) {
				testCase.setName((String) a);
			} else if (col == 3) {
				testCase.setDesc((String) a);
			} else if (col == 4) {
				testCase.setResult((String) a);
			}

			controller.updateTestCase(testCase);
			dirty = true;
		}
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col == ColumnNames.Selected.ordinal()) {
			return Boolean.class;
		} else if (col == ColumnNames.ID.ordinal()) {
			return Integer.class;
		} else if (col == ColumnNames.Name.ordinal()) {
			return String.class;
		} else if (col == ColumnNames.Description.ordinal()) {
			return String.class;
		} else if (col == ColumnNames.Comments.ordinal()) {
			return String.class;
		}
		return null;
	}

	public void addRow(TestCaseDO testCase) {
		controller.addTestCase(state.getProjectId(), testCase);
		dirty = true;
		fireTableDataChanged();
	}

	public void removeRow(TestCaseDO testCase) {
		controller.removeTestCase(state.getProjectId(), testCase.getId());
		dirty = true;
		fireTableDataChanged();
	}

	public void removeRow(int testCaseId) {
		controller.removeTestCase(state.getProjectId(), testCaseId);
		dirty = true;

		fireTableDataChanged();
	}

	public TestCaseDO getRow(int selectedRow) {
		return testCases.get(selectedRow);
	}

	public void dataChanged() {
		dirty = true;
		fireTableDataChanged();
	}

}
