package itu.malta.drunkendroidserver.util;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

public class HeatMap {

	private BufferedImage _image;
	private int _zoomLevel;
	private int _width;
	private int _height;
	
	private void Heatmap(int width, int height, int zoomLevel) {
		_width = width;
		_height = height;
		_zoomLevel = zoomLevel;
		_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
				new Color[] { Color.BLACK, new Color(0,0,0,0)});
		
		g.setPaint(gradient);
		//g.setComposite(BlendComposite.Multiply.derive(alpha));
		g.fillRect(0, 0, _width, _height);
		
		g.drawImage(_image, null, x - radius, y - radius);
		g.dispose();

		return _image;
    }
}
