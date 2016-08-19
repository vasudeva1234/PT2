//*********************************************************************************************************************
// AbstractProtocolController.java
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

import org.epri.pt2.ProtocolMapper;
import org.epri.pt2.ProtocolType;
import org.epri.pt2.DO.AbstractIfaceDO;
import org.epri.pt2.DO.AbstractProtocolDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Controls the addition, update, and removal of AbstractProtocolDO objects from
 * the hibernate database.
 * 
 * @author tdo
 * 
 */
public class AbstractProtocolController extends ViewCallback {
	private final static AbstractProtocolController controller;

	static {
		controller = new AbstractProtocolController();
		controller.createProtocols();
	}

	private AbstractProtocolController() {
	};

	/**
	 * Get an instance of the controller.
	 * 
	 * @return
	 */
	public static AbstractProtocolController getInstance() {
		return controller;
	}

	/**
	 * For each ProtocolType defined, create an instance of the protocol and
	 * store the protocol in the database.
	 */
	public void createProtocols() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		for (ProtocolType prot_type : ProtocolType.values()) {
			AbstractProtocolDO prot = ProtocolMapper.getClass(prot_type);
			prot = manager.merge(prot);
			@SuppressWarnings("unused")
			int i = 0;
		}

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();
	}

	/**
	 * For a given interface, set the protocol.
	 * 
	 * @param ifaceId
	 * @param protocolId
	 * @return
	 */
	public AbstractProtocolDO setProtocol(int ifaceId, int protocolId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		AbstractIfaceDO iface = manager.find(AbstractIfaceDO.class,
				new Integer(ifaceId));
		AbstractProtocolDO prot = manager.find(AbstractProtocolDO.class,
				new Integer(protocolId));

		iface.setProtocol(prot);
		prot.addInterface(iface);

		prot = manager.merge(prot);
		manager.merge(iface);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return prot;
	}

	/**
	 * Get the protocol object belonging to the interface.
	 * 
	 * @param ifaceId
	 * @return
	 */
	public AbstractProtocolDO getProtocol(int ifaceId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		AbstractIfaceDO iface = manager.find(AbstractIfaceDO.class,
				new Integer(ifaceId));

		manager.getTransaction().commit();

		manager.close();

		return iface.getProtocol();
	}

	/**
	 * Retrieve a list of all protocols in the database.
	 * 
	 * @return
	 */
	public List<AbstractProtocolDO> getAllProtocols() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		List<AbstractProtocolDO> protocols = manager.createQuery(
				"from AbstractProtocolDO", AbstractProtocolDO.class)
				.getResultList();

		manager.getTransaction().commit();
		manager.close();

		return protocols;
	}
}