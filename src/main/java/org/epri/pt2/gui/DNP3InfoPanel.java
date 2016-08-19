//*********************************************************************************************************************
// DNP3InfoPanel.java
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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.DNP3LinkHeader;

public class DNP3InfoPanel extends JPanel {

	private JLabel sourceLabel;
	private JTextField sourceText;
	private JLabel destinationLabel;
	private JTextField destinationText;
	private JCheckBox dirBitCheckbox;
	private JCheckBox prmBitCheckbox;
	private JLabel functionCodeLabel;
	private JTextField functionCodeText;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5745095959353908751L;

	public DNP3InfoPanel() {
		initializeComponents();
	}

	private void initializeComponents() {
		sourceLabel = new JLabel("Source: ");
		sourceText = new JTextField(5);
		sourceText.setEditable(false);
		destinationLabel = new JLabel("Destination: ");
		destinationText = new JTextField(5);
		destinationText.setEditable(false);
		dirBitCheckbox = new JCheckBox("DIR ", false);
		dirBitCheckbox.setEnabled(false);
		prmBitCheckbox = new JCheckBox("PRM ", false);
		prmBitCheckbox.setEnabled(false);
		functionCodeLabel = new JLabel("Function Code: ");
		functionCodeText = new JTextField(30);
		functionCodeText.setEditable(false);

		setLayout(new MigLayout());
		add(sourceLabel);
		add(sourceText);
		add(destinationLabel);
		add(destinationText);
		add(dirBitCheckbox);
		add(prmBitCheckbox);
		add(functionCodeLabel);
		add(functionCodeText);
	}

	public void updatePanel(DNP3ApplicationPacketDO appPacket) {
		DNP3LinkHeader linkHeader = new DNP3LinkHeader(appPacket.getRawData());

		sourceText.setText(Integer.toString(linkHeader.getSource()));
		destinationText.setText(Integer.toString(linkHeader.getDestination()));
		if (linkHeader.getControl().getDir() > 0) {
			dirBitCheckbox.setSelected(true);
		} else {
			dirBitCheckbox.setSelected(false);
		}
		if (linkHeader.getControl().getPrm() > 0) {
			prmBitCheckbox.setSelected(true);
		} else {
			prmBitCheckbox.setSelected(false);
		}

		String text = "";

		if (linkHeader.getControl().getPrm() > 0) {
			switch (linkHeader.getControl().getFunction_code()) {
			case 0:
				text = "0 (RESET_LINK_STATES)";
				break;
			case 2:
				text = "2 (TEST_LINK_STATES)";
				break;
			case 3:
				text = "3 (CONFIRMED_USER_DATA)";
				break;
			case 4:
				text = "4 (UNCONFIRMED_USER_DATA)";
				break;
			case 9:
				text = "9 (REQUEST_LINK_STATUS)";
				break;
			default:
				text = linkHeader.getControl().getFunction_code()
						+ " (RESERVED)";
			}
		} else {
			switch (linkHeader.getControl().getFunction_code()) {
			case 0:
				text = "0 (ACK)";
				break;
			case 1:
				text = "1 (NACK)";
				break;
			case 11:
				text = "11 (LINK_STATUS)";
				break;
			case 15:
				text = "15 (NOT_SUPPORTED)";
				break;
			default:
				text = linkHeader.getControl().getFunction_code()
						+ " (RESERVED)";
			}
		}

		functionCodeText.setText(text);
	}
}
