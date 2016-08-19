//*********************************************************************************************************************
// TcpLink.java
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
package org.epri.pt2.reassembler;

/**
 * A data structure which represents a chain of tcp fragments.
 * 
 * @author Tam Do
 * 
 */
public class TcpLink implements Comparable<TcpLink> {
	protected TcpFragment fragment;

	private TcpLink next;
	private TcpLink first;
	private boolean processed;

	public TcpLink(TcpFragment f) {
		fragment = f;
		first = this;
		processed = false;
	}

	public TcpFragment pop() {
		if (first.next != null) {
			first.next.setFirst(first.next);
		}

		return first.fragment;
	}

	public TcpFragment get() {
		return fragment;
	}

	public boolean add(TcpFragment f) {
		return add(new TcpLink(f));
	}

	public boolean add(TcpLink link) {
		boolean result = false;

		if (link == null) {
			return result;
		}

		// check if the fragment we are adding can belong to the front
		else if ((link.fragment.getSeq() + link.fragment.getLen()) == fragment
				.getSeq()) {
			link.setNext(this);
			link.setFirst(link);
			result = true;
		}
		// check if the fragment we are adding is immediately after
		else if (link.fragment.getSeq() == (fragment.getLen() + fragment
				.getSeq())) {

			setNext(link);
			first.setFirst(this.first);

			result = true;
		} else if (next != null) {
			result = next.add(link);
		}

		return result;
	}

	public TcpLink getNext() {
		return next;
	}

	public void setNext(TcpLink next) {
		this.next = next;
	}

	public TcpLink getFirst() {
		return first;
	}

	public void setFirst(TcpLink first) {
		this.first = first;

		// recursively set first
		if (next != null) {
			next.setFirst(first);
		}
	}

	public byte[] getData() {
		byte[] b = fragment.getData();

		if (next == null) {
			return b;
		} else {
			byte[] nextData = next.getData();
			byte[] c = new byte[b.length + nextData.length];

			System.arraycopy(b, 0, c, 0, b.length);
			System.arraycopy(nextData, 0, c, b.length, nextData.length);
			return c;
		}
	}

	public int compareTo(TcpLink link) {
		long val = link.fragment.getSeq() - fragment.getSeq();

		if (val > 0) {
			return 1;
		} else if (val == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * @param processed
	 *            the processed to set
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
