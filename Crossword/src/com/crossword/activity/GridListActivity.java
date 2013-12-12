/*
 * Copyright 2011 Alexis Lauper <alexis.lauper@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crossword.activity;

import java.io.File;
import java.util.Calendar;
import org.xml.sax.helpers.DefaultHandler;

import com.crossword.Crossword;
import com.crossword.CrosswordException;
import com.crossword.DownloadFilesInterface;
import com.crossword.DownloadFilesTask;
import com.crossword.R;
import com.crossword.SAXFileHandler;
import com.crossword.adapter.GridListAdapter;
import com.crossword.data.Grid;
import com.crossword.parser.GridParser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GridListActivity extends CrosswordParentActivity implements OnItemClickListener, OnItemLongClickListener, DownloadFilesInterface {

	private GridListAdapter	gridAdapter;
	private ListView		gridListView;
	private TextView 		gridListMessage;
	private Notification	notification;
	private boolean			refreshRequested;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gridlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
        	if (this.refreshRequested == false) {
        		this.refreshRequested = true;
        		this.downloadGrid();
        	}
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.gridlist);
	    this.initComponents();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// If gridlist is outdated, update and download new grid
		File gridListFile = new File(Crossword.GRIDLIST_LOCAL_PATH);
		long now = Calendar.getInstance().getTimeInMillis();
		long expire = gridListFile.lastModified() + Crossword.GRIDLIST_LIFE_TIME;
		if (gridListFile.exists() == false) {
			this.gridListMessage.setVisibility(View.VISIBLE);
	    	this.gridAdapter.clear();
			this.gridAdapter.notifyDataSetChanged();
			this.downloadGrid();
		} else if (now > expire) {
			this.downloadGrid();
		}
		else {
			this.readGridDirectory();
		}
	}

	private void initComponents()
	{
	    // Set listview
	    this.gridAdapter = new GridListAdapter(this);
	    this.gridListView = (ListView)findViewById(R.id.gridListView);
	    this.gridListView.setOnItemClickListener(this);
	    this.gridListView.setOnItemLongClickListener(this);
	    this.gridListView.setAdapter(this.gridAdapter);
	    this.gridListMessage = (TextView)findViewById(R.id.gridListMessage);
	}
		
	// Download grid list
	private void downloadGrid() {
		DownloadFilesTask task = new DownloadFilesTask(this, this);
		task.execute();
	}
		
	private void readGridDirectory()
	{
		// Read grids
	    try {
	    	File directoryToScan = new File(Crossword.GRID_DIRECTORY); 
	    	File files[] = directoryToScan.listFiles();
	    	
	    	this.gridAdapter.clear();
	    	for (File file: files) {
		    	GridParser parser = new GridParser();
		    	parser.setFileName(file.getName());
				SAXFileHandler.read((DefaultHandler)parser, file.getAbsolutePath());
				this.gridAdapter.addGrid(parser.getData());
	    	}
		} catch (CrosswordException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
	    }

	    // Add separator (a week ago, two week ago, a month ago)
	    this.gridAdapter.addSeparator(getString(R.string.today), 0);
	    this.gridAdapter.addSeparator(getString(R.string.one_day_ago), -1);
	    this.gridAdapter.addSeparator(getString(R.string.one_week_ago), -7);
	    this.gridAdapter.addSeparator(getString(R.string.one_month_ago), -31);
	    this.gridAdapter.addSeparator(getString(R.string.one_year_ago), -365);

	    // Sort gridlist by date
		this.gridAdapter.sort();
		
		// Notify adapter
		this.gridAdapter.notifyDataSetChanged();

		// Hide message
		this.gridListMessage.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> p, View v, int i, long l) {
		Grid grid = (Grid)this.gridAdapter.getItem(i);

		// Save grid name in preference (from 'last grid' on main menu)
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.edit().putString("last_grid", grid.getFileName()).commit();

		// Lauch grid
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("filename", grid.getFileName());
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		this.initComponents();
//		this.gridAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Grid grid = (Grid)this.gridAdapter.getItem(position);
		Intent intent = new Intent(this, GridActivity.class);
		intent.putExtra("grid", grid);
		startActivity(intent);
		return true;
	}

	@Override
	public void onDownloadTaskStarted()
	{
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.icon;
		CharSequence tickerText = getResources().getString(R.string.notification_update_grids);
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.notification_update_grids);
		CharSequence contentText = "Download grid list";
		Intent notificationIntent = new Intent(this, GridListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNotificationManager.notify(Crossword.NOTIFICATION_DOWNLOAD_ID, notification);
	}
	
	@Override
	public void onDownloadUpdateProgressStatus(String status) {
		if (notification != null) {
			notification.setLatestEventInfo(this,
					getResources().getString(R.string.notification_update_grids),
					status,
					notification.contentIntent);
			NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(Crossword.NOTIFICATION_DOWNLOAD_ID, notification);
		}
	}

	@Override
	public void onDownloadTaskCompleted(boolean completed, int progress, String errorMessage) {
		// Si une notification est en cours, l'efface
		if (notification != null) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			if (completed)
			{
				notification.tickerText = getResources().getString(R.string.notification_update_grids_complete);
				mNotificationManager.notify(Crossword.NOTIFICATION_DOWNLOAD_ID, notification);
			}
			notification = null;
			mNotificationManager.cancel(Crossword.NOTIFICATION_DOWNLOAD_ID);
		}
		
		// Read grids directory if succeed, display error message otherwise
		if (completed) {
			if (this.refreshRequested) {
	        	this.refreshRequested = false;
	        	String message = progress == 0 ?
	        			getResources().getString(R.string.grid_list_new, progress) :
	        				getResources().getString(R.string.grid_list_up_to_date);
	        	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
		this.readGridDirectory();
	}
}
