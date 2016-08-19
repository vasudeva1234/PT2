//*********************************************************************************************************************
// DataPacketTableModel.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.controller.PacketController;
import org.epri.pt2.gui.SelectionState;
import org.epri.pt2.listeners.PacketListenerInterface;
//import org.epri.pt2.sniffer.AbstractDataPacket;

/**
 * @author Tam Do
 * 
 */
public class DataPacketTableModel extends AbstractTableModel implements
		PacketListenerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final DataPacketTableModel tableModel;

	private List<AbstractPacketDO> packets;
	public static enum ColumnNames {
		Time, Type, SrcIP, DstIP, SrcMac, DstMac, Payload
	};

	static {
		tableModel = new DataPacketTableModel();
	}

	private DataPacketTableModel() {
		packets = new ArrayList<AbstractPacketDO>();
		
		loadProjectPackets();
	};

	public void loadProjectPackets() {
		packets.clear();
		
		List<AbstractPacketDO> allPackets = PacketController.getInstance().getPacketList();
		
		for(AbstractPacketDO packet : allPackets) {
			int ownerId = PacketController.getInstance().getOwnerID(packet.getId());
			
			if(ownerId > 0 && ownerId == SelectionState.getInstance().getProjectId()) {
				packets.add(packet);
			}
		}
		
		fireTableDataChanged();
	}
	
	public static DataPacketTableModel getInstance() {
		return tableModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	@Override
	public String getColumnName(int column) {
		return ColumnNames.values()[column].toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return packets.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		if (col == ColumnNames.Time.ordinal()) {
			Date timestamp = packets.get(row).getTimestamp();
			if (timestamp != null) {
				return packets.get(row).getTimestamp().toString();
			} else {
				return "";
			}
		} else if (col == ColumnNames.Type.ordinal()) {
			return packets.get(row).getType();
		} else if (col == ColumnNames.SrcIP.ordinal()) {
			return packets.get(row).getSource();
		} else if (col == ColumnNames.DstIP.ordinal()) {
			return packets.get(row).getDestination();
		} else if (col == ColumnNames.SrcMac.ordinal()) {
			return packets.get(row).getMacSrc();
		} else if (col == ColumnNames.DstMac.ordinal()) {
			return packets.get(row).getMacDst();
		} else if (col == ColumnNames.Payload.ordinal()) {
			return new String(packets.get(row).getData());
		}

		return null;
	}

	public AbstractPacketDO getRow(int row) {
		return packets.get(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.epri.pt2.listeners.PacketListenerInterface#packetArrived(org.epri
	 * .pt2.sniffer.AbstractDataPacket)
	 */
	public void packetArrived(AbstractPacketDO packet) {
		// persist the packet in the database
		packet = PacketController.getInstance().addPacket(packet, SelectionState.getInstance().getProjectId());

		packets.add(packet);
		
		fireTableRowsInserted(packets.size() - 1, packets.size() - 1);
		// fireTableDataChanged();
	}

	/**
	 * remove all packets
	 */
	public void removeAll() {
		if(!packets.isEmpty())
		{
			for(AbstractPacketDO packet : packets) {
				
				// TODO this does not drop the packets from the database
				PacketController.getInstance().removePacket(packet.getId(), SelectionState.getInstance().getProjectId());
			}
			packets.clear();
			fireTableDataChanged();
		}
	}
}
