//*********************************************************************************************************************
// TestScriptController.java
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
package org.epri.pt2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.DO.TestScriptDO;
import org.epri.pt2.DO.TestScriptParameterDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Controls the addition, update, or removal of test scripts from the hibernate
 * database.
 * 
 * @author Tam Do
 * 
 */
public class TestScriptController extends ViewCallback {
	private final static TestScriptController controller;

	static {
		controller = new TestScriptController();
	}

	private TestScriptController() {
	};

	public static TestScriptController getInstance() {
		return controller;

	}

	public TestScriptDO addTestScript(int testCaseId, int projectId, TestScriptDO script) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		TestCaseDO testCase = manager.find(TestCaseDO.class, new Integer(
				testCaseId));
		ProjectDO project = manager.find(ProjectDO.class, new Integer(projectId));
		
		testCase.addTestScript(script);
		script.setOwner(testCase);
		script.setProject(project);

		manager.merge(testCase);
		script = manager.merge(script);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return script;
	}

	public TestScriptDO removeTestScript(int testCaseId, int testScriptId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		TestCaseDO testCase = manager.find(TestCaseDO.class, new Integer(
				testCaseId));
		TestScriptDO testScript = manager.find(TestScriptDO.class, new Integer(
				testScriptId));
		testCase.removeTestScript(testScript);
		testScript = manager.merge(testScript);
		manager.merge(testCase);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return testScript;
	}

	public TestScriptDO updateTestScript(TestScriptDO testScript) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		testScript = manager.merge(testScript);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();
		return testScript;
	}

	public List<TestScriptDO> getTestScripts(int testCaseId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<TestScriptDO> testScripts = manager.createQuery(
				"from TestScriptDO where OWNER_ID = " + testCaseId,
				TestScriptDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		return testScripts;
	}
	
	public TestScriptDO setParameters(TestScriptDO script, List<TestScriptParameterDO> params) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		script.setParameters(params);
		script = manager.merge(script);
		
		manager.getTransaction().commit();
		manager.close();
	
		return script;
	}

	public List<TestScriptParameterDO> getParameters(TestScriptDO script) {
		List<TestScriptParameterDO> p = new ArrayList<TestScriptParameterDO>();
		
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		script = manager.find(TestScriptDO.class, script.getId());
		List<TestScriptParameterDO> params = script.getParameters();
		
		for(TestScriptParameterDO param : params) {
			p.add(param);
		}
		
		manager.getTransaction().commit();
		manager.close();
	
		return p;
	}
	public void ExecuteScript(TestCaseDO testCase, TestScriptDO script) {
		try {
			ProcessBuilder pb = new ProcessBuilder(testCase.getCommand()
					+ script.getParams());
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
