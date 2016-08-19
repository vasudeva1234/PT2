//*********************************************************************************************************************
// FuzzerComponent.java
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
package org.epri.pt2.fuzzer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

/**
 * Contains the FuzzerPanel component.
 * @author Southwest Research Institute
 *
 */
public class FuzzerComponent extends JPanel implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6582031289147338163L;	
	protected RequestPanel requestPanel;
	protected ResponsePanel responsePanel;
	protected JTabbedPane tabbedPane;
	protected JFrame parentFrame;

	/**
	 * 
	 * 
	 */
	public FuzzerComponent() {
		
		super();		
		initComponents();	
	}
	
	private void initComponents() {
		requestPanel = new RequestPanel();
		responsePanel = new ResponsePanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.add("Requests", requestPanel);
		tabbedPane.add("Responses", responsePanel);
		tabbedPane.addChangeListener(this);

		setLayout(new MigLayout());

		add(tabbedPane, "dock center");
	}
	
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource().equals(tabbedPane)) {
			if(tabbedPane.getSelectedComponent().equals(requestPanel)) {				
				requestPanel.requestPacketTableModel.fireTableDataChanged();				
			} else if(tabbedPane.getSelectedComponent().equals(responsePanel)) {
				responsePanel.responsePacketTableModel.fireTableDataChanged();
			}
		}
	}
}
