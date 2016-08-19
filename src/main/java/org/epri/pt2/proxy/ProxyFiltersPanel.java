//*********************************************************************************************************************
// ProxyFiltersPanel.java
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
package org.epri.pt2.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.gui.model.FilterTableModel;

import com.jgoodies.common.base.Strings;
import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;

/**
 * Controls the filters for the proxy.
 * 
 * @author Tam Do
 * 
 */
public class ProxyFiltersPanel extends JPanel implements ActionListener {

	protected JLabel selectTypeLabel;
	protected JComboBox typeComboBox;
	protected JLabel replacementRuleLabel;
	protected JTextField replacementRule;
	protected JLabel replacementValueLabel;
	protected JTextField replacementValue;

	protected JToggleButton enableFilters;
	
	protected JButton addFilter;
	protected JButton removeFilter;
	protected JButton raisePriority;
	protected JButton lowerPriority;

	protected JTable filterTable;
	protected FilterTableModel filterModel;

	private FilterValidation validation;

	private static final long serialVersionUID = 5256020721889052304L;

	private ProxyController controller;
	
	public ProxyFiltersPanel() {
		initComponents();
	}

	private void initComponents() {
		controller = ProxyController.getInstance();
		
		validation = new FilterValidation();

		enableFilters = new JToggleButton("Enable Filters");
		enableFilters.setSelected(controller.isFilteringEnabled());
		enableFilters.addActionListener(this);
		
		selectTypeLabel = new JLabel("Type:");
		typeComboBox = new JComboBox(FilterType.values());
		replacementRuleLabel = new JLabel("Match Rule:");
		replacementRule = new JTextField();

		replacementValueLabel = new JLabel("Replace Text:");
		replacementValue = new JTextField();

		JPanel filterAddPanel = new JPanel();
		filterAddPanel.setLayout(new MigLayout("wrap 2",
				"[right,60]10[fill,200]"));
		filterAddPanel.add(enableFilters, "span 2, align left");
		filterAddPanel.add(selectTypeLabel);
		filterAddPanel.add(typeComboBox);
		filterAddPanel.add(replacementRuleLabel);
		filterAddPanel.add(replacementRule);
		filterAddPanel.add(replacementValueLabel);
		filterAddPanel.add(replacementValue);

		addFilter = new JButton("Add");
		addFilter.addActionListener(this);

		removeFilter = new JButton("Remove");
		removeFilter.addActionListener(this);

		raisePriority = new JButton("Raise Priority");
		raisePriority.addActionListener(this);
		lowerPriority = new JButton("Lower Priority");
		lowerPriority.addActionListener(this);
		JPanel filterButtonPanel = new JPanel();
		filterButtonPanel.setLayout(new MigLayout());
		//filterButtonPanel.add(enableFilters);
		filterButtonPanel.add(addFilter);
		filterButtonPanel.add(removeFilter);
		filterButtonPanel.add(raisePriority);
		filterButtonPanel.add(lowerPriority);

		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new MigLayout("wrap 1"));
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterButtonPanel);

		filterModel = FilterTableModel.getInstance();
		filterTable = new JTable(filterModel);
		filterTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		JScrollPane scrollPane = new JScrollPane(filterTable);
		setLayout(new MigLayout());

		add(filterPanel, "dock north");
		add(scrollPane, "dock center");

		JComponent validationResultsComponent = ValidationResultViewFactory
				.createReportTextArea(validation.validationResultModel);

		add(validationResultsComponent, "dock south");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addFilter)) {
			ValidationResult result = validation.validate();
			validation.validationResultModel.setResult(result);

			if (!validation.validationResultModel.hasErrors()) {
				FilterDO filter = new FilterDO();
				filter.setType((FilterType) typeComboBox.getSelectedItem());
				filter.setReplaceRule(replacementRule.getText());
				filter.setReplaceVal(replacementValue.getText());
				filterModel.addFilter(filter);
			}
		} else if (e.getSource().equals(removeFilter)) {
			filterModel.removeRows(filterTable.getSelectedRows());
		} else if (e.getSource().equals(raisePriority)) {
			int newPos = filterModel.moveRow(filterTable.getSelectedRow(), true);
			if(newPos > -1) {
				filterTable.setRowSelectionInterval(newPos, newPos);
			}
		} else if (e.getSource().equals(lowerPriority)) {
			int newPos = filterModel.moveRow(filterTable.getSelectedRow(), false);
			if(newPos > -1) {
				filterTable.setRowSelectionInterval(newPos, newPos);
			}
		} else if (e.getSource().equals(enableFilters)) {
			controller.setFilteringEnabled(enableFilters.isSelected());
		}
	}

	protected class FilterValidation implements Validatable {
		protected ValidationResultModel validationResultModel = new DefaultValidationResultModel();

		public ValidationResult validate() {
			ValidationResult validationResult = new ValidationResult();

			if (Strings.isEmpty(replacementRule.getText())) {
				validationResult.addError("Match rule can not be empty.");
			}

			try {
				Pattern.compile(replacementRule.getText());
			} catch (PatternSyntaxException e) {
				validationResult
						.addError("Invalid match rule syntax.  Please enter a valid regular expression.");
			}

			return validationResult;
		}

	}

}
