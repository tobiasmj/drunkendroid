package itu.malta.drunkendroidserver.domain;
/**
 * 
 * Class that represents a MoodMap.
 *
 */
public class MoodMap {

	private long _startReadingTime, _endReadingTime;

	private double _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude;

	private double _latMax, _latMin;
	private double _longMax, _longMin;
	
	private int _gridX;
	private int _gridY;
	
	public int getGridX() {
		return _gridX;
	}
	
	public int getGridY() {
		return _gridY;
	}

	public double getLRLongitude(){
		return _LRlongitude;
	}
	public double getLRlatitude() {
		return _LRlatitude;
	}
	
	public double getULlatitude() {
		return _ULlatitude;
	}
	public double getULlongitude() {
		return _ULlongitude;
	}
	public long getStartReadingTime() {
		return _startReadingTime;
	}
	public long getEndReadingTime() {
		return _endReadingTime;
	}
	public double getLatMax() {
		return _latMax;
	}
	public double getLatMin() {
		return _latMin;
	}
	public double getLongMax() {
		return _longMax;
	}
	public double getLongMin() {
		return _longMin;
	}
	/***
	 * Constructor.
	 * @param readingTime timeStamp of the requested MoodMap 
	 * @param ULlatitude UpperLeftCorner latitude of the MoodMap
	 * @param ULlongitude UpperLeftCorner longitude of the MoodMap
	 * @param LRlatitude LowerRightCorner latitude of the MoodMap
	 * @param LRlongitude LowerRightCorner longitude of the MoodMap
	 */
	public MoodMap (long startReadingTime,long endReadingTime, double ULlatitude, double ULlongitude, double LRlatitude, double LRlongitude, int gridX, int gridY) {
		_startReadingTime = startReadingTime;
		_endReadingTime = endReadingTime;
		_ULlatitude = ULlatitude;
		_ULlongitude = ULlongitude;
		_LRlatitude = LRlatitude;
		_LRlongitude = LRlongitude;
		_gridX = gridX;
		_gridY = gridY;
		if(ULlatitude > LRlatitude) {
			_latMax = ULlatitude;
			_latMin = LRlatitude;
		} else {
			_latMax = LRlatitude;
			_latMin = ULlatitude;
		}
		if(ULlongitude > LRlongitude) {
			_longMax = ULlongitude;
			_longMin = LRlongitude;
		} else {
			_longMax = LRlongitude;
			_longMin = ULlongitude;
		}
		
		
	}
	
}


