//*********************************************************************************************************************
// TestExecutionTask.java
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import javax.swing.JTextArea;

import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.DO.TestScriptDO;

import com.jgoodies.common.base.SystemUtils;

/**
 * A class for executing programs on an operating system.
 * 
 * @author Tam Do
 * 
 */
public class TestExecutionTask implements ActionListener {
	protected JTextArea statusArea;
	protected TestCaseDO testCase;
	protected TestScriptDO testScript;
	protected Process p;
	protected Object pid;

	public TestExecutionTask(TestCaseDO testCase, TestScriptDO testScript) {
		this.testCase = testCase;
		this.testScript = testScript;
	}

	public void executeTask() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					String command = testCase.getCommand()
							+ testScript.getParams();

					if(SystemUtils.IS_OS_WINDOWS) {
						p = Runtime.getRuntime().exec(command, new String[] {},
								new File(testCase.getDirectory()));						
					}
					else {
						p = Runtime.getRuntime().exec(new String[] {"bash", "-c", "cd " + testCase.getDirectory() + "; " + command});						
					}

					Field f = p.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.get(p);

					BufferedReader buffRead = new BufferedReader(
							new InputStreamReader(p.getInputStream()));

					String line = "";

					statusArea = StatusArea.getInstance().addRunTab(
							"(" + testCase.getId() + ") " + testCase.getName(),
							TestExecutionTask.this);

					statusArea.append(testCase.getCommand()
							+ testScript.getParams() + "\r\n");
					statusArea.append("cd " + testCase.getDirectory() + "; " + command);

					while ((line = buffRead.readLine()) != null) {
						statusArea.append(line + "\r\n");
						Thread.sleep(250);
					}

					int exitVal = p.waitFor();

					statusArea.append("ExitCode: " + exitVal);

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		thread.start();

	}

	public void stopTask() {
		if (pid != null) {
			try {
				//System.out.println("Attempting to kill " + pid);
				Runtime.getRuntime().exec("pkill -TERM -P " + pid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		stopTask();
	}
}
