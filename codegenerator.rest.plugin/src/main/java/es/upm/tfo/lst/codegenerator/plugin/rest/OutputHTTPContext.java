/*******************************************************************************
 * Copyright 2019 Universidad Politécnica de Madrid UPM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package es.upm.tfo.lst.codegenerator.plugin.rest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * @author amedrano
 *
 */
public class OutputHTTPContext implements HttpContext {

	private File outputDir;

	/**
	 *
	 */
	public OutputHTTPContext(File outputDir) {
		this.outputDir = outputDir;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#handleSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		try {
			return new File(outputDir, name).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
	 */
	@Override
	public String getMimeType(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
