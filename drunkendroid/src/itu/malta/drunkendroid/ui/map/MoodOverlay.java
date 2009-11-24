package itu.malta.drunkendroid.ui.map;

import java.util.List;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.domain.ReadingEvent;
import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MoodOverlay extends Overlay
{
	private DataFacade _dataFacade;
    private HeatMap _heatmap = HeatMap.getInstance();;
    private MapView _mapView;
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			// Create a heatmap if map is null
			if(_mapView == null)
			{
				/*
				_dataFacade = new DataFacade(mapView.getContext());
				List<ReadingEvent> data = _dataFacade.getReadingEvents(
						Long.getLong("130773960"), 
						Long.getLong("131027900"), 
						35.923954, 
						14.49002, 
						Long.getLong("1000"));
				
				for(ReadingEvent event : data)
				{
					GeoPoint gp = new GeoPoint(event.latitude.intValue(), event.longitude.intValue());
					MoodMapPoint mp = new MoodMapPoint(gp, event.mood);
					_heatmap.addMoodMapPoint(mp);
				}
				*/
				int mood1 = 234; Double long1 = 14.487617755003868*1E6; Double lat1 = 35.9232054039299*1E6;
				int mood2 = 134; Double long2 = 14.487617755003868*1E6; Double lat2 = 35.92350484235794*1E6;
				int mood3 = 87; Double long3 = 14.487617755003868*1E6; Double lat3 = 35.92380428078598*1E6;
				int mood4 = 113; Double long4 = 14.487617755003868*1E6; Double lat4 = 35.924103719214024*1E6;
				int mood5 = 130; Double long5 = 14.48798752501975*1E6; Double lat5 = 35.9232054039299*1E6;
				int mood6 = 130; Double long6 = 14.48798752501975*1E6; Double lat6 = 35.92350484235794*1E6;
				int mood7 = 115; Double long7 = 14.48798752501975*1E6; Double lat7 = 35.92380428078598*1E6;
				int mood8 = 136; Double long8 = 14.48798752501975*1E6; Double lat8 = 35.924103719214024*1E6;
				int mood9 = 118; Double long9 = 14.488357295035632*1E6; Double lat9 = 35.9232054039299*1E6;
				int mood10 = 112; Double long10 = 14.488357295035632*1E6; Double lat10 = 35.92350484235794*1E6;
				int mood11 = 142; Double long11 = 14.488357295035632*1E6; Double lat11 = 35.92380428078598*1E6;
				int mood12 = 122; Double long12 = 14.488357295035632*1E6; Double lat12 = 35.924103719214024*1E6;
				int mood13 = 208; Double long13 = 14.488357295035632*1E6; Double lat13 = 35.92440315764206*1E6;
				int mood14 = 136; Double long14 = 14.488727065051513*1E6; Double lat14 = 35.9232054039299*1E6;
				int mood15 = 124; Double long15 = 14.488727065051513*1E6; Double lat15 = 35.92350484235794*1E6;
				int mood16 = 129; Double long16 = 14.488727065051513*1E6; Double lat16 = 35.92380428078598*1E6;
				int mood17 = 107; Double long17 = 14.488727065051513*1E6; Double lat17 = 35.924103719214024*1E6;
				int mood18 = 68; Double long18 = 14.488727065051513*1E6; Double lat18 = 35.92440315764206*1E6;
				int mood19 = 123; Double long19 = 14.489096835067395*1E6; Double lat19 = 35.9232054039299*1E6;
				int mood20 = 124; Double long20 = 14.489096835067395*1E6; Double lat20 = 35.92350484235794*1E6;
				int mood21 = 115; Double long21 = 14.489096835067395*1E6; Double lat21 = 35.92380428078598*1E6;
				int mood22 = 111; Double long22 = 14.489096835067395*1E6; Double lat22 = 35.924103719214024*1E6;
				int mood23 = 152; Double long23 = 14.489096835067395*1E6; Double lat23 = 35.92440315764206*1E6;
				int mood24 = 123; Double long24 = 14.489466605083276*1E6; Double lat24 = 35.9232054039299*1E6;
				int mood25 = 136; Double long25 = 14.489466605083276*1E6; Double lat25 = 35.92350484235794*1E6;
				int mood26 = 104; Double long26 = 14.489466605083276*1E6; Double lat26 = 35.92380428078598*1E6;
				int mood27 = 133; Double long27 = 14.489466605083276*1E6; Double lat27 = 35.924103719214024*1E6;
				int mood28 = 77; Double long28 = 14.489466605083276*1E6; Double lat28 = 35.92440315764206*1E6;
				int mood29 = 251; Double long29 = 14.489836375099157*1E6; Double lat29 = 35.92290596550186*1E6;
				int mood30 = 147; Double long30 = 14.489836375099157*1E6; Double lat30 = 35.9232054039299*1E6;
				int mood31 = 106; Double long31 = 14.489836375099157*1E6; Double lat31 = 35.92350484235794*1E6;
				int mood32 = 113; Double long32 = 14.489836375099157*1E6; Double lat32 = 35.92380428078598*1E6;
				int mood33 = 119; Double long33 = 14.489836375099157*1E6; Double lat33 = 35.924103719214024*1E6;
				int mood34 = 124; Double long34 = 14.490206145115039*1E6; Double lat34 = 35.9232054039299*1E6;
				int mood35 = 118; Double long35 = 14.490206145115039*1E6; Double lat35 = 35.92350484235794*1E6;
				int mood36 = 128; Double long36 = 14.490206145115039*1E6; Double lat36 = 35.92380428078598*1E6;
				int mood37 = 161; Double long37 = 14.490206145115039*1E6; Double lat37 = 35.924103719214024*1E6;
				
				GeoPoint gp1 = new GeoPoint(lat1.intValue(), long1.intValue());
				GeoPoint gp2 = new GeoPoint(lat2.intValue(), long2.intValue());
				GeoPoint gp3 = new GeoPoint(lat3.intValue(), long3.intValue());
				GeoPoint gp4 = new GeoPoint(lat4.intValue(), long4.intValue());
				GeoPoint gp5 = new GeoPoint(lat5.intValue(), long5.intValue());
				GeoPoint gp6 = new GeoPoint(lat6.intValue(), long6.intValue());
				GeoPoint gp7 = new GeoPoint(lat7.intValue(), long7.intValue());
				GeoPoint gp8 = new GeoPoint(lat8.intValue(), long8.intValue());
				GeoPoint gp9 = new GeoPoint(lat9.intValue(), long9.intValue());
				GeoPoint gp10 = new GeoPoint(lat10.intValue(), long10.intValue());
				GeoPoint gp11 = new GeoPoint(lat11.intValue(), long11.intValue());
				GeoPoint gp12 = new GeoPoint(lat12.intValue(), long12.intValue());
				GeoPoint gp13 = new GeoPoint(lat13.intValue(), long13.intValue());
				GeoPoint gp14 = new GeoPoint(lat14.intValue(), long14.intValue());
				GeoPoint gp15 = new GeoPoint(lat15.intValue(), long15.intValue());
				GeoPoint gp16 = new GeoPoint(lat16.intValue(), long16.intValue());
				GeoPoint gp17 = new GeoPoint(lat17.intValue(), long17.intValue());
				GeoPoint gp18 = new GeoPoint(lat18.intValue(), long18.intValue());
				GeoPoint gp19 = new GeoPoint(lat19.intValue(), long19.intValue());
				GeoPoint gp20 = new GeoPoint(lat20.intValue(), long20.intValue());
				GeoPoint gp21 = new GeoPoint(lat21.intValue(), long21.intValue());
				GeoPoint gp22 = new GeoPoint(lat22.intValue(), long22.intValue());
				GeoPoint gp23 = new GeoPoint(lat23.intValue(), long23.intValue());
				GeoPoint gp24 = new GeoPoint(lat24.intValue(), long24.intValue());
				GeoPoint gp25 = new GeoPoint(lat25.intValue(), long25.intValue());
				GeoPoint gp26 = new GeoPoint(lat26.intValue(), long26.intValue());
				GeoPoint gp27 = new GeoPoint(lat27.intValue(), long27.intValue());
				GeoPoint gp28 = new GeoPoint(lat28.intValue(), long28.intValue());
				GeoPoint gp29 = new GeoPoint(lat29.intValue(), long29.intValue());
				GeoPoint gp30 = new GeoPoint(lat30.intValue(), long30.intValue());
				GeoPoint gp31 = new GeoPoint(lat31.intValue(), long31.intValue());
				GeoPoint gp32 = new GeoPoint(lat32.intValue(), long32.intValue());
				GeoPoint gp33 = new GeoPoint(lat33.intValue(), long33.intValue());
				GeoPoint gp34 = new GeoPoint(lat34.intValue(), long34.intValue());
				GeoPoint gp35 = new GeoPoint(lat35.intValue(), long35.intValue());
				GeoPoint gp36 = new GeoPoint(lat36.intValue(), long36.intValue());
				GeoPoint gp37 = new GeoPoint(lat37.intValue(), long37.intValue());

		        MoodMapPoint mp1 = new MoodMapPoint(gp1, mood1);
		        MoodMapPoint mp2 = new MoodMapPoint(gp2, mood2);
		        MoodMapPoint mp3 = new MoodMapPoint(gp3, mood3);
		        MoodMapPoint mp4 = new MoodMapPoint(gp4, mood4);
		        MoodMapPoint mp5 = new MoodMapPoint(gp5, mood5);
		        MoodMapPoint mp6 = new MoodMapPoint(gp6, mood6);
		        MoodMapPoint mp7 = new MoodMapPoint(gp7, mood7);
		        MoodMapPoint mp8 = new MoodMapPoint(gp8, mood8);
		        MoodMapPoint mp9 = new MoodMapPoint(gp9, mood9);
		        MoodMapPoint mp10 = new MoodMapPoint(gp10, mood10);
		        MoodMapPoint mp11 = new MoodMapPoint(gp11, mood11);
		        MoodMapPoint mp12 = new MoodMapPoint(gp12, mood12);
		        MoodMapPoint mp13 = new MoodMapPoint(gp13, mood13);
		        MoodMapPoint mp14 = new MoodMapPoint(gp14, mood14);
		        MoodMapPoint mp15 = new MoodMapPoint(gp15, mood15);
		        MoodMapPoint mp16 = new MoodMapPoint(gp16, mood16);
		        MoodMapPoint mp17 = new MoodMapPoint(gp17, mood17);
		        MoodMapPoint mp18 = new MoodMapPoint(gp18, mood18);
		        MoodMapPoint mp19 = new MoodMapPoint(gp19, mood19);
		        MoodMapPoint mp20 = new MoodMapPoint(gp20, mood20);
		        MoodMapPoint mp21 = new MoodMapPoint(gp21, mood21);
		        MoodMapPoint mp22 = new MoodMapPoint(gp22, mood22);
		        MoodMapPoint mp23 = new MoodMapPoint(gp23, mood23);
		        MoodMapPoint mp24 = new MoodMapPoint(gp24, mood24);
		        MoodMapPoint mp25 = new MoodMapPoint(gp25, mood25);
		        MoodMapPoint mp26 = new MoodMapPoint(gp26, mood26);
		        MoodMapPoint mp27 = new MoodMapPoint(gp27, mood27);
		        MoodMapPoint mp28 = new MoodMapPoint(gp28, mood28);
		        MoodMapPoint mp29 = new MoodMapPoint(gp29, mood29);
		        MoodMapPoint mp30 = new MoodMapPoint(gp30, mood30);
		        MoodMapPoint mp31 = new MoodMapPoint(gp31, mood31);
		        MoodMapPoint mp32 = new MoodMapPoint(gp32, mood32);
		        MoodMapPoint mp33 = new MoodMapPoint(gp33, mood33);
		        MoodMapPoint mp34 = new MoodMapPoint(gp34, mood34);
		        MoodMapPoint mp35 = new MoodMapPoint(gp35, mood35);
		        MoodMapPoint mp36 = new MoodMapPoint(gp36, mood36);
		        MoodMapPoint mp37 = new MoodMapPoint(gp37, mood37);

				_heatmap.addMoodMapPoint(mp1);
				_heatmap.addMoodMapPoint(mp2);
				_heatmap.addMoodMapPoint(mp3);
				_heatmap.addMoodMapPoint(mp4);
				_heatmap.addMoodMapPoint(mp5);
				_heatmap.addMoodMapPoint(mp6);
				_heatmap.addMoodMapPoint(mp7);
				_heatmap.addMoodMapPoint(mp8);
				_heatmap.addMoodMapPoint(mp9);
				_heatmap.addMoodMapPoint(mp10);
				_heatmap.addMoodMapPoint(mp11);
				_heatmap.addMoodMapPoint(mp12);
				_heatmap.addMoodMapPoint(mp13);
				_heatmap.addMoodMapPoint(mp14);
				_heatmap.addMoodMapPoint(mp15);
				_heatmap.addMoodMapPoint(mp16);
				_heatmap.addMoodMapPoint(mp17);
				_heatmap.addMoodMapPoint(mp18);
				_heatmap.addMoodMapPoint(mp19);
				_heatmap.addMoodMapPoint(mp20);
				_heatmap.addMoodMapPoint(mp21);
				_heatmap.addMoodMapPoint(mp22);
				_heatmap.addMoodMapPoint(mp23);
				_heatmap.addMoodMapPoint(mp24);
				_heatmap.addMoodMapPoint(mp25);
				_heatmap.addMoodMapPoint(mp26);
				_heatmap.addMoodMapPoint(mp27);
				_heatmap.addMoodMapPoint(mp28);
				_heatmap.addMoodMapPoint(mp29);
				_heatmap.addMoodMapPoint(mp30);
				_heatmap.addMoodMapPoint(mp31);
				_heatmap.addMoodMapPoint(mp32);
				_heatmap.addMoodMapPoint(mp33);
				_heatmap.addMoodMapPoint(mp34);
				_heatmap.addMoodMapPoint(mp35);
				_heatmap.addMoodMapPoint(mp36);
				_heatmap.addMoodMapPoint(mp37);
		        
				
				_mapView = mapView;
				System.out.println("Creating heatmap");
				canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);
			}
			// Update heatmap if map has changed
			else //if(!_mapView.getMapCenter().equals(mapView.getMapCenter()))
			{
				System.out.println("Updating heatmap");
				_mapView = mapView;
				canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);	
			}
			// Reuse heatmap if map is unchanged
			/*
			else
			{
				System.out.println("Reusing heatmap - " + _mapView.getMapCenter() + " = " + mapView.getMapCenter());
				canvas.drawBitmap(_heatmap.getHeatmap(), 0, 0, null);
			}
			*/
		}
	}
}
