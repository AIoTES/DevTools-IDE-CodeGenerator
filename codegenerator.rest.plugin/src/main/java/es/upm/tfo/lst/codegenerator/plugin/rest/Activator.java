/**
 *
 */
package es.upm.tfo.lst.codegenerator.plugin.rest;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.servlet.DefaultServlet;
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
	private String outputAlias = "/generated";


	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		 ServiceReference ref = context.getServiceReference(LoggerFactory.class.getName());
	        if (ref != null)
	        {
	            loggerFactory = (LoggerFactory) context.getService(ref);
	        }
	        outputDir = context.getBundle().getDataFile("output");
	        servlet = new GenerateServlet(outputDir.getAbsolutePath());
	        
	        servlet.setOutputDir(outputDir, outputAlias);
	        register();
	        context.addServiceListener(this,
					"(" + Constants.OBJECTCLASS + "=" + HttpService.class.getName() + ")");
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
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
				httpService.registerServlet(servlet.getClass().getAnnotation(WebServlet.class).value()[0], servlet, null, new OutputHTTPContext(outputDir));
				//httpService.registerServlet("/", servlet, null, null);
				// TODO register defaultservlet (http://www.eclipse.org/jetty/javadoc/9.4.12.v20180830/org/eclipse/jetty/servlet/DefaultServlet.html)
				// for serving static content results on outputAlias
				//httpService.registerServlet(outputAlias, new DefaultServlet(), null, new OutputHTTPContext(outputDir));
			} catch (ServletException e) {
				logger.error("Exception while registering Servlet.", e);
				return false;
			} catch (NamespaceException e) {
				logger.error("Servlet Namespace exception; alias (URI) is already in use." , e);
				return false;
			}
			logger.info("Servlet started." );
			return true;

		} else
			logger.info("Servlet cannot be registered: no http service available." );
		return false;
	}

	public boolean unregister() {
		Logger logger = LoggerFactory.getLogger(Activator.class);
		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
		if (sRef != null) {
			HttpService httpService = (HttpService) context.getService(sRef);

			try {
				httpService.unregister(servlet.getClass().getAnnotation(WebServlet.class).value()[0]);
				// TODO unregister defaultServlet
			} catch (IllegalArgumentException e) {
				logger.error("Servlet cannot be unregistered: illegal argument.", e);
				return false;
			}
			logger.info( "Servlet stopped." );
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
