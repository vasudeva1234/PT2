//*********************************************************************************************************************
// DataTypeController.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.epri.pt2.DO.DataTypeDO;
import org.epri.pt2.DO.FuzzTestDO;
import org.epri.pt2.listeners.ViewCallback;

/**
 * @author root
 *
 */
public class DataTypeController extends ViewCallback {
		private final static DataTypeController controller;

		static {
			controller = new DataTypeController();
		}

		private DataTypeController() {
		};

		/**
		 * Get an instance of the controller.
		 * @return
		 */
		public static DataTypeController getInstance() {
			return controller;
		}

		/**
		 * Add a data type to the database.
		 * @param DataType
		 * @return
		 */
		public DataTypeDO addDataType(DataTypeDO dataType) {
			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();
			dataType = manager.merge(dataType);
			manager.getTransaction().commit();
			manager.close();
			return dataType;
		}

		/**
		 * Add a data type to the fuzzTest.
		 * @param fuzzTest
		 * @param dataType
		 * @return
		 */
		public DataTypeDO addDataType(FuzzTestDO fuzzTest, DataTypeDO dataType) {
			return addDataType(fuzzTest.getId(), dataType);
		}

		/**
		 * Add a data type to the fuzzTest.
		 * @param currentFuzzTestId
		 * @param DataType
		 * @return
		 */
		public DataTypeDO addDataType(int currentFuzzTestId, DataTypeDO dataType) {
			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();

			dataType = manager.merge(dataType);
			manager.flush();

			FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class, new Integer(
					currentFuzzTestId));

			fuzzTest.addDataType(dataType);
			dataType.addOwner(fuzzTest);

			dataType = manager.merge(dataType);
			manager.merge(fuzzTest);
			manager.getTransaction().commit();
			manager.close();
			notifyListeners();

			return dataType;
		}

		/**
		 * Remove a data type from the fuzzTest.
		 * @param fuzzTestId
		 * @param DataTypeId
		 */
		public void removeDataType(int fuzzTestId, int dataTypeId) {

			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();

			DataTypeDO dataType = manager.find(DataTypeDO.class, new Integer(
					dataTypeId));
			FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class,
					new Integer(fuzzTestId));

			dataType.removeOwner(fuzzTest);
			fuzzTest.removeDataType(dataType);

			manager.merge(fuzzTest);
			manager.remove(dataType);

			manager.getTransaction().commit();
			manager.close();
			notifyListeners();

		}

		/**
		 * Update a data type.
		 * @param DataType
		 */
		public DataTypeDO updateDataType(DataTypeDO dataType) {
			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();

			dataType = manager.merge(dataType);

			manager.getTransaction().commit();
			manager.close();
			notifyListeners();

			return dataType;
		}

		/**
		 * List all test cases in the fuzzTest.
		 * @param fuzzTestid
		 * @return
		 */
		public List<DataTypeDO> getDataTypes(int fuzzTestid) {

			if (fuzzTestid > 0) {
				EntityManager manager = EntityManagerFactoryInstance.getInstance()
						.createEntityManager();
				manager.getTransaction().begin();

				FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class, new Integer(
						fuzzTestid));

				manager.getTransaction().commit();
				manager.close();

				if (fuzzTest.getDataTypeSet() != null) {
					ArrayList<DataTypeDO> list = new ArrayList<DataTypeDO>(
							fuzzTest.getDataTypeSet());
					Collections.sort(list);

					return list;
				}
			}
			return new ArrayList<DataTypeDO>();
		}

		/**
		 * Get a specific test case.
		 * @param id
		 * @return
		 */
		public DataTypeDO getDataType(int id) {
			DataTypeDO retval = null;

			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();

			retval = manager.find(DataTypeDO.class, new Integer(id));

			manager.getTransaction().commit();
			manager.close();

			return retval;
		}
		
		/**
		 * Get data type by type
		 * @return
		 */
		public DataTypeDO getDataType(String type) {
			EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
			manager.getTransaction().begin();
			
			List<DataTypeDO> dataTypes = manager.createQuery("from DataTypeDO", DataTypeDO.class).getResultList();
			DataTypeDO dataType = null;
			for(DataTypeDO item : dataTypes) {
				if(item.getDataTypeName().equals(type))
				{
					dataType = item;
					break;
				}
			}
			manager.getTransaction().commit();
			manager.close();
			
			return dataType;
		}
		
//		public DataTypeDO getDataType(String type) {
//			DataTypeDO retval = null;
//
//			EntityManager manager = EntityManagerFactoryInstance.getInstance()
//					.createEntityManager();
//			manager.getTransaction().begin();
//
//			retval = manager.find(DataTypeDO.class, new String(type));
//
//			manager.getTransaction().commit();
//			manager.close();
//
//			return retval;
//		}
		
		/**
		 * Get all data types
		 * @return
		 */
		public List<DataTypeDO> getDataTypes() {
			EntityManager manager = EntityManagerFactoryInstance.getInstance().createEntityManager();
			manager.getTransaction().begin();
			
			List<DataTypeDO> dataTypes = manager.createQuery("from DataTypeDO", DataTypeDO.class).getResultList();
			
			manager.getTransaction().commit();
			manager.close();
			
			return dataTypes;
		}
		

		/**
		 * Get all data types for a given data type.
		 * @param fuzzTestId
		 * @param dataType
		 */
		/*
		public void loadDataTypeDataTypes(int fuzzTestId, String dataType) {
			List<DataTypeDO> DataTypes;

			EntityManager manager = EntityManagerFactoryInstance.getInstance()
					.createEntityManager();
			manager.getTransaction().begin();
			DataTypes = manager.createQuery(
					"from DataTypeDO DataTypes where DataTypes.dataType = '"
							+ dataType + "'", DataTypeDO.class).getResultList();

			FuzzTestDO fuzzTest = manager.find(FuzzTestDO.class,
					new Integer(fuzzTestId));

			for (DataTypeDO DataType : DataTypes) {
				fuzzTest.addDataType(DataType);
				DataType.addOwner(fuzzTest);
				manager.merge(fuzzTest);
				manager.merge(DataType);
			}

			manager.getTransaction().commit();
			manager.close();
		}
		*/
}
