//*********************************************************************************************************************
// DNP3ProxyInterceptorPanel.java
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
package org.epri.pt2.dnp3proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.gui.DNP3InfoPanel;
import org.epri.pt2.proxy.ProxyInterceptorPanel.UserAction;

import net.miginfocom.swing.MigLayout;
import at.HexLib.library.HexLib;
import at.HexLib.library.HexLibHEX;

public class DNP3ProxyInterceptorPanel extends JPanel implements
		ActionListener, ChangeListener, DNP3ProxyInterceptorInterface {

	protected JToggleButton interceptButton;
	protected JButton forwardButton;
	protected JButton modifyButton;
	protected JButton dropButton;

	protected JTabbedPane editorPane;
	protected HexLib hexEditorPane;
	protected DNP3InfoPanel infoPanel;

	protected DNP3ProxyController controller;

	private UserAction selectedAction = null;

	private Object lock = new Object();

	public static enum UserAction {
		NONE, DROPPED, SEND_ORIGINAL, SEND_MODIFIED
	}

	public DNP3ProxyInterceptorPanel() {
		initComponents();
		controller = DNP3ProxyController.getInstance();
		controller.getInterceptorController().addListener(this);
	}

	private void initComponents() {
		interceptButton = new JToggleButton("Intercept Enabled");
		interceptButton
				.setToolTipText("Enable/Disable interception of DNP3 packets passed through the proxy");
		interceptButton.addActionListener(this);
		forwardButton = new JButton("Forward");
		forwardButton
				.setToolTipText("Forward packets unmodified through the proxy.");
		forwardButton.addActionListener(this);
		modifyButton = new JButton("Modify");
		modifyButton
				.setToolTipText("Send the modified packet from the editor below through the proxy");
		modifyButton.addActionListener(this);
		dropButton = new JButton("Drop");
		dropButton.setToolTipText("Drop the packet from the proxy");
		dropButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(interceptButton);
		buttonPanel.add(forwardButton);
		buttonPanel.add(modifyButton);
		buttonPanel.add(dropButton);

		JPanel hexEditorPanel = new JPanel(new MigLayout());
		infoPanel = new DNP3InfoPanel();
		hexEditorPane = new HexLib();
		hexEditorPane.setASCIIEditorVisible(true);
		hexEditorPanel.add(infoPanel, "wrap");
		hexEditorPanel.add(hexEditorPane, "span");
		
		editorPane = new JTabbedPane();
		editorPane.addTab("Hex Editor", hexEditorPanel);
		editorPane.addChangeListener(this);

		setLayout(new MigLayout());
		add(buttonPanel, "dock north");
		add(editorPane, "dock center");

		updateButtonStates();
	}

	private void updateButtonStates() {
		if (interceptButton.isSelected()) {
			if (selectedAction == null) {
				forwardButton.setEnabled(false);
				modifyButton.setEnabled(false);
				dropButton.setEnabled(false);
			}
			if (selectedAction == UserAction.NONE) {
				forwardButton.setEnabled(true);
				modifyButton.setEnabled(true);
				dropButton.setEnabled(true);
			} else {
				forwardButton.setEnabled(false);
				modifyButton.setEnabled(false);
				dropButton.setEnabled(false);
			}
		} else {
			forwardButton.setEnabled(false);
			modifyButton.setEnabled(false);
			dropButton.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5072420168931920791L;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(modifyButton)) {
			selectedAction = UserAction.SEND_MODIFIED;
			synchronized (lock) {
				lock.notify();
			}
		} else if (e.getSource().equals(dropButton)) {
			selectedAction = UserAction.DROPPED;
			synchronized (lock) {
				lock.notify();
			}
		} else if (e.getSource().equals(interceptButton)) {
			controller.setInterceptEnabled(interceptButton.isSelected());

			if (!interceptButton.isSelected()) {
				if (selectedAction == null) {
					selectedAction = UserAction.SEND_ORIGINAL;
					hexEditorPane.setByteContent(new byte[0]);
				}
			}
		} else if (e.getSource().equals(forwardButton)) {
			selectedAction = UserAction.SEND_ORIGINAL;
			synchronized (lock) {
				lock.notify();
			}
		}

	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(editorPane)) {
			if (editorPane.getSelectedComponent().equals(hexEditorPane)) {
				// TODO
				// xmlEditorPane.setContent(rawEditorPane.getText());
			}
		}
	}

	public void processAppPacket(DNP3ApplicationPacketDO appPacket) {

		if (controller.isInterceptEnabled()) {
			synchronized (lock) {
				this.requestFocusInWindow();

				// Update the hex editor panel
				hexEditorPane.setByteContent(appPacket.getData());
				
				// Update the info panel
				infoPanel.updatePanel(appPacket);

				selectedAction = UserAction.NONE;
				updateButtonStates();

				while (selectedAction == UserAction.NONE) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// TODO add some indication as to what type of packet this is
				// (e.g., from which source etc)
				switch (selectedAction) {
				case DROPPED:
					break;

				case SEND_MODIFIED:
					appPacket.setData(hexEditorPane.getByteContent());
					break;
				case SEND_ORIGINAL:
					break;
				default:
					break;
				}

				hexEditorPane.setByteContent(new byte[0]);
				updateButtonStates();
			}
		}
	}
}
