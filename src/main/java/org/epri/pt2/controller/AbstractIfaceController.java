//*********************************************************************************************************************
// AbstractIfaceController.java
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

import org.epri.pt2.DO.AbstractIfaceDO;
import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Controls the addition, update, and removal of AbstractInterfaceDO objects
 * from the hibernate database. Implemented as a singleton.
 * 
 * @author Tam Do
 * 
 */
public class AbstractIfaceController extends ViewCallback {
	private final static AbstractIfaceController controller;

	static {
		controller = new AbstractIfaceController();
	}

	private AbstractIfaceController() {
	};

	/**
	 * Get an instance of the controller.
	 * 
	 * @return an instance of the controller
	 */
	public static AbstractIfaceController getInstance() {
		return controller;
	}

	/**
	 * Get a list of interfaces which contains the device.
	 * 
	 * @param deviceId
	 *            The device id
	 * @return A list of interfaces which contain the device
	 */
	public List<AbstractIfaceDO> getInterfaces(int deviceId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		DeviceDO device = manager.find(DeviceDO.class, new Integer(deviceId));

		manager.getTransaction().commit();
		manager.close();

		// Get a list of interfaces sorted
		ArrayList<AbstractIfaceDO> list = new ArrayList<AbstractIfaceDO>(
				device.getId());
		Collections.sort(list);

		return list;
	}

	/**
	 * Get all interfaces in the database.
	 * 
	 * @return A list of all AbstractIfaceDO objects in the database
	 */
	public List<AbstractIfaceDO> getAllInterfaces() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		List<AbstractIfaceDO> ifaces = manager.createQuery(
				"From AbstractIfaceDO", AbstractIfaceDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();

		return ifaces;
	}

	/**
	 * Add an interface to the database.
	 * 
	 * @param deviceId
	 *            The deviceid to associate with the interface
	 * @param iface
	 *            The interface instance
	 * @return The updated interface instance
	 */
	public AbstractIfaceDO addInterface(int deviceId, AbstractIfaceDO iface) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		iface = manager.merge(iface);
		manager.flush();

		DeviceDO device = manager.find(DeviceDO.class, new Integer(deviceId));

		device.addInterface(iface);
		iface.addOwner(device);

		manager.merge(device);
		iface = manager.merge(iface);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return iface;
	}

	/**
	 * Remove an interface from the database.
	 * 
	 * @param deviceId
	 *            The deviceId to remove from the interface
	 * @param ifaceId
	 *            The interface id
	 * @return The updated interface
	 */
	public AbstractIfaceDO removeInterface(int deviceId, int ifaceId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		DeviceDO device = manager.find(DeviceDO.class, new Integer(deviceId));
		AbstractIfaceDO iface = manager.find(AbstractIfaceDO.class,
				new Integer(ifaceId));

		device.removeInterface(iface);
		iface.removeOwner(device);

		manager.merge(device);
		manager.merge(iface);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return iface;
	}

	/**
	 * Update an interface in the database.
	 * 
	 * @param iface
	 *            The interface
	 * @return The updated interface
	 */
	public AbstractIfaceDO updateInterface(AbstractIfaceDO iface) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		iface = manager.merge(iface);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();
		return iface;
	}

	/**
	 * Get a list of interfaces which contain the device.
	 * 
	 * @param device
	 *            The instance of the device
	 * @return A list of interfaces contain the device
	 */
	public List<AbstractIfaceDO> getAbstractIfaceDO(DeviceDO device) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<AbstractIfaceDO> ifaces = manager.createQuery(
				"from AbstractIfaceDO WHERE OWNER_ID = " + device.getId(),
				AbstractIfaceDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();
		return ifaces;
	}
}