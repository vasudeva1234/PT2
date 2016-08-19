//*********************************************************************************************************************
// DNP3ApplicationPacketDO.java
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.text.html.HTMLEditorKit.LinkController;

import org.epri.pt2.reassembler.TcpFragment;
import org.epri.pt2.reassembler.TcpLink;

import com.google.common.primitives.Bytes;

@Entity
public class DNP3ApplicationPacketDO extends AbstractPacketDO {

	/* Stores a list of packets which comprise the DNP3 Application Packet */
	@Transient
	DNP3LinkHeader linkHeader;

	public DNP3LinkHeader getLinkHeader() {
		return linkHeader;
	}

	public void setLinkHeader(DNP3LinkHeader linkHeader) {
		this.linkHeader = linkHeader;
	}

	@Transient
	TransportHeader transportHeader;
	@Transient
	List<TcpLink> links;
	@Transient
	List<Byte> appData;
	@Transient
	List<Byte> rawData;
	
	@Transient
	boolean isAppPacket;
	@Transient
	int sequence;
	@Transient
	byte[] remainingData;
	
	String appDataHash;

	public class TransportHeader {
		int fin = 0;
		int fir = 0;
		int sequence = -1;
		byte data[];

		boolean isPresent = true;

		public TransportHeader(DNP3LinkHeader link) {
			isPresent = true;
			
			if (link.getLength() <= 5) {
				isPresent = false;
			} else {
				if ((link.getData()[0] & 0x80) > 0) {
					fin = 1;
				}

				if ((link.getData()[0] & 0x40) > 0) {
					fir = 1;
				}

				sequence = link.getData()[0] & 0x3F;

				data = Arrays.copyOfRange(link.getData(), 1,
						link.getData().length);
			}
		}

		public TransportHeader(boolean isFirst, boolean isLast, int sequence) {
			if (isFirst) {
				fir = 1;
			}
			if (isLast) {
				fin = 1;
			}
			this.sequence = sequence;
			data = new byte[0];
		}

		public byte getByte() {
			return (byte) ((fin << 7) | (fir << 6) | (sequence & 0x3F));
		}
	}

	@Entity
	public class ApplicationChunk {
		int crc;
		byte[] data;

		public ApplicationChunk(byte[] data) {
			if (data.length > 2) {
				/* copy all but last two bytes */
				this.data = Arrays.copyOfRange(data, 0, data.length - 2);

				/* crc is last two bytes */
				crc = (int) data[data.length - 2]
						| ((int) data[data.length - 1] << 8);
			} else {
				this.data = new byte[0];
				crc = (int) data[0] | ((int) data[1] << 8);
			}
		}
	}

	public DNP3ApplicationPacketDO() {
		super("DNP3", new byte[0]);
		initialize();
	}

	/**
	 * 
	 * @param data
	 * @param linkHead
	 *            - Contains the source and destination information
	 */
	public DNP3ApplicationPacketDO(byte[] data, DNP3LinkHeader linkHead) {
		super("DNP3", data);
		links = new ArrayList<TcpLink>();
		appData = new ArrayList<Byte>();
		sequence = -1;
		isAppPacket = true;
	}

	public void initialize() {
		links = new ArrayList<TcpLink>();
		appData = new ArrayList<Byte>();
		rawData = new ArrayList<Byte>();
		remainingData = new byte[0];
		sequence = -1;
		isAppPacket = true;
		linkHeader = new DNP3LinkHeader();
		transportHeader = null;
	}

	public boolean addData(byte[] data) {
		return addData(data, null);
	}

	public boolean addData(byte[] data, TcpFragment fragment) {
		int lengthIndex = 0;

		linkHeader = new DNP3LinkHeader(data);

		/*
		 * Check that we have a valid DNP3 header, otherwise this is not a DNP3
		 * packet.
		 */
		if (!linkHeader.isValid()) {
			return false;
		}

		/* Add the raw data */
		for(byte b : data) {
			rawData.add((Byte)b);
		}
		
		// Ctrl, Src, Dest fields
		lengthIndex += 5;

		transportHeader = new TransportHeader(linkHeader);

		// Transport header field
		lengthIndex += 1;

		/*
		 * If we don't have an a transport layer header, just emit the packet
		 */
		if (!transportHeader.isPresent) {
			setFragment(fragment);
			isAppPacket = false;
			this.data = data;
			this.raw_data = Bytes.toArray(rawData);
			return true;
		}

		/*
		 * If sequence is less than 0 then we have a new application data packet
		 * chain
		 */
		if (sequence < 0) {
			links.clear();
			appData.clear();
			//rawData.clear();
			sequence = transportHeader.sequence;
		} else {
			/* Make sure that the next packet follows in sequence */
			if (((sequence + 1) % 256) == transportHeader.sequence) {
				sequence = transportHeader.sequence;
			}
			/* If not then reset the sequence */
			else {
				sequence = -1;
				return false;
			}
		}

		/* If we have the first packet, lets clear out any leftover data */
		if (transportHeader.fir > 0) {
			appData.clear();
			links.clear();
			//rawData.clear();
		}

		/*
		 * Add the application data fragments TODO there are still some bugs
		 * here. The packets don't match up with wireshark.
		 */
		int i = 0;

		/**
		 * i = the current pointer to the data buffer lengthIndex = the current
		 * number of bytes counted
		 */
		while ((i < transportHeader.data.length)
				&& (lengthIndex < linkHeader.getLength())) {
			ApplicationChunk chunk;
			int correction = 0;

			if (i == 0) {
				correction = 0;
			} else {
				correction = 0;
			}

			/* copy over the data bytes with the CRC */
			if (((i + 18) < transportHeader.data.length)
					&& ((lengthIndex + 16) < linkHeader.getLength())) {
				chunk = new ApplicationChunk(Arrays.copyOfRange(
						transportHeader.data, i, i + 18 + correction));

				// Advance to the next user data chunk
				i += 18 + correction;
				// We just copied 16 bytes
				lengthIndex += 16 + correction;
			} else {
				int remainingBytes = linkHeader.getLength() - lengthIndex;

				chunk = new ApplicationChunk(Arrays.copyOfRange(
						transportHeader.data, i, i + remainingBytes + 2
								+ correction));

				i += remainingBytes + 2;
				lengthIndex += remainingBytes;
			}

			for (byte b : chunk.data) {
				appData.add((Byte) b);
			}
		}


		/*
		 * Set the leftover data
		 */
		remainingData = Arrays.copyOfRange(transportHeader.data, i,
				transportHeader.data.length);


		/* If this is the last packet then lets return true */
		if (transportHeader.fin > 0) {
			setFragment(fragment);
			/* parse the packet and fill out the rest of the fields */
			parse();
			
			/*
			 * Store the hash of the application data
			 */
			try {
				appDataHash = MessageDigest.getInstance("md5").digest(data).toString();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}

		return false;
	}

	/**
	 * Adds a fragment of TCP data to the application packet. Once all parts of
	 * the packet has been reassembled, indicate to the reassembler that we are
	 * done by returning True.
	 * 
	 * @param link
	 * @return
	 */
	public boolean addLink(TcpLink link) {
		/* indicate that we have processed this link */
		link.setProcessed(true);

		return addData(link.get().getData(), link.get());
	}

	/**
	 * TODO generate the rest of the application layer packet
	 */
	private void parse() {
		/* set the application data */
		this.data = Bytes.toArray(appData);
		/* set the raw data */
		this.raw_data = Bytes.toArray(rawData);
	}

	public boolean isAppPacket() {
		return isAppPacket;
	}

	public byte[] getRemainingData() {
		return remainingData;
	}

	public byte[] updatePacket() {
		List<Byte> buf = new ArrayList<Byte>();

		/*
		 * This is actually 250, but to keep things 8 byte aligned we set it to
		 * 248 - 1 (for transport word size)
		 */
		int MAX_USER_DATA_SIZE = 247;

		/*
		 * 5 bytes for data link header 1 byte for transport
		 */
		int PACKET_OVERHEAD_SIZE = 6;

		int appdata_index = 0;
		int sequence = 0;

		/* Generate a DNP3 IP Packet Payload from the Application Packet */
		while (appdata_index < data.length) {

			/* Fields to be included in data link layer and transport bytes */
			int packet_length = 0;
			boolean isFirst = false;
			boolean isLast = false;

			/* Is this the first packet? */
			if (appdata_index == 0) {
				isFirst = true;
			}

			/* Is this the last packet? */
			if ((appdata_index + MAX_USER_DATA_SIZE) > data.length) {
				isLast = true;

				/*
				 * Get the remaining bytes, add the transport byte and data link
				 * layer bytes which is not included in the application data
				 */
				packet_length = data.length - appdata_index
						+ PACKET_OVERHEAD_SIZE;
			} else {

				/* Set the length of this chunk (which is the max size 253) */
				packet_length = MAX_USER_DATA_SIZE + PACKET_OVERHEAD_SIZE;
			}

			/*
			 * Copy the link header information from the packet and update
			 * length. TODO When using this function make sure that the link
			 * header is initialized
			 */
			DNP3LinkHeader linkHead = new DNP3LinkHeader(linkHeader);
			linkHead.setLength(packet_length);

			/*
			 * Since we updated the length, we need to grab the new raw bytes,
			 * which includes an updated CRC
			 */
			for (byte b : linkHead.getBytes()) {
				buf.add((Byte) b);
			}

			/*
			 * Generate the updated transport header
			 */
			TransportHeader transportHeader = new TransportHeader(isFirst,
					isLast, sequence);

			int userdata_index = 0;

			while (userdata_index < (packet_length - PACKET_OVERHEAD_SIZE)) {
				
				byte[] block;
				int block_length;

				/*
				 * 16 bytes = maximum block size Add 2 bytes for CRC 15 bytes if
				 * we are the first packet to save room for transport word
				 */
				if ((userdata_index == 0)
						&& ((userdata_index + 15) < (packet_length - PACKET_OVERHEAD_SIZE))) {
					block_length = 15 + 2;
				} else if ((userdata_index + 16) < (packet_length - PACKET_OVERHEAD_SIZE)) {
					block_length = 16 + 2;
				} else {
					block_length = packet_length - PACKET_OVERHEAD_SIZE
							- userdata_index + 2;
				}

				/*
				 * We need one more byte for the transport byte
				 */
				if (userdata_index == 0) {
					block_length++;
				}

				block = new byte[block_length];

				int block_index = 0;

				// The transport header is stuffed in the first user data
				// block
				if (userdata_index == 0) {
					block[block_index++] = transportHeader.getByte();
				}

				while (block_index < (block_length - 2)) {
//					if ((userdata_index + appdata_index + block_index) >= data.length) {
//						System.out.println("Warning");
//					}
					
					/* subtract one to realign offset */
					block[block_index] = this.data[userdata_index
							+ appdata_index + block_index-1];

					block_index++;
				}

				int usercrc = DNP3Utils.calculateCRC(Arrays.copyOfRange(block,
						0, block_length - 2));

				// Add the CRC
				block[block_index] = (byte) (usercrc & 0xFF);
				block[block_index + 1] = (byte) ((usercrc >> 8) & 0xFF);

				if(DNP3Utils.checkCRC(block) == false) {
					System.out.println("whoopsie!");
				}
				
				// Add the user data to the buf
				for (byte b : block) {
					buf.add((Byte) b);
				}

				/*
				 * Only increment by 15 if we are the first index, as the
				 * transport word is not included in the appdata
				 */
				if (userdata_index == 0) {
					userdata_index += 15;
				} else {
					userdata_index += 16;
				}
			}

			/*
			 * Increment by actual number of bytes consumed
			 */
			appdata_index += packet_length - PACKET_OVERHEAD_SIZE;

			/*
			 * Make sure we don't overflow
			 */
			sequence++;
			sequence %= 64;
		}

		return Bytes.toArray(buf);
	}

	public String getAppDataHash() {
		return appDataHash;
	}

	public void setAppDataHash(String appDataHash) {
		this.appDataHash = appDataHash;
	}
}
