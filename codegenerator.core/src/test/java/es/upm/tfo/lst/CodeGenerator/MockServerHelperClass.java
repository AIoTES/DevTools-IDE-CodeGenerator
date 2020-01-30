package es.upm.tfo.lst.CodeGenerator;

import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;
import org.mockserver.server.initialize.ExpectationInitializer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MockServerHelperClass implements ExpectationInitializer {
	
	@Override
	public Expectation[] initializeExpectations(){
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		 String path="src/test/resources/template-complex/";
		 try {
				FileInputStream fis = new FileInputStream(new File(path+"complexXml.xml"));
				 
					br = new BufferedReader(new InputStreamReader(fis));
					
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}			  
					  br.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		   return new Expectation[]{
		            new Expectation(
		                request()
		                    .withPath("src/test/resources/template-complex/complexXml.xml")
		            )
		                .thenRespond(
		                response()
		                    .withBody(sb.toString())
		            ),
		            new Expectation(
		                request()
		                    .withPath("/simpleSecond")
		            )
		                .thenRespond(
		                response()
		                    .withBody("some second response")
		            )
		        };
	}


	
	
}
