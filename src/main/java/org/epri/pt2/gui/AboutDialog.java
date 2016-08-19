//*********************************************************************************************************************
// AboutDialog.java
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
package org.epri.pt2.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -271003320967855464L;

	public AboutDialog() {
		super();
		initializeComponents();
	}

	private void initializeComponents() {
		setLayout(new MigLayout());
		setSize(640, 480);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("wrap 2", "[120!]20[400!]"));
		panel.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(panel);
		
		panel.add(new JLabel("Software:"), "top, left");
		panel.add(new JLabel("<html>Penetration Testing Toolkit (PT<sup>2</sup>)<br>Version 1.0</html>"));
		panel.add(new JLabel("Developed for:"), "top, left");
		ClassLoader cl = this.getClass().getClassLoader();
		Image epriLogo;
		try {
			epriLogo = ImageIO.read(cl.getResource("images/eprilogo.png"));
			ImageIcon icon = new ImageIcon(getScaledImage(epriLogo, 148, 24));
			
			panel.add(new JLabel(icon));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		panel.add(new JLabel(
				"<html>Electric Power Research Institute (EPRI)<br>3420 Hillview Ave.<br>Palo Alto, CA 94304</html>"),
				"skip");
		panel.add(new JLabel("Support:"), "top, left");
		panel.add(new JLabel(
				"<html>EPRI Customer Assistance Center<br>Phone: 800-313-3774<br>askepri@epri.com</html>"));
		panel.add(new JLabel("Copyright:"), "top, left");
		panel.add(new JLabel(
				"<html>Copyright&copy 2015 Electric Power Research Institute, Inc.<br><br>EPRI reserves all rights in the Program as delivered.  The Program or any portion thereof may not be reproduced in any form whatsoever except as provided by license without the written consent of EPRI.  A license under EPRI's rights in the Program may be available directly from EPRI.</html>"));
		panel.add(new JLabel("Developed by:"), "top, left");
		panel.add(new JLabel("<html>Southwest Research Institute&reg<br>6220 Culebra Rd.<br>San Antonio, TX 78238-5166</html>"));
		panel.add(new JLabel("Disclaimer:"), "top, left");
		panel.add(new JLabel("<html><b>THIS NOTICE MAY NOT BE REMOVED FROM THE PROGRAM BY ANY USER THEREOF.<br><br>NEITHER EPRI, ANY MEMBER OF EPRI, NOR ANY PERSON OR ORGANIZATION ACTING ON BEHALF OF THEM:<br><br>1.	MAKES ANY WARRANTY OR REPRESENTATION WHATSOEVER, EXPRESS OR IMPLIED, INCLUDING ANY WARRANTY OF MERCHANTABILITY OR FITNESS OF ANY PURPOSE WITH RESPECT TO THE PROGRAM; OR<br><br>2.	ASSUMES ANY LIABILITY WHATSOEVER WITH RESPECT TO ANY USE OF THE PROGRAM OR ANY PORTION THEREOF OR WITH RESPECT TO ANY DAMAGES WHICH MAY RESULT FROM SUCH USE.<br><br>RESTRICTED RIGHTS LEGEND:  USE, DUPLICATION, OR DISCLOSURE BY THE UNITED STATES FEDERAL GOVERNMENT IS SUBJECT TO RESTRICTION AS SET FORTH IN PARAGRAPH (g) (3) (i), WITH THE EXCEPTION OF PARAGRAPH (g) (3) (i) (b) (5), OF THE RIGHTS IN TECHNICAL DATA AND COMPUTER SOFTWARE CLAUSE IN FAR 52.227-14, ALTERNATE III.</b></html>"));
		
		add(scrollPane, "dock center");
	}
	
	  private Image getScaledImage(Image srcImg, int w, int h){
		    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2 = resizedImg.createGraphics();
		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		    g2.drawImage(srcImg, 0, 0, w, h, null);
		    g2.dispose();
		    return resizedImg;
		}
}
