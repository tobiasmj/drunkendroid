package itu.malta.drunkendroidserver.domain;
/**
 * 
 * Class that represents a MoodMap.
 *
 */
public class MoodMap {

	long _startReadingTime, _endReadingTime;

	double _ULlatitude, _ULlongitude, _LRlatitude, _LRlongitude;

	double _latMax, _latMin;
	double _longMax, _longMin;
	
	double _worldGridSize;
	
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
	public MoodMap (long startReadingTime,long endReadingTime, double ULlatitude, double ULlongitude, double LRlatitude, double LRlongitude  ) {
		this._startReadingTime = startReadingTime;
		this._endReadingTime = endReadingTime;
		this._ULlatitude = ULlatitude;
		this._ULlongitude = ULlongitude;
		this._LRlatitude = LRlatitude;
		this._LRlongitude = LRlongitude;
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


