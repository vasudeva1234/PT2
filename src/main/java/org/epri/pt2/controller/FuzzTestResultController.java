//*********************************************************************************************************************
// FuzzTestResultController.java
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

import org.epri.pt2.DO.FuzzTestResultDO;
import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * @author Southwest Research Institute
 *
 */
public class FuzzTestResultController extends ViewCallback {
	private final static FuzzTestResultController controller;

	static {
		controller = new FuzzTestResultController();
	}

	private FuzzTestResultController() {
	};

	/**
	 * Get an instance of the controller.
	 * @return
	 */
	public static FuzzTestResultController getInstance() {
		return controller;
	}

	/**
	 * Add a result to the database.
	 * @param fuzzTestResult
	 * @return
	 */
	public FuzzTestResultDO addFuzzTestResult(FuzzTestResultDO fuzzTestResult) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		fuzzTestResult = manager.merge(fuzzTestResult);
		manager.getTransaction().commit();
		manager.close();
		return fuzzTestResult;
	}

	/**
	 * Add result.
	 * @param packet
	 * @param fuzzTestResult
	 * @return
	 */
	public FuzzTestResultDO addFuzzTestResult(AbstractPacketDO packet, FuzzTestResultDO fuzzTestResult) {
		return addFuzzTestResult(packet.getId(), fuzzTestResult);
	}
	
	/**
	 * Add a result to the database.
	 * @param fuzzTestResult
	 * @param packetId
	 * @return
	 */
	public FuzzTestResultDO addFuzzTestResult(int packetId, FuzzTestResultDO fuzzTestResult) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		// Add result.
		fuzzTestResult = manager.merge(fuzzTestResult);
		manager.flush();

		// Update the relationships.
		AbstractPacketDO packet = manager.find(AbstractPacketDO.class, new Integer(
				packetId));

		packet.addFuzzTestResult(fuzzTestResult);		
		fuzzTestResult.setOwner(packet);

		fuzzTestResult = manager.merge(fuzzTestResult);
		manager.merge(packet);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return fuzzTestResult;
	}

	/**
	 * Remove result.
	 * @param fuzzTestResultId
	 * @param packetId
	 */
	public void removeFuzzTestResult(int fuzzTestResultId, int packetId) {

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		FuzzTestResultDO fuzzTestResult = manager.find(FuzzTestResultDO.class, 
				new Integer(fuzzTestResultId));

		AbstractPacketDO packet = manager.find(AbstractPacketDO.class, 
				new Integer(packetId));

		// Remove relationships.
		fuzzTestResult.setOwner(null);
		packet.getFuzzTestResultList().remove(fuzzTestResult);

		manager.merge(fuzzTestResult);
		manager.merge(packet);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

	}
	
	/**
	 * Update a result.
	 * @param fuzzTestResult
	 */
	public FuzzTestResultDO updateFuzzTestResult(FuzzTestResultDO fuzzTestResult) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		fuzzTestResult = manager.merge(fuzzTestResult);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return fuzzTestResult;
	}
	
	/**
	 * Get result with given id.
	 * @param id
	 * @return
	 */
	public FuzzTestResultDO getFuzzTestResult(int id) {
		FuzzTestResultDO retval = null;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		retval = manager.find(FuzzTestResultDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		return retval;
	}
	
	/**
	 * Get all result.
	 * @return
	 */
	public List<FuzzTestResultDO> getFuzzTestResultList() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
		manager.getTransaction().begin();
		
		List<FuzzTestResultDO> fuzzTestResult = manager.createQuery("from FuzzTestResultDO", FuzzTestResultDO.class).getResultList();
		
		manager.getTransaction().commit();
		manager.close();
		
		return fuzzTestResult;
	}

}

