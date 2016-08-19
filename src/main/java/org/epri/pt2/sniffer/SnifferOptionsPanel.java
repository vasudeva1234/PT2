//*********************************************************************************************************************
// SnifferOptionsPanel.java
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

import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.InterfaceType;
import org.epri.pt2.gui.model.DeviceDOTableModel;

/**
 * Provides a panel to select the device and configure its options.
 * 
 * @author Tam Do
 * 
 */
public class SnifferOptionsPanel extends JPanel implements
		ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5859933283703073000L;
	protected EthernetOptionsPanel ethernetPanel;
	protected JTable table;
	protected DeviceDOTableModel model;
	protected CardLayout cl;
	protected JPanel cards;

	public SnifferOptionsPanel() {
		super();

		model = new DeviceDOTableModel();
		table = new JTable(model);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setPreferredWidth(40);
		idColumn.setMaxWidth(40);

		setLayout(new MigLayout());

		JScrollPane scrollPane = new JScrollPane(table);

		cards = new JPanel(new CardLayout());
		cl = (CardLayout) cards.getLayout();

		JPanel emptyPanel = new JPanel();
		emptyPanel.setLayout(new MigLayout());
		emptyPanel.add(new JLabel("Please select a device."));

		ethernetPanel = new EthernetOptionsPanel(this);

		cards.add(emptyPanel, "None");
		cards.add(InterfaceType.Ethernet.toString(), ethernetPanel);

		add(scrollPane, "dock center");
		add(cards, "dock south");
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(table.getSelectionModel())) {
			int selectedRow = table.getSelectedRow();

			if (selectedRow >= 0) {
				InterfaceType type = (InterfaceType) model.getValueAt(
						selectedRow, 2);
				if (model.getValueAt(selectedRow, 5) == "Connected") {
					ethernetPanel.snifferButton.setSelected(true);
				} else if (model.getValueAt(selectedRow, 5) == "Not Connected") {
					ethernetPanel.snifferButton.setSelected(false);
				}
				if (type.equals(InterfaceType.Ethernet)) {
					cl.show(cards, InterfaceType.Ethernet.toString());
					return;
				}
			}

			cl.show(cards, "None");
		} else {
			model.fireTableDataChanged();
		}
	}

}
