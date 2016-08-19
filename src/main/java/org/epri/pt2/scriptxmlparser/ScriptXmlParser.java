//*********************************************************************************************************************
// ScriptXmlParser.java
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
package org.epri.pt2.scriptxmlparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.epri.pt2.DO.TestCaseDO;
import org.epri.pt2.DO.TestScriptParameterDO;
import org.epri.pt2.controller.TestCaseController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses script xml definition files.
 * 
 * @author Tam Do
 * 
 */
public class ScriptXmlParser {

	public static void loadTestCase(String fileName) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			int index = 0;
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(fileName));
			doc.getDocumentElement().normalize();

			NodeList scriptList = doc.getElementsByTagName("testscript");

			while(index < scriptList.getLength()) {
				Node script = scriptList.item(index);

				// Get the test case
				String protocol = getTagValue("protocol", script);
				String name = getTagValue("name", script);
				String desc = getTagValue("description", script);

				TestCaseDO testCase = new TestCaseDO(name);
				testCase.setDesc(desc);
				testCase.setProtocol(protocol);
				testCase.setFileName(fileName);
				testCase.setCommand(getTagValue("command", script));
				testCase.setDirectory(new File(fileName).getParent());
				testCase = TestCaseController.getInstance().addTestCase(
						testCase);
				
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<TestScriptParameterDO> getParameters(String fileName) {
		List<TestScriptParameterDO> list = new ArrayList<TestScriptParameterDO>();

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);

			doc.getDocumentElement().normalize();

			NodeList params = doc.getElementsByTagName("param");

			for (int i = 0; i < params.getLength(); i++) {
				String name = params.item(i).getAttributes()
						.getNamedItem("name").getNodeValue();
				String flag = params.item(i).getAttributes()
						.getNamedItem("flag").getNodeValue();
				String defValue = params.item(i).getAttributes()
						.getNamedItem("default").getNodeValue();
				String hint = params.item(i).getAttributes()
						.getNamedItem("hint").getNodeValue();

				TestScriptParameterDO param = new TestScriptParameterDO(name,
						flag, defValue, hint);
				list.add(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static String getCommandString(String fileName) {
		String command = "";
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);
			doc.getDocumentElement().normalize();

			NodeList scriptList = doc.getElementsByTagName("testscript");

			if (scriptList.getLength() > 0) {
				Node script = scriptList.item(0);

				// Get the test case
				command = getTagValue("command", script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return command;
	}

	/**
	 * @param string
	 * @param firstScriptElement
	 * @return
	 */
	private static String getTagValue(String s, Node script) {
		Element e = (Element) script;

		NodeList nList = e.getElementsByTagName(s).item(0).getChildNodes();
		Node nValue = (Node) nList.item(0);

		return nValue.getNodeValue();
	}
}
