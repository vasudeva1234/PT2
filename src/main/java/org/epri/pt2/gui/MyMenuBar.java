//*********************************************************************************************************************
// MyMenuBar.java
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.epri.pt2.gui.model.DataPacketTableModel;
import org.epri.pt2.sniffer.EthernetOptionsPanel;
import org.epri.pt2.sniffer.SnifferController;

/**
 * Implements the project menu bar.
 * @author tdo
 *
 */
public class MyMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 573443867233723135L;
	
	protected SnifferController controller = SnifferController.getInstance();

	private JMenuItem newWorkspace;
	private JMenuItem newProject;
	private JMenuItem newFuzzTest;
	//private JMenuItem save;
	//private JMenuItem saveAs;
	//private JMenuItem edit;
	//private JMenuItem undo;
	private JMenuItem exit;
	private JMenuItem about;
	
	private DataPacketTableModel model;

	private JFrame frame;

	public MyMenuBar(JFrame frame) {
		super();
		this.frame = frame;

		JMenu file = new JMenu("File");
		file.setMnemonic('F');

		newWorkspace = new JMenuItem("New Workspace");
		newWorkspace.addActionListener(this);
		newWorkspace.setMnemonic('W');
		newWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		file.add(newWorkspace);

		newProject = new JMenuItem("New Project");
		newProject.addActionListener(this);
		newProject.setMnemonic('P');
		newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		file.add(newProject);
		
		newFuzzTest = new JMenuItem("Add Fuzz Test");
		newFuzzTest.addActionListener(this);
		newFuzzTest.setMnemonic('T');
		newFuzzTest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
		file.add(newFuzzTest);
		
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		exit.setMnemonic('X');
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		file.add(exit);

		//save = new JMenuItem("Save (Ctrl + S)");
		//file.add(save);
		//saveAs = new JMenuItem("Save As");
		//file.add(saveAs);

		//edit = new JMenu("Edit");
		//undo = new JMenuItem("Undo (Ctrl + Z)");
		//edit.add(undo);

		add(file);
		
		JMenu help = new JMenu("Help");
		help.setMnemonic('H');
		about = new JMenuItem("About");
		about.addActionListener(this);
		about.setMnemonic('A');
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		help.add(about);
		
		add(help);
		
		
		//add(edit);
	}

	public void actionPerformed(ActionEvent e) {

		// WorkspaceController workControl = new WorkspaceController();
		if (e.getSource().equals(newWorkspace)) {
			WorkspaceWizard wizard = new WorkspaceWizard(frame);
			wizard.setVisible(true);
		} else if (e.getSource().equals(newProject)) {
			ProjectWizardDialog wizard = new ProjectWizardDialog(frame);
			wizard.setVisible(true);
		} else if (e.getSource().equals(newFuzzTest)) {			
			AddFuzzTest newWizard = new AddFuzzTest(null);			
			newWizard.setVisible(true);			
		} else if (e.getSource().equals(exit)) {
			int reply = JOptionPane.showConfirmDialog(this,"Would you like to save your work?","Save",JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
            	if (EthernetOptionsPanel.getRunningDevice() != null) {
    				controller.stop(EthernetOptionsPanel.getRunningDevice());
    			}
            	DataPacketTableModel.getInstance().removeAll();
            }
			TaskController.getInstance().killProcesses();
			System.exit(0);
		} else if (e.getSource().equals(about)) {
			AboutDialog dialog = new AboutDialog();
			dialog.setVisible(true);
		}

//		else if (e.getSource().equals(save)) {
//			System.out.println("project has been saved...");
//		}
//
//		else if (e.getSource().equals(saveAs)) {
//			System.out.println("project has been saved as...");
//		}
//
//		else if (e.getSource().equals(edit)) {
//			System.out.println("project has been edited...");
//		}
//
//		if (e.getSource().equals(undo)) {
//			System.out.println("undo has occured...");
//		}
	}
}
