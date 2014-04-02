/*
 * Copyright (c) 2014 Jared González
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
package com.jarego.jayatana;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de envolutara de características para gestionar el numbero de desplieges y
 * control de errores durante este despliege.
 * 
 * @author Jared González
 */
public class FeatureWrapper {
	private String className;
	private Feature featrue = null;
	private boolean deployed = false;
	
	/**
	 * Crea una envolutara de característica basado en el nombre de la clase implementada de la
	 * interface <code>com.jarego.jayatana.Feature</code>.
	 * 
	 * @param className clase implementada de la interface <code>com.jarego.jayatana.Feature</code>
	 */
	public FeatureWrapper(String className) {
		this.className = className;
	}
	
	/**
	 * Obtiene la instancia de característica, se instancia por primera vez al invocar el método.
	 * 
	 * @return retorna la interface de característica
	 */
	private Feature getInstance() {
		if (featrue == null)
			try {
				featrue = (Feature)Class.forName(className).newInstance();
			} catch (Exception e) {
				Logger.getLogger(FeatureWrapper.class.getName())
					.log(Level.WARNING, "can't create feature", e);
			}
		return featrue;
	}
	
	/**
	 * Verifica si la característica ya ha sido desplegada.
	 * 
	 * @return Regresa <code>True</code> si ha sido deplegado de lo
	 * contrario regresa <code>False</code>.
	 */
	public synchronized boolean isDeployed() {
		return deployed;
	}
	
	/**
	 * Realiza el despliege de la característica una sola vez
	 * 
	 * @return Regresa <code>True</code> si es desplegada por primera vez
	 * de lo contrario regresa <code>False</code>.
	 */
	public synchronized boolean deployOnce() {
		if (!deployed) {
			Feature feature;
			if ((feature = getInstance()) != null) {
				try {
					feature.deploy();
				} catch (Exception e) {
					Logger.getLogger(FeatureWrapper.class.getName())
						.log(Level.WARNING, "can't deploy feature", e);
				}
				deployed = true;
				return true;
			}
		}
		return false;
	}
}