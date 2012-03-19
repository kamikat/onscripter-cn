package org.hanenoshino.onscripter.ui;

import org.hanenoshino.onscripter.core.ONScripter;

import android.app.Activity;

public class ONScripterView extends ONScripter{
	
	public ONScripterView(
			Activity context, 
			String currentDirectoryPath, boolean isRescaleDisabled) {
		super(context, currentDirectoryPath, isRescaleDisabled);
	}
	
}
