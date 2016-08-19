//*********************************************************************************************************************
// HttpXMLPacketParser.java
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

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.packetparser.treemodel.XMLTreeModel;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A class parsing HTTP/XML packets.
 * 
 * @author Nakul Jeirath
 *
 */
public class HttpXMLPacketParser extends AbstractPacketParser {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2719051257984745641L;

	protected XMLTreeModel tm;
	protected JTree tree;
	protected JTable headerTable;
	protected HeaderTableModel headerTableModel;
	protected JScrollPane headerPane;
	protected JSplitPane splitPane;
	private AbstractPacketDO packet;
	private String rootNode;

	public HttpXMLPacketParser() {
		super();
		initComponents(null, true, false);
	}

	public HttpXMLPacketParser(boolean isEditable) {
		super();
		initComponents(null, true, isEditable);
	}

	public HttpXMLPacketParser(InputStream stream) {
		super();
		initComponents(stream, true, false);
	}

	public HttpXMLPacketParser(InputStream stream, boolean isHeaderPaneVisible) {
		super();
		initComponents(stream, isHeaderPaneVisible, false);
	}

	public HttpXMLPacketParser(InputStream stream, boolean isHeaderPaneVisible,
			boolean isEditable) {
		super();
		initComponents(stream, isHeaderPaneVisible, false);
	}

	public JTree getTree() {
		return tree;
	}

	private void initComponents(InputStream stream,
			boolean isHeaderPaneVisible, boolean isEditable) {
		headerTableModel = new HeaderTableModel();
		headerTable = new JTable(headerTableModel);
		headerPane = new JScrollPane(headerTable);
		headerPane.setMinimumSize(new Dimension(this.getMinimumSize().width,
				100));

		// headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumn col = headerTable.getColumnModel().getColumn(
				HeaderTableModel.ColumnNames.Field.ordinal());
		col.setPreferredWidth(100);
		col.setMaxWidth(100);

		tree = new JTree();
		setModel(stream);
		tree.setEditable(isEditable);

		setLayout(new MigLayout());
		JScrollPane treePane = new JScrollPane(tree);

		if (isHeaderPaneVisible) {
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headerPane,
					treePane);
			this.add(splitPane, "dock center");
		} else {
			this.add(treePane, "dock center");
		}
	}

	public void setPacket(AbstractPacketDO packet) {
		this.packet = packet;
		try {
			headerTableModel.setHeaders(packet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		setModel(new ByteArrayInputStream(packet.getData()));
	}

	@Override
	public String toString() {
		this.packet = getPacket();
		try {
			if (packet != null) {
				return new String(packet.getData(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void setContent(String content) {
		if (packet != null) {
			packet.setData(content.getBytes());
			try {
				headerTableModel.setHeaders(packet);
				setModel(new ByteArrayInputStream(packet.getData()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private void setModel(InputStream stream) {
		InputStream s = getXMLfromContent(stream);

		if (s != null) {
			Document dom = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				dom = db.parse(s);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				// Packet could not be parsed, return empty tree
			} catch (IOException e) {
				e.printStackTrace();
			}

			tm = new XMLTreeModel(dom);
			rootNode = tm.getRoot().toString();
			tree.setModel(tm);
		} else {
			tm = new XMLTreeModel(null);
			rootNode = "";
			tree.setModel(tm);
		}
		// tm.notifyAll();

	}
	
	public String getRootNode()
	{
		return rootNode;
	}

	private InputStream getXMLfromContent(InputStream stream) {
		if (stream != null) {
			try {
				String html = new Scanner(stream).useDelimiter("\\A").next();

				// get the index of this first occurrence
				//int i = html.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				int i = html.indexOf("<?xml");

				if (i < 0) {
					return null;
				}

				// get the substring from this point on
				String xml = html.substring(i);

				// convert the xml to an inputstream

				InputStream s = new ByteArrayInputStream(xml.getBytes());
				return s;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return stream;
	}

	public AbstractPacketDO getPacket() {
		// recover the header information
		String headers = headerTableModel.getHeaders();

		// get the xml content
		OutputStream os = new ByteArrayOutputStream();
		tm.prettyPrint(os);
		String xml = os.toString();
		xml = xml.trim();
		xml += "\n";

		String content = "";

		if (headers.contains("Content-Length")) {
			int contentLength = xml.getBytes().length;
			headers = headers.replaceAll("Content-Length:\\s\\d+",
					String.format("Content-Length: %d", contentLength));
			content = headers + "\r\n" + xml;
		} else {
			content = headers + "\r\n";
		}

		if (packet != null) {
			packet.setData(content.getBytes());
		}

		return packet;
	}

	public void reset() {
		setContent("");
	}
}
