/*******************************************************************************
 * Copyright 2018 Universidad Politécnica de Madrid UPM
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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author amedrano
 *
 */
public class Activator implements BundleActivator, ServiceListener {

	private BundleContext context;

	private volatile LoggerFactory loggerFactory;

	private GenerateServlet servlet;

	private File outputDir;
	//private static String aliasPath = System.getenv().getOrDefault("CODEGENERATOR_PATH", "/GenerateCode");

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		ServiceReference ref = context.getServiceReference(LoggerFactory.class.getName());
		if (ref != null) {
			loggerFactory = (LoggerFactory) context.getService(ref);
		}
		outputDir = context.getBundle().getDataFile("output");
		servlet = new GenerateServlet();

		servlet.setOutputDir(outputDir, "/GenerateCode");
		register();
		context.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + HttpService.class.getName() + ")");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		unregister();
	}

	public boolean register() {
		Logger logger = LoggerFactory.getLogger(Activator.class);
		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
		if (sRef != null) {
			HttpService httpService = (HttpService) context.getService(sRef);

			try {
				httpService.registerServlet("/GenerateCode", servlet,
						null, new OutputHTTPContext(outputDir));
			} catch (ServletException e) {
				logger.error("Exception while registering Servlet.", e);
				return false;
			} catch (NamespaceException e) {
				logger.error("Servlet Namespace exception; alias (URI) is already in use.", e);
				return false;
			}
			logger.info("Servlet started.");
			return true;

		} else
			logger.info("Servlet cannot be registered: no http service available.");
		return false;
	}

	public boolean unregister() {
		Logger logger = LoggerFactory.getLogger(Activator.class);
		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
		if (sRef != null) {
			HttpService httpService = (HttpService) context.getService(sRef);

			try {
				httpService.unregister(servlet.getClass().getAnnotation(WebServlet.class).value()[0]);
			} catch (IllegalArgumentException e) {
				logger.error("Servlet cannot be unregistered: illegal argument.", e);
				return false;
			}
			logger.info("Servlet stopped.");
			return true;

		} else
			logger.info("Servlet cannot be unregistered: no http service available.");
		return false;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			register();
		}
		if (event.getType() == ServiceEvent.UNREGISTERING) {
			unregister();
		}

	}
}
