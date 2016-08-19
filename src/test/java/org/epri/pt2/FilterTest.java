//*********************************************************************************************************************
// FilterTest.java
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
//package org.epri.pt2;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import junit.framework.TestCase;
//
//import org.epri.pt2.proxy.FilterDO;
//import org.epri.pt2.sniffer.EthernetDataPacket;
//
//public class FilterTest extends TestCase {
//	private String[] fileNames = { "packets/packet4_response.txt" };
//	private List<EthernetDataPacket> packets;
//
//	public FilterTest(String name) {
//		super(name);
//
//	}
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		packets = new ArrayList<EthernetDataPacket>();
//
//		ClassLoader cl = this.getClass().getClassLoader();
//
//		for (String f : fileNames) {
//			File fh;
//			try {
//				fh = new File(cl.getResource(f).toURI());
//				InputStream is = new FileInputStream(fh);
//				byte[] bytes = new byte[(int) fh.length()];
//
//				int offset = 0;
//				int numRead = 0;
//				while (offset < bytes.length
//						&& (numRead = is.read(bytes, offset, bytes.length
//								- offset)) >= 0) {
//					offset += numRead;
//				}
//
//				EthernetDataPacket p = new EthernetDataPacket("NULL", bytes);
//				packets.add(p);
//				is.close();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}
//
//	public void testFilter() {
//		// FilterDO filter = new FilterDO();
//		// filter.setReplaceRule("(?<=<ei:value>)([-+]?[0-9]*\\.?[0-9]+)(?=</ei:value>)");
//		// filter.setReplaceVal(".90");
//		// for (EthernetDataPacket p : packets) {
//		// try {
//		// //assertEquals(new String(p.getData(), "UTF-8"), new
//		// String(processFilter(p.getData(), filter), "UTF-8"));
//		// } catch (UnsupportedEncodingException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		// }
//	}
//
//	protected void tearDown() throws Exception {
//	}
//
//	private byte[] processFilter(byte[] bytes, FilterDO filter) {
//		String s;
//		try {
//			s = new String(bytes, "UTF-8");
//
//			Pattern p = Pattern.compile(filter.getReplaceRule());
//			Matcher m = p.matcher(s);
//
//			if (m.find()) {
//				s = m.replaceAll(filter.getReplaceVal());
//			}
//
//			s.replaceAll(filter.getReplaceRule(), filter.getReplaceVal());
//
//			return s.getBytes();
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		// if replacement fails, return original packet
//		return bytes;
//	}
//}
