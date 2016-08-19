//*********************************************************************************************************************
// DeviceController.java
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

import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.DO.ProjectDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * Add, update, removes, or lists devices associated with a project.
 * 
 * @author tdo
 */
public class DeviceController extends ViewCallback {
	private static final DeviceController controller;

	static {
		controller = new DeviceController();
		controller.resetDeviceState();
	}

	private DeviceController() {
	};

	/**
	 * Get an instance of the controller.
	 * 
	 * @return
	 */
	public static DeviceController getInstance() {
		return controller;
	}

	private void resetDeviceState() {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		List<DeviceDO> devices = manager.createQuery("from DeviceDO",
				DeviceDO.class).getResultList();

		for (DeviceDO device : devices) {
			try {
				device.getInterfaces().get(0).setPhysicalInterface(null);
				device.getInterfaces().get(0).setConnected(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		manager.getTransaction().commit();
		manager.close();
	}

	/**
	 * Adds a device to the project.
	 * 
	 * @param project
	 * @param device
	 */
	public DeviceDO addDevice(ProjectDO project, DeviceDO device) {
		return addDevice(project.getId(), device);
	}

	/**
	 * Adds a device to the project.
	 * 
	 * @param projectId
	 * @param device
	 * @return
	 */
	public DeviceDO addDevice(int projectId, DeviceDO device) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		ProjectDO project = manager.find(ProjectDO.class,
				new Integer(projectId));

		project.getDeviceList().add(device);
		device.setOwner(project);

		device = manager.merge(device);
		manager.merge(project);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return device;
	}

	/**
	 * Updates a device in the database.
	 * 
	 * @param device
	 * @return
	 */
	public DeviceDO updateDevice(DeviceDO device) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();

		device = manager.merge(device);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return device;
	}

	/**
	 * Removes a device from the project. The device is still persisted in the
	 * database and is an orphan.
	 * 
	 * @param project
	 * @param device
	 */
	public DeviceDO removeDevice(int projectId, int deviceId) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		ProjectDO project = manager.find(ProjectDO.class,
				new Integer(projectId));
		DeviceDO device = manager.find(DeviceDO.class, new Integer(deviceId));
		project.removeDevice(device);
		device.setOwner(null);

		manager.merge(project);
		device = manager.merge(device);
		manager.getTransaction().commit();
		manager.close();

		notifyListeners();

		return device;
	}

	/**
	 * Retrieves the list of devices associated with a project.
	 * 
	 * @param project
	 */
	public List<DeviceDO> getDevices(ProjectDO project) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<DeviceDO> devices = manager.createQuery(
				"from DeviceDO WHERE OWNER_ID = " + project.getId(),
				DeviceDO.class).getResultList();

		manager.getTransaction().commit();
		manager.close();
		return devices;
	}

	/**
	 * Retrieves a list of devices associated with a project.
	 * 
	 * @param id
	 * @return
	 */
	public List<DeviceDO> getDevices(int id) {
		EntityManager manager = EntityManagerFactoryInstance.getInstance()
				.createEntityManager();
		manager.getTransaction().begin();
		List<DeviceDO> devices = manager.createQuery(
				"from DeviceDO WHERE OWNER_ID = " + id, DeviceDO.class)
				.getResultList();

		manager.getTransaction().commit();
		manager.close();
		return devices;
	}
}
