package org.hanenoshino.onscripter.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.hanenoshino.onscripter.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LauncherActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

	// Launcher contributed by katane-san

	private File mCurrentDirectory = null;
	private ListView listView = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mCurrentDirectory = new File(
				Environment.getExternalStorageDirectory() + "/ons"
				);

		if (!mCurrentDirectory.exists()){
			mCurrentDirectory = Environment.getExternalStorageDirectory();

			if (!mCurrentDirectory.exists()) {

				new AlertDialog.Builder(this)
				.setTitle(getString(R.string.warning))
				.setMessage(getString(R.string.no_sd_card))
				.setPositiveButton(getString(R.string.known), 
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton) {
						// Nothing to do
					}
				})
				.setNegativeButton(getString(R.string.quit), 
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})
				.create()
				.show();

				mCurrentDirectory = new File("/");
			}

		}

		setContentView(R.layout.launcher);

		listView = (ListView) findViewById(R.id.games);

		refresh();

	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		TextView textView = (TextView) v;

		if (textView.getText().equals("..")){
			mCurrentDirectory = new File(mCurrentDirectory.getParent());
		} else {
			File dir = new File(mCurrentDirectory, textView.getText().toString());
			if(validate(dir)){
				run(dir, "");
				return;
			}
			mCurrentDirectory = dir;
		}

		refresh();
	}

	@Override
	public boolean onItemLongClick(
			AdapterView<?> parent, View v, int position, long id) {
		final TextView textView = (TextView) v;
		final File dir = new File(mCurrentDirectory, textView.getText().toString());
		
		if(validate(dir)){
			
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.information))
			.setMessage(getString(R.string.create_shortcut, textView.getText().toString()))
			.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton) {
					shortcut(textView.getText().toString(), dir.getAbsolutePath(), "");
				}
			})
			.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton) {
					// Nothing to do
				}
			})
			.create()
			.show();
			
			return true;
		}
		return false;
	}

	private void refresh() {

		File[] mDirectoryFiles = getFileList(mCurrentDirectory);

		int length = mDirectoryFiles.length;
		if (mCurrentDirectory.getParent() != null) length++;
		String [] names = new String[length];

		int j=0;
		if (mCurrentDirectory.getParent() != null) names[j++] = "..";
		for (int i=0 ; i<mDirectoryFiles.length ; i++){
			names[j++] = mDirectoryFiles[i].getName();
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

		listView.setAdapter(arrayAdapter);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

	}

	// Utility Functions

	private File[] getFileList(File path){

		File[] mDirectoryFiles = path.listFiles(
				new FileFilter() {

					public boolean accept(File file) {
						return (!file.isHidden() && file.isDirectory());
					}

				});

		Arrays.sort(mDirectoryFiles, new Comparator<File>(){

			public int compare(File src, File target){
				return src.getName().compareTo(target.getName());
			}

		});

		return mDirectoryFiles;

	}

	private boolean validate(File path){

		File[] mDirectoryFiles = path.listFiles(new FileFilter() {

			public boolean accept(File file) {
				return (file.isFile() && 
						(file.getName().equals("0.txt") ||
								file.getName().equals("00.txt") ||
								file.getName().equals("nscr_sec.dat") ||
								file.getName().equals("nscript.___") ||
								file.getName().equals("nscript.dat")));
			}

		});

		if (mDirectoryFiles.length == 0){
			// No script data is found.
			return false;
		}else{

			mDirectoryFiles = path.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return (file.isFile() && 
							(file.getName().equals("default.ttf")));
				}
			});

			if (mDirectoryFiles.length == 0){

				new AlertDialog.Builder(this)
				.setTitle(getString(R.string.error))
				.setMessage(getString(R.string.no_font))
				.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton) {
						// Nothing to do
					}
				})
				.create()
				.show();

				return false;
			}else{
				return true;
			}
		}

	}
	
	public void shortcut(String name, String path, String lang) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcut.putExtra("duplicate", false);
		ComponentName comp = new ComponentName(
				getPackageName(), "." + this.getLocalClassName());
		shortcut.putExtra(
				Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));
		Intent shortcutIntent = new Intent(Intent.ACTION_RUN);
		shortcutIntent.setClass(this, ONScripterActivity.class);
		shortcutIntent.putExtra(ONScripterActivity.EXTRA_GAME_PATH, path);
		shortcutIntent.putExtra(ONScripterActivity.EXTRA_GAME_LANG, lang);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		String iconPath = path + "/ICON.PNG";
		Drawable d = Drawable.createFromPath(iconPath);
		if (d != null) {
//			TODO shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, Bitmap);
		} else {
			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(this, R.drawable.icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		}
		sendBroadcast(shortcut);
	}

	public void run(File gameDir, String lang){
		Intent intent = new Intent(this, ONScripterActivity.class);
		intent.putExtra(ONScripterActivity.EXTRA_GAME_PATH, gameDir.getAbsolutePath());
		intent.putExtra(ONScripterActivity.EXTRA_GAME_LANG, lang);
		startActivity(intent);
		finish();
	}

}
