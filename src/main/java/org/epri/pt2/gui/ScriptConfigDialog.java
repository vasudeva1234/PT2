//*********************************************************************************************************************
// ScriptConfigDialog.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.TestScriptParameterDO;

/**
 * Contains a dialog for configuring script parameters.
 * 
 * @author Tam Do
 * 
 */
public class ScriptConfigDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6658156040561037563L;
	private List<TestScriptParameterDO> params;
	private List<ParamComponent> paramComponents;
	private JButton okButton;

	public ScriptConfigDialog() {
		super();
		params = new ArrayList<TestScriptParameterDO>();
		paramComponents = new ArrayList<ParamComponent>();
		setLayout(new MigLayout("wrap 2", "[120!]10[300:300:300]"));
	}
	
	public ScriptConfigDialog(List<TestScriptParameterDO> params) {
		this();
		this.params = params;
	}

	public void generatePanel() {
		for (TestScriptParameterDO param : params) {
			ParamComponent pc;

			if (param.getValue() != null) {
				pc = new ParamComponent(param.getName(), param.getValue(), param.getHint());
			} else if (param.getDefValue() != "") {
				pc = new ParamComponent(param.getName(), param.getDefValue(), param.getHint());
			} else {
				pc = new ParamComponent(param.getName(), param.getHint());
			}

			add(pc.name);
			add(pc.value, "growx");
			paramComponents.add(pc);
		}

		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		add(okButton);
		pack();
		setLocationRelativeTo(null);
		setModal(true);
	}

	public void getValuesFromComponents() {
		for (int i = 0; i < params.size(); i++) {
			params.get(i).setValue(paramComponents.get(i).value.getText());
		}
	}

	public void addParameter(TestScriptParameterDO param) {
		params.add(param);
	}

	public void removeParameter(TestScriptParameterDO param) {
		params.remove(param);
	}

	public List<TestScriptParameterDO> getParams() {
		return params;
	}
	
	public static String getParamString(List<TestScriptParameterDO> params) {
		String paramString = "";

		for (TestScriptParameterDO param : params) {
			if (param.getFlag() != null && param.getFlag() != "") {
				paramString += " " + param.getFlag();
			}
			
			if (param.getValue() != null && param.getValue() != "") {
				paramString += " " + param.getValue();
			} else if(param.getDefValue() != null) {
				paramString += " " + param.getDefValue();
			}
		}

		return paramString;
	}

	public class ParamComponent {
		protected JLabel name;
		protected JTextField value;

		public ParamComponent(String name, String hint) {
			this.name = new JLabel(name);
			value = new JTextField();
			value.setToolTipText(hint);
		}

		public ParamComponent(String name, String defval, String hint) {
			this.name = new JLabel(name);
			value = new JTextField(defval);
			value.setToolTipText(hint);
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(okButton)) {
			getValuesFromComponents();
			setVisible(false);
		}
	}
}
