//*********************************************************************************************************************
// DNP3ProxyConfigurationPanel.java
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.common.base.Strings;
import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;

import net.miginfocom.swing.MigLayout;

public class DNP3ProxyConfigurationPanel extends JPanel implements
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1433885720299343440L;

	private DNP3ProxyController controller;

	protected JLabel proxyEnabledLabel;
	protected JLabel proxyPortLabel;
	protected JCheckBox proxyEnabled;
	protected JTextField proxyPort;
	protected JLabel proxyAddressLabel;
	protected JTextField proxyAddress;
	
    private final Pattern addressPattern =
            Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");
	
    private ValidationResultModel validationResultModel = new DefaultValidationResultModel();

	public DNP3ProxyConfigurationPanel(DNP3ProxyController controller) {
		this.controller = controller;
		
		initComponents();
	}

	private void initComponents() {
		// TODO initialize form components
		proxyEnabledLabel = new JLabel("Enable Proxy:");
		proxyEnabled = new JCheckBox();
		proxyEnabled.setSelected(controller.isEnabled());
		proxyEnabled.addActionListener(this);

		proxyAddressLabel = new JLabel("Proxy Address:");
		proxyAddress = new JTextField(controller.getListenAddress());

		proxyPortLabel = new JLabel("Proxy Port:");
		proxyPort = new JTextField(String.format("%d",
				controller.getListenPort()));

		JPanel proxyConfigPanel = new JPanel();

		proxyConfigPanel.setLayout(new MigLayout("wrap 2",
				"[right,60]10[fill,200]"));

		proxyConfigPanel.add(proxyEnabledLabel);
		proxyConfigPanel.add(proxyEnabled);
		proxyConfigPanel.add(proxyAddressLabel);
		proxyConfigPanel.add(proxyAddress);
		proxyConfigPanel.add(proxyPortLabel);
		proxyConfigPanel.add(proxyPort);

		JComponent validationResultsComponent = ValidationResultViewFactory
				.createReportTextArea(validationResultModel);
		add(validationResultsComponent, "span 2");

		setLayout(new MigLayout());

		// TODO add filter configuration panel if it makes sense

		add(proxyConfigPanel, "dock north");
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		ValidationResult validationResult = validateInput();
		validationResultModel.setResult(validationResult);
		if (validationResultModel.hasErrors()) {
			controller.setEnabled(false);
			proxyEnabled.setSelected(false);
			return;
		}

		if (e.getSource().equals(proxyEnabled)) {

			if (proxyEnabled.isSelected()) {
				controller.setListenAddress(proxyAddress.getText());
				controller.setListenPort(Integer.parseInt(proxyPort.getText()));
				try {
					controller.start();
				} catch (Exception be)
				{
					validationResult.addError("The port is in use or an invalid hostname or IP address has been entered.");
					validationResultModel.setResult(validationResult);
					controller.setEnabled(false);;
					proxyEnabled.setSelected(false);
				}
				proxyEnabled.setSelected(controller.isEnabled());
			} else {
				controller.stop();
				proxyEnabled.setSelected(controller.isEnabled());
			}
		}
	}

	public ValidationResult validateInput() {
		ValidationResult validationResult = new ValidationResult();
		
		if (Strings.isEmpty(proxyAddress.getText())) {
			validationResult
					.addError("The proxy address field can not be blank.");
		} else {
	        String address = proxyAddress.getText();

            Matcher matcher = addressPattern.matcher(address);
            
            if (!matcher.matches()) {
                validationResult.addWarning(
                        "The proxy address field must be a valid ip address or hostname.");
            }
		}

		if (Strings.isEmpty(proxyPort.getText())) {
			validationResult.addError("The proxy port field can not be blank.");
		}

		
		try {
			if (Integer.parseInt(proxyPort.getText()) < 1) {
				validationResult
						.addError("The proxy port must be a number in the range of 1-65535.");
			}
		} catch (NumberFormatException e) {
			validationResult
					.addError("The proxy port must be a number in the range of 1-65535.");
		}
		/*
		 * if (Strings.isEmpty(nameText.getText())) {
		 * validationResult.addError("The name field can not be blank."); }
		 * 
		 * if (Strings.isEmpty(descText.getText())) { validationResult
		 * .addInfo("Please add a description to the project."); }
		 */

		return validationResult;
	}
}
