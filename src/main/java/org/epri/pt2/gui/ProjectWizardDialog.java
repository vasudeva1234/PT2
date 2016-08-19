//*********************************************************************************************************************
// ProjectWizardDialog.java
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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.InterfaceMapper;
import org.epri.pt2.InterfaceType;
import org.epri.pt2.ProtocolType;
import org.epri.pt2.DO.AbstractIfaceDO;
import org.epri.pt2.DO.AbstractProtocolDO;
import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.controller.AbstractIfaceController;
import org.epri.pt2.controller.AbstractProtocolController;
import org.epri.pt2.controller.DeviceController;
import org.epri.pt2.controller.ProjectController;
import org.epri.pt2.controller.TestCaseController;
import org.epri.pt2.gui.model.DeviceDOTableModel;

import com.jgoodies.common.base.Strings;
import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;

/**
 * This class consists of a dialog to create a new project and populate the
 * project with a device, interface, and associated protocol.
 * 
 * @author Tam Do
 * 
 */
public class ProjectWizardDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -465746685493520322L;
	private ProjectWizardPanel projectPanel;
	private DeviceWizardPanel devicePanel;

	private ProjectController projectController = ProjectController
			.getInstance();
	private DeviceController deviceController = DeviceController.getInstance();
	private AbstractIfaceController ifaceController = AbstractIfaceController
			.getInstance();
	private AbstractProtocolController protocolController = AbstractProtocolController
			.getInstance();

	private JButton nextButton;
	private JButton backButton;
	private JButton finishButton;
	private WizardState state;

	private JPanel cards;

	final static String PROJECTPANEL = "Project Panel";
	final static String DEVICEPANEL = "Device Panel";
	final static String INTERFACEPANEL = "Interface Panel";

	private CardLayout cl;

	private ProjectDO project;

	private enum WizardState {
		START_PANEL, DEVICE_PANEL
	}

	public ProjectWizardDialog(JFrame frame) {
		super(frame);

		initComponents();

		updateButtonState();
	}

	private void initComponents() {
		projectPanel = new ProjectWizardPanel();
		devicePanel = new DeviceWizardPanel();

		nextButton = new JButton("Next");
		backButton = new JButton("Back");
		finishButton = new JButton("Finish");

		nextButton.addActionListener(this);
		backButton.addActionListener(this);
		finishButton.addActionListener(this);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new MigLayout());
		controlPanel.add(backButton);
		controlPanel.add(nextButton);
		controlPanel.add(finishButton);

		setLayout(new MigLayout());

		cards = new JPanel(new CardLayout());
		cl = (CardLayout) cards.getLayout();

		cards.add(projectPanel.panel, PROJECTPANEL);
		cards.add(devicePanel, DEVICEPANEL);

		cl.show(cards, PROJECTPANEL);

		add(cards, "dock center, grow");
		add(controlPanel, "dock south");

		state = WizardState.START_PANEL;

		setSize(640, 320);
		setLocationRelativeTo(null);
	}

	private void updateButtonState() {
		switch (state) {
		case START_PANEL:
			nextButton.setEnabled(true);
			backButton.setEnabled(false);
			finishButton.setEnabled(false);
			break;
		case DEVICE_PANEL:
			nextButton.setEnabled(false);
			backButton.setEnabled(true);
			finishButton.setEnabled(true);
			break;
		}
	}

	public void openDialog() {
		setModal(true);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(nextButton)) {
			switch (state) {
			case START_PANEL:
				ValidationResult result = projectPanel.validate();
				projectPanel.validationResultModel.setResult(result);

				if (!projectPanel.validationResultModel.hasErrors()) {
					state = WizardState.DEVICE_PANEL;
					cl.show(cards, DEVICEPANEL);

					// create the project
					if (project == null) {
						project = new ProjectDO(projectPanel.nameText.getText());
						project.setDesc(projectPanel.descText.getText());

						// add the project to the controller
						project = projectController.addProject(SelectionState
								.getInstance().getWorkspaceId(), project);

						// update the local instance of the project
						// project = projectController.getProjectByName(project
						// .getName());

						// update the selection state
						SelectionState.getInstance().setProjectId(
								project.getId());
					}

					updateButtonState();
				}
				break;
			default:
				break;
			}
		} else if (e.getSource().equals(backButton)) {
			switch (state) {
			case DEVICE_PANEL:
				state = WizardState.START_PANEL;
				cl.show(cards, PROJECTPANEL);
				updateButtonState();
				break;
			default:
				break;
			}
		} else if (e.getSource().equals(finishButton)) {
			switch (state) {
			case DEVICE_PANEL:
				// Close the dialog
				setVisible(false);

				break;
			default:
				break;
			}
		}
	}

	public class ProjectWizardPanel implements Validatable {
		/**
		 * 
		 */
		protected JPanel panel;

		private JLabel nameLabel = new JLabel("Name:");
		protected JTextField nameText;
		private JLabel descLabel = new JLabel("Description:");
		protected JTextArea descText;

		private ValidationResultModel validationResultModel = new DefaultValidationResultModel();

		public ProjectWizardPanel() {
			initComponents();
		}

		private void initComponents() {
			panel = new JPanel();

			nameText = new JTextField();
			descText = new JTextArea();
			descText.setRows(10);

			JScrollPane scrollPane = new JScrollPane(descText);

			panel.setLayout(new MigLayout("wrap 2", "[100!]10[grow, push]"));

			panel.add(nameLabel);
			panel.add(nameText, "growx");
			panel.add(descLabel);
			panel.add(scrollPane, "grow");

			JComponent validationResultsComponent = ValidationResultViewFactory
					.createReportTextArea(validationResultModel);
			panel.add(validationResultsComponent, "span 2");
		}

		public ValidationResult validate() {
			ValidationResult validationResult = new ValidationResult();

			if (Strings.isEmpty(nameText.getText())) {
				validationResult.addError("The name field can not be blank.");
			}

			if (Strings.isEmpty(descText.getText())) {
				validationResult
						.addInfo("Please add a description to the project.");
			}

			return validationResult;
		}
	}

	public class DeviceWizardPanel extends JPanel implements ActionListener,
			ListSelectionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3740728116688974728L;
		private DeviceDOTableModel model;
		protected JTable deviceTable;
		protected JButton addButton;
		protected JButton removeButton;
		private JLabel nameLabel = new JLabel("Device Name:");
		protected JTextField nameText;
		private JLabel descLabel = new JLabel("Description:");
		protected JTextField descText;

		private JLabel interfaceLabel = new JLabel("Interface:");
		protected JComboBox interfaceCombo;
		private JLabel protocolLabel = new JLabel("Protocol:");
		protected JComboBox protocolCombo;

		private ValidationResultModel validationResultModel = new DefaultValidationResultModel();
		private DeviceValidator validator;

		public DeviceWizardPanel() {
			initComponents();
			validator = new DeviceValidator(this);
		}

		private void initComponents() {
			setTitle("Create a new project");
			model = new DeviceDOTableModel();

			deviceTable = new JTable(model);
			deviceTable.getSelectionModel().addListSelectionListener(this);
			TableColumn idColumn = deviceTable.getColumnModel().getColumn(0);
			idColumn.setPreferredWidth(40);
			idColumn.setMaxWidth(40);

			JScrollPane devicePane = new JScrollPane(deviceTable);

			nameText = new JTextField("");
			descText = new JTextField("");
			interfaceCombo = new JComboBox(InterfaceType.values());
			protocolCombo = new JComboBox(ProtocolType.values());

			addButton = new JButton("Add");
			addButton.addActionListener(this);
			removeButton = new JButton("Remove");
			removeButton.addActionListener(this);

			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new MigLayout("wrap 2",
					"[120!]10[300:300:450]"));
			controlPanel.add(nameLabel);
			controlPanel.add(nameText, "growx");
			controlPanel.add(descLabel);
			controlPanel.add(descText, "growx");
			controlPanel.add(interfaceLabel);
			controlPanel.add(interfaceCombo, "growx");
			controlPanel.add(protocolLabel);
			controlPanel.add(protocolCombo, "growx");
			controlPanel.add(addButton, "span 2, split 2, growx");
			controlPanel.add(removeButton, "growx");

			JComponent validationResultsComponent = ValidationResultViewFactory
					.createReportTextArea(validationResultModel);
			controlPanel.add(validationResultsComponent, "span 2");

			setLayout(new MigLayout());

			add(devicePane, "dock center");
			add(controlPanel, "dock south, growx");

			updateButtonState();
		}

		public void updateButtonState() {
			if (deviceTable.getSelectedRow() > -1) {
				removeButton.setEnabled(true);
			} else {
				removeButton.setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(addButton)) {

				ValidationResult result = validator.validate();
				validationResultModel.setResult(result);

				if (!result.hasErrors()) {

					DeviceDO device = new DeviceDO(nameText.getText());
					device.setDescription(descText.getText());

					// add the device to the project
					device = deviceController
							.addDevice(project.getId(), device);

					// create the interface
					AbstractIfaceDO iface = InterfaceMapper
							.getClass((InterfaceType) interfaceCombo
									.getSelectedItem());

					// persist the interface
					iface = ifaceController.addInterface(device.getId(), iface);

					// get the list of protocols
					List<AbstractProtocolDO> protocols = protocolController
							.getAllProtocols();

					for (AbstractProtocolDO protocol : protocols) {
						if (protocol.getName().equals(
								(ProtocolType) protocolCombo.getSelectedItem())) {
							protocolController.setProtocol(iface.getId(),
									protocol.getId());
						}
					}

					switch ((ProtocolType) protocolCombo.getSelectedItem()) {
					case OpenADR_20a:
						// Add the test cases to the project
						TestCaseController.getInstance().loadProtocolTestCases(
								project.getId(), "OpenADR");
						break;
					case DNP3:
						TestCaseController.getInstance().loadProtocolTestCases(
								project.getId(), "DNP3");
						break;
					}

					model.fireTableDataChanged();

				}
			} else if (e.getSource().equals(removeButton)) {
				int rowSelected = deviceTable.getSelectedRow();

				if (rowSelected > -1) {
					model.removeDevice(rowSelected);
				}
			}

			updateButtonState();
			/*
			 * else if (e.getSource().equals(removeButton)) { int rowSelected =
			 * deviceTable.getSelectedRow(); if (rowSelected >= 0) {
			 * devices.remove(rowSelected); } }
			 */
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				updateButtonState();
			}
		}
	}

	protected class DeviceValidator implements Validatable {
		DeviceWizardPanel panel;

		public DeviceValidator(DeviceWizardPanel panel) {
			this.panel = panel;
		}

		public ValidationResult validate() {
			ValidationResult validationResult = new ValidationResult();

			if (Strings.isEmpty(panel.nameText.getText())) {
				validationResult.addError("The name field can not be blank.");
			}

			return validationResult;
		}

	}
}
