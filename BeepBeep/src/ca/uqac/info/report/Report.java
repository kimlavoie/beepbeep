/**
  * File: Report.java
  *
  * Author: Kim Lavoie
  *
  * Last modified: 12/06/2013
  *
  * Description: This class is used to report bugs, by writing in a file; easily extensible.
  *
  */

import java.util.Date;

public class Report{
	
	protected String specFile;
	protected String trace;
	protected String brokenProperty;
	protected Calendar dateDiscovered = new Calendar(new Date());
	protected BufferedWriter = new BufferedWriter(new FileWriter("report.xml"));

	public Report(){
		
	}
}
