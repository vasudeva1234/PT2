//*********************************************************************************************************************
// TestCaseController.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Add, update, or remove a test case controller from the database.
 * 
 * @author tdo
 * 
 */
public class TestCaseController extends ViewCallback {
	private final static TestCaseController controller;

	static {
		controller = new TestCaseController();
	}

	private TestCaseController() {
	};

	/**
	 * Get an instance of the controller.
	 * 
	 * @return
	 */
	public static TestCaseController getInstance() {
		return controller;
	}

	/**
	 * Add a testcase to the database.
	 * 
	 * @param testCase
	 * @return
	 */
	public TestCaseDO addTestCase(TestCaseDO testCase) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		testCase = manager.merge(testCase);
		manager.getTransaction().commit();
		manager.close();
		return testCase;
	}

	/**
	 * Add a test case to the project.
	 * 
	 * @param project
	 * @param testCase
	 * @return
	 */
	public TestCaseDO addTestCase(ProjectDO project, TestCaseDO testCase) {
		return addTestCase(project.getId(), testCase);
	}

	/**
	 * Add a test case to the project.
	 * 
	 * @param currentProjectId
	 * @param testCase
	 * @return
	 */
	public TestCaseDO addTestCase(int currentProjectId, TestCaseDO testCase) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		testCase = manager.merge(testCase);
		manager.flush();

		ProjectDO project = manager.find(ProjectDO.class, new Integer(
				currentProjectId));

		project.addTestCase(testCase);
		testCase.addOwner(project);

		testCase = manager.merge(testCase);
		manager.merge(project);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return testCase;
	}

	/**
	 * Remove a testcase from the project.
	 * 
	 * @param projectId
	 * @param testCaseId
	 */
	public void removeTestCase(int projectId, int testCaseId) {

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		TestCaseDO testCase = manager.find(TestCaseDO.class, new Integer(
				testCaseId));
		ProjectDO project = manager.find(ProjectDO.class,
				new Integer(projectId));

		testCase.removeOwner(project);
		project.removeTestCase(testCase);

		manager.merge(project);
		testCase = manager.merge(testCase);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

	}

	/**
	 * Update a testcase.
	 * 
	 * @param testCase
	 */
	public TestCaseDO updateTestCase(TestCaseDO testCase) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		testCase = manager.merge(testCase);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return testCase;
	}

	/**
	 * List all test cases in the project.
	 * 
	 * @param projectid
	 * @return
	 */
	public List<TestCaseDO> getTestCases(int projectid) {

		if (projectid > 0) {
			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();

			ProjectDO project = manager.find(ProjectDO.class, new Integer(
					projectid));

			manager.getTransaction().commit();
			manager.close();

			if (project.getTestCaseSet() != null) {
				ArrayList<TestCaseDO> list = new ArrayList<TestCaseDO>(
						project.getTestCaseSet());
				Collections.sort(list);

				return list;
			}
		}
		return new ArrayList<TestCaseDO>();
	}

	/**
	 * Get a specific test case.
	 * 
	 * @param id
	 * @return
	 */
	public TestCaseDO getTestCase(int id) {
		TestCaseDO retval = null;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		retval = manager.find(TestCaseDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		return retval;
	}

	/**
	 * Get all test cases.
	 * 
	 * @return
	 */
	public List<TestCaseDO> getTestCases() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		List<TestCaseDO> testCases = manager.createQuery("from TestCaseDO",
				TestCaseDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		return testCases;
	}

	/**
	 * Get all test cases for a given protocol.
	 * 
	 * @param projectId
	 * @param protocol
	 */
	public void loadProtocolTestCases(int projectId, String protocol) {
		List<TestCaseDO> testCases;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		testCases = manager.createQuery(
				"from TestCaseDO testCases where testCases.protocol = '"
						+ protocol + "'", TestCaseDO.class).getResultList();

		ProjectDO project = manager.find(ProjectDO.class,
				new Integer(projectId));

		for (TestCaseDO testCase : testCases) {
			project.addTestCase(testCase);
			testCase.addOwner(project);
			manager.merge(project);
			manager.merge(testCase);
		}

		manager.getTransaction().commit();
		manager.close();
	}
}
