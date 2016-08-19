//*********************************************************************************************************************
// AbstractSniffer.java
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
package org.epri.pt2.sniffer;

import java.util.ArrayList;
import java.util.List;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.listeners.PacketListenerInterface;

/**
 * An abstract class for create interface specific sniffers.
 * 
 * @author Tam Do
 * 
 */
public abstract class AbstractSniffer implements SnifferInterface {
	protected List<PacketListenerInterface> listeners;
	protected List<Integer> types;

	protected AbstractSniffer() {
		listeners = new ArrayList<PacketListenerInterface>();
		types = new ArrayList<Integer>();
	}

	public void addPacketListener(PacketListenerInterface listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removePacketListener(PacketListenerInterface listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	protected void notifyListeners(AbstractPacketDO packet) {
		for (PacketListenerInterface listener : listeners) {
			listener.packetArrived(packet);
		}
	}
}