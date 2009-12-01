package itu.malta.drunkendroidserver.domain;
/**
 * 
 * Class that represents a MoodMap.
 *
 */
public class MoodMap {

	long startReadingTime, endReadingTime;

	double ULlatitude, ULlongitude, LRlatitude, LRlongitude;

	double latMax, latMin;
	double longMax, longMin;
	
	double worldGridSize;
	
	public double getULlatitude() {
		return ULlatitude;
	}
	public double getULlongitude() {
		return ULlongitude;
	}
	public long getStartReadingTime() {
		return startReadingTime;
	}
	public long getEndReadingTime() {
		return endReadingTime;
	}
	public double getLatMax() {
		return latMax;
	}
	public double getLatMin() {
		return latMin;
	}
	public double getLongMax() {
		return longMax;
	}
	public double getLongMin() {
		return longMin;
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
}


