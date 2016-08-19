//*********************************************************************************************************************
// StatusArea.java
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

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

/**
 * Contains a singleton component which may be used to report status of various
 * application events.
 * 
 * @author Tam Do
 * 
 */
public class StatusArea {
	private static final StatusArea status;
	protected JPanel panel;
	protected JTextArea statusArea;
	protected JTabbedPane tabbedPane;

	static {
		status = new StatusArea();

	}

	/**
	 * 
	 */
	private StatusArea() {
		panel = new JPanel();
		statusArea = new JTextArea();
		statusArea.setRows(10);
		tabbedPane = new JTabbedPane();

		Font font = new Font("Monospaced", Font.PLAIN, 12);
		statusArea.setFont(font);
		statusArea.setEditable(false);

		DefaultCaret caret = (DefaultCaret) statusArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scrollPane = new JScrollPane(statusArea);

		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.add("Console", scrollPane);

		panel.setLayout(new MigLayout());

		panel.add(tabbedPane, "dock center");
	}

	public static StatusArea getInstance() {
		return status;
	}

	public void appendStatusMessage(String message) {
		statusArea.append(message);
	}

	public JTextArea addRunTab(String label, TestExecutionTask task) {
		RunAreaComponent p = new RunAreaComponent();
		tabbedPane.add(p);
		initTabComponent(tabbedPane.getTabCount() - 1, label, task);
		return p.runArea;
	}

	public JTextArea getCurrentRunTabArea() {
		return ((RunAreaComponent) tabbedPane.getSelectedComponent()).runArea;
	}

	public void initTabComponent(int i, String label, TestExecutionTask task) {
		ButtonTabComponent c = new ButtonTabComponent(tabbedPane, label);
		c.stopButton.addActionListener(task);
		tabbedPane.setTabComponentAt(i, c);
	}

	public class RunAreaComponent extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9040270685763239232L;
		protected JTextArea runArea;

		public RunAreaComponent() {
			runArea = new JTextArea();
			runArea.setRows(10);
			JScrollPane scrollPane = new JScrollPane(runArea);

			setLayout(new MigLayout());
			add(scrollPane, "dock center");
		}
	}
}