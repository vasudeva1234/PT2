//*********************************************************************************************************************
// FilterTableModel.java
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

import org.epri.pt2.proxy.FilterDO;

/**
 * @author Tam Do
 * 
 */
public class FilterTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 5367286487784202913L;
	private static final FilterTableModel tableModel;

	private List<FilterDO> filters;

	public static enum ColumnNames {
		Type, Rule, Replacement
	};

	static {
		tableModel = new FilterTableModel();
	}

	private FilterTableModel() {
		filters = new ArrayList<FilterDO>();
	};

	public static FilterTableModel getInstance() {
		return tableModel;
	}

	/**
	 * @return the filters
	 */
	public List<FilterDO> getFilters() {
		return filters;
	}

	public void addFilter(FilterDO filter) {
		filters.add(filter);
		fireTableRowsInserted(filters.size() - 1, filters.size() - 1);
	}

	public void removeFilters(List<FilterDO> filterList) {
		for (FilterDO filter : filters) {
			filters.remove(filter);
		}
	}

	public void removeRows(int[] rows) {
		for (Integer row : rows) {
			filters.remove((int) row);
		}

		fireTableDataChanged();
	}

	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	@Override
	public String getColumnName(int column) {
		return ColumnNames.values()[column].toString();
	}

	public int getRowCount() {
		return filters.size();
	}

	public Object getValueAt(int row, int col) {
		if (col == ColumnNames.Type.ordinal()) {
			return filters.get(row).getType();
		} else if (col == ColumnNames.Rule.ordinal()) {
			return filters.get(row).getReplaceRule();
		} else if (col == ColumnNames.Replacement.ordinal()) {
			return filters.get(row).getReplaceVal();
		}

		return null;
	}

	/** 
	 * return the new position
	 * @param row
	 * @param moveUp
	 * @return
	 */
	public int moveRow(int row, boolean moveUp) {
		if(row > -1) {
			FilterDO filter = filters.remove(row);
			int insertPos = -1;
			if(moveUp) {
				insertPos = row - 1;
				if(insertPos < 0) {
					insertPos = 0;
					filters.add(filter);
				} else {
					filters.add(insertPos, filter);
					fireTableRowsUpdated(insertPos, row);
				}
			} else {
				insertPos = row + 1;
				if(insertPos > filters.size()) {
					insertPos = filters.size();
					filters.add(filter);
				} else {
					filters.add(insertPos, filter);
					fireTableRowsUpdated(row, insertPos);
				}
			}
			return insertPos;
		}
		return -1;
	}
}
