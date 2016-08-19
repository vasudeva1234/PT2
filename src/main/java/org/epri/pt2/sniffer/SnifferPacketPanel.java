//*********************************************************************************************************************
// SnifferPacketPanel.java
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
package org.epri.pt2.sniffer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.util.regex.PatternSyntaxException;
import org.epri.pt2.DO.DeviceDO;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.DNP3LinkHeader;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.controller.PacketController;
import org.epri.pt2.gui.DNP3InfoPanel;
import org.epri.pt2.gui.model.RequestPacketTableModel;
import org.epri.pt2.gui.model.ResponsePacketTableModel;
import org.epri.pt2.gui.model.DataPacketTableModel;
import org.epri.pt2.packetparser.AbstractPacketParser;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
import org.epri.pt2.reassembler.HttpUtils.MessageType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdesktop.swingx.JXTable;

import at.HexLib.library.HexLib;

public class SnifferPacketPanel extends JPanel implements
		ListSelectionListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4050190664305067631L;
	private static final SnifferPacketPanel snifferPacketPanel;

	protected SnifferController controller = SnifferController.getInstance();
	protected DataPacketTableModel model = DataPacketTableModel.getInstance();
	protected PacketController packetController = PacketController
			.getInstance();
	protected RequestPacketTableModel requestPacketTableModel = RequestPacketTableModel
			.getInstance();
	protected ResponsePacketTableModel responsePacketTableModel = ResponsePacketTableModel
			.getInstance();
	protected JXTable packetTable;
	protected RSyntaxTextArea packetArea;
	protected DNP3InfoPanel dnp3InfoPanel;
	protected HexLib hexEditor;
	protected AbstractPacketParser httpXMLParser;
	protected JLabel filterLabel;
	protected JTextField filterText;
	protected JButton filterApplyButton;
	protected JButton filterResetButton;
	protected DeviceDO runningDevice;

	protected JMenuItem menuItemSendToFuzzer;
	protected JMenuItem menuItemClearPackets;

	protected TableRowSorter<TableModel> sorter;

	static {
		snifferPacketPanel = new SnifferPacketPanel();
	}

	public static SnifferPacketPanel getInstance() {
		return snifferPacketPanel;
	}

	public void loadPackets() {
		model.loadProjectPackets();

		httpXMLParser.reset();
		packetArea.setText("");
		hexEditor.setByteContent(new byte[0]);
	}

	/**
	 * 
	 */
	private SnifferPacketPanel() {
		super();

		final JPopupMenu popupMenu = new JPopupMenu();

		httpXMLParser = new HttpXMLPacketParser(new ByteArrayInputStream(
				"EMPTY".getBytes()));

		// Create the popup menu items
		menuItemSendToFuzzer = new JMenuItem("Send to Fuzzer");
		menuItemSendToFuzzer.addActionListener(this);
		popupMenu.add(menuItemSendToFuzzer);

		// Create clear packets menu item
		menuItemClearPackets = new JMenuItem("Clear all packets");
		menuItemClearPackets.addActionListener(this);
		popupMenu.add(menuItemClearPackets);

		// Create the packet table
		packetTable = new JXTable(model);
		packetTable.setColumnControlVisible(true);
		packetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		packetTable.getSelectionModel().addListSelectionListener(this);

		sorter = new TableRowSorter<TableModel>(model);
		packetTable.setRowSorter(sorter);

		// Create the raw text view
		packetArea = new RSyntaxTextArea();
		packetArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		packetArea.setAntiAliasingEnabled(true);
		packetArea.setEditable(false);

		// Create the Hex Editor view
		hexEditor = new HexLib();
		hexEditor.setASCIIEditorVisible(true);

		// Create the packet filter
		filterLabel = new JLabel("Filter Expression:");
		filterText = new JTextField();
		filterText.setToolTipText("Enter a regular expression.");
		filterApplyButton = new JButton("Apply Filter");
		filterApplyButton
				.setToolTipText("Apply the regular expression to the packet list.");
		filterApplyButton.addActionListener(this);
		filterResetButton = new JButton("Reset");
		filterResetButton
				.setToolTipText("Remove any applied filters from the packet list display.");
		filterResetButton.addActionListener(this);

		// Create a panel for the filter controls
		JPanel filterControlPanel = new JPanel();
		filterControlPanel.setLayout(new MigLayout("wrap 4"));
		filterControlPanel.add(filterLabel);
		filterControlPanel.add(filterText, "growx, pushx");
		filterControlPanel.add(filterApplyButton);
		filterControlPanel.add(filterResetButton);

		// Create the scroll panes for the packet table and packet text editor
		RTextScrollPane packetAreaScrollPane = new RTextScrollPane(packetArea);
		JScrollPane packetTableScrollPane = new JScrollPane(packetTable);

		// Create a panel for the packet list view
		JPanel packetListPanel = new JPanel();
		packetListPanel.setLayout(new MigLayout());
		packetListPanel.add(packetTableScrollPane, "dock center");
		packetListPanel.add(filterControlPanel, "dock south");

		// Create a panel for the hex editor
		JPanel hexEditorPanel = new JPanel(new MigLayout());
		dnp3InfoPanel = new DNP3InfoPanel();
		hexEditorPanel.add(dnp3InfoPanel, "wrap");
		hexEditorPanel.add(hexEditor, "span");

		// Add the xml parser and text editor to tabbed panes
		JTabbedPane tabbedPanes = new JTabbedPane();
		tabbedPanes.add("HTTP/XML Parser", httpXMLParser);
		tabbedPanes.add("Raw/Text", packetAreaScrollPane);
		tabbedPanes.add("DNP3 Hex Editor", hexEditorPanel);

		// Create an adjustable split pane between the packet viewer and the
		// packet list
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				packetListPanel, tabbedPanes);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);

		// Add the split pane to the current panel
		setLayout(new MigLayout());
		add(splitPane, "dock center");

		// Add a mouse listener for sending packets to the fuzzer
		packetTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = packetTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < packetTable.getRowCount()) {
					packetTable.setRowSelectionInterval(r, r);
				} else {
					packetTable.clearSelection();
				}

				int rowindex = packetTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int r = packetTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < packetTable.getRowCount()) {
					packetTable.setRowSelectionInterval(r, r);
				} else {
					packetTable.clearSelection();
				}

				int rowindex = packetTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(packetTable.getSelectionModel())) {
			int selectedRow = packetTable.getSelectedRow();

			if (selectedRow > -1) {
				AbstractPacketDO packet = model.getRow(selectedRow);
				// Update the JTree model
				if (packet instanceof HTTPPacketDO) {
					httpXMLParser.setPacket(packet);
					packetArea.setText("");
					hexEditor.setByteContent(httpXMLParser.toString()
							.getBytes());

				} else if (packet instanceof DNP3ApplicationPacketDO) {
					packetArea.setText(httpXMLParser.toString());
					hexEditor.setByteContent(packet.getData());
					dnp3InfoPanel.updatePanel((DNP3ApplicationPacketDO) packet);
				}
			}
		} else {
			if (!e.getValueIsAdjusting()) {
				model.loadProjectPackets();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(menuItemSendToFuzzer)) {
			int selectedRow = packetTable.getSelectedRow();

			if (selectedRow > -1) {
				AbstractPacketDO packet = model.getRow(selectedRow);
				packet.setFuzzed(true);
				packetController.updatePacket(packet);

				if (packet.getType() == MessageType.Request) {
					requestPacketTableModel.packetArrived(packet,
							packet.getId());
				} else if (packet.getType() == MessageType.Response) {
					responsePacketTableModel.packetArrived(packet,
							packet.getId());
				}
			}
		} else if (e.getSource().equals(filterApplyButton)) {
			// validate and apply the regular expression
			String text = filterText.getText();
			try {
				sorter.setRowFilter(RowFilter.regexFilter(text));
			} catch (PatternSyntaxException pse) {
				// print the exception
				System.err.println("Bad regex pattern");
			}

		} else if (e.getSource().equals(filterResetButton)) {
			sorter.setRowFilter(null);
		} else if (e.getSource().equals(menuItemClearPackets)) {
			if (EthernetOptionsPanel.getRunningDevice() != null) {
				controller.stop(EthernetOptionsPanel.getRunningDevice());
				model.removeAll();
				controller.start(EthernetOptionsPanel.getRunningDevice(),EthernetOptionsPanel.getRunningDevice().getName());
			}
			model.removeAll();

			// Reset the XML tree and Raw packet view
			httpXMLParser.reset();
			packetArea.setText("");
			hexEditor.setByteContent(new byte[0]);
		}
	}
}
