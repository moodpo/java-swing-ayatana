/*
 * Copyright (c) 2013 Jared González
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jarego.jayatana.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * Clase de un editor simple de texto
 * 
 * @author Jared González
 */
public class Swing extends JFrame implements ActionListener, ItemListener {
	private static final ResourceBundle bundle = ResourceBundle
			.getBundle("com.jarego.jayatana.test.Bundle");
	
	public static void main(String args[]) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Swing swingDemo = new Swing();
		        swingDemo.loadDefaultText();
		        swingDemo.setVisible(true);
			}
		});
	}

	private JMenuBar menubar;
	private JMenuItem menucut;
	private JMenuItem menucopy;
	private File file;
	private JTextArea textarea;
	private JMenu menufile;
	private JMenu menuedit;
	private JMenu menuhelp;

	/**
	 * Crear un editor de texto básico
	 * 
	 */
	public Swing() {
		this(null);
	}

	/**
	 * Crea un editor de texto básico a partir de archivo
	 * 
	 * @param file
	 *            Archivo de texto
	 */
	public Swing(File file) {
		// TODO: Utilizando openjdk6, al actualizar el objeto splashScreen la aplicacion muere.
		// Existe un conflicto al utilizar XInitThread en el proyecto jayatanaag
		// Error:
		//   java: pthread_mutex_lock.c:317: __pthread_mutex_lock_full: La declaración `(-(e)) != 3 || !robust' no se cumple.
		//SplashScreen splashScreen = SplashScreen.getSplashScreen();
		//Graphics2D g2d = splashScreen.createGraphics();
		//g2d.setColor(Color.RED);
		//g2d.drawString("Cargnado...", 10, 10);
		//splashScreen.update();
		
		this.file = file;

		menubar = new JMenuBar();
		menubar.add(getMenuFile());
		menubar.add(getMenuEdit());
		menubar.add(getMenuHelp());

		textarea = new JTextArea();
		textarea.setFont(new Font("DialogInput", textarea.getFont().getStyle(),
				textarea.getFont().getSize()));
		textarea.setTabSize(4);
		textarea.setLineWrap(true);
		textarea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (textarea.getSelectedText() == null) {
					menucut.setEnabled(false);
					menucopy.setEnabled(false);
				} else {
					menucut.setEnabled(true);
					menucopy.setEnabled(true);
				}
			}
		});

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(getToolBar(), BorderLayout.PAGE_START);
		contentPane.add(new JScrollPane(textarea), BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle(bundle.getString("title"));
		this.setJMenuBar(menubar);
		this.setContentPane(contentPane);
		this.setSize(500, 350);
	}

	/**
	 * Cargar texto predeterminado
	 */
	public void loadDefaultText() {
		if (file == null) {
			try {
				InputStream input = getClass().getResourceAsStream(
						"/com/jarego/jayatana/test/content.txt");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					textarea.append(line + "\n");
				}
				reader.close();
				input.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Crear menu de archivo
	 * 
	 * @return menu
	 */
	private JMenu getMenuFile() {
		if (menufile == null) {
			JMenuItem menunew = new JMenuItem(bundle.getString("menu_new"));
			menunew.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_new_ac")));
			menunew.setActionCommand("new");
			menunew.addActionListener(this);
			JMenuItem menuopen = new JMenuItem(bundle.getString("menu_open"));
			menuopen.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_open_ac")));
			menuopen.setActionCommand("open");
			menuopen.addActionListener(this);
			JMenuItem menusave = new JMenuItem(bundle.getString("menu_save"));
			menusave.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_save_ac")));
			menusave.setActionCommand("save");
			menusave.addActionListener(this);
			JMenuItem menusaveas = new JMenuItem(bundle.getString("menu_save_as"));
			menusaveas.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_save_as_ac")));
			menusaveas.setActionCommand("saveas");
			menusaveas.addActionListener(this);
			JMenuItem menuclose = new JMenuItem(bundle.getString("menu_close"));
			menuclose.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_close_ac")));
			menuclose.setActionCommand("close");
			menuclose.addActionListener(this);
			JMenuItem menuexit = new JMenuItem(bundle.getString("menu_exit"));
			menuexit.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_exit_ac")));
			menuexit.setActionCommand("exit");
			menuexit.addActionListener(this);
	
			menufile = new JMenu(bundle.getString("menu_file"));
			menufile.setMnemonic(bundle.getString("menu_file_mn").charAt(0));
			menufile.add(menunew);
			menufile.add(menuopen);
			menufile.add(menusave);
			menufile.add(menusaveas);
			menufile.addSeparator();
			menufile.add(menuclose);
			menufile.add(menuexit);
		}

		return menufile;
	}

	/**
	 * Crea el menu de edición
	 * 
	 * @return menu
	 */
	private JMenu getMenuEdit() {
		if (menuedit == null) {
			JMenuItem menuundo = new JMenuItem(bundle.getString("menu_undo"));
			menuundo.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_undo_ac")));
			menuundo.setActionCommand("undo");
			menuundo.addActionListener(this);
			menuundo.setEnabled(false);
			JMenuItem menuredo = new JMenuItem(bundle.getString("menu_redo"));
			menuredo.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_redo_ac")));
			menuredo.setActionCommand("redo");
			menuredo.addActionListener(this);
			menuredo.setEnabled(false);
			menucut = new JMenuItem(bundle.getString("menu_cut"));
			menucut.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_cut_ac")));
			menucut.setActionCommand("cut");
			menucut.addActionListener(this);
			menucut.setEnabled(false);
			menucopy = new JMenuItem(bundle.getString("menu_copy"));
			menucopy.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_copy_ac")));
			menucopy.setActionCommand("copy");
			menucopy.addActionListener(this);
			menucopy.setEnabled(false);
			JMenuItem menupaste = new JMenuItem(bundle.getString("menu_paste"));
			menupaste.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_paste_ac")));
			menupaste.setActionCommand("paste");
			menupaste.addActionListener(this);
			menupaste.setEnabled(false);
			JMenuItem menuselall = new JMenuItem(bundle.getString("menu_selall"));
			menuselall.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_selall_ac")));
			menuselall.setActionCommand("selall");
			menuselall.addActionListener(this);
	
			menuedit = new JMenu(bundle.getString("menu_edit"));
			menuedit.setMnemonic(bundle.getString("menu_edit_mn").charAt(0));
			menuedit.add(menuundo);
			menuedit.add(menuredo);
			menuedit.addSeparator();
			menuedit.add(menucut);
			menuedit.add(menucopy);
			menuedit.add(menupaste);
			menuedit.addSeparator();
			menuedit.add(menuselall);
			menuedit.addSeparator();
			menuedit.add(getMenuLaF());
		}
		return menuedit;
	}

	/**
	 * Crea el menu de cambio de apariencia
	 * 
	 * @return menu
	 */
	private JMenu getMenuLaF() {
		JMenu menulaf = new JMenu(bundle.getString("menu_laf"));
		menulaf.setMnemonic(bundle.getString("menu_laf_mn").charAt(0));
		ButtonGroup bg = new ButtonGroup();
		for (UIManager.LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
			JRadioButtonMenuItem menul = new JRadioButtonMenuItem(
					info.getName());
			menul.setActionCommand("laf" + info.getClassName());
			menul.addItemListener(this);
			if (info.getClassName().equals(
					UIManager.getLookAndFeel().getClass().getName()))
				menul.setSelected(true);
			bg.add(menul);
			menulaf.add(menul);
		}
		return menulaf;
	}

	/**
	 * Crear el menu de ayuda
	 * 
	 * @return menu
	 */
	private JMenu getMenuHelp() {
		if (menuhelp == null) {
			JMenuItem menuAbout = new JMenuItem(bundle.getString("menu_about"));
			menuAbout.setAccelerator(KeyStroke.getKeyStroke(bundle
					.getString("menu_about_ac")));
			menuAbout.setActionCommand("about");
			menuAbout.addActionListener(this);
	
			menuhelp = new JMenu(bundle.getString("menu_help"));
			menuhelp.add(menuAbout);
		}
		return menuhelp;
	}

	/**
	 * Crea la barra de herramientas
	 * 
	 * @return barra de herramientas
	 */
	private JToolBar getToolBar() {
		JButton btnnew = createToolBarButton("Add24.gif", null);
		btnnew.setActionCommand("new");
		btnnew.addActionListener(this);
		JButton btnopen = createToolBarButton("Open24.gif", null);
		btnopen.setActionCommand("open");
		btnopen.addActionListener(this);
		JButton btnsave = createToolBarButton("Save24.gif", null);
		btnsave.setActionCommand("save");
		btnsave.addActionListener(this);
		
		JButton btnRebuild = new JButton("Rebuild");
		btnRebuild.setActionCommand("rebuild");
		btnRebuild.setFocusable(false);
		btnRebuild.setOpaque(false);
		btnRebuild.addActionListener(this);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setActionCommand("remove");
		btnRemove.setFocusable(false);
		btnRemove.setOpaque(false);
		btnRemove.addActionListener(this);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setActionCommand("add");
		btnAdd.setFocusable(false);
		btnAdd.setOpaque(false);
		btnAdd.addActionListener(this);

		JToolBar toolbar = new JToolBar();
		toolbar.add(btnnew);
		toolbar.add(btnopen);
		toolbar.add(btnsave);
		toolbar.add(btnRebuild);
		toolbar.add(btnRemove);
		toolbar.add(btnAdd);

		return toolbar;
	}

	/**
	 * Crea un boton para la barra de herramientas
	 * 
	 * @param icon
	 *            nombre de archivo de icono
	 * @param tooltip
	 *            mensaje del boton
	 * @return
	 */
	private JButton createToolBarButton(String icon, String tooltip) {
		JButton btn = new JButton(new ImageIcon(getClass().getResource(
				"/com/jarego/jayatana/test/" + icon)));
		btn.setFocusable(false);
		btn.setOpaque(false);
		return btn;
	}

	/*
	 * Gestion de eventos centrales
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("new".equals(e.getActionCommand())) {
			Swing swingDemo = new Swing();
			swingDemo.setVisible(true);
		} else if ("open".equals(e.getActionCommand())) {
			JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
			fc.showOpenDialog(this);
			File f = fc.getSelectedFile();
			if (f != null) {
				try {
					textarea.setText("");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new FileInputStream(f)));
					String line;
					while ((line = reader.readLine()) != null)
						textarea.append(line + "\n");
					reader.close();
				} catch (IOException err) {
				}
			}
		} else if ("close".equals(e.getActionCommand())) {
			this.dispose();
		} else if ("exit".equals(e.getActionCommand())) {
			System.exit(0);
			//for (Frame frame : Frame.getFrames())
			//	frame.dispose();
		} else if ("rebuild".equals(e.getActionCommand())) {
			menubar.remove(getMenuFile());
			menubar.remove(getMenuEdit());
			menubar.remove(getMenuHelp());
			menubar.add(getMenuFile());
			menubar.add(getMenuEdit());
			menubar.add(getMenuHelp());
		} else if ("remove".equals(e.getActionCommand())) {
			menubar.remove(getMenuFile());
			menubar.remove(getMenuEdit());
			menubar.remove(getMenuHelp());
		} else if ("add".equals(e.getActionCommand())) {
			menubar.add(getMenuFile());
			menubar.add(getMenuEdit());
			menubar.add(getMenuHelp());
		}
	}

	/*
	 * Eventos de cambio de apariencia
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() instanceof AbstractButton) {
			AbstractButton ab = (AbstractButton) e.getSource();
			if (ab.getActionCommand() != null
					&& ab.getActionCommand().startsWith("laf")) {
				try {
					UIManager
							.setLookAndFeel(ab.getActionCommand().substring(3));
					SwingUtilities.updateComponentTreeUI(this);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
	}
}