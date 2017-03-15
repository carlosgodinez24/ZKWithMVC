package com.qitcorp.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
	static Connection conn = null;
	
	/*
	 * The following method of connection is not recommended, by the way in which it works, but you can use any method of connection to DB.
	 * I used this connection method through JDBC to do it in a simpler way
	 * TRY TO NOT WRITE HARD CODE LIKE THE FOLLOWING METHOD...AS I SAID BEFORE, I DID THROUGH THIS WAY TO DO IN A SIMPLER AND FASTER WAY
    */
	public static Connection getConn(){
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zk_with_mvc", "root", "");
        }
        catch(SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        return conn;
    }
}
