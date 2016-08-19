//*********************************************************************************************************************
// AbstractPacketDO.java
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.epri.pt2.reassembler.TcpFragment;
import org.epri.pt2.reassembler.HttpUtils.MessageType;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Implements the packet data object.
 * 
 * @author Southwest Research Institute
 */

@Entity
@Table(name = "PACKETS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractPacketDO implements Comparable<AbstractPacketDO> {

	// Setup the column values for the Packet Table.
	@Id
	@GeneratedValue
	@AccessType("property")
	@Column(name = "PACKET_ID")
	protected int id;

	protected String name;

	protected long ack;
	protected long seq;
	protected long l_time;

	@Column(name = "PACKET_FUZZTESTID")
	protected int fuzzTestId;

	@Column(name = "PACKET_STR")
	protected String packetStr;

	@Column(name = "PACKET_DATA")
	@Lob
	protected byte data[];
	
	@Column(name = "RAW_DATA")
	@Lob
	protected byte raw_data[];

	@Column(name = "PACKET_TIMESTAMP")
	protected Date timestamp;

	@Column(name = "PACKET_MSGTYPE")
	@Enumerated(EnumType.STRING)
	protected MessageType msgType;

	@Column(name = "PACKET_SRC")
	protected String src;

	@Column(name = "PACKET_DST")
	protected String dst;

	@Column(name = "PACKET_SRC_PORT")
	protected int srcPort;

	@Transient
	protected TcpFragment fragment;

	// Setup the relationships for the Packet Table.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID")
	@LazyCollection(LazyCollectionOption.FALSE)
	protected ProjectDO owner;

	@OneToMany(mappedBy = "owner")
	protected List<FuzzTestResultDO> fuzzTestResultList;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "PACKET_FUZZTEST", joinColumns = { @JoinColumn(name = "PACKET_ID") }, inverseJoinColumns = { @JoinColumn(name = "FUZZTEST_ID") })
	protected Set<FuzzTestDO> fuzzTestSet = new HashSet<FuzzTestDO>();

	protected boolean selected = false;

	protected boolean isFuzzed = false;

	@Column(name = "PACKET_DST_PORT")
	protected int dstPort;

	@Column(name = "PACKET_MACSRC")
	protected String macSrc;

	@Column(name = "PACKET_MACDST")
	protected String macDst;

	/**
	 * @return the srcPort
	 */
	public int getSrcPort() {
		return srcPort;
	}

	/**
	 * @param srcPort
	 *            the srcPort to set
	 */
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	/**
	 * @return the dstPort
	 */
	public int getDstPort() {
		return dstPort;
	}

	/**
	 * @param dstPort
	 *            the dstPort to set
	 */
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	protected AbstractPacketDO() {
	}

	public AbstractPacketDO(int fuzzTestId) {
		this.fuzzTestId = fuzzTestId;
	}

	public AbstractPacketDO(String name, byte[] data) {
		this.data = data;
		this.setName(name);
		this.timestamp = new Date();
	}

	public AbstractPacketDO(String name, byte[] payload, TcpFragment fragment) {
		this.setName(name);
		this.data = payload;
		setFragment(fragment);
	}

	public AbstractPacketDO(AbstractPacketDO packet) {
		this("NULL", packet.getData());
		setSrcPort(packet.getSrcPort());
		setDstPort(packet.getDstPort());
		setSource(packet.getSource());
		setDestination(packet.getDestination());
		setMacSrc(packet.getMacSrc());
		setMacDst(packet.getMacDst());
		setType(packet.getType());
		setTimestamp(packet.getTimestamp());
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the fuzz test id
	 */
	public int getFuzzTestId() {
		return fuzzTestId;
	}

	/**
	 * @param fuzzTestId
	 *            the fuzz test id to set
	 */
	public void setFuzzTestId(int fuzzTestId) {
		this.fuzzTestId = fuzzTestId;
	}

	/**
	 * @return the packet as string
	 */
	public String getPacketString() {
		return packetStr;
	}

	/**
	 * @param source
	 *            set packet string
	 */
	public void setPacketString(String packetStr) {
		this.packetStr = packetStr;
	}

	/**
	 * @return the packet source
	 */
	public String getSource() {
		return src;
	}

	/**
	 * @param source
	 *            the packet source
	 */
	public void setSource(String source) {
		this.src = source;
	}

	/**
	 * @return the packet destination
	 */
	public String getDestination() {
		return dst;
	}

	/**
	 * @param destination
	 *            the packet destination
	 */
	public void setDestination(String destination) {
		this.dst = destination;
	}

	/**
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getRawData() {
		return raw_data;
	}
	
	public void setRawData(byte[] raw_data) {
		this.raw_data = raw_data;
	}

	/**
	 * @return
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timeStamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the macSrc
	 */
	public String getMacSrc() {
		return macSrc;
	}

	/**
	 * @param macSrc
	 *            the macSrc to set
	 */
	public void setMacSrc(String macSrc) {
		this.macSrc = macSrc;
	}

	/**
	 * @return the macDst
	 */
	public String getMacDst() {
		return macDst;
	}

	/**
	 * @param macDst
	 *            the macDst to set
	 */
	public void setMacDst(String macDst) {
		this.macDst = macDst;
	}

	/**
	 * @return the message type, i.e. Request/Response
	 */
	public MessageType getType() {
		return msgType;
	}

	/**
	 * @param the
	 *            message type to set
	 */
	public void setType(MessageType type) {
		this.msgType = type;
	}

	/**
	 * @return the fuzzTestResultList
	 */
	public List<FuzzTestResultDO> getFuzzTestResultList() {
		return fuzzTestResultList;
	}

	/**
	 * @param fuzzTestResultList
	 *            the fuzzTestResultList to set
	 */
	public void setFuzzTestResultList(List<FuzzTestResultDO> fuzzTestResultList) {
		this.fuzzTestResultList = fuzzTestResultList;
	}

	public void addFuzzTestResult(FuzzTestResultDO fuzzTestResult) {
		fuzzTestResultList.add(fuzzTestResult);
	}

	public void removeFuzzTestResult(FuzzTestResultDO fuzzTestResult) {
		fuzzTestResultList.remove(fuzzTestResult);
	}

	/**
	 * @return the fuzzTestSet
	 */
	public Set<FuzzTestDO> getFuzzTestSet() {
		return fuzzTestSet;
	}

	/**
	 * @param fuzzTestSet
	 *            the fuzzTestSet to set
	 */
	public void setFuzzTestSet(Set<FuzzTestDO> fuzzTestSet) {
		this.fuzzTestSet = fuzzTestSet;
	}

	public void addFuzzTest(FuzzTestDO fuzzTest) {
		fuzzTestSet.add(fuzzTest);
	}

	public void removeFuzzTest(FuzzTestDO fuzzTest) {
		fuzzTestSet.remove(fuzzTest);
	}

	public void removeFuzzTest(int fuzzTestId) {
		for (FuzzTestDO fuzzTest : fuzzTestSet) {
			if (fuzzTest.getId() == fuzzTestId) {
				fuzzTestSet.remove(fuzzTest);
			}
		}
	}

	/**
	 * @return the owner
	 */
	public ProjectDO getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(ProjectDO owner) {
		this.owner = owner;
	}

	public int compareTo(AbstractPacketDO o) {
		MessageType type = (MessageType) getType();
		MessageType otype = (MessageType) o.getType();

		if (type == MessageType.Request) {
			if (otype == MessageType.Request) {
				return (int) (getSeq() - o.getSeq());
			} else if (otype == MessageType.Response) {
				if (getAck() == o.getSeq()) {
					return -1;
				} else {
					return (int) (getAck() - o.getSeq());
				}
			}
		} else if (type == MessageType.Response) {
			if (otype == MessageType.Response) {
				return (int) (getSeq() - o.getSeq());
			} else if (otype == MessageType.Request) {
				if (getSeq() == o.getAck()) {
					return 1;
				} else {
					return (int) (getSeq() - o.getAck());
				}
			}
		} else if (this.equals(o)) {
			return 0;
		}

		return (int) (l_time - o.getL_time());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the isFuzzed
	 */
	public boolean isFuzzed() {
		return isFuzzed;
	}

	/**
	 * @param isFuzzed
	 *            the isFuzzed to set
	 */
	public void setFuzzed(boolean isFuzzed) {
		this.isFuzzed = isFuzzed;
	}

	/**
	 * @return the fragment
	 */
	public TcpFragment getFragment() {
		return fragment;
	}

	/**
	 * @param fragment
	 *            the fragment to set
	 */
	public void setFragment(TcpFragment fragment) {
		if (fragment != null) {
			this.fragment = fragment;
			this.ack = fragment.getAck();
			this.seq = fragment.getSeq();
			this.l_time = fragment.getTimestamp();
			this.timestamp = new Date(l_time);
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ack
	 */
	public long getAck() {
		return ack;
	}

	/**
	 * @param ack
	 *            the ack to set
	 */
	public void setAck(long ack) {
		this.ack = ack;
	}

	/**
	 * @return the seq
	 */
	public long getSeq() {
		return seq;
	}

	/**
	 * @param seq
	 *            the seq to set
	 */
	public void setSeq(long seq) {
		this.seq = seq;
	}

	/**
	 * @return the l_time
	 */
	public long getL_time() {
		return l_time;
	}

	/**
	 * @param l_time
	 *            the l_time to set
	 */
	public void setL_time(long l_time) {
		this.l_time = l_time;
	}

}
