/**
 * Copyright (C) 2011 The Serval Project
 *
 * This file is part of Serval Software (http://www.servalproject.org)
 *
 * Serval Software is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.servalproject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.servalproject.ServalBatPhoneApplication.State;
import org.servalproject.account.AccountService;
import org.servalproject.batphone.CallDirector;
import org.servalproject.rhizome.RhizomeMain;
import org.servalproject.servald.Identity;
import org.servalproject.servald.PeerListService;
import org.servalproject.servald.ServalD;
import org.servalproject.system.NetworkManager;
import org.servalproject.ui.Networks;
import org.servalproject.ui.ShareUsActivity;
import org.servalproject.ui.help.HtmlHelp;
import org.servalproject.wizard.Wizard;

/**
 *
 * Main activity which presents the Serval launcher style screen. On the first
 * time Serval is installed, this activity ensures that a warning dialog is
 * presented and the user is taken through the setup wizard. Once setup has been
 * confirmed the user is taken to the main screen.
 *
 * @author Paul Gardner-Stephen <paul@servalproject.org>
 * @author Andrew Bettison <andrew@servalproject.org>
 * @author Corey Wallis <corey@servalproject.org>
 * @author Jeremy Lakeman <jeremy@servalproject.org>
 * @author Romana Challans <romana@servalproject.org>
 */
public class Main extends Activity implements OnClickListener {
	public ServalBatPhoneApplication app;

	private static final String TAG = "Main";
	private TextView buttonToggle;
	private ImageView buttonToggleImg;
	private Drawable powerOnDrawable;
	private Drawable powerOffDrawable;

	private void openMaps() {
		// check to see if maps is installed
		try {
			PackageManager mManager = getPackageManager();
			mManager.getApplicationInfo("org.servalproject.maps",
					PackageManager.GET_META_DATA);

			Intent mIntent = mManager
					.getLaunchIntentForPackage("org.servalproject.maps");
			mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(mIntent);

		} catch (NameNotFoundException e) {
			startActivity(new Intent(getApplicationContext(),
					org.servalproject.ui.MapsActivity.class));
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
		case R.id.btncall:

			if (app.getState() != State.On ||
					!NetworkManager.getNetworkManager(app).isUsableNetworkConnected()) {
				app.displayToastMessage("You must enable services and connect to a network first");
				return;
			}
			if (!PeerListService.havePeers()) {
				app.displayToastMessage("There are no other phones on this network");
				return;
			}
			try {
				startActivity(new Intent(Intent.ACTION_DIAL));
				return;
			} catch (ActivityNotFoundException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			startActivity(new Intent(app, CallDirector.class));
			break;
		case R.id.messageLabel:
			if (!ServalD.isRhizomeEnabled()) {
				app.displayToastMessage("Messaging cannot function without an sdcard");
				return;
			}
			startActivity(new Intent(getApplicationContext(),
					org.servalproject.messages.MessagesListActivity.class));
			break;
		case R.id.mapsLabel:
			openMaps();
			break;
		case R.id.contactsLabel:
			startActivity(new Intent(getApplicationContext(),
					org.servalproject.ui.ContactsActivity.class));
			break;
		case R.id.settingsLabel:
			startActivity(new Intent(getApplicationContext(),
					org.servalproject.ui.SettingsScreenActivity.class));
			break;
		case R.id.sharingLabel:
			startActivity(new Intent(getApplicationContext(),
					RhizomeMain.class));
			break;
		case R.id.helpLabel:
			Intent intent = new Intent(getApplicationContext(),
					HtmlHelp.class);
			intent.putExtra("page", "helpindex.html");
			startActivity(intent);
			break;
		case R.id.servalLabel:
			startActivity(new Intent(getApplicationContext(),
					ShareUsActivity.class));
			break;
		case R.id.powerLabel:
			startActivity(new Intent(getApplicationContext(),
					Networks.class));
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.v("Leaf", "Main_onCreate");
		this.app = (ServalBatPhoneApplication) this.getApplication();
		setContentView(R.layout.main);

		// adjust the power button label on startup
		buttonToggle = (TextView) findViewById(R.id.btntoggle);
		buttonToggleImg = (ImageView) findViewById(R.id.powerLabel);
		buttonToggleImg.setOnClickListener(this);

		// load the power drawables
		powerOnDrawable = getResources().getDrawable(
				R.drawable.ic_launcher_power);
		powerOffDrawable = getResources().getDrawable(
				R.drawable.ic_launcher_power_off);

		int listenTo[] = {
				R.id.btncall,
				R.id.messageLabel,
				R.id.mapsLabel,
				R.id.contactsLabel,
				R.id.settingsLabel,
				R.id.sharingLabel,
				R.id.helpLabel,
				R.id.servalLabel,
			};
		for (int i = 0; i < listenTo.length; i++) {
			this.findViewById(listenTo[i]).setOnClickListener(this);
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int stateOrd = intent.getIntExtra(
					ServalBatPhoneApplication.EXTRA_STATE, 0);
			State state = State.values()[stateOrd];
			stateChanged(state);
		}
	};

	boolean registered = false;

	private void stateChanged(State state) {
		buttonToggle.setText(state.getResourceId());

		// change the image for the power button
		// TODO add other drawables for other state if req'd
		switch (state) {
		case On:
			// set the drawable to the power on image
			buttonToggleImg.setImageDrawable(powerOnDrawable);
			break;
		default:
			// for every other state use the power off drawable
			buttonToggleImg.setImageDrawable(powerOffDrawable);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		checkAppSetup();
	}

	/**
	 * Run initialisation procedures to setup everything after install. Called
	 * from onResume() and after agreeing Warning dialog
	 */
	private void checkAppSetup() {
		State state = app.getState();
		stateChanged(state);

		if (state == State.Installing || state == State.Upgrading) {
			// Construct an intent to start the install
			Intent i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.servalproject.org/donations"));

			Notification n = new Notification(R.drawable.ic_serval_logo,
					"The Serval Project needs your support",
					System.currentTimeMillis());

			n.setLatestEventInfo(
					this,
					"We need your support",
					"Serval depends on donations.",
					PendingIntent.getActivity(this, 0, i,
							PendingIntent.FLAG_ONE_SHOT));

			n.flags = Notification.FLAG_AUTO_CANCEL;

			NotificationManager nm = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify("Donate", ServalBatPhoneApplication.NOTIFY_DONATE, n);


			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... arg0) {
					app.installFiles();
					return null;
				}
			}.execute();

			if (state == State.Installing) {
				this.startActivity(new Intent(this, Wizard.class));
				finish();
				return;
			}
		}

		// Start by showing the preparation wizard
		// Intent prepintent = new Intent(this, PreparationWizard.class);
		// prepintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(prepintent);

		Identity main = Identity.getMainIdentity();
		if (main == null || AccountService.getAccount(this) == null
				|| main.getDid() == null) {
			Log.v("MAIN",
					"Keyring doesn't seem to be initialised, starting wizard");

			this.startActivity(new Intent(this, Wizard.class));
			finish();
			return;
		}

		if (!registered) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ServalBatPhoneApplication.ACTION_STATE);
			this.registerReceiver(receiver, filter);
			registered = true;
		}

		TextView pn = (TextView) this.findViewById(R.id.mainphonenumber);
		String id = "";

		if (main.getDid() != null)
			id = main.getDid();
		else
			id = main.subscriberId.abbreviation();

		pn.setText(id);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (registered) {
			this.unregisterReceiver(receiver);
			registered = false;
		}
	}
}
