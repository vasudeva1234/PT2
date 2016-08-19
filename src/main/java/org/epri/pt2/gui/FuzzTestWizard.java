//*********************************************************************************************************************
// FuzzTestWizard.java
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.listeners.ViewCallbackInterface;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
//import org.epri.pt2.sniffer.AbstractDataPacket;
//import org.epri.pt2.sniffer.EthernetDataPacket;

/**
 * Implements a wizard for configuring a fuzz test case.
 * 
 * @author Southwest Research Institute
 * 
 */
public class FuzzTestWizard extends JDialog implements ViewCallbackInterface,
		ActionListener, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3054827509813327745L;
	// private FuzzTestController controller = FuzzTestController.getInstance();
	// private JTable fuzzTestTable;
	// private FuzzTestDOTableModel model;
	private JButton addButton;
	private JButton okButton;
	private JButton addNewDataTypesButton;
	private JButton addNewFuzzTestsButton;
	private JList dataTypeList;
	private JList fuzzTestList;
	// private JTabbedPane parentPane;
	public WorkspaceFrame parentFrame;
	public HttpXMLPacketParser parser;

	// public static enum DataTypes {Integer, Char, Boolean, Date};
	String dataTypes[] = { "Integer", "Char", "Boolean", "Date" };
	// TODO: read these from a file
	String fuzzTests[] = { "OutOfRangeNeg", "OutOfRangeLarge",
			"OutOfRangeZero", "OutOfRangeEmpty" };

	// private SelectionState state = SelectionState.getInstance();

	public FuzzTestWizard(WorkspaceFrame frame, AbstractPacketDO packet) {
		super(frame);
		parentFrame = frame;
		// controller.registerListener(this);

		initComponents(packet);
		// dataChanged();
	}

	private void initComponents(AbstractPacketDO packet) {
		setTitle("Fuzz Tests");

		parser = new HttpXMLPacketParser(new ByteArrayInputStream("EMPTY".getBytes()),
				false);
		// Get the bytes from the packet
		// InputStream stream = new ByteArrayInputStream(packet.getData());

		// Update the JTree model
		parser.setPacket(packet);
		// parser.setModel(stream);

		ClassLoader cl = this.getClass().getClassLoader();
		/*
		 * model = new WorkspaceDOTableModel(controller);
		 * 
		 * workspaceTable = new JTable(model);
		 * workspaceTable.getSelectionModel().addListSelectionListener(this);
		 * workspaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 * 
		 * TableColumn idColumn = workspaceTable.getColumnModel().getColumn(0);
		 * idColumn.setPreferredWidth(1);
		 * 
		 * JScrollPane scrollPane = new JScrollPane(workspaceTable);
		 */
		setLayout(new MigLayout("inset 5", "[]25[][]", "[][]25[]10"));

		// Create the xml pane.
		JScrollPane xmlAreaScrollPane = new JScrollPane(parser);

		// Create the list boxes and labels.
		JLabel dataTypeLabel = new JLabel("Data Types: ");
		JLabel fuzzTestLabel = new JLabel("Available Fuzz Tests: ");

		dataTypeList = new JList(dataTypes);
		dataTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fuzzTestList = new JList(fuzzTests);

		// Create the buttons.
		addButton = new JButton("Add");
		addButton.addActionListener(this);

		okButton = new JButton("Ok");
		okButton.addActionListener(this);

		addNewDataTypesButton = new JButton();
		addNewDataTypesButton.setIcon(new ImageIcon(cl
				.getResource("images/green_plus_sign.png")));

		addNewFuzzTestsButton = new JButton();
		addNewFuzzTestsButton.setIcon(new ImageIcon(cl
				.getResource("images/green_plus_sign.png")));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(okButton);

		// Add the components to the dialog box.
		add(xmlAreaScrollPane, "dock north");
		add(dataTypeLabel, "h 25");
		add(fuzzTestLabel, "h 25, wrap");
		add(dataTypeList, "split 2, h 250, w 250");
		add(addNewDataTypesButton,
				"width 15:15:15, height 15:15:15, gaptop push");
		add(fuzzTestList, "split 2, h 250, w 250");
		add(addNewFuzzTestsButton,
				"width 15:15:15, height 15:15:15, gaptop push, wrap");
		add(buttonPanel, "skip, align right");

		pack();
		setSize(500, 500);
		setLocationRelativeTo(null);
		setModal(true);
	}

	public void dataChanged() {
		/*
		 * if (state.getWorkspaceId() > 0) { okButton.setEnabled(true); } else {
		 * okButton.setEnabled(false); }
		 */
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addButton)) {
			// String name = JOptionPane.showInputDialog(null, "Name: ",
			// "FuzzTest");
			Object[] selectedFuzzTests = fuzzTestList.getSelectedValues();

			for (int i = 0; i < selectedFuzzTests.length; i++) {
				/*
				 * 
				 * FuzzTestDO fuzzTest = new
				 * FuzzTestDO(selected_item[i].fuzzTestName);
				 * fuzzTest.replacementField =
				 * selected_item[i].replacementField; fuzzTest.dataType =
				 * dataTypeList[dataTypeList.getSelectedIndex()];
				 * controller.addFuzzTest(fuzzTest); model.addRow(fuzzTest);
				 */
			}
		} else if (e.getSource().equals(okButton)) {
			// Select Fuzzer tab once this window is closed.
			JTabbedPane pane = parentFrame.getTabbedPane();

			pane.setSelectedComponent(parentFrame.getFuzzerComponent());
			setVisible(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		/*
		 * List<WorkspaceDO> workspaces = controller.getWorkspaces(); int
		 * selectedRow = workspaceTable.getSelectedRow();
		 * 
		 * if (selectedRow >= 0) {
		 * state.setWorkspaceId(workspaces.get(selectedRow).getId());
		 * okButton.setEnabled(true); } else { okButton.setEnabled(false); }
		 */
	}

	public Object getSelectionModel() {
		return addButton;
		// TODO Auto-generated method stub
		// return workspaceTable.getSelectionModel();
	}

}
