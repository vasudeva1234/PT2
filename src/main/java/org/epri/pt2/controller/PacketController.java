//*********************************************************************************************************************
// PacketController.java
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
import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.listeners.ViewCallback;
import org.hibernate.Hibernate;

/**
 * @author root
 *
 */
public class PacketController extends ViewCallback {
	private final static PacketController controller;

	static {
		controller = new PacketController();
	}

	private PacketController() {
	};

	/**
	 * Get an instance of the controller.
	 * @return
	 */
	public static PacketController getInstance() {
		return controller;
	}

	/**
	 * Add a packet to the database.
	 * @param packet
	 * @return
	 */
	public AbstractPacketDO addPacket(AbstractPacketDO packet) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		packet = manager.merge(packet);
		manager.getTransaction().commit();
		manager.close();
		return packet;
	}
	
	/**
	 * Add a packet to the database.
	 * @param packet
	 * @param project
	 * @param content data
	 * @return
	 */
	public HTTPPacketDO addPacket(HTTPPacketDO packet, ProjectDO project, HTTPContentDataDO contentData) {
		return addPacket(packet, project.getId(), contentData.getId());
	}

	/**
	 * Add a content data to the database.
	 * @param packet
	 * @param projectId
	 * @param contentDataId
	 * @return
	 */
	public HTTPPacketDO addPacket(HTTPPacketDO packet, int projectId, int contentDataId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		// Add packet.
		packet = manager.merge(packet);
		manager.flush();

		// Update the relationships.
		ProjectDO project = manager.find(ProjectDO.class, new Integer(
				projectId));

		project.getPacketList().add(packet);
		packet.setOwner(project);
		
		HTTPContentDataDO contentData = manager.find(HTTPContentDataDO.class, new Integer(
				contentDataId));

		packet.getContentDataList().add(contentData);
		contentData.setOwner(packet);
		
		packet = manager.merge(packet);
		manager.merge(project);
		manager.merge(contentData);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return packet;
	}
	
	/**
	 * Add a content data to the database.
	 * @param packet
	 * @param projectId
	 * @param contentDataId
	 * @return
	 */
	public AbstractPacketDO addPacket(AbstractPacketDO packet, int projectId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		// Add packet.
		packet = manager.merge(packet);
		manager.flush();

		// Update the relationships.
		ProjectDO project = manager.find(ProjectDO.class, new Integer(
				projectId));

		project.getPacketList().add(packet);
		packet.setOwner(project);
		
		packet = manager.merge(packet);
		manager.merge(project);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return packet;
	}

	/**
	 * Remove packet.
	 * @param packetId
	 * @param projectId
	 */
	public void removePacket(int packetId, int projectId) {

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		AbstractPacketDO packet = manager.find(AbstractPacketDO.class,
				new Integer(packetId));
//		ContentDataDO contentData = manager.find(ContentDataDO.class, 
//				new Integer(contentDataId));

		ProjectDO project = manager.find(ProjectDO.class, 
				new Integer(projectId));

		// Remove relationships.
		packet.setOwner(null);
		project.getPacketList().remove(packet);
		manager.merge(project);
		manager.flush();
		manager.remove(packet);
//		manager.merge(packet);
		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

	}
	
	/**
	 * Update packet.
	 * @param packet
	 */
	public AbstractPacketDO updatePacket(AbstractPacketDO packet) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		packet = manager.merge(packet);

		manager.getTransaction().commit();
		manager.close();
		notifyListeners();

		return packet;
	}
	
	/**
	 * Get packet with given id.
	 * @param id
	 * @return
	 */
	public AbstractPacketDO getPacket(int id) {
		AbstractPacketDO retval = null;

		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		retval = manager.find(AbstractPacketDO.class, new Integer(id));

		manager.getTransaction().commit();
		manager.close();

		Hibernate.initialize(retval);
		return retval;
	}
	
	/**
	 * Get all packets.
	 * @return
	 */
	public List<AbstractPacketDO> getPacketList() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
		manager.getTransaction().begin();
		
		List<AbstractPacketDO> packets = manager.createQuery("from AbstractPacketDO", AbstractPacketDO.class).getResultList();
		
		manager.getTransaction().commit();
		manager.close();
		
		return packets;
	}
	
	public int getOwnerID(int packetId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
		manager.getTransaction().begin();
		
		AbstractPacketDO packet = manager.find(AbstractPacketDO.class, new Integer(packetId));
		manager.getTransaction().commit();
		
		int ownerId = packet.getOwner().getId();
		manager.close();
		
		return ownerId;
	}
}
