//*********************************************************************************************************************
// PacketParserTest.java
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
package org.epri.pt2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.TestCase;
import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.packetparser.HttpXMLPacketParser;

/**
 * @author root
 * 
 */
public class PacketParserTest extends TestCase {
	private List<HTTPPacketDO> packets;
	private String[] fileNames = { "packets/packet1_request.txt",
			"packets/packet2_response.txt" };
			
	private HttpXMLPacketParser parser;

	/**
	 * @param name
	 */
	public PacketParserTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		parser = new HttpXMLPacketParser();
		packets = new ArrayList<HTTPPacketDO>();

		for (String f : fileNames) {
			File fh = new File(ClassLoader.getSystemClassLoader().getResource(f).getPath());
			InputStream is = new FileInputStream(fh);
			byte[] bytes = new byte[(int) fh.length()];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			is.close();

			HTTPPacketDO p = new HTTPPacketDO("NULL", bytes);
			packets.add(p);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAll() throws UnsupportedEncodingException {
		/*for (HTTPPacketDO p : packets) {
			String before = new String(p.getData(), "UTF-8");
			System.out.print(before);
			parser.setPacket(p);
			String after = parser.toString();
			System.out.print(after);
			assertEquals(before, after);
			if (before.contentEquals(after)) {

			} else {
				fail("Content not equal");
			}

		}*/
	}
	
	public void testTree() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setLayout(new MigLayout());
				frame.add(parser);
				frame.setVisible(true);
			}
		});

	}
}
