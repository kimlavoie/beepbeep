/**
* File: BugReporterMantis.java
*
* Author: Raphaël Laguerre
*
* Last modified: 15/06/2013
*
* Description: This class extends BugReporter. It is used to report bugs in Mantis, by using JDBC to interact with the mantis database.
*
*/
package ca.uqac.info.monitor;

import ca.uqac.info.monitor.OutilsJDBC;
import java.sql.Connection;

public class BugReporterMantisJDBC extends BugReporter {

	private Connection co;
	private String request = "insert into mantis_bug_table (os,os_build,platform,version,fixed_in_version,build,summary,target_version)"+
			   " values(1,1,1,1,1,1,'description',1)";
	private String url;
	private String driver;
	private String user;
	private String passwd;
	public BugReporterMantisJDBC(String specFile, String brokenProperty, String trace, String user, String passwd, String url, String driver) {
		super(specFile, brokenProperty, trace);
		this.url = url;
		this.driver = driver;
		this.user = user;
		this.passwd = passwd;		
	}
	
	public BugReporterMantisJDBC(String specFile, String brokenProperty, String trace) {
		super(specFile, brokenProperty, trace);	
		url = "jdbc:mysql://localhost:3306/mantisdb";
		driver = "com.mysql.jdbc.Driver";
		user = "mantis";
		passwd = "mantis";
	}
	
	@Override
	public void sendReport(){
		co = OutilsJDBC.openConnection(url, driver, user, passwd);				
		int res = OutilsJDBC.execRequete(brokenProperty, co);
		OutilsJDBC.closeConnection(co); 
	}	
}






