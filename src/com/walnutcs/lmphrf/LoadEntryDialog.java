/**
 * 
 */
package com.walnutcs.lmphrf;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * @author george
 *
 */
public class LoadEntryDialog extends JDialog implements ActionListener {

	
	private final JPanel contentPanel = new JPanel();
	
	/**
	 * 
	 */
	public LoadEntryDialog() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 */
	public LoadEntryDialog(Frame owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 */
	public LoadEntryDialog(Dialog owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 */
	public LoadEntryDialog(Window owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public LoadEntryDialog(Frame owner, boolean modal) {
		super(owner, modal);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public LoadEntryDialog(Frame owner, String title) {
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public LoadEntryDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public LoadEntryDialog(Dialog owner, String title) {
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modalityType
	 */
	public LoadEntryDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public LoadEntryDialog(Window owner, String title) {
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public LoadEntryDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public LoadEntryDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public LoadEntryDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public LoadEntryDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public LoadEntryDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 * @param gc
	 */
	public LoadEntryDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
