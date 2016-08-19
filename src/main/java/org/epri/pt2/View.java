//*********************************************************************************************************************
// View.java
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
package org.epri.pt2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.swing.JFrame;

import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.controller.TestCaseController;
import org.epri.pt2.gui.WorkspaceFrame;
import org.epri.pt2.scriptxmlparser.ScriptXmlParser;

/**
 * The main workspace view for the project.
 * 
 * @author tdo
 */

public class View {
	private final static String DEFAULT_SCRIPT_DIRECTORY = "scripts/";

	/**
	 * Generates the main workspace frame.
	 */
	private static JFrame createGUI() {

		// Load the default test cases
		loadTestCases();

		try {
			// UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e) {
		}

		return new WorkspaceFrame();
	}

	/**
	 * Load the list of default test cases from the scripts/ directory.
	 */
	private static void loadTestCases() {

		// Check if test cases already exist in the database
		List<TestCaseDO> testCases = TestCaseController.getInstance()
				.getTestCases();

		if (testCases.size() == 0) {

			// Scan the default directory for XML files
			File dir = new File(DEFAULT_SCRIPT_DIRECTORY);
			
			if (!dir.exists()) {
				dir = new File("res/" + DEFAULT_SCRIPT_DIRECTORY);
			}
			
			File[] dirs = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".xml");
				}
			});

			// Load the test cases
			for (File f : dirs) {
				ScriptXmlParser.loadTestCase(f.getPath());
			}
		}
	}

	/**
	 * Creates a thread for the GUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	}
}
