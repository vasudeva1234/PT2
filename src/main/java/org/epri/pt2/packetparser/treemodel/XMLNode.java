//*********************************************************************************************************************
// XMLNode.java
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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents an XMLNode in the tree.
 * 
 * @author Nakul Jeirath
 * 
 */
public class XMLNode {
	private Node node;
	MyNodeList nl;

	public XMLNode(Node n) {
		this.node = n;
		NodeList list = node.getChildNodes();
		nl = new MyNodeList();

		for (int i = 0; i < list.getLength(); i++) {
			Node nde = list.item(i);
			if ((nde.getNodeType() == Node.TEXT_NODE)
					&& (nde.getNodeValue().trim().length() == 0)) {
				continue;
			} else {
				nl.addNode(nde);
			}
		}		
	}

	public int childCount() {
		return nl.getLength();
	}

	public XMLNode getChild(int i) {
		return new XMLNode(nl.item(i));
	}

	public int getIndexOfChild(XMLNode child) {
		for (int i = 0; i < nl.getLength(); i++) {
			if (child.equals(nl.item(i))) {
				return i;
			}
		}

		return -1;
	}
	
	public Node getParentNode() {
		return this.node.getParentNode();
	}

	public boolean isLeaf() {
		if (nl.getLength() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isText() {		
		if (node.getNodeType() == Node.TEXT_NODE) {			
			return true;
		} else {
			return false;	
		}
	}
	
	public void setValue(Object val) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			node.setNodeValue(val.toString());
		} else {
			Document doc = node.getOwnerDocument();
			Element newNode = doc.createElement(val.toString());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Attr newAttr = (Attr) doc.importNode(attrs.item(i), true);
				newNode.getAttributes().setNamedItem(newAttr);
			}

			while (node.hasChildNodes()) {
				newNode.appendChild(node.getFirstChild());
			}

			node.getParentNode().replaceChild(newNode, node);
			this.node = newNode;

			// System.err.println("Change name not implemented yet");
		}
	}

	public String toString() {
		if (node.getNodeType() == Node.TEXT_NODE) {
			return node.getNodeValue();
		} else {
			return node.getNodeName();
		}
	}

	private class MyNodeList implements NodeList {
		private List<Node> list;

		public MyNodeList() {
			list = new ArrayList<Node>();
		}

		public int getLength() {
			return list.size();
		}

		public Node item(int i) {
			return list.get(i);
		}

		public void addNode(Node n) {
			list.add(n);
		}

	}

}
