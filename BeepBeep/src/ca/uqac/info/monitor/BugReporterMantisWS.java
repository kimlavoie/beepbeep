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

public class BugReporterMantisWS extends BugReporter {
	private String category;
	private int projectID;
	private IMCSession session;

	public BugReporterMantisWS (String specFile, String brokenProperty, String trace, String url, String user, String passwd, String category, int projectID){
		super(specFile, brokenProperty, trace);		
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

	public BugReporterMantisWS (String specFile, String brokenProperty, String trace, String url, String user, String passwd) {
		super(specFile, brokenProperty, trace);		
		category = "General";
		projectID = 1;
		try{	
    			URL uRl = new URL(url);
   			session = new MCSession(uRl, user, passwd);
		} catch (MCException e) {			
			e.printStackTrace();
		}catch (MalformedURLException ex){
    			ex.printStackTrace();
  		}
	}		
	
	@Override
	public void sendReport(){				
		try{	
			IIssue issue = session.newIssue(projectID);
			issue.setDescription(brokenProperty);
			issue.setSummary(specFile);		
			issue.setCategory(category);
 			issue.setAdditionalInformation(trace);
			session.addIssue(issue);
		}catch (MCException e) {
			e.printStackTrace();
		}		
	}	
}



