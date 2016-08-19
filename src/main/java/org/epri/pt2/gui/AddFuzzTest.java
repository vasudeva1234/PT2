//*********************************************************************************************************************
// AddFuzzTest.java
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

import java.util.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.DataTypeDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.controller.DataTypeController;
import org.epri.pt2.controller.FuzzTestController;

/**
 * @author Southwest Research Institute
 *
 */
public class AddFuzzTest extends JDialog implements ActionListener {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 417503605228000943L;
	private FuzzTestController fuzzTestController = FuzzTestController.getInstance();
	private DataTypeController dataTypeController = DataTypeController.getInstance();
	private JButton addButton;
	private JButton addNewDataTypeButton;
	private JButton removeButton;
	private JButton okButton;
	private JTextField fuzzTestNameTextField;
	private JTextField fuzzValueTextField;
	private JComboBox comboBox;	
	private DefaultComboBoxModel comboBoxModel;
	private JList fuzzTestList;
	private DefaultListModel fuzzTestListModel;
	private FuzzTestDO newFuzzTest;
	private DataTypeDO dataType;
	private FuzzTestUtils fuzzTestUtils;
	

	
	public AddFuzzTest(DataTypeDO dataType) {
		super();
		
		this.dataType = dataType;
		initComponents();
//		dataChanged();
	}

	private void initComponents() {
		setTitle("Add Fuzz Test");
		
		
		fuzzTestUtils = new FuzzTestUtils();
		fuzzTestUtils.initFuzzTestsFromDatabase();
		
		ClassLoader cl = this.getClass().getClassLoader();
		
		// Create list.
		fuzzTestListModel = new DefaultListModel();
		fuzzTestList = new JList(fuzzTestListModel);
		JScrollPane fuzzTestAreaScrollPane = new JScrollPane(fuzzTestList);
			
		// Create the labels.
		JLabel availableFuzzTestLabel = new JLabel("Available:");
		JLabel dataTypeLabel = new JLabel("Data Type: ");
		JLabel fuzzTestNameLabel = new JLabel("Fuzz Test Name: ");		
		JLabel fuzzValueLabel = new JLabel("Fuzz Value: ");		
		
		// Create the combo box.
		comboBoxModel = new DefaultComboBoxModel();
		comboBox = new JComboBox(comboBoxModel);
		comboBox.addActionListener(this);
		updateComboBoxFromDatabase();

		if(dataType != null)
		{
			comboBoxModel.addElement(dataType);
			comboBox.setSelectedItem(dataType.getDataTypeName()); 
		}

		// Create text fields.
		fuzzTestNameTextField = new JTextField();
		fuzzValueTextField = new JTextField();
		
		setLayout(new MigLayout("inset 5"));
		
		// Create the buttons.
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		addNewDataTypeButton = new JButton();
		addNewDataTypeButton.setIcon(new ImageIcon(cl.getResource("images/green_plus_sign.png")));
		addNewDataTypeButton.addActionListener(this);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		
		JPanel dataEntryPanel = new JPanel();
		dataEntryPanel.setLayout(new MigLayout());
		dataEntryPanel.add(dataTypeLabel);
		dataEntryPanel.add(comboBox, "split 2, w 150");
		dataEntryPanel.add(addNewDataTypeButton, "width 15:15:15, height 15:15:15, gaptop push, wrap");
		dataEntryPanel.add(fuzzTestNameLabel);
		dataEntryPanel.add(fuzzTestNameTextField, "wrap, growx, push");
		dataEntryPanel.add(fuzzValueLabel);
		dataEntryPanel.add(fuzzValueTextField, "wrap, growx, push");		
		dataEntryPanel.add(addButton, "skip, split, align right");
		dataEntryPanel.add(okButton, "align right");
		
		JPanel fuzzTestListPanel = new JPanel();
		fuzzTestListPanel.setLayout(new MigLayout());
		fuzzTestListPanel.add(availableFuzzTestLabel, "wrap");
		fuzzTestListPanel.add(fuzzTestAreaScrollPane, "span 2, h 250, w 250, wrap");
		// TODO fix remove functionality
//		fuzzTestListPanel.add(removeButton, "skip, align right");
		add(fuzzTestListPanel);
		add(dataEntryPanel);

		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
	}

	public void dataChanged() {
		/*
		if (state.getWorkspaceId() > 0) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
		*/
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addButton)) {
			boolean found = false;
			DataTypeDO selectedDataType = getSelectedDataType();
			
			if((selectedDataType == null) || (fuzzTestNameTextField.getText().isEmpty())) 
			{				
				JOptionPane.showMessageDialog(this, 
						"Date Type, Fuzz Test Name and/or Fuzz Value must be specified!",
						"Invalid Input",
						JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				// Only add new fuzz test if it doesn't already exist.
				newFuzzTest = new FuzzTestDO(fuzzTestNameTextField.getText(), fuzzValueTextField.getText());
				
				for(FuzzTestDO fuzzTest : fuzzTestController.getFuzzTests())
				{
					if(newFuzzTest.getFuzzTestName().equals(fuzzTest.getFuzzTestName()))
					{
						found = true;
						JOptionPane.showMessageDialog(this, 
								"Fuzz Test already exists!",
								"Invalid Input",
								JOptionPane.WARNING_MESSAGE);
						break;
					}					
				}
				
				if(!found)
				{					
					if(selectedDataType != null)
					{
						newFuzzTest = fuzzTestController.addFuzzTest(newFuzzTest, selectedDataType);
					}

					fuzzTestNameTextField.setText("");
					fuzzValueTextField.setText("");					
				}
				updateFuzzTestListFromDatabase();
			}			
		} else if(e.getSource().equals(addNewDataTypeButton)) {
			String type = JOptionPane.showInputDialog(null, "Data Type: ",
					"New Data Type");
			DataTypeDO newDataType = new DataTypeDO(type);
			//Prevent duplicates.
			boolean found = false;
			for(DataTypeDO dataType : dataTypeController.getDataTypes())
			{
				if(newDataType.getDataTypeName().equals(dataType.getDataTypeName()))
				{
					found = true;
					JOptionPane.showMessageDialog(this, 
							"Data Type already exists!",
							"Invalid Input",
							JOptionPane.WARNING_MESSAGE);
					break;
				}					
			}
			
			if(!found)
			{
				newDataType = dataTypeController.addDataType(newDataType);						
				updateComboBoxFromDatabase();
				comboBox.setSelectedItem(newDataType.getDataTypeName());
			}
		} else if(e.getSource().equals(removeButton)) {
			DataTypeDO dataType = getSelectedDataType();
			
			List<FuzzTestDO> fuzzTests = fuzzTestController.getFuzzTests();
			for(FuzzTestDO fuzzTest : fuzzTests) {
				if(fuzzTest.getFuzzTestName() == fuzzTestList.getSelectedValue().toString()) {
					dataType.removeOwner(fuzzTest);
					fuzzTest.removeDataType(dataType);
					
					int status = fuzzTestController.removeFuzzTestFromDB(fuzzTest.getId());
					
					if(status != -1) {					
						JOptionPane.showMessageDialog(this, 
								"Successfully deleted Fuzz Test!",
								"Invalid Input",
								JOptionPane.INFORMATION_MESSAGE);	
						dataTypeController.updateDataType(dataType);
					} else {					
						JOptionPane.showMessageDialog(this, 
								"Cannot delete fuzz test because it is being used by a fuzzed packet!",
								"Delete Operation",
								JOptionPane.WARNING_MESSAGE);					
					}
				}
			}
			updateFuzzTestListFromDatabase();
		} else if(e.getSource().equals(comboBox)) {
			updateFuzzTestListFromDatabase();
		} else if(e.getSource().equals(okButton)) {
			setVisible(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		/*
		List<FuzzTestDO> fuzzTests = controller.getFuzzTests();
		int selectedRow = fuzzTestTable.getSelectedRow();

		if (selectedRow >= 0) {
			state.setFuzzTestId(fuzzTest.get(selectedRow).getId());
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
		*/
	}

	public Object getSelectionModel() {
		return addButton;
		// TODO Auto-generated method stub
		//return workspaceTable.getSelectionModel();
	}
	
	public void updateFuzzTestListFromDatabase() {
		fuzzTestListModel.clear();	
		
		DataTypeDO dataType = getSelectedDataType();
		
		if(dataType != null)
		{		
			// Add fuzz tests to the fuzz test list that are associated with the 
			// selected data type. 
			for(FuzzTestDO fuzzTest : dataType.getOwnerSet()) {
				
				// Do not add duplicates.
				if(!fuzzTestListModel.contains(fuzzTest.getFuzzTestName()))
				{
					fuzzTestListModel.addElement(fuzzTest.getFuzzTestName());
				}
			}	
			
			fuzzTestListModel = fuzzTestUtils.sortList(fuzzTestListModel);
		}
	}
	
	public void updateComboBoxFromDatabase() {
		comboBoxModel.removeAllElements();	
		
		for(DataTypeDO dataType : dataTypeController.getDataTypes())
		{
			if(!comboBoxModel.equals(dataType.getDataTypeName()))
			{
				comboBoxModel.addElement(dataType.getDataTypeName());
			}
		}
	}
	
	public DataTypeDO getSelectedDataType() {
		DataTypeDO dataType = null;
		
		List<DataTypeDO> dataTypes = dataTypeController.getDataTypes();
		int selectedIdx = comboBox.getSelectedIndex();
		if(selectedIdx >= 0)
		{
			dataType = dataTypes.get(comboBox.getSelectedIndex());
		}
		
		return dataType;
	}
	

}
