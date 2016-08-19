//*********************************************************************************************************************
// FuzzTestUtils.java
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
package org.epri.pt2.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

import org.epri.pt2.DO.DataTypeDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.controller.DataTypeController;
import org.epri.pt2.controller.FuzzTestController;

/**
 * @author root
 *
 */
public class FuzzTestUtils {
	
	private FuzzTestController fuzzTestController = FuzzTestController.getInstance();
	private DataTypeController dataTypeController = DataTypeController.getInstance();
	
	String[] dataTypes = {"INTEGER", "BOOLEAN", "CHAR"};
	String[] integerFuzzTestNames = {"OUTOFRANGE_NEG", "OUTOFRANGE_LARGE", "OUTOFRANGE_EMPTY"};
	String[] integerFuzzTestValues = {"-1", "77777777777777777777777777", ""};
	String[] booleanFuzzTestNames = {"BOOL_TRUE", "BOOL_FALSE", "BOOL_0", "BOOL_1"};
	String[] booleanFuzzTestValues = {"true", "false", "0", "1"};
	
	public FuzzTestUtils()
	{
	}
	
	public void initFuzzTestsFromDatabase()
	{
		boolean found = false;
		
		for(String type : dataTypes)
		{			
			DataTypeDO newDataType = new DataTypeDO(type);
			for(DataTypeDO dataType : dataTypeController.getDataTypes())
			{
				if(newDataType.getDataTypeName().equals(dataType.getDataTypeName()))
				{
					found = true;
					break;
				}	
			}
			if(!found)
			{
				newDataType = dataTypeController.addDataType(newDataType);
				found = false;
			
				if(type == "INTEGER")
				{
					int i = 0;
					for(String integerFuzzTestName : integerFuzzTestNames)
					{
						FuzzTestDO newFuzzTest = new FuzzTestDO(integerFuzzTestName, integerFuzzTestValues[i]);
						
						for(FuzzTestDO fuzzTest : fuzzTestController.getFuzzTests())
						{
							if(newFuzzTest.getFuzzTestName().equals(fuzzTest.getFuzzTestName()))
							{
								found = true;
								break;
							}					
						}
						
						i++;
						
						if(!found)
						{
							if(newDataType != null)
							{
								newFuzzTest = fuzzTestController.addFuzzTest(newFuzzTest, newDataType);
							}
						}
					}
				} else if(type == "BOOLEAN") {
					int i = 0;
					for(String booleanFuzzTestName : booleanFuzzTestNames)
					{
						FuzzTestDO newFuzzTest = new FuzzTestDO(booleanFuzzTestName, booleanFuzzTestValues[i]);
						
						for(FuzzTestDO fuzzTest : fuzzTestController.getFuzzTests())
						{
							if(newFuzzTest.getFuzzTestName().equals(fuzzTest.getFuzzTestName()))
							{
								found = true;
								break;
							}					
						}
						
						i++;
						
						if(!found)
						{
							if(newDataType != null)
							{
								newFuzzTest = fuzzTestController.addFuzzTest(newFuzzTest, newDataType);
							}
						}
					}
				}
					
			}
		}
	}
	
	public DefaultListModel sortList(DefaultListModel listModel) {
		
		List<String> tempList = new ArrayList<String>();
		
		for(Object item : listModel.toArray()) {				
			tempList.add(item.toString());
		}
		
		Collections.sort(tempList);
		
		listModel.clear();
		
		Iterator<String> iter = tempList.iterator();
		int i = 0;
		while(iter.hasNext() && i < tempList.size()) {
			listModel.addElement(tempList.get(i++));
		}
		
		return listModel;
	}

}
