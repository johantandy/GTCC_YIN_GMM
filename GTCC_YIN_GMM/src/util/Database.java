package util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;


public class Database {
	
	public void insertGtcc(String classy,double [] feature) {
		
		try {
			ObjectFeatureGtcc ofg = new ObjectFeatureGtcc();
			ofg.data = feature;
			ofg.classy = classy;
			FileOutputStream fileout = new FileOutputStream("data.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileout);
			out.writeObject(ofg);
			out.close();
			fileout.close();
			Connection conn=DB.getConnection();
			DB.insertobject("data.ser", conn,classy,"gtccAset");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
}
