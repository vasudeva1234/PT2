//*********************************************************************************************************************
// ResponsePacketTableModel.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.controller.PacketController;


/**
 * @author Southwest Research Institute
 *
 */
public class ResponsePacketTableModel extends AbstractTableModel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 956092483631402909L;

	private static final ResponsePacketTableModel tableModel;

	Map<Integer, AbstractPacketDO> packetMap = new HashMap<Integer, AbstractPacketDO>();	

	public static enum ResponseColumnNames {
		ID, Time, Type, SrcIP, DstIP, SrcMac, DstMac, Payload
	};

	static {
		tableModel = new ResponsePacketTableModel();
	}

	private ResponsePacketTableModel() {
	};

	public static ResponsePacketTableModel getInstance() {
		return tableModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return ResponseColumnNames.values().length;
	}

	@Override
	public String getColumnName(int column) {
		return ResponseColumnNames.values()[column].toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		if (col == ResponseColumnNames.ID.ordinal()) {
			return getPacketDOId(row);
		} else if (col == ResponseColumnNames.Time.ordinal()) {
			Date timestamp = packetMap.get(getPacketDOId(row)).getTimestamp();
			if(timestamp != null) {
				return packetMap.get(getPacketDOId(row)).getTimestamp().toString();
			} else {
				return "";
			}
		} else if (col == ResponseColumnNames.Type.ordinal()) {
			return packetMap.get(getPacketDOId(row)).getType();
		} else if (col == ResponseColumnNames.SrcIP.ordinal()) {
			return packetMap.get(getPacketDOId(row)).getSource();
		} else if (col == ResponseColumnNames.DstIP.ordinal()) {
			return packetMap.get(getPacketDOId(row)).getDestination();
		} else if (col == ResponseColumnNames.SrcMac.ordinal()) {
			return packetMap.get(getPacketDOId(row)).getMacSrc();
		} else if (col == ResponseColumnNames.DstMac.ordinal()) {
			return packetMap.get(getPacketDOId(row)).getMacDst();
		} else if (col == ResponseColumnNames.Payload.ordinal()) {
			return new String(packetMap.get(getPacketDOId(row)).getData());
		}

		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {		
		return packetMap.size();
	}

	public AbstractPacketDO getRow(int row) {		
		return packetMap.get(getPacketDOId(row));
	}

	public void removeRow(int row) {		
		AbstractPacketDO packet = packetMap.remove(getPacketDOId(row));
		packet.setFuzzed(false);
		PacketController.getInstance().updatePacket(packet);
		fireTableDataChanged();
	}
	
	public int getPacketDOId(int row)
	{
		int idx = 0;		
		int packetDataId = 0;
		
		for(Integer key : packetMap.keySet())
		{
			if(idx == row)
			{				
				packetDataId = key;
				break;
			}
			idx++;
		}
		
		return packetDataId;
	}

	public void packetArrived(AbstractPacketDO packet, int id) {
		packetMap.put(id, packet);
		fireTableRowsInserted(packetMap.size() - 1, packetMap.size() - 1);		
	}

	/**
	 * remove all packets
	 */
	public void removeAll() {		
		packetMap.clear();
		
		// TODO flag removed packets as not in fuzzer
		fireTableDataChanged();
	}
}
