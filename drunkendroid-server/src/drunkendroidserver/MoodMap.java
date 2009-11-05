package drunkendroidserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import drunkendroidserver.Util.DatabaseConnection;

public class MoodMap {

	long startReadingTime, endReadingTime;
	double ULlatitude, ULlongitude, LRlatitude, LRlongitude;
	int gridX = 60 , gridY = 60;
	double latMax, latMin;
	double longMax, longMin;
	
	gridCell[][] moodMapGrid = new gridCell[gridX][gridY];
	/***
	 * 
	 * @param readingTime timeStamp of the requested moodmap 
	 * @param ULlatitude UpperLeftCorner latitude of the moodmap
	 * @param ULlongitude UpperLeftCorner longitude of the moodmap
	 * @param LRlatitude LowerRightCorner latitude of the moodmap
	 * @param LRlongitude LowerRightCorner longitude of the moodmap
	 */
	public MoodMap (long startReadingTime,long endReadingTime, double ULlatitude, double ULlongitude, double LRlatitude, double LRlongitude  ) {
		this.startReadingTime = startReadingTime;
		this.endReadingTime = endReadingTime;
		this.ULlatitude = ULlatitude;
		this.ULlongitude = ULlongitude;
		this.LRlatitude = LRlatitude;
		this.LRlongitude = LRlongitude;
		if(ULlatitude > LRlatitude) {
			latMax = ULlatitude;
			latMin = LRlatitude;
		} else {
			latMax = LRlatitude;
			latMin = ULlatitude;
		}
		if(ULlongitude > LRlongitude) {
			longMax = ULlongitude;
			longMin = LRlongitude;
		} else {
			longMax = LRlongitude;
			longMin = ULlongitude;
		}
	}
	
	public void execute() throws SQLException{

		double readingLong, readingLat;
		double width = longMax - longMin;
		double height = latMax - latMin;
		double gridWidth = width/gridX;
		double gridHeight = height/gridY;
		
		
		
		Connection conn = DatabaseConnection.getInstance().getConn();
		Statement stmt = null;
		ResultSet rs = null;
		try { 
			stmt = conn.createStatement();
			String query =  "select mood, longtitude, latitude from Reading where dateTime between " + startReadingTime + " and " + endReadingTime +
			" and longtitude between " + longMin + " and " + longMax + " and latitude between " + latMin + " and " + latMax;
			stmt.executeQuery(query);
			//stmt.executeQuery("select mood longtitude latitude from Reading where dateTime between " + startReadingTime + " and " + endReadingTime +
			//			" and longtitude between " + ULlongitude + " and " + LRlongitude + " and latitude between " + ULlatitude + " and " + LRlatitude);
			//stmt.executeUpdate("Insert into Reading(trip,dateTime,latitude,longtitude,mood) values (" + tripID + "," + readingTime+ "," + latitude + "," + longtitude + "," + mood +")");
			rs = stmt.getResultSet();
			int xCoord, yCoord;
			while(rs.next()) {
				readingLong = rs.getDouble("longtitude");
				readingLat = rs.getDouble("Latitude");
				xCoord = (int)((readingLong - longMin)/gridWidth) -1;
				yCoord = (int)((readingLat - latMin)/gridHeight) -1;
				if(moodMapGrid[xCoord][yCoord] == null) {
					moodMapGrid[xCoord][yCoord] = new gridCell((xCoord + 0.5)*gridWidth+ULlongitude,(yCoord + 0.5)*gridHeight+ULlatitude);
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				} else {
					moodMapGrid[xCoord][yCoord].addValue(rs.getInt("mood"));
				}
				
			}
			
			
			rs = null;
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	}
	
	public Representation getMoodMap() throws IOException{
		DomRepresentation result = null;
		
            result = new DomRepresentation(MediaType.TEXT_XML);  
            // Generate a DOM document representing the list of  
            // MoodMapReadings.  
            Document d = result.getDocument();  
  
            Element eltMoodMap = d.createElement("MoodMap");  
            d.appendChild(eltMoodMap);
            for(int i = 0; i < gridY; i++){
            	for(int j = 0; j < gridX; j++){
            		if (moodMapGrid[i][j] != null){
            			Element eltMoodMapReading = d.createElement("MoodMapReading");
            			
            			Element eltMoodMapReadingValue = d.createElement("MoodMapValue");
            			eltMoodMapReadingValue.appendChild(d.createTextNode(Integer.toString(moodMapGrid[i][j].getAvarage())));  
            			eltMoodMapReading.appendChild(eltMoodMapReadingValue);
            			
            			Element eltMoodMapLongitudeValue = d.createElement("MoodMapLongitude");
            			eltMoodMapLongitudeValue.appendChild(d.createTextNode(Double.toString(moodMapGrid[i][j].getLongitude())));  
            			eltMoodMapReading.appendChild(eltMoodMapLongitudeValue);
            			
            			Element eltMoodMapLatitudeValue = d.createElement("MoodMaplatitude");
            			eltMoodMapLatitudeValue.appendChild(d.createTextNode(Double.toString(moodMapGrid[i][j].getLatitude())));  
            			eltMoodMapReading.appendChild(eltMoodMapLatitudeValue);
            			
            			eltMoodMap.appendChild(eltMoodMapReading);  
            		}
            	}
            }
            
		return result;
	}
	
	private class gridCell {
		private double longitude, latitude;
		private int value, count;
		private gridCell(double longitude, double latitude) {
			this.longitude = longitude;
			this.latitude = latitude;
		}
		private void addValue(int value){
			this.count = this.count +1;
			this.value = this.value + value;
		}
		private int getAvarage() {
			return (this.value/this.count);
		}
		private double getLongitude(){
			return this.longitude;
		}
		private double getLatitude(){
			return this.latitude;
		}
	
	}

}
