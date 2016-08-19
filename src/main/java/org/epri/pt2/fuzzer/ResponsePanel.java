//*********************************************************************************************************************
// ResponsePanel.java
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
package org.epri.pt2.fuzzer;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.FuzzTestResultDO;
import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.controller.HTTPContentDataController;
import org.epri.pt2.controller.FuzzTestResultController;
import org.epri.pt2.controller.PacketController;
import org.epri.pt2.gui.FuzzHTTPPacketWizard;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.gui.model.FuzzResponsePacketTableModel;
import org.epri.pt2.gui.model.FuzzResponsePacketTableModel.FuzzResponseColumnNames;
import org.epri.pt2.gui.model.ResponsePacketTableModel;
import org.epri.pt2.gui.model.RequestPacketTableModel.RequestColumnNames;
import org.epri.pt2.gui.model.ResponsePacketTableModel.ResponseColumnNames;
import org.epri.pt2.listeners.ViewCallbackInterface;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
import org.epri.pt2.proxy.FilterDO;
import org.epri.pt2.proxy.ProxyController;
import org.epri.pt2.reassembler.HttpUtils.MessageType;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.jdesktop.swingx.JXTable;

/**
 * Provides a panel for configuring fuzz test cases.
 * 
 * @author Southwest Research Institute
 * 
 */
public class ResponsePanel extends JPanel implements ActionListener,
		ViewCallbackInterface, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3657926907212206593L;

	protected JTable fuzzTestTable;
	protected JXTable responsePacketTable;
	protected JXTable fuzzResponsePacketTable;
	protected PacketController packetController = PacketController
			.getInstance();
	protected HTTPContentDataController contentDataController = HTTPContentDataController
			.getInstance();
	public ResponsePacketTableModel responsePacketTableModel = ResponsePacketTableModel
			.getInstance();
	private FuzzResponsePacketTableModel fuzzResponsePacketTableModel = FuzzResponsePacketTableModel
			.getInstance();
	private FuzzerHelper fuzzerHelper;
	private FilterDO filter;
	private HttpXMLPacketParser parser;
	AbstractPacketDO packetData = null;

	private JButton runButton;
	private JButton viewDiffButton;
	private JButton removePacketButton;
	private JButton removeFuzzPacketButton;
	private JButton selectAllButton;
	private JMenuItem menuItemFuzzData;

	protected TableRowSorter<TableModel> sorter;
	private List<Integer> runningTests;
	private ResultUpdater resultUpdator;
	private Thread updateThread;

	/**
	 * @param controller
	 * @param state
	 */
	public ResponsePanel() {
		super();

		runningTests = new ArrayList<Integer>();

		fuzzerHelper = new FuzzerHelper();
		filter = new FilterDO();

		parser = new HttpXMLPacketParser(new ByteArrayInputStream(
				"EMPTY".getBytes()), false);

		initResponsePacketTable();

		JLabel packetTableLabel = new JLabel("Original Packets:");
		JLabel fuzzPacketTableLabel = new JLabel("Fuzz Packets: ");

		TableColumn col;
		// Create the packet table
		responsePacketTable = new JXTable(responsePacketTableModel);
		responsePacketTable.setColumnControlVisible(true);

		responsePacketTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		responsePacketTable.getSelectionModel().addListSelectionListener(this);
		col = responsePacketTable.getColumnModel().getColumn(
				RequestColumnNames.ID.ordinal());
		col.setPreferredWidth(10);
		col = responsePacketTable.getColumnModel().getColumn(
				ResponseColumnNames.Payload.ordinal());
		col.setPreferredWidth(200);

		sorter = new TableRowSorter<TableModel>(responsePacketTableModel);
		responsePacketTable.setRowSorter(sorter);

		fuzzResponsePacketTable = new JXTable(fuzzResponsePacketTableModel);
		fuzzResponsePacketTable.setColumnControlVisible(true);
		fuzzResponsePacketTable.getSelectionModel().addListSelectionListener(
				this);
		col = fuzzResponsePacketTable.getColumnModel().getColumn(
				RequestColumnNames.ID.ordinal());
		col.setPreferredWidth(10);
		col = fuzzResponsePacketTable.getColumnModel().getColumn(
				FuzzResponseColumnNames.Result.ordinal());
		col.setPreferredWidth(200);

		sorter = new TableRowSorter<TableModel>(fuzzResponsePacketTableModel);
		fuzzResponsePacketTable.setRowSorter(sorter);

		JScrollPane responsePacketTableScrollPane = new JScrollPane(
				responsePacketTable);
		JScrollPane fuzzResponseTableScrollPane = new JScrollPane(
				fuzzResponsePacketTable);

		// Create the popup menu items
		final JPopupMenu popupMenu = new JPopupMenu();
		menuItemFuzzData = new JMenuItem("Fuzz Data");
		menuItemFuzzData.addActionListener(this);
		popupMenu.add(menuItemFuzzData);

		viewDiffButton = new JButton("View Diff");
		viewDiffButton.addActionListener(this);
		viewDiffButton.setEnabled(false);
		removePacketButton = new JButton("Remove");
		removePacketButton.addActionListener(this);
		removePacketButton.setEnabled(false);
		removeFuzzPacketButton = new JButton("Remove");
		removeFuzzPacketButton.addActionListener(this);
		removeFuzzPacketButton.setEnabled(false);
		selectAllButton = new JButton("Select All");
		selectAllButton.addActionListener(this);

		runButton = new JButton("Run Fuzzer");
		runButton.addActionListener(this);
		runButton.setEnabled(false);

		setLayout(new MigLayout());
		JPanel packetListPanel = new JPanel();
		packetListPanel.setLayout(new MigLayout());
		packetListPanel.add(packetTableLabel, "wrap");
		packetListPanel.add(responsePacketTableScrollPane,
				"dock center, grow, push, wrap");
		packetListPanel.add(removePacketButton, "align right, pushx");

		JPanel fuzzPacketListPanel = new JPanel();
		fuzzPacketListPanel.setLayout(new MigLayout());
		fuzzPacketListPanel.add(fuzzPacketTableLabel, "wrap");
		fuzzPacketListPanel.add(fuzzResponseTableScrollPane,
				"dock center, grow, push, wrap");
		fuzzPacketListPanel.add(selectAllButton, "split 3, align left");
		fuzzPacketListPanel.add(viewDiffButton, "split 2, gapleft push");
		fuzzPacketListPanel.add(removeFuzzPacketButton, "align right, wrap");
		fuzzPacketListPanel.add(runButton, "align right");

		// Create an adjustable split pane between the packet viewer and the
		// packet list
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				packetListPanel, fuzzPacketListPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);

		add(splitPane, "dock center");

		// Add a mouse listener for sending packets to the fuzzer
		responsePacketTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = responsePacketTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < responsePacketTable.getRowCount()) {
					responsePacketTable.setRowSelectionInterval(r, r);
				} else {
					responsePacketTable.clearSelection();
				}

				int rowindex = responsePacketTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int r = responsePacketTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < responsePacketTable.getRowCount()) {
					responsePacketTable.setRowSelectionInterval(r, r);
				} else {
					responsePacketTable.clearSelection();
				}

				int rowindex = responsePacketTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		resultUpdator = new ResultUpdater();

		updateThread = new Thread(resultUpdator);
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
		return fuzzTestTable.getSelectionModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {

		if (e.getSource().equals(responsePacketTable.getSelectionModel())) {

			int selectedRow = responsePacketTable.getSelectedRow();

			if (selectedRow >= 0) {
				removePacketButton.setEnabled(true);
			} else {
				removePacketButton.setEnabled(false);
			}
		} else if (e.getSource().equals(
				fuzzResponsePacketTable.getSelectionModel())) {

			int selectedRow = fuzzResponsePacketTable.getSelectedRow();

			if (selectedRow >= 0) {
				removeFuzzPacketButton.setEnabled(true);
				viewDiffButton.setEnabled(true);
				runButton.setEnabled(true);
				SelectionState.getInstance().setContentDataId(
						fuzzResponsePacketTableModel
								.getContentDataId(selectedRow));
			} else {
				removeFuzzPacketButton.setEnabled(false);
				viewDiffButton.setEnabled(false);
				runButton.setEnabled(false);

				if (!e.getValueIsAdjusting()) {
					fuzzResponsePacketTableModel.fireTableDataChanged();
				}
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(menuItemFuzzData)) {

			int selectedRow = responsePacketTable.getSelectedRow();

			if (selectedRow > -1) {
				AbstractPacketDO packet = responsePacketTableModel
						.getRow(selectedRow);
				int packetId = responsePacketTableModel
						.getPacketDOId(selectedRow);

				if (packet instanceof HTTPPacketDO) {
					FuzzHTTPPacketWizard fuzzPacketWizard = new FuzzHTTPPacketWizard(
							(HTTPPacketDO)packet, packetId);
					fuzzPacketWizard.setVisible(true);
					fuzzPacketWizard.toFront();
					fuzzPacketWizard.repaint();
				}
			}
		} else if (e.getSource().equals(runButton)) {
			if (!ProxyController.getInstance().isEnabled()) {
				JOptionPane.showMessageDialog(this,
						"Enable the Proxy before attempting to fuzz responses");
			} else {
				int[] selectedIndices = fuzzResponsePacketTable
						.getSelectedRows();
				runningTests.clear();

				for (int row : selectedIndices) {
					AbstractPacketDO packet = fuzzResponsePacketTableModel
							.getRow(row);
					parser.setPacket(packet);
					filter.setReplaceRule(parser.getRootNode());

					filter.setReplaceVal(new String(packet.getData()));

					fuzzerHelper.queueResponse(filter);

					// Add the current row to the running test list
					runningTests.add(row);

					FuzzTestResultDO fuzzTestResult = fuzzResponsePacketTableModel
							.getFuzzTestResult(row);
					fuzzTestResult.setResult("Running");
					FuzzTestResultController.getInstance()
							.updateFuzzTestResult(fuzzTestResult);
				}

				// Start the updateRunningTests thread
				ProxyController.getInstance().setFuzzingEnabled(true);
				resultUpdator.reset();
				updateThread.start();
			}
		} else if (e.getSource().equals(viewDiffButton)) {
			int packetTableSelectedRow = responsePacketTable.getSelectedRow();
			int fuzzPacketTableSelectedRow = fuzzResponsePacketTable
					.getSelectedRow();
			if (fuzzPacketTableSelectedRow >= 0) {
				Window parentWindow = SwingUtilities
						.windowForComponent(viewDiffButton);
				AbstractPacketDO originalPacket = responsePacketTableModel
						.getRow(packetTableSelectedRow);
				AbstractPacketDO fuzzPacket = fuzzResponsePacketTableModel
						.getRow(fuzzPacketTableSelectedRow);

				// Setup the criteria to search on. This is used by the viewer.
				SearchCriteria.getInstance().setSearchTag(
						fuzzResponsePacketTableModel.getContentData(
								fuzzPacketTableSelectedRow).getParentName());
				SearchCriteria.getInstance().setSearchData(
						fuzzResponsePacketTableModel.getContentData(
								fuzzPacketTableSelectedRow).getContentData());
				SearchCriteria.getInstance().setSearchFuzzValue(
						fuzzResponsePacketTableModel.getFuzzTest(
								fuzzPacketTableSelectedRow).getFuzzValue());

				ViewDiffWindow diffWindow = new ViewDiffWindow(originalPacket,
						fuzzPacket);
				diffWindow.showDialog(parentWindow);
			}
		} else if (e.getSource().equals(removePacketButton)) {
			int packetTableSelectedRow = responsePacketTable.getSelectedRow();
			if (packetTableSelectedRow >= 0) {
				// Remove all fuzz tests associated with this packet.
				fuzzResponsePacketTableModel
						.removeRows(responsePacketTableModel
								.getRow(packetTableSelectedRow));

				// Remove selected packet.
				responsePacketTableModel.removeRow(packetTableSelectedRow);
				this.repaint();
			}
		} else if (e.getSource().equals(removeFuzzPacketButton)) {
			int[] selectedIndices = fuzzResponsePacketTable.getSelectedRows();
			int[] ids = new int[selectedIndices.length];
			int idx = 0;
			for (int fuzzPacketTableSelectedRow : selectedIndices) {
				// Get the ContentDataDO ids for each selected row.
				ids[idx] = fuzzResponsePacketTableModel
						.getContentDataId(fuzzPacketTableSelectedRow);
				idx++;
			}
			fuzzResponsePacketTableModel.removeRowWithIds(ids);
			this.repaint();
		} else if (e.getSource().equals(selectAllButton)) {
			ListSelectionModel selectionModel = fuzzResponsePacketTable
					.getSelectionModel();
			selectionModel.setSelectionInterval(0,
					fuzzResponsePacketTable.getRowCount() - 1);

		}
	}

	public void initResponsePacketTable() {
		List<AbstractPacketDO> packetList = packetController.getPacketList();
		for (AbstractPacketDO packet : packetList) {
			if (packet.isFuzzed() && (packet.getType() == MessageType.Response)) {
				responsePacketTableModel.packetArrived(packet, packet.getId());
			}
		}
	}

	protected class ResultUpdater implements Runnable {
		int completedTests = 0;

		public ResultUpdater() {
			completedTests = 0;
		}

		public void reset() {
			completedTests = 0;
		}

		public void run() {
			do {
				int newTotal = runningTests.size()
						- fuzzerHelper.getRemainingResponses();

				if (newTotal != completedTests) {
					// Update the rows in between
					for (int i = completedTests; i < newTotal; i++) {
						// Get the row
						FuzzTestResultDO fuzzTestResult = fuzzResponsePacketTableModel
								.getFuzzTestResult(runningTests.get(i));
						fuzzTestResult.setResult("Completed");
						FuzzTestResultController.getInstance()
								.updateFuzzTestResult(fuzzTestResult);
					}

					completedTests = newTotal;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (completedTests != runningTests.size());

			ProxyController.getInstance().setFuzzingEnabled(false);

		}

	}
}
