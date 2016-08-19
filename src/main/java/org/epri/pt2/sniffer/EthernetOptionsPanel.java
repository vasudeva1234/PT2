//*********************************************************************************************************************
// EthernetOptionsPanel.java
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.gui.model.DataPacketTableModel;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * This class provides a set of ethernet specific protocol options to the user.
 * 
 * @author Tam Do
 * 
 */
public class EthernetOptionsPanel extends AbstractOptionsPanel implements
		ActionListener {

	private SnifferController controller;
    private static DeviceDO runningDevice;
	private JLabel interfaceLabel = new JLabel("Interface:");

	protected JComboBox interfaceCombo = new JComboBox();
	protected List<PcapIf> alldevs;
	protected StringBuilder errbuf;

	public JToggleButton snifferButton;
	protected JButton loadCaptureButton;
	protected JButton applyFilterButton;

	protected JLabel filterLabel;
	protected JTextField filterText;

	private SnifferOptionsPanel snifferPanel;

	public EthernetOptionsPanel(SnifferOptionsPanel snifferPanel) {
		initComponents();
		this.snifferPanel = snifferPanel;
	}

	private void initComponents() {
		runningDevice = null;
		controller = SnifferController.getInstance();

		snifferButton = new JToggleButton("Enable Sniffer");
		snifferButton
				.setToolTipText("Toggle the state of the sniffer from connected to not connected.");
		snifferButton.addActionListener(this);
		loadCaptureButton = new JButton("Load Capture");
		loadCaptureButton
				.setToolTipText("Load a \"pcap\" packet capture into the sniffer display.");
		loadCaptureButton.addActionListener(this);

		filterLabel = new JLabel("Filter Expression:");
		filterText = new JTextField();
		filterText
				.setToolTipText("Define a PCAP filter expression on the stream of pcap packets. Please see the pcap-filter man page for more details.");
		filterText.setEnabled(false);
		applyFilterButton = new JButton("Apply Filter");
		applyFilterButton.setEnabled(false);
		applyFilterButton
				.setToolTipText("Apply the filter expression to the stream of pcap packets");
		applyFilterButton.addActionListener(this);

		setLayout(new MigLayout("wrap 4"));

		alldevs = new ArrayList<PcapIf>();
		errbuf = new StringBuilder();

		// get the list of physical interfaces from JNetPcap
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.ERROR || alldevs.isEmpty()) {
			System.err.printf(errbuf.toString());
		}

		for (PcapIf iface : alldevs) {
			interfaceCombo.addItem(iface.getName());
		}

		add(interfaceLabel);
		add(interfaceCombo);
		add(snifferButton);
		add(loadCaptureButton);
		add(filterLabel);
		add(filterText, "span 2, growx");
		add(applyFilterButton);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1485839816346253771L;

    public static DeviceDO getRunningDevice() {
		return runningDevice;
    }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(snifferButton)) {
			// Retrieve the row selected by the user to enable
			int selectedRow = snifferPanel.table.getSelectedRow();
			// If the selected row is valid
			if (selectedRow > -1) {
				DeviceDO device = snifferPanel.model.getDevice(selectedRow);

				if (snifferButton.isSelected()) {
					if (runningDevice == null) {
						controller.start(device, interfaceCombo.getSelectedItem().toString());
						runningDevice = device;
					} else {
						JOptionPane.showMessageDialog(this,"Cannot enable multiple devices.\nPlease disable current device: " + runningDevice.getName(),"Multiple Enabled Devices",JOptionPane.YES_NO_OPTION);
					}
				} else {
					controller.stop(device);
					runningDevice = null;
				}
				
				snifferButton.setSelected(controller.isOpen(device));
				if (controller.isOpen(device)) {
					PCAPEthListener sniffer = (PCAPEthListener) controller
							.getSniffer(device);
					
					if (sniffer != null) {
						sniffer.addPacketListener(DataPacketTableModel.getInstance());
						filterText.setText(sniffer.getFilter());
						filterText.setEnabled(true);
						applyFilterButton.setEnabled(true);
					} else {
						filterText.setEnabled(false);
						applyFilterButton.setEnabled(false);
					}

				}
			}
		} else if (e.getSource().equals(loadCaptureButton)) {
			// File open dialog
			JFileChooser chooser = new JFileChooser();
			int retVal = chooser.showOpenDialog(this);

			if (retVal == JFileChooser.APPROVE_OPTION) {
				// Create sniffer
				File f = chooser.getSelectedFile();

				// Pass sniffer pcap file
				AbstractSniffer sniffer = new PCAPEthListener(f);
				sniffer.addPacketListener(DataPacketTableModel.getInstance());

				if (!sniffer.isOpen()) {
					sniffer.connect();
				}
			}
		} else if (e.getSource().equals(applyFilterButton)) {
			int selectedRow = snifferPanel.table.getSelectedRow();
			if (selectedRow > -1) {
				DeviceDO device = snifferPanel.model.getDevice(selectedRow);
				PCAPEthListener sniffer = (PCAPEthListener) controller
						.getSniffer(device);
				if (sniffer != null) {
					sniffer.setFilter(filterText.getText());
					filterText.setText(sniffer.getFilter());
				}
			}
		}
	}
}
