package com.example.mangareader;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomListeners {

	OnClickListener openManga;

	public OnClickListener openManga(final Context context) {
		return openManga = new OnClickListener() {

			@Override
			public void onClick(View v) {
				new FileDialog(context).openFileDialog(new FileDialogDepends());
			}
		};
	}
	
	public class FileDialogDepends{
	    public FileDialogDepends refresh(){
	       refresh();
	       return this;
	    }
	 }

}

