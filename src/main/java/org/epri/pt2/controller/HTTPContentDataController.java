//*********************************************************************************************************************
// HTTPContentDataController.java
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

import org.epri.pt2.DO.HTTPContentDataDO;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * @author Southwest Research Institute
 *
 */
public class HTTPContentDataController extends ViewCallback {
	private final static HTTPContentDataController controller;

	static {
		controller = new HTTPContentDataController();
	}

	private HTTPContentDataController() {
	};

	/**
	 * Get an instance of the controller.
	 * @return
	 */
	public static HTTPContentDataController getInstance() {
		return controller;
	}

	/**
	 * Add a content data to the database.
	 * @param contentData
	 * @return
	 */
	public HTTPContentDataDO addHTTPContentData(HTTPContentDataDO contentData) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		contentData = manager.merge(contentData);
		manager.getTransaction().commit();
		manager.close();
		return contentData;
	}

	/**
	 * Add content data.
	 * @param packet
	 * @param contentData
	 * @return
	 */
	public HTTPContentDataDO addHTTPContentData(HTTPPacketDO packet, HTTPContentDataDO contentData) {
		return addHTTPContentData(packet.getId(), contentData);
	}
	
	/**
	 * Add a content data to the database.
	 * @param contentData
	 * @param packetId
	 * @return
	 */
	public HTTPContentDataDO addHTTPContentData(int packetId, HTTPContentDataDO contentData) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		// Add content data.
		contentData = manager.merge(contentData);
		manager.flush();

		// Update the relationships.
		HTTPPacketDO packet = manager.find(HTTPPacketDO.class, new Integer(
				packetId));

		packet.addContentData(contentData);		
		contentData.setOwner(packet);

		contentData = manager.merge(contentData);
		manager.merge(packet);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return contentData;
	}

	/**
	 * Remove content data.
	 * @param contentDataId
	 * @param packetId
	 */
	public void removeHTTPContentData(int contentDataId, int packetId) {

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		HTTPContentDataDO contentData = manager.find(HTTPContentDataDO.class, 
				new Integer(contentDataId));

		HTTPPacketDO packet = manager.find(HTTPPacketDO.class, 
				new Integer(packetId));

		// Remove relationships.
		contentData.setOwner(null);
		packet.getContentDataList().remove(contentData);

		manager.merge(contentData);
		manager.merge(packet);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

	}
	
	/**
	 * Update a content data.
	 * @param contentData
	 */
	public HTTPContentDataDO updateHTTPContentData(HTTPContentDataDO contentData) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		contentData = manager.merge(contentData);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return contentData;
	}
	
	/**
	 * Get content data with given id.
	 * @param id
	 * @return
	 */
	public HTTPContentDataDO getHTTPContentData(int id) {
		HTTPContentDataDO retval = null;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		retval = manager.find(HTTPContentDataDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		return retval;
	}
	
	/**
	 * Get all content data.
	 * @return
	 */
	public List<HTTPContentDataDO> getHTTPContentDataList() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
		manager.getTransaction().begin();
		
		List<HTTPContentDataDO> contentData = manager.createQuery("from HTTPContentDO", HTTPContentDataDO.class).getResultList();
		
		manager.getTransaction().commit();
		manager.close();
		
		return contentData;
	}

}
