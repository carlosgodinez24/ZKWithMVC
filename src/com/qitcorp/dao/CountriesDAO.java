package com.qitcorp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.qitcorp.model.CountriesModel;
import com.qitcorp.resources.ConnectionDB;

public class CountriesDAO {
	/*
	 * Method to save information in the DB
	 * @param CountriesModel the object sent from the controller
	 * @return result = the result of the operation
	 * 
	 * */
	public boolean add(CountriesModel param) throws SQLException{
		boolean result = false;
		Connection conn = ConnectionDB.getConn();
		try{
			PreparedStatement cmd = conn.prepareStatement("INSERT INTO COUNTRIES(country_id, country_name) VALUES(null,?)");
			cmd.setString(1, param.getCountry_name());
			cmd.executeUpdate();
			cmd.close();
			result = true;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return result;
	}
	
	/*
	 * The methods addBatch() and executeBatch() servs to send an ArrayList (instead of an object) and execute a mass INSERT
	 * This way is to optimize the performance of the server
	 * */
	public boolean addFromExcel(List<CountriesModel> list) throws SQLException{
		boolean result = false;
		Connection conn = ConnectionDB.getConn();
		try{
			PreparedStatement cmd = conn.prepareStatement("INSERT INTO COUNTRIES(country_id, country_name) VALUES(null,?)");
			for(CountriesModel d : list){
				cmd.setString(1, d.getCountry_name());
				cmd.addBatch();
			}
			cmd.executeBatch(); 
	        cmd.close();
	        result = true;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return result;
	}
	
	/*
	 * Method to edit information of an specific register
	 * @param CountriesModel the object sent from the controller
	 * @return result = the result of the operation
	 * 
	 * */
	public boolean edit(CountriesModel param) throws SQLException{
		boolean result = false;
		Connection conn = ConnectionDB.getConn();
		try{
			PreparedStatement cmd = conn.prepareStatement("UPDATE COUNTRIES SET country_name= ? WHERE country_id = ?");
			cmd.setString(1, param.getCountry_name());
			cmd.setInt(2, param.getCountry_id());
			cmd.executeUpdate();
			cmd.close();
			result = true;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return result;
	}
	
	/*
	 * Method to delete information of an specific register
	 * @param CountriesModel the object sent from the controller
	 * @return result = the result of the operation
	 * 
	 * */
	public boolean delete(CountriesModel param) throws SQLException{
		boolean result = false;
		Connection conn = ConnectionDB.getConn();
		try{
			PreparedStatement cmd = conn.prepareStatement("DELETE FROM COUNTRIES WHERE country_id = ?");
			cmd.setInt(1, param.getCountry_id());
			cmd.executeUpdate();
			cmd.close();
			result = true;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		return result;
	}
	
	/*
	 * Method to consult ALL the information from the table COUNTRIES
	 * @return list A TreeMap with all the countries that will serve us TO AVOID DUPLICATES REGISTERS
	 * The HashMap implements the Map interface.
	 * HashMap is a collection of data with an identifier, the key and the value have a data type.
	 * HashMap is not an ordered collection, that's why we use TreeMap.
	 * TreeMap class implements Map interface similar to HashMap class.
	 * The main difference between them is that HashMap is an unordered collection while TreeMap is sorted in the ascending order of its keys. 
	 * 
	 * */
	public Map<Integer, String> findAll() throws SQLException{
		Map<Integer, String> list = new TreeMap<Integer, String>();
		Connection conn = ConnectionDB.getConn();
		try{
			//You can use also SELECT * FROM anyTable for the PreparedStatement
			PreparedStatement cmd = conn.prepareStatement("SELECT country_id, country_name FROM COUNTRIES ORDER BY country_id");
			ResultSet rs = cmd.executeQuery();
			while(rs.next()){
				list.put(rs.getInt(1), rs.getString(2));
			}
			cmd.close();
		}
		finally{
			conn.close();
		}
		return list;
	}
}
