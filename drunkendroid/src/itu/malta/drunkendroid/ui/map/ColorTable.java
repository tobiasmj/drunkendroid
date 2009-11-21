package itu.malta.drunkendroid.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;

public class ColorTable {
	private static int[] _lookupTable;
	
	public static int[] getColorTable()
	{
		if(_lookupTable == null) _lookupTable = createColorLookupTable(createGradientImage());
		return _lookupTable;
	}
	
	private static Bitmap createGradientImage()
	{
		Bitmap bmp = Bitmap.createBitmap(256, 1, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		int[] gradientColors = new int[]{Color.TRANSPARENT,
				 Color.argb(155, 35, 170, 227), // Light blue
				 Color.argb(155, 128, 223, 59), // Green
				 Color.argb(155, 255, 200, 0), // Yellow
				 Color.argb(155, 216, 15, 15)}; // Red
		LinearGradient gradient = new LinearGradient(0, 0, bmp.getWidth(), bmp.getHeight(), gradientColors, null, TileMode.CLAMP);
		paint.setShader(gradient);
		
		canvas.drawPaint(paint);

		return bmp;
	}
	
	private static int[] createColorLookupTable(Bitmap bmp)
	{
		int tableSize = 256;
		int[] colorTable = new int[tableSize];
		for (int i = 0; i < tableSize; ++i)
			colorTable[i] = bmp.getPixel(i, 0);

		return colorTable;
	}
}
