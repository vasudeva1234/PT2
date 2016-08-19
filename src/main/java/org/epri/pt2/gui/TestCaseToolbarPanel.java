//*********************************************************************************************************************
// TestCaseToolbarPanel.java
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
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.DO.TestScriptDO;
import org.epri.pt2.DO.TestScriptParameterDO;
import org.epri.pt2.controller.TestCaseController;
import org.epri.pt2.controller.TestScriptController;
import org.epri.pt2.scriptxmlparser.ScriptXmlParser;

/**
 * A class for displaying different toolbar options for managing test cases.
 * 
 * @author Tam Do
 * 
 */
public class TestCaseToolbarPanel extends JPanel implements ActionListener,
		ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5899142858528759688L;

	private static final TestCaseToolbarPanel toolbar;

	private JButton runTestCase;
	private JButton propertiesButton;
	private JButton addTestCase;
	private JButton removeTestCase;
	private ListSelectionModel testCaseModel;
	private SelectionState state = SelectionState.getInstance();
	private TestCaseListPanel listPanel;

	// private HashMap<String, ScriptConfigDialog> dialogMap = new
	// HashMap<String, ScriptConfigDialog>();

	private TaskController taskController;

	static {
		toolbar = new TestCaseToolbarPanel();
	}

	/**
	 * @param state
	 * @param listPanel
	 */
	private TestCaseToolbarPanel() {
		super();

		listPanel = TestCaseListPanel.getInstance();
		listPanel.table.getSelectionModel().addListSelectionListener(this);
		taskController = TaskController.getInstance();

		initComponents();
	}

	public static TestCaseToolbarPanel getInstance() {
		return toolbar;
	}

	protected void finalize() {
		if (taskController != null) {
			taskController.killProcesses();
		}
	}

	private void initComponents() {
		ClassLoader cl = this.getClass().getClassLoader();

		setLayout(new MigLayout());

		addTestCase = new ProjectToolbarButton("");
		addTestCase.setIcon(new ImageIcon(cl
				.getResource("images/green_plus_sign.png")));
		addTestCase.addActionListener(this);
		addTestCase
				.setToolTipText("Create a new test case and add it to the selected project.");

		removeTestCase = new ProjectToolbarButton("");
		removeTestCase.setIcon(new ImageIcon(cl
				.getResource("images/red_minus_sign.png")));
		removeTestCase.addActionListener(this);
		removeTestCase
				.setToolTipText("Remove the selected test case from the currently selected project.");

		runTestCase = new ProjectToolbarButton("");
		runTestCase.setIcon(new ImageIcon(cl
				.getResource("images/green_triangle.png")));
		runTestCase.addActionListener(this);
		runTestCase
				.setToolTipText("Execute the currently selected test case.\r\nPlease make sure to configure the settings for the test case if necessary.");

		propertiesButton = new ProjectToolbarButton("");
		propertiesButton.setIcon(new ImageIcon(cl
				.getResource("images/gear.png")));
		propertiesButton.addActionListener(this);
		propertiesButton
				.setToolTipText("Configure the settings for the currently selected test case");

		add(addTestCase, "cell 0 0");
		add(removeTestCase, "cell 1 0");
		add(runTestCase, "cell 2 0");
		add(propertiesButton, "cell 3 0");

		updateButtonState();
	}

	protected void updateButtonState() {
		if (state.workspaceId > 0 && state.projectId > 0) {
			if (state.testCaseId > 0) {
				removeTestCase.setEnabled(true);
				propertiesButton.setEnabled(true);
				runTestCase.setEnabled(true);
			}
			addTestCase.setEnabled(true);
		} else {
			propertiesButton.setEnabled(false);
			runTestCase.setEnabled(false);
			addTestCase.setEnabled(false);
			removeTestCase.setEnabled(false);
		}
	}

	/**
	 * @param isDoubleBuffered
	 */
	public TestCaseToolbarPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public TestCaseToolbarPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	/**
	 * @param layout
	 */
	public TestCaseToolbarPanel(LayoutManager layout) {
		super(layout);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addTestCase)) {
			if (state.getProjectId() > 0) {
				TestCaseDO testCase = new TestCaseDO("<name>", "<description>",
						"<pass/fail>");
				listPanel.tableModel.addRow(testCase);
			}
		} else if (e.getSource().equals(removeTestCase)) {
			if (state.getTestCaseId() > 0) {
				listPanel.tableModel.removeRow(state.getTestCaseId());
			}
		} else if (e.getSource().equals(runTestCase)) {
			
			// TODO Check for windows and throw warning and exit
			String OS = System.getProperty("os.name");
			
			if(OS.startsWith("Windows")) {
				JOptionPane.showMessageDialog(this, 
						"The execute test script functionality is not supported under Windows platforms.", 
						"Functionality not supported.",
						JOptionPane.WARNING_MESSAGE
						);
				
				return;
			}
			
			if (state.getTestCaseId() > 0) {
				TestCaseDO testCase = TestCaseController.getInstance()
						.getTestCase(state.getTestCaseId());

				List<TestScriptDO> testScripts = TestScriptController
						.getInstance().getTestScripts(state.getTestCaseId());

				if(testScripts.size() > 0) {
					for(TestScriptDO testScript : testScripts) {
						if(testScript.getProject().getId() == state.getProjectId()) {
							TestExecutionTask task = new TestExecutionTask(testCase, testScript);
							taskController.addTask(task);
							task.executeTask();
							break;
						}
					}
				} else if (testCase.getFileName() != null){
					String fileName = testCase.getFileName();

					// Get the parameters from the config file
					List<TestScriptParameterDO> params = ScriptXmlParser.getParameters(fileName);
					
					TestScriptDO script = new TestScriptDO();
					if(params.size() == 0) {
						script.setParams("");
					} else {
						script.setParams(ScriptConfigDialog.getParamString(params));
						
					}
					
					TestScriptController.getInstance().addTestScript(testCase.getId(), state.getProjectId(), script);
					TestExecutionTask task = new TestExecutionTask(testCase, script);
					taskController.addTask(task);
					task.executeTask();
				}

			}
		} else if (e.getSource().equals(propertiesButton)) {
			if (state.getTestCaseId() > 0) {
				setTestScriptProperties();
			}
		}
	}

	private void setTestScriptProperties() {		
		// Get the current test case object
		TestCaseDO testCase = TestCaseController.getInstance()
				.getTestCase(state.getTestCaseId());
		
		// Get the test script object from the test case
		List<TestScriptDO> testScripts = TestScriptController.getInstance().getTestScripts(testCase.getId());

		TestScriptDO testScript = null;
		
		for(TestScriptDO script : testScripts) {
			if(script.getProject().getId() == state.getProjectId()) {
				// Get the list of parameters associated with the test case
				testScript = script;
				break;
			}
		}

		if(testScript == null) {
			testScript = new TestScriptDO();
			testScript.setParams("");
			testScript = TestScriptController.getInstance().addTestScript(
					testCase.getId(), state.getProjectId(), testScript);
		}
		
		List<TestScriptParameterDO> params = TestScriptController.getInstance().getParameters(testScript);

		if (params.size() <= 0 && testCase.getFileName() != null) {
			String fileName = testCase.getFileName();

			// Get the parameters from the config file
			params = ScriptXmlParser.getParameters(fileName);

		}

		ScriptConfigDialog dialog = new ScriptConfigDialog(params);
		dialog.generatePanel();
		dialog.setVisible(true);
		
		// Get the parameters from the dialog
		params = dialog.getParams();
		
		// Persist or update the parameters
		TestScriptController.getInstance().setParameters(testScript, params);
		
		testScript.setParams(ScriptConfigDialog.getParamString(params));

		TestScriptController.getInstance().updateTestScript(testScript);
	}
	
	/**
	 * @return
	 */
	public ListSelectionModel getTestCaseModel() {
		return testCaseModel;
	}

	/**
	 * @param testCaseModel
	 */
	public void setTestCaseModel(ListSelectionModel testCaseModel) {
		this.testCaseModel = testCaseModel;
	}

	/**
	 * @author Tam Do
	 * 
	 */
	public class ProjectToolbarButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1092757389247741469L;

		/**
		 * @param name
		 */
		public ProjectToolbarButton(String name) {
			super(name);

		}

		public Dimension getPreferredSize() {
			return new Dimension(30, 30);
		}

		public Dimension getMinimumSize() {
			return new Dimension(30, 30);
		}

		public Dimension getMaximumSize() {
			return new Dimension(30, 30);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		updateButtonState();
	}
}
