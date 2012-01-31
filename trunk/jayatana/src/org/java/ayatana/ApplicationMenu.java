/*
 * Copyright (c) 2012 Jared González.
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

package org.java.ayatana;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;

/**
 * Clase para instalar el menu de aplicaciones globales del
 * escritorio Ayatana de Ubuntu
 * 
 * @author Jared González
 */
final public class ApplicationMenu implements WindowListener, AWTEventListener {
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(final JFrame frame) {
		return ApplicationMenu.tryInstall(frame, frame.getJMenuBar());
	}
	/**
	 * Trata de instalar el menu de aplicaciones globales, si no es posible
	 * por incompatibilidad del sistema operativo o esta des habilitado el
	 * menu global retornara <code>False</code>.
	 * 
	 * @param frame Ventana que contiene la barra de menus
	 * @param menubar La barra de menus en caso de que este contenida entro panel
	 * @return retorna <code>True</code> si se puede instalar el menu global
	 * de lo contrario retorna <code>False</code>.
	 */
	public static boolean tryInstall(final JFrame frame, final JMenuBar menubar) {
		if (frame == null || menubar == null)
			throw new NullPointerException();
		if (!System.getProperty("os.name").startsWith("Linux"))
			return false;
		if (!System.getProperty("sun.desktop").equals("gnome"))
			return false;
		if (!"libappmenu.so".equals(System.getenv("UBUNTU_MENUPROXY")))
			return false;
		new ApplicationMenu(frame, menubar);
		return true;
	}
	
	
	/**
	 * Inicializa el proceso de GMailLoop y cración de estrucutras de
	 * control de ventanas
	 */
	private static native void nativeInitialize();
	/**
	 * Deteiene el proceso de GMainLoop y destruye las estrucutras de
	 * control de ventanas
	 */
	private static native void nativeUninitialize();
	
	
	private static boolean initialized = false;
	/**
	 * Inicializa el ApplicationMenu para iniciar con la integración con Ayatana
	 * de Ubuntu
	 */
	private static void initialize() {
		if (!initialized) {
			AyatanaLibrary.load();
			
			Thread startupThread = new Thread() {
				@Override
				public void run() {
					ApplicationMenu.nativeInitialize();
				}
			};
			startupThread.setDaemon(true);
			startupThread.start();
			
			Thread shutdownThread = new Thread() {
				@Override
				public void run() {
					ApplicationMenu.nativeUninitialize();
				}
			};
			shutdownThread.setDaemon(true);
			Runtime.getRuntime().addShutdownHook(shutdownThread);
			
			initialized = true;
		}
	}
	
	private JFrame frame;
	private JMenuBar menubar;
	private boolean tryInstalled = false;
	private Map<String, JMenuItem> acceleratorsmap;
	private AcceleratorsListener acceleratorsListener;
	private long windowxid;
	
	/**
	 * Obtiene el identificador de ventana del sistema ventanas X11
	 * 
	 * @param window ventana java
	 * @return identificador de ventana
	 */
	native private long getWindowXID(Window window);
	/**
	 * Registrar el observador de menu de aplicaciones para la ventana
	 * 
	 * @param windowxid identificador de ventana
	 */
	native private void registerWatcher(long windowxid);
	/**
	 * Desregistrar el observador de menu de aplicaciones para la ventana
	 * 
	 * @param windowxid identificador de ventana
	 */
	native private void unregisterWatcher(long windowxid);
	
	/**
	 * Crea un menu en el menu de aplicaciones globales
	 * 
	 * @param menu menu
	 */
	private void addMenu(JMenu menu) {
		this.addMenu(windowxid, menu.hashCode(), menu.getText(), menu.isEnabled());
	}
	/**
	 * Crear un menu en el menu de aplicaciones globales
	 * 
	 * @param windowxid identificador de ventana
	 * @param hashcode identificador de menu
	 * @param label texto de menu
	 */
	native private void addMenu(long windowxid, int hashcode, String label, boolean enabled);
	/**
	 * Crea un menu en el menu de aplicaciones globales
	 * 
	 * @param menuitem menu
	 */
	private void addMenuItem(JMenuItem menuitem) {
		int modifiers = -1;
		int keycode = -1;
		if (menuitem.getAccelerator() != null) {
			modifiers = menuitem.getAccelerator().getModifiers();
			keycode = menuitem.getAccelerator().getKeyCode();
		}
		
		if (menuitem instanceof JMenu) {
			this.addMenu((JMenu)menuitem);
		} else if (menuitem instanceof JRadioButtonMenuItem) {
			this.addMenuItemRadio(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode,
					menuitem.isSelected());
		} else if (menuitem instanceof JCheckBoxMenuItem) {
			this.addMenuItemCheck(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode,
					menuitem.isSelected());
		} else {
			this.addMenuItem(windowxid, menuitem.hashCode(),
					menuitem.getText(), menuitem.isEnabled(), modifiers, keycode);
		}
	}
	native private void addMenuItem(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode);
	native private void addMenuItemRadio(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	native private void addMenuItemCheck(long windowxid, int hashcode, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	private void addSeparator() {
		this.addMenuItemSeparator(windowxid);
	}
	native private void addMenuItemSeparator(long windowxid);
	
	/**
	 * Contructor de integración de Application Menu
	 * 
	 * @param frame
	 * @param menubar 
	 */
	private ApplicationMenu(JFrame frame, JMenuBar menubar) {
		this.frame = frame;
		this.menubar = menubar;
		frame.addWindowListener(this);
	}
	
	/**
	 * Tratar de instalar el applicationmenu, ya que depende que
	 * este el servicio de applicationmenu
	 */
	private synchronized void tryInstall() {
		if (tryInstalled)
			return;
		
		ApplicationMenu.initialize();
		windowxid = this.getWindowXID(frame);
		this.registerWatcher(windowxid);
		
		tryInstalled = true;
	}
	/**
	 * Tratar de desinstalar el applicationmenu, ya que depende que
	 * este el servicio de applicationmenu
	 */
	private synchronized void tryUninstall() {
		if (!tryInstalled)
			return;
		
		this.unregisterWatcher(windowxid);
		frame.removeWindowListener(this);
		
		tryInstalled = false;
	}
	
	/**
	 * Este método es invocado por la interface nativa en caso de que
	 * existe un applicationmenu registrado
	 */
	private void install() {
		acceleratorsmap = new TreeMap<String, JMenuItem>();
		acceleratorsListener = new AcceleratorsListener(
				menubar, acceleratorsmap);
		
		Toolkit.getDefaultToolkit()
				.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenu && comp.isVisible())
				this.addMenu((JMenu)comp);
		
		menubar.setVisible(false);
	}
	/**
	 * Este método es invocado por la interface nativa en caso de que se
	 * deshabilite al applicationmenu registrado
	 */
	private void uninstall() {
		menubar.setVisible(true);
		
		Toolkit.getDefaultToolkit()
				.removeAWTEventListener(this);
		
		acceleratorsListener.uninstall();
		acceleratorsmap.clear();
	}
	
	/**
	 * Obtene un menu del hashcode
	 * 
	 * @param hashcode identificador
	 * @return menu
	 */
	private JMenuItem getJMenuItem(int hashcode) {
		for (Component comp : menubar.getComponents())
			if (comp instanceof JMenuItem) {
				JMenuItem item;
				if ((item = getJMenuItem((JMenuItem)comp, hashcode)) != null)
					return item;
			}
		return null;
	}
	/**
	 * Obtene un menu del hashcode
	 * 
	 * @param menu menu padre
	 * @param hashcode identificador
	 * @return menu
	 */
	private JMenuItem getJMenuItem(JMenuItem menu, int hashcode) {
		if (menu.hashCode() == hashcode) {
			return menu;
		} else if (menu instanceof JMenu) {
			for (Component comp : ((JMenu)menu).getMenuComponents())
				if (comp instanceof JMenuItem) {
					JMenuItem item;
					if ((item = getJMenuItem((JMenuItem)comp, hashcode)) != null)
						return item;
				}
		}
		return null;
	}
	/**
	 * Invoka evento de menu basado en el identificador de menu
	 * 
	 * @param hashcode identificador de menu
	 */
	private void itemActivated(int hashcode) {
		this.invokeMenuItem(this.getJMenuItem(hashcode));
	}
	/**
	 * Invoka el evento de menu antes de mostrar
	 * 
	 * @param hashcode identificador de menu
	 */
	private void itemAboutToShow(int hashcode) {
		JMenu menu = (JMenu)this.getJMenuItem(hashcode);
		for (Component comp : menu.getMenuComponents()) {
			if (comp instanceof JMenuItem)
				this.addMenuItem((JMenuItem)comp);
			else if (comp instanceof JSeparator)
				this.addSeparator();
		}
	}
	
	/**
	 * Invoca la accion de un elemento de menu
	 * 
	 * @param menuitem menu
	 */
	private void invokeMenuItem(JMenuItem menuitem) {
		if (menuitem != null) {
			if (menuitem.isEnabled() && menuitem.isVisible()) {
				menuitem.getModel().setArmed(true);
				menuitem.getModel().setPressed(true);
				try {
					Thread.currentThread().sleep(68);
				} catch (InterruptedException e) {}
				menuitem.getModel().setPressed(false);
				menuitem.getModel().setArmed(false);
			}
		}
	}
	
	/**
	 * Invokar eventos de acceleradores de menus
	 * 
	 * @param modifiers
	 * @param keycode 
	 */
	private void invokeAccelerator(int modifiers, int keycode) {
		JMenuItem menuitem = acceleratorsmap.get(
				KeyEvent.getKeyModifiersText(modifiers)+
				KeyEvent.getKeyText(keycode));
		this.invokeMenuItem(menuitem);
	}
	
	private JFrame getFrame(Component comp) {
		if (comp == null)
			return null;
		else if (comp instanceof JFrame)
			return (JFrame)comp;
		else
			return this.getFrame(comp.getParent());
	}
	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getID() == KeyEvent.KEY_RELEASED) {
			KeyEvent e = (KeyEvent)event;
			if (e.getKeyCode() != KeyEvent.VK_ALT &&
					e.getKeyCode() != KeyEvent.VK_SHIFT &&
					e.getKeyCode() != KeyEvent.VK_CONTROL &&
					e.getKeyCode() != KeyEvent.VK_META &&
					e.getKeyCode() != KeyEvent.VK_ALT_GRAPH &&
					frame.isActive()) {
				JFrame currentframe;
				if (event.getSource() instanceof Component)
					currentframe = this.getFrame((Component)event.getSource());
				else if (event.getSource() instanceof JFrame)
					currentframe = (JFrame)event.getSource();
				else 
					currentframe = null;
				if (frame.equals(currentframe))
					this.invokeAccelerator(e.getModifiers(), e.getKeyCode());
			}
		}
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		tryInstall();
	}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		tryUninstall();
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
}
