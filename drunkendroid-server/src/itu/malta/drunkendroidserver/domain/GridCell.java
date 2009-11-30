package itu.malta.drunkendroidserver.domain;

/**
 * class to represent a gridCell in the moodMap array.
 *
 */

public class GridCell{


	
	private double longitude, latitude;
	private int value, count;
	
	// used when xStream unmarshaling
	private boolean unmarshaled = false;

	/**
	 * Constructor
	 * @param longitude value in the center of the GridCell.
	 * @param latitude value in the center of the GridCell.
	 */
	public GridCell(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * Method to add a reading value to the GridCell
	 * @param value
	 */
	public void addValue(int value){
		if (unmarshaled == false) {
			this.count = this.count +1;
			this.value = this.value + value;
		} else {
			count = 1;
			this.value = value;
			unmarshaled = false;
		}
	}
	
	/**
	 * get the avarage value of readings within the GridCell.
	 * @return integer from 0-255.
	 */
	public int getAverage() {
		return (this.value/this.count);
	}
	public double getLongitude(){
		return this.longitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	
	/**
	 * used for xStreem unmarshaling
	 * @param val the value set in the gridCell
	 */
	public void setValue(int val) {
		unmarshaled = true;
		this.count = 1;
		this.value = val;
	}
}
