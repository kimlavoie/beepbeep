/**
* File: BugReporterMantis.java
*
* Author: Raphaël Laguerre
*
* Last modified: 15/06/2013
*
* Description: This class extends BugReporter. It is used to report bugs in Mantis, by using the SOAP web service interface.
*
*/
package ca.uqac.info.monitor;

import java.net.URL;
import org.mantisbt.connect.IMCSession;
import org.mantisbt.connect.MCException;
import org.mantisbt.connect.axis.MCSession;
import org.mantisbt.connect.model.IIssue;
import java.net.URL;
import java.net.MalformedURLException; 
import java.io.FileReader;
import java.io.BufferedReader;

public class BugReporterMantisWS extends BugReporter {
	private String category;
	private String url;
	private String user;
	private String passwd;
	
	private int projectID;
	private IMCSession session;

	public BugReporterMantisWS (String category, int projectID){
		super();		
		this.category = category;
		this.projectID = projectID;
		try{	
    			URL uRl = new URL(url);
   			session = new MCSession(uRl, user, passwd);
		} catch (MCException e) {			
			e.printStackTrace();
		}catch (MalformedURLException ex){
    			ex.printStackTrace();
  		}		
	}	

	public BugReporterMantisWS () {
		super();
		category = "General";
		projectID = 1;
		readConfig();
		try{	
    			URL uRl = new URL(url);
   			session = new MCSession(uRl, user, passwd);
		} catch (MCException e) {			
			e.printStackTrace();
		}catch (MalformedURLException ex){
    			ex.printStackTrace();
  		}
	}		
	

	public void sendReport(String specFile, String brokenProperty, String trace){				
		try{
			IIssue issue = session.newIssue(projectID);
			System.out.println(session.getIdFromSummary(specFile));	
			if (session.getIdFromSummary(specFile)==0){ //Checks if the bug has already been reported		
				issue.setDescription(brokenProperty);
				issue.setSummary(specFile);		
				issue.setCategory(category);
	 			session.addIssue(issue);			
				byte[] content = trace.getBytes();  
	    			//Add the trace as an attachment of the bug
				session.addIssueAttachment(session.getIdFromSummary(specFile), String.valueOf(session.getIdFromSummary(specFile)), "txt", content);
			}			
		}catch (MCException e) {
			e.printStackTrace();
		}		
	}	
	
	private void readConfig(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader("mantis.conf"));
			url = reader.readLine();
			user = reader.readLine();
			passwd = reader.readLine();
			reader.close();
		} catch (Exception e){
				e.printStackTrace();
		}
	}
}



