//*********************************************************************************************************************
// FuzzResponsePacketTableModel.java
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.HTTPContentDataDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.DO.FuzzTestResultDO;
import org.epri.pt2.controller.HTTPContentDataController;
import org.epri.pt2.controller.FuzzTestController;
import org.epri.pt2.controller.FuzzTestResultController;
import org.epri.pt2.controller.PacketController;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.epri.pt2.gui.SelectionState;

/**
 * @author Southwest Research Institute
 *
 */
public class FuzzResponsePacketTableModel extends AbstractTableModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6975702610411335265L;

	private final static FuzzResponsePacketTableModel tableModel;
	
	private FuzzTestController fuzzTestController = FuzzTestController.
			getInstance();
	private HTTPContentDataController contentDataController = HTTPContentDataController.
			getInstance();	
	private FuzzTestResultController fuzzTestResultController = FuzzTestResultController.
			getInstance();
	private PacketController packetController = PacketController.
			getInstance();
	private SelectionState state = SelectionState.getInstance();
	private AbstractPacketDO selectedPacketData;
	private HTTPContentDataDO contentData;
	private FuzzTestDO fuzzTest;
	private FuzzTestResultDO fuzzTestResult;
	private boolean dirty = false;
	private int lastContentData = -1;
 
	Map<Integer, AbstractPacketDO> fuzzPacketMap = new LinkedHashMap<
			Integer, AbstractPacketDO>();
	
	public static enum FuzzResponseColumnNames {ID, Content, TestName, FuzzValue, DataType, Result};

	static {
		tableModel = new FuzzResponsePacketTableModel();
	}
	
	private FuzzResponsePacketTableModel() {		
	};
	
	/**
	 * Get an instance of the FuzzResponsePacketTableModel.
	 * @return
	 */
	public static FuzzResponsePacketTableModel getInstance() {
		return tableModel;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return FuzzResponseColumnNames.values().length;
	}

	@Override
	public String getColumnName(int column) {
		return FuzzResponseColumnNames.values()[column].toString();
	}
	
	private void updateContentData(int row) {
		//int contentDataId = state.getContentDataId();
		int contentDataId = getContentDataId(row);
		
		if (contentDataId > 0) {
			if (contentData == null) {
				lastContentData = contentDataId;
				contentData = contentDataController.
						getHTTPContentData(getContentDataId(row));
				fuzzTest = fuzzTestController.getFuzzTest(
						contentData.getFuzzTestId());
				fuzzTestResult = getFuzzTestResult(row);
				selectedPacketData = contentData.getOwner();
			} else if (contentDataId > 0 && (contentDataId != lastContentData)) {
				lastContentData = contentDataId;
				contentData = contentDataController.
						getHTTPContentData(getContentDataId(row));
				fuzzTest = fuzzTestController.getFuzzTest(
						contentData.getFuzzTestId());
				fuzzTestResult = getFuzzTestResult(row);
				selectedPacketData = contentData.getOwner();
			} else if (dirty) {
				contentData = contentDataController.
						getHTTPContentData(getContentDataId(row));
				fuzzTest = fuzzTestController.getFuzzTest(
						contentData.getFuzzTestId());
				fuzzTestResult = getFuzzTestResult(row);
				selectedPacketData = contentData.getOwner();
			}
		}
		
		dirty = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	
	public Object getValueAt(int row, int col) {
		state.setContentDataId(getContentDataId(row));
		updateContentData(row);
		if (col == FuzzResponseColumnNames.ID.ordinal()) {
			if(selectedPacketData != null)
			{
				return selectedPacketData.getId();
			}
			else return "";
		} else if (col == FuzzResponseColumnNames.TestName.ordinal()) {
			if(fuzzTest != null)
			{
				return fuzzTest.getFuzzTestName();	
			}
			else return "";		
		} else if (col == FuzzResponseColumnNames.FuzzValue.ordinal()) {
			if(fuzzTest != null)
			{
				return fuzzTest.getFuzzValue();	
			}
			else return "";
		} else if (col == FuzzResponseColumnNames.DataType.ordinal()) {
			return fuzzTestResult.getDataType();	
		} else if (col == FuzzResponseColumnNames.Result.ordinal()) {
			return fuzzTestResult.getResult();			
		} else if (col == FuzzResponseColumnNames.Content.ordinal()) {
			if(contentData != null)
			{
				return contentData.getContentData();
			}
		}

		return null;
	}
	
	public void setSelectedPacket(AbstractPacketDO packetData)
	{
		this.selectedPacketData = packetData;
		dirty = true;
	}
	
	public AbstractPacketDO getSelectedPacket() {
		return selectedPacketData;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fuzzPacketMap.size();
	}

	public AbstractPacketDO getRow(int row) {
		return fuzzPacketMap.get(getContentDataId(row));
	}

	public void removeRow(int row) {
		fuzzPacketMap.remove(getContentDataId(row));
		dirty = true;
		fireTableDataChanged();
	}
	
	public void removeRowWithIds(int[] ids) {
		for(int i : ids) {
			fuzzPacketMap.remove(i);
		}
	}
	
	public void removeRows(AbstractPacketDO packet) {		
		for(HTTPContentDataDO contentData : contentDataController.getHTTPContentDataList())
		{
			if(contentData.getOwner().getId() == packet.getId()) {
				fuzzPacketMap.remove(contentData.getId());
			}
		}
		dirty = true;
		fireTableDataChanged();
	}
	
	public HTTPContentDataDO getContentData(int row)
	{
		contentData = contentDataController.getHTTPContentData(getContentDataId(row));
		return contentData;
	}
	
	public FuzzTestDO getFuzzTest(int row)
	{
		contentData = contentDataController.getHTTPContentData(getContentDataId(row));
		FuzzTestDO fuzzTest = fuzzTestController.getFuzzTest(contentData.getFuzzTestId());		
		return fuzzTest;
	}
	
	public int getContentDataId(int row)
	{
		int idx = 0;
		int contentDataId = 0;

		for(Integer key : fuzzPacketMap.keySet())
		{
			if(idx == row)
			{				
				contentDataId = key;
				break;
			}
			idx++;
		}
		
		return contentDataId;
	}
	
	public FuzzTestResultDO getFuzzTestResult(int row)
	{
		FuzzTestResultDO fuzzTestResult = null;
		HTTPContentDataDO contentData = contentDataController.getHTTPContentData(getContentDataId(row));		
		AbstractPacketDO packetData = packetController.getPacket(contentData.getOwner().getId());	
		
		for(FuzzTestResultDO result : fuzzTestResultController.getFuzzTestResultList())
		{
			if((result.getFuzzTestId() == contentData.getFuzzTestId()) && 
					(result.getOwner().getId() == packetData.getId()))
			{
				fuzzTestResult = result;
				break;
			}
		}
		
		return fuzzTestResult;
		
	}
	
	public FuzzTestResultDO getFuzzTestResultWithId(int contentDataId)
	{
		FuzzTestResultDO fuzzTestResult = null;
		HTTPContentDataDO contentData = contentDataController.getHTTPContentData(contentDataId);		
		AbstractPacketDO packetData = packetController.getPacket(contentData.getOwner().getId());	
		
		for(FuzzTestResultDO result : fuzzTestResultController.getFuzzTestResultList())
		{
			if((result.getFuzzTestId() == contentData.getFuzzTestId()) && 
					(result.getOwner().getId() == packetData.getId()))
			{
				fuzzTestResult = result;
				break;
			}
		}
		
		return fuzzTestResult;
		
	}
	
	public void packetArrived(AbstractPacketDO packet, int id) {
		fuzzPacketMap.put(id, packet);
		fireTableRowsInserted(fuzzPacketMap.size() - 1, fuzzPacketMap.size() - 1);		
	}

	/**
	 * remove all packets
	 */
	public void removeAll() {
		fuzzPacketMap.clear();
		dirty = true;
		fireTableDataChanged();
	}
	
	public void dataChanged() {
		dirty = true;
		fireTableDataChanged();
	}
}
