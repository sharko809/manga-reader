package com.example.mangareader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

public class PictureActivity extends FragmentActivity {

	ImageView picToDisp;
	int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picture_activity);

		Intent intent = getIntent();
		final String path = intent.getStringExtra("path");
		final String[] fileList = intent.getStringArrayExtra("fileList");
		setImageBitmap(path);

		final View mDecorView = this.getWindow().getDecorView();

		mDecorView.setClickable(true);
		final GestureDetector clickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
				if (visible) {
					hideSystemUI();
				} else {
					showSystemUI();
				}
				return true;
			}
			@Override
			public void onLongPress(MotionEvent e) {
				boolean visible = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
				if (visible) {
					hideSystemUI();
				} else {
					showSystemUI();
				}
			}
			
		});
		
		mDecorView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return clickDetector.onTouchEvent(motionEvent);
			}
		});
		mDecorView.setOnTouchListener(new OnSwipeTouchListener(this) {
			// public void onSwipeTop() {
			// Toast.makeText(PictureActivity.this, "top",
			// Toast.LENGTH_SHORT).show();
			// }

			public void onSwipeRight() {
				Toast.makeText(PictureActivity.this, "right", Toast.LENGTH_SHORT).show();
				previousImage(fileList, path);
			}

			public void onSwipeLeft() {
				Toast.makeText(PictureActivity.this, "left", Toast.LENGTH_SHORT).show();
				nextImage(fileList, path);
			}

			// public void onSwipeBottom() {
			// Toast.makeText(PictureActivity.this, "bottom",
			// Toast.LENGTH_SHORT).show();
			// }

		});

	}

	private int startPosition(String[] list, String file) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(file)) {
				Log.d("Match found", list[i]);
				Log.d("Position:", i + "");
				// position = i;
			}
		}
		return position;
	}

	private void nextImage(String[] list, String file) {
		if (startPosition(list, file) + 1 <= list.length) {
			setImageBitmap(list[startPosition(list, file) + 1]);
			position += 1;
		} else {
			Toast.makeText(PictureActivity.this, "Reached end", Toast.LENGTH_SHORT).show();
		}
	}

	private void previousImage(String[] list, String file) {
		if (startPosition(list, file) - 1 >= 0) {
			setImageBitmap(list[startPosition(list, file) - 1]);
			position -= 1;
		} else {
			Toast.makeText(PictureActivity.this, "Reached start", Toast.LENGTH_SHORT).show();
		}
	}

	private void setImageBitmap(String path) {
		Bitmap bitmap = null;
		picToDisp = (ImageView) findViewById(R.id.pictureToDisplay);
		bitmap = BitmapFactory.decodeFile(path);
		Log.d("Path to img file", path.toString() + " " + bitmap);
		picToDisp.setImageBitmap(bitmap);
	}

	private void hideSystemUI() {
		this.getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE
						| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	private void showSystemUI() {
		this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

}
