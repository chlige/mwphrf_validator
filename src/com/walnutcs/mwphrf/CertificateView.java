/**
 * 
 */
package com.walnutcs.mwphrf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;
import com.walnutcs.mwphrf.PHRFCertificateValues.PHRFVariable;

/**
 * @author George Chlipala
 *
 */
public class CertificateView {

	private JFrame frame;
	private PDFFile pdfFile;
	private PagePanel pdfPanel;
	
	private int currPage = 0;
	private int pageCount = 0;
	
	private JButton nextPageButton;
	private JButton prevPageButton;
	private JTextField pageField;
	
	public static void displayCertificate(URL pdfURL, String title) throws IOException {
		CertificateView certView = new CertificateView();
		certView.frame.setTitle(title);
		certView.loadPDF(pdfURL);
		certView.init();
	}
	
	private CertificateView() {
		this.frame = new JFrame();
	}
	
	private void loadPDF(URL pdfURL) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		InputStream inStream = pdfURL.openStream();
		
		int n = 0;
		byte[] buffer = new byte[4096 * 1024];
		while ( -1 != (n = inStream.read(buffer)) ) {
			byteStream.write(buffer, 0, n);
		}
		byteStream.flush();
		ByteBuffer pdfBuffer = ByteBuffer.wrap(byteStream.toByteArray());
		this.pdfFile = new PDFFile(pdfBuffer);
		this.pageCount = this.pdfFile.getNumPages();

	}
	
	private void init() throws IOException {
		this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		this.frame.add(topPanel);

		Box toolbar = Box.createHorizontalBox();
				
		this.prevPageButton = new JButton("<");
		this.prevPageButton.setAction(new PrevPageAction());
		this.prevPageButton.setEnabled(false);
		toolbar.add(this.prevPageButton);
		
		this.pageField = new JTextField("1", 3);
		this.pageField.setMaximumSize(this.pageField.getPreferredSize());
		this.pageField.setFocusable(false);
		toolbar.add(this.pageField);
		
		this.nextPageButton = new JButton(">");
		this.nextPageButton.setAction(new NextPageAction());
		if ( this.pageCount == 1 ) 
			this.nextPageButton.setEnabled(false);
		toolbar.add(this.nextPageButton);
		
		JButton zoomButton = new JButton("Zoom");
		zoomButton.setAction(new ZoomAction());
		toolbar.add(zoomButton);
		
		JButton fitButton = new JButton("Fit");
		fitButton.setAction(new FullPageAction());
		toolbar.add(fitButton);		
		
		topPanel.add(toolbar, BorderLayout.NORTH);
		
		this.pdfPanel = new PagePanel();		
		topPanel.add(new JScrollPane(this.pdfPanel), BorderLayout.CENTER);
		
		PDFPage page = pdfFile.getPage(0);
		
		this.frame.pack();
		this.frame.setVisible(true);
		this.pdfPanel.showPage(page);

	}
	
	private class NextPageAction extends AbstractAction {

		public NextPageAction() {
			putValue(NAME, ">");
			putValue(SHORT_DESCRIPTION, "Next page");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int nextPage = currPage + 1;
			if ( nextPage >= pageCount ) {
				nextPageButton.setEnabled(false);
				return;
			} 
			if ( nextPage > 0 && ! prevPageButton.isEnabled()) {
				prevPageButton.setEnabled(true);
			}
			PDFPage page = pdfFile.getPage(nextPage);
			pdfPanel.showPage(page);
			currPage = nextPage;
			pageField.setText(String.format("%d", currPage + 1));
			nextPage++;
			if ( nextPage >= pageCount ) {
				nextPageButton.setEnabled(false);
			}
		}		
	}


	private class PrevPageAction extends AbstractAction {

		public PrevPageAction() {
			putValue(NAME, "<");
			putValue(SHORT_DESCRIPTION, "Previous page");
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			if ( currPage <= 0 ) {
				prevPageButton.setEnabled(false);
				return;
			} 
			int prevPage = currPage - 1;
			if ( prevPage < ( pageCount - 1 ) && ! nextPageButton.isEnabled()) {
				nextPageButton.setEnabled(true);
			}
			PDFPage page = pdfFile.getPage(prevPage);
			pdfPanel.showPage(page);
			currPage = prevPage;
			pageField.setText(String.format("%d", currPage + 1));
			if ( prevPage <= 0 ) {
				prevPageButton.setEnabled(false);
			}
		}		
	}

	
	private class ZoomAction extends AbstractAction {

		public ZoomAction() {
			putValue(NAME, "Zoom");
			putValue(SHORT_DESCRIPTION, "Zoom in");
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			pdfPanel.useZoomTool(true);
			
		}		
	}
	
	private class FullPageAction extends AbstractAction {

		public FullPageAction() {
			putValue(NAME, "Fit");
			putValue(SHORT_DESCRIPTION, "Fit page");
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			pdfPanel.useZoomTool(false);
			pdfPanel.setClip(null);
		}		
	}
}
