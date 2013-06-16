package ca.uqac.info.monitor;

import java.sql.*;

public class OutilsJDBC {
	private static String request = "insert into mantis_bug_table (os,os_build,platform,version,fixed_in_version,build,summary,target_version)"+
			   " values(1,1,1,1,1,1,?,1)";
	public static Connection openConnection (String url, String driver, String user, String passwd){
		Connection co = null;
		try{		
			co = DriverManager.getConnection(url, user, passwd);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.out.println("can't connect to url : "+url);
			System.exit(1);
		}
		System.out.println("Connection established");
		return co;		
	}
	
	public static int execRequete(String brokenProperty, Connection co){
		int res = -1;
		try{
			PreparedStatement psm = co.prepareStatement(request);
			psm.setString(1, brokenProperty);
			res = psm.executeUpdate();			
		}
		catch(SQLException e){
			System.out.println("problem to execute the request : "+request+" res : "+res);
			e.printStackTrace();
			
		}		
		System.out.println("Request executed");
		return res;
	}
	
	public static void closeConnection(Connection co){
		try{
			co.close();
			System.out.println("Connection closed");
		}
		catch(SQLException e){
			System.out.println("can't close the connection");			
		}
	}
}
