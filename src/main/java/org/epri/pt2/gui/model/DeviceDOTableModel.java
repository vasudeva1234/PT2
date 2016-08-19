//*********************************************************************************************************************
// DeviceDOTableModel.java
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
package org.epri.pt2.gui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.AbstractIfaceDO;
import org.epri.pt2.DO.AbstractProtocolDO;
import org.epri.pt2.DO.DeviceDO;
import org.epri.pt2.controller.AbstractIfaceController;
import org.epri.pt2.controller.AbstractProtocolController;
import org.epri.pt2.controller.DeviceController;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.listeners.ViewCallbackInterface;

/**
 * @author tdo
 * 
 */
public class DeviceDOTableModel extends AbstractTableModel implements
		ViewCallbackInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8292788416126871524L;

	public static enum ColumnNames {
		ID, Device, Type, Protocol, Interface, State
	};

	private DeviceController deviceController = DeviceController.getInstance();
	private AbstractIfaceController ifaceController = AbstractIfaceController
			.getInstance();
	private AbstractProtocolController protController = AbstractProtocolController
			.getInstance();

	private SelectionState state = SelectionState.getInstance();

	public DeviceDOTableModel() {
		deviceController.registerListener(this);
		ifaceController.registerListener(this);
		protController.registerListener(this);
	}

	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	public int getRowCount() {
		return deviceController.getDevices(state.getProjectId()).size();
	}

	@Override
	public String getColumnName(int column) {
		return ColumnNames.values()[column].toString();
	}

	public Object getValueAt(int row, int col) {
		if (state.getProjectId() > 0) {

			if (col == ColumnNames.ID.ordinal()) {
				return deviceController.getDevices(state.getProjectId())
						.get(row).getId();
			} else if (col == ColumnNames.Device.ordinal()) {
				return deviceController.getDevices(state.getProjectId())
						.get(row).getName();
			} else if (col == ColumnNames.Type.ordinal()) {
				List<AbstractIfaceDO> ifaces = deviceController
						.getDevices(state.getProjectId()).get(row)
						.getInterfaces();
				if (ifaces.size() > 0) {
					return ifaces.get(0).getName();
				}
			} else if (col == ColumnNames.Protocol.ordinal()) {
				List<AbstractIfaceDO> ifaces = deviceController
						.getDevices(state.getProjectId()).get(row)
						.getInterfaces();
				if (ifaces.size() > 0) {
					// TODO add support for multiple interfaces
					AbstractProtocolDO protocol = ifaces.get(0).getProtocol();
					if (protocol.getName() != null) {
						return protocol.getName();
					}
				}
			} else if (col == ColumnNames.Interface.ordinal()) {
				List<AbstractIfaceDO> ifaces = deviceController
						.getDevices(state.getProjectId()).get(row)
						.getInterfaces();
				if (ifaces.size() > 0) {
					// TODO add support for multiple interfaces
					return ifaces.get(0).getPhysicalInterface();
				}
			} else if (col == ColumnNames.State.ordinal()) {
				List<AbstractIfaceDO> ifaces = deviceController
						.getDevices(state.getProjectId()).get(row)
						.getInterfaces();
				if (ifaces.size() > 0) {
					// TODO add support for multiple interfaces
					if (ifaces.get(0).isConnected()) {
						return "Connected";
					} else {
						return "Not Connected";
					}
				}
			}
		}
		return null;
	}

	public DeviceDO getDevice(int row) {
		return deviceController.getDevices(state.getProjectId()).get(row);
	}

	public DeviceDO removeDevice(int row) {
		return deviceController.removeDevice(state.getProjectId(),
				getDevice(row).getId());
	}

	public void dataChanged() {

		fireTableDataChanged();
	}
}
