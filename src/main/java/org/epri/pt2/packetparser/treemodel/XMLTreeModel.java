//*********************************************************************************************************************
// XMLTreeModel.java
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
package org.epri.pt2.packetparser.treemodel;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This class implements an XML TreeModel representing the XML data structure.
 * 
 * @author Nakul Jeirath
 * 
 */
public class XMLTreeModel implements TreeModel {
	private Document doc;

	public XMLTreeModel(Document doc) {
		super();

		this.doc = doc;
		if (doc != null) {
			this.doc.setXmlStandalone(true);
		}
	}

	public Object getRoot() {
		if (doc == null) {
			return new DefaultMutableTreeNode("No XML Detected.");
		}
		XMLNode root = new XMLNode(doc.getDocumentElement());
		return root;
	}

	public int getChildCount(Object parent) {
		return ((XMLNode) parent).childCount();
	}

	public Object getChild(Object parent, int index) {
		return ((XMLNode) parent).getChild(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		if ((parent == null) || (child == null)) {
			return -1;
		}

		return ((XMLNode) parent).getIndexOfChild((XMLNode) child);
	}

	public boolean isLeaf(Object node) {
		if (node instanceof XMLNode) {
			return ((XMLNode) node).isLeaf();
		}

		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		XMLNode parent = (XMLNode) path.getLastPathComponent();
		parent.setValue(newValue);
//		prettyPrint(System.out);
	}

	public void prettyPrint(OutputStream out) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			trans.transform(new DOMSource(doc), new StreamResult(
					new OutputStreamWriter(out)));

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	// ***********************************************************************************************************************************

	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

}
