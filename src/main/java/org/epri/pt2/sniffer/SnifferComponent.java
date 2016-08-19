//*********************************************************************************************************************
// SnifferComponent.java
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.gui.WorkspaceFrame;

/**
 * Displays the sniffer packet panel and the sniffer options panel.
 * 
 * @author Tam Do
 * 
 */
public class SnifferComponent extends JPanel implements ChangeListener {

	protected JTabbedPane tabbedPane;
	protected SnifferPacketPanel packetPanel;
	protected SnifferOptionsPanel optionsPanel;
	protected JFrame parentFrame;

	/**
	 * 
	 */
	private static final long serialVersionUID = -684538420336112239L;

	public SnifferComponent(WorkspaceFrame frame) {
		super();
		initComponents(frame);
	}

	private void initComponents(WorkspaceFrame frame) {
		packetPanel = SnifferPacketPanel.getInstance();
		optionsPanel = new SnifferOptionsPanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.add("Packets", packetPanel);
		tabbedPane.add("Options", optionsPanel);
		tabbedPane.addChangeListener(this);

		setLayout(new MigLayout());

		add(tabbedPane, "dock center");
	}

	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource().equals(tabbedPane)) {
			if (tabbedPane.getSelectedComponent().equals(packetPanel)) {
				packetPanel.model.fireTableDataChanged();
			} else if (tabbedPane.getSelectedComponent().equals(optionsPanel)) {
				optionsPanel.model.fireTableDataChanged();
			}
		}
	}

	public SnifferOptionsPanel getSnifferOptionsPanel() {
		return optionsPanel;
	}
	
	public SnifferPacketPanel getSnifferPacketPanel() {
		return packetPanel;
	}
}
