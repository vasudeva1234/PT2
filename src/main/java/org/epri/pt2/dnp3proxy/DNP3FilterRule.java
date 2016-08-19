//*********************************************************************************************************************
// DNP3FilterRule.java
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
package org.epri.pt2.dnp3proxy;

import org.epri.pt2.DO.DNP3ApplicationPacketDO;
import org.epri.pt2.DO.DNP3LinkHeader;

public class DNP3FilterRule {
	private int source;
	private int destination;
	private int dir;
	private int prm;
	private int length;
	private int function_code;
	private String app_data_hash;
	
	private byte[] replacement;
	
	private boolean checkSource;
	private boolean checkDestination;
	private boolean checkDir;
	private boolean checkPrm;
	private boolean checkLength;
	private boolean checkFunctionCode;
	private boolean checkAppDataHash;
	
	public DNP3FilterRule() {
		
	}
	
	public DNP3FilterRule(DNP3ApplicationPacketDO packet) {
		DNP3LinkHeader linkHeader = packet.getLinkHeader();
		source = linkHeader.getSource();
		destination = linkHeader.getDestination();
		dir = linkHeader.getControl().getDir();
		prm = linkHeader.getControl().getPrm();
		length = linkHeader.getLength();
		function_code = linkHeader.getControl().getFunction_code();
		app_data_hash = packet.getAppDataHash();
	}
	
	public boolean matches(DNP3ApplicationPacketDO packet) {
		boolean matchFound = true;
		DNP3LinkHeader linkHeader = packet.getLinkHeader();
		
		if(checkSource) {
			if(source != linkHeader.getSource()) {
				matchFound = false;
			}
		}
		
		if(checkDestination) {
			if(destination != linkHeader.getDestination()) {
				matchFound = false;
			}
		}
		
		if(checkDir) {
			if(dir != linkHeader.getControl().getDir()) {
				matchFound = false;
			}
		}
		
		if(checkPrm) {
			if(prm != linkHeader.getControl().getPrm()) {
				matchFound = false;
			}
		}
		
		if(checkLength) {
			if(length != linkHeader.getLength()) {
				matchFound = false;
			}
		}
		
		if(checkFunctionCode) {
			if(function_code != linkHeader.getControl().getFunction_code()) {
				matchFound = false;
			}
		}
		
		if(checkAppDataHash) {
			if(app_data_hash != packet.getAppDataHash()) {
				matchFound = false;
			}
		}
		
		return true;
	}

	public boolean isCheckSource() {
		return checkSource;
	}

	public void setCheckSource(boolean checkSource) {
		this.checkSource = checkSource;
	}

	public boolean isCheckDestination() {
		return checkDestination;
	}

	public void setCheckDestination(boolean checkDestination) {
		this.checkDestination = checkDestination;
	}

	public boolean isCheckDir() {
		return checkDir;
	}

	public void setCheckDir(boolean checkDir) {
		this.checkDir = checkDir;
	}

	public boolean isCheckPrm() {
		return checkPrm;
	}

	public void setCheckPrm(boolean checkPrm) {
		this.checkPrm = checkPrm;
	}

	public boolean isCheckLength() {
		return checkLength;
	}

	public void setCheckLength(boolean checkLength) {
		this.checkLength = checkLength;
	}

	public boolean isCheckFunctionCode() {
		return checkFunctionCode;
	}

	public void setCheckFunctionCode(boolean checkFunctionCode) {
		this.checkFunctionCode = checkFunctionCode;
	}

	public boolean isCheckAppDataHash() {
		return checkAppDataHash;
	}

	public void setCheckAppDataHash(boolean checkAppDataHash) {
		this.checkAppDataHash = checkAppDataHash;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFunction_code() {
		return function_code;
	}

	public void setFunction_code(int function_code) {
		this.function_code = function_code;
	}

	public String getApp_data_hash() {
		return app_data_hash;
	}

	public void setApp_data_hash(String app_data_hash) {
		this.app_data_hash = app_data_hash;
	}

	public byte[] getReplacement() {
		return replacement;
	}

	public void setReplacement(byte[] replacement) {
		this.replacement = replacement;
	}
}
