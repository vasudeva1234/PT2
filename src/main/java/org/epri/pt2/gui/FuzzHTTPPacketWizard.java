//*********************************************************************************************************************
// FuzzHTTPPacketWizard.java
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
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.HTTPContentDataDO;
import org.epri.pt2.DO.DataTypeDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.DO.FuzzTestResultDO;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.controller.HTTPContentDataController;
import org.epri.pt2.controller.DataTypeController;
import org.epri.pt2.controller.FuzzTestResultController;
import org.epri.pt2.controller.PacketController;
import org.epri.pt2.gui.model.FuzzRequestPacketTableModel;
import org.epri.pt2.gui.model.FuzzResponsePacketTableModel;
import org.epri.pt2.listeners.ViewCallbackInterface;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
import org.epri.pt2.packetparser.treemodel.XMLNode;
import org.epri.pt2.reassembler.HttpUtils.MessageType;

/**
 * @author root
 *
 */
public class FuzzHTTPPacketWizard extends JDialog implements ViewCallbackInterface,
ActionListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3960387977683468645L;
	private DataTypeController dataTypeController = DataTypeController.getInstance();
	private HTTPContentDataController contentDataController = HTTPContentDataController.getInstance();
	private FuzzTestResultController fuzzTestResultController = FuzzTestResultController.getInstance();
	private PacketController packetController = PacketController.getInstance();	
	private SelectionState state = SelectionState.getInstance();
	private FuzzRequestPacketTableModel fuzzRequestPacketTableModel = FuzzRequestPacketTableModel.getInstance();
	private FuzzResponsePacketTableModel fuzzResponsePacketTableModel = FuzzResponsePacketTableModel.getInstance();
	private JButton addNewFuzzTestButton;
	private JButton fuzzDataButton;
	private JButton cancelButton;
	private JList fuzzTestList;
	private JTree tree;
	private DefaultListModel fuzzTestListModel;
	private JComboBox comboBox;	
	private DefaultComboBoxModel comboBoxModel;
	private HttpXMLPacketParser parser;
	private HttpXMLPacketParser fuzzParser;
	private HTTPPacketDO packetData;
	private HTTPContentDataDO contentData;
	private FuzzTestResultDO fuzzTestResult;
	private FuzzTestUtils fuzzTestUtils;
	HTTPPacketDO originalPacket;
	
	private int[] selectedFuzzTests;
	private int packetDataId;

	public FuzzHTTPPacketWizard(HTTPPacketDO originalPacket, int packetDataId) {
		//fuzzTestController.registerListener(this);
		//dataTypeController.registerListener(this);
		
		this.originalPacket = originalPacket;
		this.packetDataId = packetDataId;
		
		initComponents();
//		dataChanged();
	}

	private void initComponents() {
		setTitle("Fuzz Data");
		
		fuzzTestUtils = new FuzzTestUtils();
		fuzzTestUtils.initFuzzTestsFromDatabase();

		parser = new HttpXMLPacketParser(new ByteArrayInputStream("EMPTY".getBytes()), false);
		fuzzParser = new HttpXMLPacketParser(new ByteArrayInputStream("EMPTY".getBytes()), false);

		// Update the JTree model
		parser.setPacket(originalPacket);
		
		fuzzParser.setPacket(new HTTPPacketDO(originalPacket));
		
		ClassLoader cl = this.getClass().getClassLoader();
		
		// Create the xml pane.
		JScrollPane xmlAreaScrollPane = new JScrollPane(fuzzParser);		

		// Create the list boxes and labels.
		JLabel dataTypeLabel = new JLabel("Data Type: ");
		JLabel fuzzTestLabel = new JLabel("Fuzz Tests: ");		
		
		fuzzTestListModel = new DefaultListModel();
		fuzzTestList = new JList(fuzzTestListModel);
		fuzzTestList.addListSelectionListener(this);		
		JScrollPane fuzzTestAreaScrollPane = new JScrollPane(fuzzTestList);
		
		
		// Create the combo box.
		comboBoxModel = new DefaultComboBoxModel();
		comboBox = new JComboBox(comboBoxModel);
		comboBox.addActionListener(this);
		updateComboBoxFromDatabase();
		
		// Create the buttons.
		fuzzDataButton = new JButton("Fuzz Data");
		fuzzDataButton.addActionListener(this);
		fuzzDataButton.setEnabled(false);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		addNewFuzzTestButton = new JButton();				    
		addNewFuzzTestButton.setIcon(new ImageIcon(cl.getResource("images/green_plus_sign.png")));
		addNewFuzzTestButton.addActionListener(this);
		
		setLayout(new MigLayout());
		
		// Add the components to the dialog box.
		JPanel fuzzTestPanel = new JPanel();
		fuzzTestPanel.setLayout(new MigLayout("inset 10", "[]", "[]20[][]20[]"));	
		fuzzTestPanel.add(dataTypeLabel, "split 2, h 25");
		fuzzTestPanel.add(comboBox, "w 150, wrap");
		fuzzTestPanel.add(fuzzTestLabel, "h 25, wrap");
		fuzzTestPanel.add(fuzzTestAreaScrollPane, "split 2, h 250, w 250");		
		fuzzTestPanel.add(addNewFuzzTestButton, "width 15:15:15, height 15:15:15, gaptop push, wrap");
		fuzzTestPanel.	add(fuzzDataButton, "split, align right");
		fuzzTestPanel.	add(cancelButton, "align right");

		add(xmlAreaScrollPane, "dock north");
		add(fuzzTestPanel, "dock south");
		
		pack();
		setSize(300, 450);
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
		if (e.getSource().equals(fuzzDataButton)) {
			
			tree = fuzzParser.getTree();
			XMLNode selectedData = null;
			
			try {
				selectedData = (XMLNode) tree.getLastSelectedPathComponent();
			}
			catch (Exception ex)
			{
				selectedData = null;
			}
			
			// expand all nodes in the tree
			for(int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
			
			// save the index of the current selected row
			int selectedRows[] = tree.getSelectionRows();
			int selectedRow = -1;
			if(selectedRows != null && selectedRows.length > 0) {
				selectedRow = selectedRows[0];
			}
			
			// save the original packet
			
			if(selectedData == null) 
			{
				JOptionPane.showMessageDialog(this, 
						"Data must be selected!",
						"Invalid Data",
						JOptionPane.WARNING_MESSAGE);
			}
			else if ((!selectedData.isLeaf()) || (!selectedData.isText()))
			{
				JOptionPane.showMessageDialog(this, 
						"Selected field is a tag element!",
						"Invalid Data",
						JOptionPane.WARNING_MESSAGE);
			}
			else 
			{
				packetData = (HTTPPacketDO)packetController.getPacket(packetDataId);

				DataTypeDO selectedDataType = getSelectedDataType();
				Set<FuzzTestDO> fuzzTests = selectedDataType.getOwnerSet();		
				
				selectedFuzzTests = fuzzTestList.getSelectedIndices();
				FuzzTestDO fuzzTest = null;
				
				for(int selectedIdx : selectedFuzzTests)				
				{		
					Iterator<FuzzTestDO> iter = fuzzTests.iterator();
					while(iter.hasNext())
					{
						fuzzTest = iter.next();
						if(fuzzTest.getFuzzTestName() == (String) fuzzTestListModel.get(selectedIdx)) {
							break;
						}
					}
				
					if(fuzzTest == null)
					{
						JOptionPane.showMessageDialog(this, 
								"Fuzz Test must be selected!",
								"Invalid Data",
								JOptionPane.WARNING_MESSAGE);
					}
					else
					{
						contentData = new HTTPContentDataDO(selectedData.toString(), fuzzTest.getId(), selectedData.getParentNode().getNodeName());
						contentData = contentDataController.addHTTPContentData(packetData, contentData);						
						
						fuzzTestResult = new FuzzTestResultDO(fuzzTest.getId(), selectedDataType.getDataTypeName());
						fuzzTestResult = fuzzTestResultController.addFuzzTestResult(packetData, fuzzTestResult);						
						
						packetData.addFuzzTest(fuzzTest);
						fuzzTest.addOwner(packetData);						
												
						state.setContentDataId(contentData.getId());
						
						// Fuzz the selected content data and update the packet.
						// save the original packet
						AbstractPacketDO oPacket = null;
						if(fuzzParser.getPacket() instanceof HTTPPacketDO)
						{
							oPacket = new HTTPPacketDO(fuzzParser.getPacket());
						}
		
						selectedData.setValue(fuzzTest.getFuzzValue());					
						fuzzParser.setContent(fuzzParser.toString());
						
						// Update the appropriate Fuzzer tab with the fuzzed packet.
						if(originalPacket.getType() == MessageType.Request)
						{
							fuzzRequestPacketTableModel.packetArrived(fuzzParser.getPacket(), contentData.getId());
							fuzzRequestPacketTableModel.fireTableDataChanged();
						} else {
							fuzzResponsePacketTableModel.packetArrived(fuzzParser.getPacket(), contentData.getId());
							fuzzResponsePacketTableModel.fireTableDataChanged();
						}
						
						// TODO reset fuzzer
						//selectedData.setValue(saved);
						fuzzParser = new HttpXMLPacketParser();
						
						if(oPacket != null) {
							fuzzParser.setPacket(oPacket);
						}
						
						tree = fuzzParser.getTree();
						
						// expand all nodes in the tree
						for(int i = 0; i < tree.getRowCount(); i++) {
							tree.expandRow(i);
						}
						
						// set the row selection
						tree.setSelectionRow(selectedRow);
						
						// update the selected data
						selectedData = (XMLNode)tree.getLastSelectedPathComponent();
					}
				}
				
				setVisible(false);
			}			
		} else if (e.getSource().equals(addNewFuzzTestButton)) {						
			AddFuzzTest newWizard = new AddFuzzTest(getSelectedDataType());			
			newWizard.setVisible(true);
			newWizard.toFront();
			newWizard.repaint();
			updateComboBoxFromDatabase();
			updateFuzzTestListFromDatabase();			
		} else if(e.getSource().equals(comboBox)) {
			updateFuzzTestListFromDatabase();
		} else if (e.getSource().equals(cancelButton)) {			
			setVisible(false);			
		} 
	}

	public Object getSelectionModel() {
		return fuzzDataButton;
		// TODO Auto-generated method stub
		//return workspaceTable.getSelectionModel();
	}
	
	public void valueChanged(ListSelectionEvent e) {
		
		if(e.getSource().equals(fuzzTestList))
		{
			int selectedIdx = fuzzTestList.getSelectedIndex();
	
			if (selectedIdx >= 0) {				
				fuzzDataButton.setEnabled(true);
			} else {				
				fuzzDataButton.setEnabled(false);
			}
		}
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
			
			// Sort the list.
			fuzzTestListModel = fuzzTestUtils.sortList(fuzzTestListModel);		
		}
	}
	
	public void updateComboBoxFromDatabase() {
		comboBoxModel.removeAllElements();	
		
		// Update the combo box with data types retrieved from the database.
		for(DataTypeDO dataType : dataTypeController.getDataTypes())
		{
			// Do not add duplicates.
			if(!comboBoxModel.equals(dataType.getDataTypeName()))
			{
				comboBoxModel.addElement(dataType.getDataTypeName());
			}
		}
	}
	
	// Get the selected data type object from the database.
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
