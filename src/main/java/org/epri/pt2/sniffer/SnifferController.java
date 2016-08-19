//*********************************************************************************************************************
// SnifferController.java
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
package org.epri.pt2.sniffer;

import java.util.HashMap;
import java.util.Map;

import org.epri.pt2.DO.AbstractIfaceDO;
import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.controller.AbstractIfaceController;
import org.epri.pt2.gui.model.DataPacketTableModel;

/**
 * A controller for managing multiple sniffer instances from a View class.
 * 
 * @author Tam Do
 * 
 */
public class SnifferController {
	private static SnifferController controller;

	Map<Integer, AbstractSniffer> sniffers;

	static {
		controller = new SnifferController();
	}

	private SnifferController() {
		sniffers = new HashMap<Integer, AbstractSniffer>();
	}

	public static SnifferController getInstance() {
		return controller;
	}

	public AbstractSniffer getSniffer(DeviceDO device) {
		if (sniffers.containsKey(device.getId())) {
			return sniffers.get(device.getId());
		}

		return null;
	}

	public void start(DeviceDO device, String name) {
		if (sniffers.containsKey(device.getId())) {
			AbstractSniffer sniffer = sniffers.get(device.getId());
			sniffer.connect();
			
			if (device.getInterfaces().size() > 0) {
				AbstractIfaceDO iface = device.getInterfaces().get(0);
				iface.setPhysicalInterface(name);
				switch (iface.getName()) {
				case Ethernet:
					iface.setConnected(sniffer.isOpen());
					AbstractIfaceController.getInstance().updateInterface(iface);
					break;
				default:
					break;
				}
			}
		} else {
			if (device.getInterfaces().size() > 0) {
				AbstractIfaceDO iface = device.getInterfaces().get(0);
				switch (iface.getName()) {
				case Ethernet:
					AbstractSniffer sniffer = new PCAPEthListener(name);
					sniffer.addPacketListener(DataPacketTableModel
							.getInstance());
					sniffer.connect();
					iface.setConnected(sniffer.isOpen());
					iface.setPhysicalInterface(name);
					AbstractIfaceController.getInstance()
							.updateInterface(iface);
					sniffers.put(device.getId(), sniffer);
					break;
				default:
					break;

				}
			}
		}
	}

	public void stop(DeviceDO device) {
		if (sniffers.containsKey(device.getId())) {
			AbstractSniffer sniffer = sniffers.get(device.getId());
			sniffer.disconnect();

			if (device.getInterfaces().size() > 0) {
				AbstractIfaceDO iface = device.getInterfaces().get(0);
				switch (iface.getName()) {
				case Ethernet:
					iface.setConnected(sniffer.isOpen());
					AbstractIfaceController.getInstance().updateInterface(iface);
					break;
				default:
					break;
				}
			}
		}
	}

	public boolean isOpen(DeviceDO device) {
		if (sniffers.containsKey(device.getId())) {
			AbstractSniffer sniffer = sniffers.get(device.getId());
			return sniffer.isOpen();
		} else {
			return false;
		}
	}
}
