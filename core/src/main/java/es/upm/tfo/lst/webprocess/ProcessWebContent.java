package es.upm.tfo.lst.webprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 
 * @author edu
 *
 */
public class ProcessWebContent {
	private Document doc; 
	private String test=null;
	
	private URI uri;
	private Elements t;
	private String output="";
	public void setOutput(String output) {
		this.output = output;
	}

	private List<URL> arrayOfSites = new ArrayList<>();
	private List<String> arrayOfNames = new ArrayList<>();
	
	/**
	 * Obtain all data provided from URL and stores it in temporary directory 
	 * @param url
	 * @throws Exception
	 */
	public String processURL(URL url) throws Exception{
		
		this.uri=url.toURI().resolve(".");
		StringBuilder sb = new StringBuilder();
    	try {
    		 //reading XML content
	         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	         String inputLine;
	         while ((inputLine = in.readLine()) != null)
	        	 sb.append(inputLine+"\n");
	         in.close();
	        test=sb.toString();
	        this.generateFiles();
    	}catch(Exception a) {
    		//a.printStackTrace();
    	}
    	
    	return test;
	}
	
	private void getDirectoryContent() throws Exception{
		
		doc = Jsoup.connect(uri.toString()).get();
		t =  doc.select("a[href*=.vm]");
		String base=uri.toURL().toString();
		t.stream().forEach(f->{
			try {
				this.arrayOfNames.add(f.text());
				this.arrayOfSites.add(new URL(base+f.text()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		});
	}

	private void generateFiles() throws Exception{
		this.getDirectoryContent();
		StringBuilder sb = new StringBuilder();
		BufferedWriter bufferWriter=null;
    	BufferedWriter bw = null;
		FileWriter fw = null;
		for (URL url : arrayOfSites) {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String inputLine="";
	        while ((inputLine = in.readLine()) != null)
	       	 	sb.append(inputLine+"\n");
	        in.close();
	        //System.out.println(sb.toString());
	        inputLine="";
	        File fileToWrite = new File(this.output,this.arrayOfNames.get(this.arrayOfSites.indexOf(url)));
            bufferWriter= new BufferedWriter(new java.io.FileWriter(fileToWrite,true));
            bufferWriter.write(sb.toString());
            bufferWriter.write("\n");
            bufferWriter.close();
		}
		
		

    }
	
	
}
