//*********************************************************************************************************************
// ProxyInterceptorPanel.java
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
package org.epri.pt2.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.epri.pt2.DO.AbstractPacketDO;
import org.epri.pt2.DO.HTTPPacketDO;
import org.epri.pt2.packetparser.HttpXMLPacketParser;
//import org.epri.pt2.sniffer.EthernetDataPacket;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;

/**
 * This class consists of a panel which is used to modify packets before they
 * are sent to the sender or receiver.
 * 
 * @author Tam Do
 * 
 */
public class ProxyInterceptorPanel extends JPanel implements ActionListener,
		ProxyInterceptorInterface, ChangeListener {

	protected JToggleButton interceptButton;
	protected JButton forwardButton;
	protected JButton modifyButton;
	protected JButton dropButton;

	protected JTabbedPane editorPane;
	protected HttpXMLPacketParser xmlEditorPane;
	protected RSyntaxTextArea rawEditorPane;

	protected ProxyController controller;

	protected JScrollPane xmlScrollPane;
	protected JScrollPane rawEditorScrollPane;

	private Object lock = new Object();

	private UserAction selectedAction = null;

	public static enum UserAction {
		NONE, DROPPED, SEND_ORIGINAL, SEND_MODIFIED
	}

	public ProxyInterceptorPanel() {
		initComponents();
		controller = ProxyController.getInstance();
		controller.getInterceptorController().addListener(this);
	}

	private void initComponents() {
		interceptButton = new JToggleButton("Intercept Enabled");
		interceptButton
				.setToolTipText("Enable/Disable interception of packets passed through the proxy.");
		interceptButton.addActionListener(this);
		forwardButton = new JButton("Forward");
		forwardButton
				.setToolTipText("Forward packets unmodified through the proxy.");
		forwardButton.addActionListener(this);
		modifyButton = new JButton("Modify");
		modifyButton
				.setToolTipText("Send the modified packet from the editor below through the proxy.");
		modifyButton.addActionListener(this);
		dropButton = new JButton("Drop");
		dropButton
				.setToolTipText("Drop the packet from the proxy. Please note that the server or client will not receive the dropped packet.");
		dropButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(interceptButton);
		buttonPanel.add(forwardButton);
		buttonPanel.add(modifyButton);
		buttonPanel.add(dropButton);

		xmlEditorPane = new HttpXMLPacketParser(true);
		xmlScrollPane = new JScrollPane(xmlEditorPane);
		rawEditorPane = new RSyntaxTextArea();
		rawEditorScrollPane = new JScrollPane(rawEditorPane);

		rawEditorPane.setAntiAliasingEnabled(true);
		rawEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);

		editorPane = new JTabbedPane();
		editorPane.addTab("XML", xmlScrollPane);
		editorPane.addTab("Raw/Text", rawEditorScrollPane);
		editorPane.addChangeListener(this);

		setLayout(new MigLayout());
		add(buttonPanel, "dock north");
		add(editorPane, "dock center");

		updateButtonStates();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5505095339294571308L;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(modifyButton)) {
			selectedAction = UserAction.SEND_MODIFIED;
			synchronized (lock) {
				lock.notify();
			}
		} else if (e.getSource().equals(dropButton)) {
			selectedAction = UserAction.DROPPED;
			synchronized (lock) {
				lock.notify();
			}
		} else if (e.getSource().equals(interceptButton)) {
			controller.setInterceptEnabled(interceptButton.isSelected());

			if (!interceptButton.isSelected()) {
				if (selectedAction == null) {
					selectedAction = UserAction.SEND_ORIGINAL;
					xmlEditorPane.setContent("");
					rawEditorPane.setText("");
				}
			}
		} else if (e.getSource().equals(forwardButton)) {
			selectedAction = UserAction.SEND_ORIGINAL;
			synchronized (lock) {
				lock.notify();
			}
		}

		updateButtonStates();
	}

	private void updateButtonStates() {
		if (interceptButton.isSelected()) {
			if (selectedAction == null) {
				forwardButton.setEnabled(false);
				modifyButton.setEnabled(false);
				dropButton.setEnabled(false);
			}
			if (selectedAction == UserAction.NONE) {
				forwardButton.setEnabled(true);
				modifyButton.setEnabled(true);
				dropButton.setEnabled(true);
			} else {
				forwardButton.setEnabled(false);
				modifyButton.setEnabled(false);
				dropButton.setEnabled(false);
			}
		} else {
			forwardButton.setEnabled(false);
			modifyButton.setEnabled(false);
			dropButton.setEnabled(false);
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(editorPane)) {
			if (editorPane.getSelectedComponent().equals(xmlScrollPane)) {
				// assume xml editor just selected
				// use content in textarea to update xml editor
				xmlEditorPane.setContent(rawEditorPane.getText());
			} else if (editorPane.getSelectedComponent().equals(
					rawEditorScrollPane)) {
				// assume raw editor just selected
				// get content from xmlEditorPane
				rawEditorPane.setText(xmlEditorPane.toString());
			}
		}
	}

	public void processRequest(final MutableBufferedRequest request) {
		if (controller.isInterceptEnabled() && !controller.isFuzzingEnabled()) {

			synchronized (lock) {
				this.requestFocusInWindow();

				// Update the xmlEditorPane and the rawEditorPane
				xmlEditorPane.setPacket(getPacketFromRequest(request));
				rawEditorPane.setText(xmlEditorPane.toString());

				selectedAction = UserAction.NONE;
				updateButtonStates();

				while (selectedAction == UserAction.NONE) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				switch (selectedAction) {
				case DROPPED:
					request.setHeader(new byte[0]);
					request.setContent(new byte[0]);
				case SEND_MODIFIED:
					try {
						if (editorPane.getSelectedComponent().equals(
								rawEditorScrollPane)) {
							xmlEditorPane.setContent(rawEditorPane.getText());
						}

						String packet = xmlEditorPane.toString();

						int idx = packet.indexOf("\r\n\r\n");

						String header = packet.substring(0, idx + 4);
						String content = packet.substring(idx + 4);

						if (header == null || header == "") {
							throw new Exception("No header");
						}

						request.setHeader(header.getBytes());

						if (content != null && content.length() > 0) {
							request.setContent(content.getBytes());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;
				case SEND_ORIGINAL:
					break;
				default:
					break;

				}

				xmlEditorPane.setContent("");
				rawEditorPane.setText("");
				updateButtonStates();
			}
		}
	}

	public void processResponse(BufferedRequest request,
			final MutableBufferedResponse response) {
		if (controller.isInterceptEnabled() && !controller.isFuzzingEnabled()) {
			synchronized (lock) {
				this.requestFocusInWindow();

				// Update the xmlEditorPane and the rawEditorPane
				xmlEditorPane.setPacket(getPacketFromResponse(response));
				rawEditorPane.setText(xmlEditorPane.toString());

				selectedAction = UserAction.NONE;
				updateButtonStates();

				while (selectedAction == UserAction.NONE) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				switch (selectedAction) {
				case DROPPED:
					response.setHeader(new byte[0]);
					response.setContent(new byte[0]);
				case SEND_MODIFIED:
					try {
						if (editorPane.getSelectedComponent().equals(
								rawEditorScrollPane)) {
							xmlEditorPane.setContent(rawEditorPane.getText());
						}

						String packet = xmlEditorPane.toString();

						int idx = packet.indexOf("\r\n\r\n");

						String header = packet.substring(0, idx + 4);
						String content = packet.substring(idx + 4);

						if (header == null || header == "") {
							throw new Exception("No header");
						}

						response.setHeader(header.getBytes());

						if (content != null && content.length() > 0) {
							response.setContent(content.getBytes());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case SEND_ORIGINAL:
					break;
				default:
					break;

				}

				xmlEditorPane.setContent("");
				rawEditorPane.setText("");
			}
		}
	}

	/**
	 * TODO Check if we can inspect the contents of the packet.  If we can, we can
	 * use this to make a decision whether or not to generate DNP3 or a HTTP packet.
	 * @param request
	 * @return
	 */
	private AbstractPacketDO getPacketFromRequest(
			final MutableBufferedRequest request) {
		byte[] header, content;

		try {
			header = request.getHeader();
			content = request.getDecodedContent();

			if (content == null) {
				content = request.getContent();
			}

			String s;
			if (content != null) {
				s = new String(header, "UTF-8") + "\r\n"
						+ new String(content, "UTF-8");
			} else {
				s = new String(header, "UTF-8") + "\r\n";
			}

			// TODO Add support for DNP3
			return new HTTPPacketDO("INTERCEPTED", s.getBytes());
		} catch (MessageFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private AbstractPacketDO getPacketFromResponse(
			final MutableBufferedResponse response) {
		byte[] header, content;

		try {
			header = response.getHeader();
			content = response.getDecodedContent();

			if (content == null) {
				content = response.getContent();
			}

			String s;
			if (content != null) {
				s = new String(header, "UTF-8") + "\r\n"
						+ new String(content, "UTF-8");
			} else {
				s = new String(header, "UTF-8") + "\r\n";
			}

			// TODO Add support for DNP3
			return new HTTPPacketDO("INTERCEPTED", s.getBytes());
		} catch (MessageFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
