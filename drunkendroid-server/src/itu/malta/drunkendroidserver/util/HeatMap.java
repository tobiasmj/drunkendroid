package itu.malta.drunkendroidserver.util;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

public class HeatMap {

	private static HeatMap _instance = null;
	private BufferedImage _image;
	private int _zoomLevel;
	private int _width;
	private int _height;
	
	public static HeatMap getInstance()
	{
		if(_instance == null) _instance = new HeatMap();
		return _instance;
	}
	
	public void setWidth(int width) {
		_width = width;
	}
	public void setHeight(int height) {
		_height = height;
	}
	public void setZoomLevel(int zoomLevel) {
		_zoomLevel = zoomLevel;
	}
	
	private int getRadius(int mood)
	{
		switch(_zoomLevel)
		{
			case 19:
				return (128 * mood) / 255;
			case 18:
				return (64 * mood) / 255;
			case 17:
				return (32 * mood) / 255;
			default:
				return (16 * mood) / 255;
		}
	}
	
	/**
	 * Draw a semi-transparent white circle on the canvas
	 * @param canvas
	 * @param projection
	 * @param mp
	 * @return a canvas with the circle drawn upon it
	 */
    public BufferedImage drawCircle(int mood, int x, int y)
    {
		int radius = getRadius(mood);
		if(radius < 1) radius = 1;
		
		Graphics2D g = (Graphics2D) _image.getGraphics();
		
		RadialGradientPaint gradient = new RadialGradientPaint( radius, radius, radius, 
				new float[] { 0f, 1f }, 
				new Color[] { Color.BLACK, new Color(0xffffffff, true) });
		
		g.setPaint(gradient);
		//g.setComposite(BlendComposite.Multiply.derive(alpha));
		g.fillRect(0, 0, _width, _height);
		
		g.drawImage(_image, null, x - radius, y - radius);
		g.dispose();

		return _image;
    }
}
