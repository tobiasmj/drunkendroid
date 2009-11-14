package itu.malta.drunkendroidserver;

/**
 * private class to represent a gridCell in the moodMap matrice.
 * @author tobiasmj
 *
 */

public class GridCell{


	
	private double longitude, latitude;
	private int value, count;
	private boolean unmarshaled = false;
	
	public GridCell(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
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
