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
package ca.uqac.info.monitor;

import java.util.Date;
import java.util.GregorianCalendar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

class BugReporter{
	
	public static final String DEFAULT_OUTPUT_FILE = new String("report.xml");

	protected String specFile;
	protected String trace;
	protected String brokenProperty;
	protected GregorianCalendar dateDiscovered = new GregorianCalendar();
	protected BufferedWriter outputFile;

	public BugReporter(){
		try{
			File file = new File(DEFAULT_OUTPUT_FILE);
			file.createNewFile();
			this.outputFile = new BufferedWriter(new FileWriter(file));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public BugReporter(String outputFile){
		try{
			File file = new File(outputFile);
			file.createNewFile();
			this.outputFile = new BufferedWriter(new FileWriter(file));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendReport(String specFile, String brokenProperty, String trace){
		this.specFile = specFile;
		this.trace = trace;
		this.brokenProperty = brokenProperty;
		try{
			outputFile.write(formatReport());
			outputFile.flush();
		} catch (Exception e){
			System.out.print("ERREUR D'ECRITURE");
		}
	}

	public String formatReport(){
		String outputString = new String();
		outputString += "<report>\n";
		outputString += "    <date>" + formatDate() + "</date>\n";
		outputString += "    <spec>" + specFile + "</spec>\n";
		outputString += "    <broken>" + brokenProperty + "</broken>\n";
		outputString += "    <trace>\n";
		outputString += trace + "\n";
		outputString += "    </trace>\n";
		outputString += "</report>\n";
		return outputString;
	}
	
	public String formatDate(){
		String output = new String();

		output += String.format("%04d", dateDiscovered.get(GregorianCalendar.YEAR)) + "-";
		output += String.format("%02d", dateDiscovered.get(GregorianCalendar.MONTH) + 1) + "-";
		output += String.format("%02d", dateDiscovered.get(GregorianCalendar.DAY_OF_MONTH)) + " ";
		output += String.format("%02d", dateDiscovered.get(GregorianCalendar.HOUR)) + ":";
		output += String.format("%02d", dateDiscovered.get(GregorianCalendar.MINUTE)) + ":";
		output += String.format("%02d", dateDiscovered.get(GregorianCalendar.SECOND));
		return output;
	}
	
}
