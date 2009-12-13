package itu.dd.server.domain;

/**
 * class to represent a gridCell in the moodMap array.
 *
 */

public class GridCell{



	private double _longitude, _latitude;
	private int _value, _count;

	// used when xStream unmarshaling
	private boolean _unmarshaled = false;

	/**
	 * Constructor
	 * @param longitude value in the center of the GridCell.
	 * @param latitude value in the center of the GridCell.
	 */
	public GridCell(double longitude, double latitude) {
		this._longitude = longitude;
		this._latitude = latitude;
	}

	/**
	 * Method to add a mood value to the GridCell
	 * @param value
	 */
	public void addValue(int value){
		if (_unmarshaled == false) {
			this._count = this._count +1;
			this._value = this._value + value;
		} else {
			_count = 1;
			this._value = value;
			_unmarshaled = false;
		}
	}

	/**
	 * get the average value of mood readings within the GridCell.
	 * @return integer from 0-255.
	 */
	public long getAverage() {
		if(_count <= 2 ) {
			return Math.round((_value/_count)*0.2);
		} else if (_count <=10) {
			return Math.round((_value/_count)*0.3);
		} else if (_count <=25) {
			return Math.round((_value/_count)*0.5);
		} else if (_count <= 50) {
			return Math.round((_value/_count)*0.75);
		} else if (_count <= 100) {
			return Math.round((_value/_count)*0.9);
		}
		return _value/_count;
	}
	public double getLongitude(){
		return this._longitude;
	}
	public double getLatitude(){
		return this._latitude;
	}

	/**
	 * used for xStreem unmarshaling
	 * @param val the value set in the gridCell
	 */
	public void setValue(int val) {
		_unmarshaled = true;
		this._count = 1;
		this._value = val;
	}
}
