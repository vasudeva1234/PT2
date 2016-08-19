//*********************************************************************************************************************
// FuzzTestController.java
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

import java.util.List;

import javax.persistence.EntityManager;

import org.epri.pt2.DO.DataTypeDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Add, update, or remove a fuzz test controller from the database.
 * @author Southwest Research Institute
 *
 */
public class FuzzTestController extends ViewCallback {
	private final static FuzzTestController controller;

	static {
		controller = new FuzzTestController();
	}

	private FuzzTestController() {
	};

	/**
	 * Get an instance of the controller.
	 * @return
	 */
	public static FuzzTestController getInstance() {
		return controller;
	}

	/**
	 * Add a fuzz test.
	 * @param fuzzTest
	 * @return
	 */
	public FuzzTestDO addFuzzTest(FuzzTestDO fuzzTest) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		fuzzTest = manager.merge(fuzzTest);
		manager.getTransaction().commit();
		manager.close();
		return fuzzTest;
	}
	
	/**
	 * Add a fuzz test.
	 * @param contentData
	 * @param fuzzTest
	 * @return
	 */
	public FuzzTestDO addFuzzTest(FuzzTestDO fuzzTest, DataTypeDO dataType) {
		return addFuzzTest(fuzzTest, dataType.getId());
	}

	/**
	 * Add a fuzz test.
	 * @param currentContentDataId
	 * @param fuzzTest
	 * @return
	 */
	public FuzzTestDO addFuzzTest(FuzzTestDO fuzzTest, int dataTypeId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		// Add fuzz test.
		fuzzTest = manager.merge(fuzzTest);
		manager.flush();

		// Update the relationships.
		DataTypeDO dataType = manager.find(DataTypeDO.class, new Integer(
				dataTypeId));

		fuzzTest.addDataType(dataType);
		dataType.addOwner(fuzzTest);
		
		fuzzTest = manager.merge(fuzzTest);
		manager.merge(dataType);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return fuzzTest;
	}
	
	/**
	 * Add a fuzz test.
	 * @param contentData
	 * @param fuzzTest
	 * @return
	 */
	public FuzzTestDO addFuzzTest(AbstractPacketDO packet, FuzzTestDO fuzzTest) {
		return addFuzzTest(packet.getId(), fuzzTest);
	}

	/**
	 * Add a fuzz test.
	 * @param packetId
	 * @param fuzzTest
	 * @return
	 */
	public FuzzTestDO addFuzzTest(int packetId, FuzzTestDO fuzzTest) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		fuzzTest = manager.merge(fuzzTest);
		manager.flush();

		AbstractPacketDO packet = manager.find(AbstractPacketDO.class, new Integer(
				packetId));

		packet.addFuzzTest(fuzzTest);
		fuzzTest.addOwner(packet);
		
		fuzzTest = manager.merge(fuzzTest);
		manager.merge(packet);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return fuzzTest;
	}



	
	/**
	 * Remove a fuzz test.
	 * @param packetId
	 * @param fuzzTestId
	 */
	public void removeFuzzTest(int packetId, int fuzzTestId) {

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class, 
				new Integer(fuzzTestId));
		AbstractPacketDO packet = manager.find(AbstractPacketDO.class,
				new Integer(packetId));


		// Remove relationship between fuzz test and packet.
		fuzzTest.removeOwner(packet);
		packet.removeFuzzTest(fuzzTest);

		manager.merge(packet);	
		manager.merge(fuzzTest);		

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

	}

	/**
	 * Remove a fuzz test from database.
	 * @param packetId
	 * @param fuzzTestId
	 */
	public int removeFuzzTestFromDB(int fuzzTestId) {

		int status = -1;
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class, 
				new Integer(fuzzTestId));
		
		if(fuzzTest.getOwners().size() > 0)
		{
			status = -1;
		}
		else
		{
			for(DataTypeDO dataType : fuzzTest.getDataTypeSet()) {
				dataType.removeOwner(fuzzTest);
				fuzzTest.removeDataType(dataType);
				manager.flush();
			}
			
//			manager.merge(fuzzTest);

			manager.remove(fuzzTest);
			status = 0;
		}	

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();
		
		return status;

	}


	/**
	 * Update a fuzz test.
	 * @param fuzzTest
	 */
	public FuzzTestDO updateFuzzTest(FuzzTestDO fuzzTest) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		fuzzTest = manager.merge(fuzzTest);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return fuzzTest;
	}

	/**
	 * Get a specific fuzz test.
	 * @param id
	 * @return
	 */
	public FuzzTestDO getFuzzTest(int id) {
		FuzzTestDO retval = null;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		retval = manager.find(FuzzTestDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		return retval;
	}
	
	/**
	 * Get all fuzz tests.
	 * @return
	 */
	public List<FuzzTestDO> getFuzzTests() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
		manager.getTransaction().begin();
		
		List<FuzzTestDO> fuzzTests = manager.createQuery("from FuzzTestDO", FuzzTestDO.class).getResultList();
		
		manager.getTransaction().commit();
		manager.close();
		
		return fuzzTests;
	}
}
