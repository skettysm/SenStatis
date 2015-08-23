/***
 * This source file handles the layout of the file list which was stored in the sd card to record the sensor raw data
 * 
 */

package app.android.senstatis;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.android.senstatis.R;
import app.android.senstatis.R.id;
import app.android.senstatis.R.layout;
import app.android.senstatis.R.menu;

public class FilesActivity extends ListActivity {
//	private static final String TAG = "at.feichtinger.sensorlogger.activities.FilesActivity";

	/* ********************************************************************
	 * Fields
	 */
	private File[] allFiles;

	/* ********************************************************************
	 * UI stuff
	 */

	/** The button which will start the send process. */
	private Button sendButton;

	/** The button to delete all selected files. */
	private Button deleteButton;

	/** The list view of this activity. */
	private ListView listView;

	/**
	 * Listener for send files button. Gets a list of selected files and passes
	 * them to the upload service.
	

	/**
	 * The listener for the delete files button. Displays an alert box to make
	 * sure the user really wants to delete the files. If he does, then all
	 * selected files will be deleted.
	 */
	private final OnClickListener deleteButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			// show an alert box if the user really wants to delete the files
			// //TODO use string resources...
			new AlertDialog.Builder(FilesActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Sure?")
					.setMessage("Are you sure you want to delete the selected files?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							deleteSelectedFiles();
						}
					}).setNegativeButton("No", null).show();
		}

	};

	/** The list adapter. */
	private class ActivitiesAdapter extends ArrayAdapter<File> {

		public ActivitiesAdapter(final Context context, final File[] objects) {
			super(context, R.layout.file_list_item, objects);
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			if (convertView == null) {
				final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.file_list_item, null);
			}

			final File f = getItem(position);
			if (f != null) {
				// show the files name
				final TextView nameView = (TextView) convertView.findViewById(R.id.filelistitem_file_name);
				if (nameView != null) {
					nameView.setText(f.getName());
				}

				// show file size
				final TextView sizeView = (TextView) convertView.findViewById(R.id.filelistitem_file_size);
				if (sizeView != null) {
					sizeView.setText(readableFileSize(f.length()));
				}

				// show a the creation date
				final TextView dateView = (TextView) convertView.findViewById(R.id.filelistitem_file_date);
				if (dateView != null) {
					final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
					final String date = dateFormat.format(new Date(f.lastModified()));
					dateView.setText(date);
				}
			}
			return convertView;
		}
	}

	/* *****************************************************************************
	 * ConextMenu methods
	 */

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.filesactivity_listview_contextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.contexmenu_open:
			openFile(allFiles[(int) info.id]);
			break;
		case R.id.contextmenu_delete:
			deleteFile(allFiles[(int) info.id]);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	/* *******************************************************************
	 * Activity life-cycle methods
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filesactivity);

		// init ui elements
		//sendButton = (Button) findViewById(R.id.files_sendSelectedFilesButton);
		//sendButton.setOnClickListener(sendButtonClickListener);

		deleteButton = (Button) findViewById(R.id.files_deleteSelectedFilesButton);
		deleteButton.setOnClickListener(deleteButtonClickListener);

		listView = getListView();
		listView.setItemsCanFocus(false);

		// activate the context menu for this list
		registerForContextMenu(listView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateFilesList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* *****************************************************************************
	 * Private helper methods
	 */

	private void openFile(final File file) {
		final Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		final Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "text/plain");
		startActivity(intent);
	}

	private void deleteFile(final File file) {
		file.delete();
		updateFilesList();
	}

	private void deleteSelectedFiles() {
		final int len = listView.getCount();
		for (int i = 0; i < len; i++) {
			final SparseBooleanArray checked = listView.getCheckedItemPositions();
			if (checked.get(i)) {
				allFiles[i].delete();
			}
		}
		updateFilesList();
	}

	private void updateFilesList() {
		
		 File RootFile = new File(Environment.getExternalStorageDirectory()
	                + File.separator + "SensorLoggerData");
		// LOG_DIRECTORY + getFilePathUniqueIdentifier()
		
		allFiles = RootFile.listFiles();
		setListAdapter(new ActivitiesAdapter(this, allFiles));
	}

	public String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}