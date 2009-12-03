package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.widget.SeekBar;

public class MoodReadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moodreading);
		SeekBar seekbar = (SeekBar)findViewById(R.id.SeekBar01);
		Drawable d = getResources().getDrawable(R.drawable.seekbar_horizontal);
		ScaleDrawable sd = new ScaleDrawable(d, 0x07, 50, 50);
		seekbar.setProgressDrawable(sd);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
