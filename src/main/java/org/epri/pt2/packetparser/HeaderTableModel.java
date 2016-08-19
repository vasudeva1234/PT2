//*********************************************************************************************************************
// HeaderTableModel.java
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
package org.epri.pt2.packetparser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.reassembler.HttpUtils;
import org.epri.pt2.reassembler.HttpUtils.MessageType;

/**
 * This class represents a the data model for a table containing the list of
 * http header parameters.
 * 
 * @author Tam Do
 * 
 */
public class HeaderTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5543254399018549428L;
	protected List<HttpHeader> headers;

	public static enum ColumnNames {
		Field, Value
	};

	public HeaderTableModel() {
		headers = new ArrayList<HeaderTableModel.HttpHeader>();
	}

	@Override
	public String getColumnName(int column) {
		return ColumnNames.values()[column].toString();
	}

	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	public int getRowCount() {
		return headers.size();
	}

	public Object getValueAt(int row, int col) {
		if (row > -1) {
			HttpHeader header = headers.get(row);
			if (ColumnNames.Field.ordinal() == col) {
				return header.key;
			} else if (ColumnNames.Value.ordinal() == col) {
				return header.value;
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex > -1) {

			// do nothing to the key field
			if (ColumnNames.Field.ordinal() == columnIndex) {
				return;
			} else if (ColumnNames.Value.ordinal() == columnIndex) {
				HttpHeader header = headers.get(rowIndex);
				header.setValue((String) aValue);
				return;
			}
		}
		super.setValueAt(aValue, rowIndex, columnIndex);
	}

	public class HttpHeader {
		private String key;
		private String value;

		public HttpHeader(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @param key
		 *            the key to set
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}

	/**
	 * Creates a set of header objects from a content string object.
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	public void setHeaders(AbstractPacketDO packet)
			throws UnsupportedEncodingException {
		headers.clear();

		String content = new String(packet.getData(), "UTF-8");
		List<String> headerList = HttpUtils.getHeaders(content);

		if (headerList != null) {
			if (headerList.size() >= 1) {
				if (HttpUtils.findRequestHeader(content) > -1) {
					packet.setType(MessageType.Request);
					HttpHeader requestHeader = new HttpHeader("Method",
							headerList.get(0));
					headers.add(requestHeader);
				} else if (HttpUtils.findResponseHeader(content) > -1) {
					packet.setType(MessageType.Response);
					HttpHeader responseHeader = new HttpHeader("Response",
							headerList.get(0));
					headers.add(responseHeader);
				}

				// iterate through remaining headers
				for (int i = 1; i < headerList.size(); i++) {
					String[] fields = headerList.get(i).split(":\\s");
					if (fields.length == 2) {
						HttpHeader h = new HttpHeader(fields[0], fields[1]);
						headers.add(h);
					}
				}
			}
		}

		fireTableDataChanged();
	}

	/**
	 * Returns the string representation of the header objects.
	 * 
	 * @return
	 */
	public String getHeaders() {
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < headers.size(); i++) {
			if (i == 0) {
				s.append(headers.get(i).getValue() + "\r\n");
			} else {
				s.append(headers.get(i).getKey() + ": "
						+ headers.get(i).getValue() + "\r\n");
			}
		}

		return s.toString();
	}

}
