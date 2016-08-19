//*********************************************************************************************************************
// ViewDiffWindow.java
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
package org.epri.pt2.fuzzer;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
//import org.epri.pt2.sniffer.AbstractDataPacket;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * @author Southwest Research Institute
 *
 */
public class ViewDiffWindow extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3996569748178498535L;
	protected RSyntaxTextArea packetArea;
	private RSyntaxTextArea fuzzPacketArea;
	private SearchContext context;
	protected HttpXMLPacketParser parser;
	protected HttpXMLPacketParser fuzzParser;
	protected AbstractPacketDO originalPacket;
	protected AbstractPacketDO fuzzPacket;
	protected JButton okButton;
	protected String searchText;	
	
	public ViewDiffWindow(AbstractPacketDO originalPacket, AbstractPacketDO fuzzPacket) {
		
		this.originalPacket = originalPacket;
		this.fuzzPacket = fuzzPacket;
	
		initComponents();
	}
	
	private void initComponents() {
		setTitle("View Diff");

		// Create the list boxes and labels.
		JLabel packetLabel = new JLabel("Original Packet: ");
		JLabel fuzzPacketLabel = new JLabel("Fuzz Packet: ");		
		
		parser = new HttpXMLPacketParser(new ByteArrayInputStream("EMPTY".getBytes()));
		fuzzParser = new HttpXMLPacketParser(new ByteArrayInputStream("EMPTY".getBytes()));
		
		// Create the raw text view for original packet.
		packetArea = new RSyntaxTextArea();
		packetArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		packetArea.setAntiAliasingEnabled(true);
		packetArea.setEditable(false);

		// Create the raw text view for fuzz packet.
		fuzzPacketArea = new RSyntaxTextArea();
		fuzzPacketArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
		fuzzPacketArea.setAntiAliasingEnabled(true);
		fuzzPacketArea.setEditable(false);
		
		// Create the scroll panes for the packet table and packet text editor
		RTextScrollPane packetAreaScrollPane = new RTextScrollPane(packetArea);
		RTextScrollPane fuzzPacketAreaScrollPane = new RTextScrollPane(fuzzPacketArea);
		
		// Update the JTree model
		parser.setPacket(originalPacket);
		fuzzParser.setPacket(fuzzPacket);

		// Update the packetArea
		packetArea.setText(parser.toString());
		fuzzPacketArea.setText(fuzzParser.toString());
		
		context = new SearchContext();
		context.setMatchCase(false);
		context.setRegularExpression(false);
		context.setWholeWord(false);
		
		highlightPacketArea();
		highlightFuzzPacketArea();
		
		okButton = new JButton("Ok");
		okButton.addActionListener(this);
		
		setLayout(new MigLayout());
		add(packetLabel);
		add(fuzzPacketLabel, "wrap");
		add(packetAreaScrollPane);
		add(fuzzPacketAreaScrollPane, "wrap");
		add(okButton, "skip, align right");
		
		pack();
		setSize(700, 500);
		setModal(true);
	}
	
	public void highlightPacketArea() {
		int pos = SearchEngine.getNextMatchPos(SearchCriteria.getInstance().getSearchTag(), packetArea.getText(), true, true, true);
	
		if(pos > 0)
		{
			context.setSearchForward(true);
			context.setSearchFor(SearchCriteria.getInstance().getSearchData());
			packetArea.setCaretPosition(pos);			
			if(SearchEngine.find(packetArea, context))
			{
				try {				
					packetArea.addLineHighlight(packetArea.getCaretLineNumber(), new Color(255, 0, 0, 64));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void highlightFuzzPacketArea() {
		int pos = SearchEngine.getNextMatchPos(SearchCriteria.getInstance().getSearchTag(), fuzzPacketArea.getText(), true, true, true);
		
		if(pos > 0)
		{	
			// Find the content data associated with the xml tag.
			context.setSearchForward(true);
			context.setSearchFor(SearchCriteria.getInstance().getSearchFuzzValue());
			fuzzPacketArea.setCaretPosition(pos);
			if(!SearchCriteria.getInstance().getSearchFuzzValue().isEmpty()) {						
				if(SearchEngine.find(fuzzPacketArea, context))
				{
					try {				
						fuzzPacketArea.addLineHighlight(fuzzPacketArea.getCaretLineNumber(), new Color(255, 0, 0, 64));
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				// If the fuzz value is an empty field highlight the line associated with the xml tag. 
				try {				
					fuzzPacketArea.addLineHighlight(fuzzPacketArea.getCaretLineNumber(), new Color(255, 0, 0, 64));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(okButton)) {
			setVisible(false);
		} 
	}
	
	public void showDialog(Window parent) {
		setLocationRelativeTo(parent);
		setVisible(true);		
	}
}


