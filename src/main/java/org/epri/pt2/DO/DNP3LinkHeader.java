//*********************************************************************************************************************
// DNP3LinkHeader.java
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
package org.epri.pt2.DO;

import java.util.Arrays;

public class DNP3LinkHeader {
	
	private boolean isValid = false;
	private byte[] data;
	private int length;
	private Control control;
	private int destination;
	private int source;
	private int crc;
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}


	
	/**
	 * Represents the Link Layer Header
	 * 
	 * @author tdo
	 */
	public class Control {
		int dir;
		int prm;
		int fcb;
		int fcv;
		int dfc;
		
		int function_code;
		
		public int getFunction_code() {
			return function_code;
		}

		public void setFunction_code(int function_code) {
			this.function_code = function_code;
		}

		public int getDir() {
			return dir;
		}

		public void setDir(int dir) {
			this.dir = dir;
		}

		public int getPrm() {
			return prm;
		}

		public void setPrm(int prm) {
			this.prm = prm;
		}

		public Control(int header) {
			if ((header & 0x80) > 0) {
				dir = 1;
			}

			if ((header & 0x40) > 0) {
				prm = 1;
			}

			if ((header & 0x20) > 0) {
				fcb = 1;
			}

			if ((header & 0x01) > 0) {
				fcv = 1;
				dfc = 1;
			}

			function_code = header & 0xF;
		}

		public Control() {
			dir = 0;
			prm = 0;
			fcb = 0;
			fcv = 0;
			dfc = 0;
			function_code = 0;
		}
		
		public Control(int dir, int prm, int fcb, int fcv, int dfc,
				int function_code) {
			this.dir = dir;
			this.prm = prm;
			this.fcb = fcb;
			this.fcv = fcv;
			this.dfc = dfc;
			this.function_code = function_code;
		}

		public Control(Control control) {
			this.dir = control.dir;
			this.prm = control.prm;
			this.fcb = control.fcb;
			this.fcv = control.fcv;
			this.dfc = control.dfc;
			this.function_code = control.function_code;
		}

		public String toString() {
			String outstr = "";

			outstr += "DIR (from): ";
			outstr += (dir == 1) ? "(1) Master" : "(0) Outstation";
			outstr += "PRM: ";
			outstr += (prm == 1) ? "(1) Initiating" : "(0) Completing";

			if (prm == 1) {
				outstr += "FCV: ";
				outstr += (fcv == 1) ? "(1) FCB Valid" : "(0) FCB Ignored";
				outstr += "FCB: ignored";
			} else {
				outstr += "DFC: ";
				outstr += (dfc == 1) ? "(1) Busy or Insufficent Buffers"
						: "(0) Ready";
			}

			return outstr;
		}

		public byte getByte() {
			byte b = 0;

			b = (byte) ((dir << 7) | (prm << 6));

			if (prm == 1) {
				b |= (fcb << 6);
				b |= (fcv << 5);
			} else {
				b |= (dfc << 5);
			}

			b |= (function_code & 0xF);

			return b;
		}
	}

	public DNP3LinkHeader() {
		initialize();
	}
	
	public DNP3LinkHeader(byte[] data) {
		initialize();
		
		parse(data);
	}

	public DNP3LinkHeader(DNP3LinkHeader link) {
		this.isValid = true;
		this.data = new byte[0];

		this.length = link.length;
		this.control = link.control;
		this.destination = link.destination;
		this.source = link.source;
		this.crc = link.crc;
	}

	public DNP3LinkHeader(int length, int destination, int source) {
		this.length = length;
		this.destination = destination;
		this.source = source;
	}
	
	private void initialize() {
		isValid = false;
		data = new byte[0];
		length = -1;
		control = new Control();
		destination = -1;
		source = -1;
		crc = -1;
	}

	private void parse(byte[] data) {
		if (data.length < 2) {
			return;
		}

		if (data.length < 10) {
			return;
		}

		if (data[0] != 0x05 && data[1] != 0x64) {
			return;
		}

		length = data[2] & 0xFF;

		if (length < 5) {
			return;
		}

		if (length != 0) {
			isValid = true;
			/* if length is more than 5 we have link layer data */
			if (length > 5) {
				this.data = Arrays.copyOfRange(data, 10, data.length);
			}
			/* otherwise we have no more data */
			else {
				this.data = new byte[0];
			}
		}

		control = new Control(data[3]);
		destination = data[4] | (data[5] << 8);
		source = data[6] | (data[7] << 8);

		crc = data[8] | (data[9] << 8);

		// Check CRC
		// if(checkCRC(Arrays.copyOfRange(data, 0, 10))) {
		// System.out.println("CRC OK");
		// }
		// else
		// {
		// System.out.println("CRC BAD");
		// }
	}
	

	public boolean isValid() {
		return isValid;
	}

	public String toString() {
		String outstr = "";

		outstr += control.toString();

		// TODO Generate output for destination, source, and crc
		//

		return outstr;
	}

	public byte[] getBytes() {
		byte[] buf = new byte[10];
		int crc;

		buf[0] = 0x05;
		buf[1] = 0x64;
		buf[2] = (byte) (length & 0xFF);
		buf[3] = control.getByte();
		buf[4] = (byte) (destination & 0xFF);
		buf[5] = (byte) ((destination >> 8) & 0xFF);
		buf[6] = (byte) (source & 0xFF);
		buf[7] = (byte) ((source >> 8) & 0xFF);

		crc = DNP3Utils.calculateCRC(Arrays.copyOfRange(buf, 0, 8));

		buf[8] = (byte) (crc & 0xFF);
		buf[9] = (byte) ((crc >> 8) & 0xFF);
		return buf;
	}
}
