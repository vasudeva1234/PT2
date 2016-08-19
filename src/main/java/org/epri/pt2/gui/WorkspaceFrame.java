//*********************************************************************************************************************
// WorkspaceFrame.java
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

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.dnp3proxy.DNP3ProxyPanel;
import org.epri.pt2.fuzzer.FuzzerComponent;
import org.epri.pt2.gui.model.DataPacketTableModel;
import org.epri.pt2.proxy.ProxyPanel;
import org.epri.pt2.sniffer.EthernetOptionsPanel;
import org.epri.pt2.sniffer.SnifferComponent;
import org.epri.pt2.sniffer.SnifferController;

/**
 * Contains a frame which contains all of the workspace elements.
 * 
 * @author Tam Do
 * 
 */
public class WorkspaceFrame extends JFrame {
	private TestCaseComponent testCaseComponent;
	private ProjectListPanel projectListPanel;
	private SnifferComponent snifferComponent;
	private ProxyPanel proxyPanel;
	private DNP3ProxyPanel dnp3ProxyPanel;
	private FuzzerComponent fuzzerComponent;
	private MyMenuBar menuBar;
	private WorkspaceWizard wizard;
	private JTabbedPane pane;
	
	protected SnifferController controller = SnifferController.getInstance();

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private static final long serialVersionUID = -5373897022331941634L;

	public WorkspaceFrame() {
		initComponents();
		setVisible(true);
	}

	/**
	 * 
	 */
	private void initComponents() {
		setTitle("Penetration Testing Toolkit (PT2)");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		testCaseComponent = new TestCaseComponent();
		testCaseComponent.setMinimumSize(new Dimension(700, HEIGHT));

		projectListPanel = ProjectListPanel.getInstance();
		projectListPanel.setMinimumSize(new Dimension(100, HEIGHT));
		projectListPanel.setPreferredSize(new Dimension(200, HEIGHT));
		// projectListPanel.table.getSelectionModel().addListSelectionListener(
		// testCaseComponent.testCaseToolbar);

		pane = new JTabbedPane();
		snifferComponent = new SnifferComponent(this);

		proxyPanel = new ProxyPanel();
		
		dnp3ProxyPanel = new DNP3ProxyPanel();

		fuzzerComponent = new FuzzerComponent();

		Dimension menuDim = new Dimension(WIDTH, 20);
		menuBar = new MyMenuBar(this);
		menuBar.setMinimumSize(menuDim);

		StatusArea statusArea = StatusArea.getInstance();

		Dimension statusDim = new Dimension(WIDTH, 30);
		statusArea.panel.setMinimumSize(statusDim);

		projectListPanel.table.getSelectionModel().addListSelectionListener(
				(testCaseComponent.testCaseListPanel));

		pane.addTab("Test Cases", testCaseComponent);
		pane.addTab("Sniffer", snifferComponent);
		pane.addTab("Proxy", proxyPanel);
		pane.addTab("DNP3 Proxy", dnp3ProxyPanel);
		pane.addTab("Fuzzer", fuzzerComponent);

		JSplitPane projectsTabsDivider = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		projectsTabsDivider.setOneTouchExpandable(true);

		JSplitPane statusPaneDivider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		statusPaneDivider.setOneTouchExpandable(true);

		projectsTabsDivider.add(projectListPanel);
		projectsTabsDivider.add(pane);
		projectsTabsDivider.setMinimumSize(new Dimension(WIDTH, 200));

		statusPaneDivider.add(projectsTabsDivider);
		statusPaneDivider.add(statusArea.panel);
		statusPaneDivider.setDividerLocation(600);
		setLayout(new MigLayout());
		add(menuBar, "dock north");
		add(statusPaneDivider, "dock center");

		WindowListener exitListener = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				int reply = JOptionPane.showConfirmDialog(menuBar,"Would you like to save your stuff?","Save",JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                	if (EthernetOptionsPanel.getRunningDevice() != null) {
        				controller.stop(EthernetOptionsPanel.getRunningDevice());
        			}
                	DataPacketTableModel.getInstance().removeAll();
				}
				TaskController.getInstance().killProcesses();
			}
		};

		addWindowListener(exitListener);

		pack();
		setLocationRelativeTo(null);

		setVisible(true);

		wizard = new WorkspaceWizard(this);
		wizard.setVisible(true);
		wizard.toFront();
		wizard.repaint();

		projectListPanel.table.getSelectionModel().addListSelectionListener(
				snifferComponent.getSnifferOptionsPanel());
//		projectListPanel.table.getSelectionModel().addListSelectionListener(
//				snifferComponent.getSnifferPacketPanel());
		projectListPanel.tableModel.fireTableDataChanged();
	}

	/**
	 *
	 */
	public JTabbedPane getTabbedPane() {
		return this.pane;
	}

	public FuzzerComponent getFuzzerComponent() {
		return this.fuzzerComponent;
	}
}
