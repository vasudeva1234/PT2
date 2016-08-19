//*********************************************************************************************************************
// RequestPanel.java
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

import java.util.List;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
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
import org.epri.pt2.fuzzer.request.RequestResult;
import org.epri.pt2.gui.FuzzHTTPPacketWizard;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.gui.model.FuzzRequestPacketTableModel;
import org.epri.pt2.gui.model.FuzzRequestPacketTableModel.FuzzRequestColumnNames;
import org.epri.pt2.gui.model.RequestPacketTableModel;
import org.epri.pt2.gui.model.RequestPacketTableModel.RequestColumnNames;
import org.epri.pt2.listeners.ViewCallbackInterface;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
import org.epri.pt2.proxy.FilterDO;
import org.epri.pt2.reassembler.HttpUtils.MessageType;
//import org.epri.pt2.sniffer.AbstractDataPacket;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.jdesktop.swingx.JXTable;

/**
 * Provides a panel for configuring fuzz test cases.
 * 
 * @author Southwest Research Institute
 *
 */
public class RequestPanel extends JPanel implements ActionListener,
		ViewCallbackInterface, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3439395956957151213L;

	protected JTable fuzzTestTable;
	protected JXTable requestPacketTable;
	protected JXTable fuzzRequestPacketTable;
	protected PacketController packetController = PacketController
			.getInstance();
	protected HTTPContentDataController contentDataController = HTTPContentDataController
			.getInstance();
	protected FuzzTestResultController fuzzTestResultController = FuzzTestResultController
			.getInstance();
	public RequestPacketTableModel requestPacketTableModel = RequestPacketTableModel
			.getInstance();
	private FuzzRequestPacketTableModel fuzzRequestPacketTableModel = FuzzRequestPacketTableModel
			.getInstance();
	private FuzzerHelper fuzzerHelper;
	private FilterDO filter;
	private RequestResult result;
	private HttpXMLPacketParser parser;
	AbstractPacketDO packetData = null;

	private JButton runButton;
	private JButton viewDiffButton;
	private JButton removePacketButton;
	private JButton removeFuzzPacketButton;
	private JButton selectAllButton;
	private JMenuItem menuItemFuzzData;

	protected TableRowSorter<TableModel> sorter;

	/**
	 * @param controller
	 * @param state
	 */
	public RequestPanel() {
		super();

		initRequestPacketTable();

		fuzzerHelper = new FuzzerHelper();
		filter = new FilterDO();

		parser = new HttpXMLPacketParser(new ByteArrayInputStream(
				"EMPTY".getBytes()), false);

		JLabel packetTableLabel = new JLabel("Original Packets:");
		JLabel fuzzPacketTableLabel = new JLabel("Fuzz Packets: ");

		TableColumn col;
		// Create the packet table
		requestPacketTable = new JXTable(requestPacketTableModel);
		requestPacketTable.setColumnControlVisible(true);
		requestPacketTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requestPacketTable.getSelectionModel().addListSelectionListener(this);
		col = requestPacketTable.getColumnModel().getColumn(
				RequestColumnNames.ID.ordinal());
		col.setPreferredWidth(10);
		col = requestPacketTable.getColumnModel().getColumn(
				RequestColumnNames.Payload.ordinal());
		col.setPreferredWidth(200);

		sorter = new TableRowSorter<TableModel>(requestPacketTableModel);
		requestPacketTable.setRowSorter(sorter);

		fuzzRequestPacketTable = new JXTable(fuzzRequestPacketTableModel);
		fuzzRequestPacketTable.setColumnControlVisible(true);
		fuzzRequestPacketTable.getSelectionModel().addListSelectionListener(
				this);
		col = fuzzRequestPacketTable.getColumnModel().getColumn(
				FuzzRequestColumnNames.ID.ordinal());
		col.setPreferredWidth(10);
		col = fuzzRequestPacketTable.getColumnModel().getColumn(
				FuzzRequestColumnNames.Result.ordinal());
		col.setPreferredWidth(200);

		sorter = new TableRowSorter<TableModel>(fuzzRequestPacketTableModel);
		fuzzRequestPacketTable.setRowSorter(sorter);

		JScrollPane requestPacketTableScrollPane = new JScrollPane(
				requestPacketTable);
		JScrollPane fuzzRequestTableScrollPane = new JScrollPane(
				fuzzRequestPacketTable);

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
		packetListPanel.add(requestPacketTableScrollPane,
				"dock center, grow, push, wrap");
		packetListPanel.add(removePacketButton, "align right, pushx");

		JPanel fuzzPacketListPanel = new JPanel();
		fuzzPacketListPanel.setLayout(new MigLayout());
		fuzzPacketListPanel.add(fuzzPacketTableLabel, "wrap");
		fuzzPacketListPanel.add(fuzzRequestTableScrollPane,
				"dock center, wrap, grow, push");
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
		requestPacketTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = requestPacketTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < requestPacketTable.getRowCount()) {
					requestPacketTable.setRowSelectionInterval(r, r);
				} else {
					requestPacketTable.clearSelection();
				}

				int rowindex = requestPacketTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int r = requestPacketTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < requestPacketTable.getRowCount()) {
					requestPacketTable.setRowSelectionInterval(r, r);
				} else {
					requestPacketTable.clearSelection();
				}

				int rowindex = requestPacketTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
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

		if (e.getSource().equals(requestPacketTable.getSelectionModel())) {

			int selectedRow = requestPacketTable.getSelectedRow();

			if (selectedRow >= 0) {
				removePacketButton.setEnabled(true);
			} else {
				removePacketButton.setEnabled(false);
			}
		} else if (e.getSource().equals(
				fuzzRequestPacketTable.getSelectionModel())) {

			int selectedRow = fuzzRequestPacketTable.getSelectedRow();

			if (selectedRow >= 0) {
				removeFuzzPacketButton.setEnabled(true);
				viewDiffButton.setEnabled(true);
				runButton.setEnabled(true);
				SelectionState.getInstance().setContentDataId(
						fuzzRequestPacketTableModel
								.getContentDataId(selectedRow));
			} else {
				removeFuzzPacketButton.setEnabled(false);
				viewDiffButton.setEnabled(false);
				runButton.setEnabled(false);

				if (!e.getValueIsAdjusting()) {
					fuzzRequestPacketTableModel.fireTableDataChanged();
				}

			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(menuItemFuzzData)) {

			int selectedRow = requestPacketTable.getSelectedRow();

			if (selectedRow > -1) {
				AbstractPacketDO packet = requestPacketTableModel
						.getRow(selectedRow);
				int packetId = requestPacketTableModel
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

			// get the list of selected rows
			int[] selectedIndices = fuzzRequestPacketTable.getSelectedRows();

			for (int row : selectedIndices) {

				// get the packet for the row
				AbstractPacketDO packet = fuzzRequestPacketTableModel
						.getRow(row);
				// int contentDataId =
				// fuzzRequestPacketTableModel.getContentDataId(row);

				parser.setPacket(packet);
				filter.setReplaceRule(parser.getRootNode());

				filter.setReplaceVal(new String(packet.getData()));

				result = fuzzerHelper.sendRequest(packet);
				if (result.exception != null) {
					FuzzTestResultDO fuzzTestResult = fuzzRequestPacketTableModel
							.getFuzzTestResult(row);
					fuzzTestResult.setResult(result.exception.toString());
					fuzzTestResultController
							.updateFuzzTestResult(fuzzTestResult);
				} else {
					FuzzTestResultDO fuzzTestResult = fuzzRequestPacketTableModel
							.getFuzzTestResult(row);
					fuzzTestResult.setResult((new String(result.data)
							.split("\r\n")[0]));
					fuzzTestResultController
							.updateFuzzTestResult(fuzzTestResult);
				}
				dataChanged();
			}

		} else if (e.getSource().equals(viewDiffButton)) {
			int packetTableSelectedRow = requestPacketTable.getSelectedRow();
			int fuzzPacketTableSelectedRow = fuzzRequestPacketTable
					.getSelectedRow();
			if (fuzzPacketTableSelectedRow >= 0) {
				Window parentWindow = SwingUtilities
						.windowForComponent(viewDiffButton);
				AbstractPacketDO originalPacket = requestPacketTableModel
						.getRow(packetTableSelectedRow);
				AbstractPacketDO fuzzPacket = fuzzRequestPacketTableModel
						.getRow(fuzzPacketTableSelectedRow);

				// Setup the criteria to search on. This is used by the viewer.
				String searchTag = fuzzRequestPacketTableModel.getContentData(
						fuzzPacketTableSelectedRow).getParentName();
				SearchCriteria.getInstance().setSearchTag(searchTag);
				String searchData = fuzzRequestPacketTableModel.getContentData(
						fuzzPacketTableSelectedRow).getContentData();
				SearchCriteria.getInstance().setSearchData(searchData);
				String searchValue = fuzzRequestPacketTableModel.getFuzzTest(
						fuzzPacketTableSelectedRow).getFuzzValue();
				SearchCriteria.getInstance().setSearchFuzzValue(searchValue);

				ViewDiffWindow diffWindow = new ViewDiffWindow(originalPacket,
						fuzzPacket);
				diffWindow.showDialog(parentWindow);

			}
		} else if (e.getSource().equals(removePacketButton)) {
			int packetTableSelectedRow = requestPacketTable.getSelectedRow();
			if (packetTableSelectedRow >= 0) {
				// Remove all fuzz tests associated with this packet.
				fuzzRequestPacketTableModel.removeRows(requestPacketTableModel
						.getRow(packetTableSelectedRow));

				// Remove selected packet.
				requestPacketTableModel.removeRow(packetTableSelectedRow);
				this.repaint();
			}
		} else if (e.getSource().equals(removeFuzzPacketButton)) {
			int[] selectedIndices = fuzzRequestPacketTable.getSelectedRows();
			int[] ids = new int[selectedIndices.length];
			int idx = 0;
			for (int fuzzPacketTableSelectedRow : selectedIndices) {
				// Get the ContentDataDO ids for each selected row.
				ids[idx] = fuzzRequestPacketTableModel
						.getContentDataId(fuzzPacketTableSelectedRow);
				idx++;
			}
			fuzzRequestPacketTableModel.removeRowWithIds(ids);
			this.repaint();
		} else if (e.getSource().equals(selectAllButton)) {
			ListSelectionModel selectionModel = fuzzRequestPacketTable
					.getSelectionModel();
			selectionModel.setSelectionInterval(0,
					fuzzRequestPacketTable.getRowCount() - 1);

		}
	}

	public void initRequestPacketTable() {
		List<AbstractPacketDO> packetList = packetController.getPacketList();
		for (AbstractPacketDO packet : packetList) {
			if (packet.isFuzzed() && (packet.getType() == MessageType.Request)) {
				requestPacketTableModel.packetArrived(packet, packet.getId());
			}
		}
	}
	/*
	 * TODO: recreate fuzz packet public void updateFuzzRequestPacketTable(int
	 * row) { fuzzRequestPacketTableModel.removeAll();
	 * 
	 * PacketParser fuzzParser; JTree tree;
	 * 
	 * // Rebuild the fuzz packets. int selectedPacketId =
	 * requestPacketTableModel.getPacketDOId(row); if(selectedPacketId != -1) {
	 * EthernetDataPacket originalPacket = new
	 * EthernetDataPacket(PacketController
	 * .getInstance().getPacket(selectedPacketId));
	 * 
	 * fuzzParser = new PacketParser(new
	 * ByteArrayInputStream("EMPTY".getBytes()), false);
	 * 
	 * // Update the JTree model parser.setPacket(originalPacket);
	 * fuzzParser.setPacket(new EthernetDataPacket(originalPacket));
	 * 
	 * tree = fuzzParser.getTree(); //XMLNode selectedData = (XMLNode)
	 * tree.getLastSelectedPathComponent(); // Fuzz the selected content data
	 * and update the packet. for(ContentDataDO contentData :
	 * ContentDataController.getInstance().getContentDataList()) { FuzzTestDO
	 * fuzzTest =
	 * FuzzTestController.getInstance().getFuzzTest(contentData.getFuzzTestId
	 * ()); for(int i : tree.) selectedData.getParentNode().getNodeName()
	 * contentData.setValue(fuzzTest.getFuzzValue());
	 * fuzzParser.setContent(fuzzParser.toString());
	 * requestPacketTableModel.packetArrived(packet, packetData.getId()); }
	 * 
	 * 
	 * 
	 * } }
	 */
}
