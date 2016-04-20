package com.sciencesquad.health.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.sciencesquad.health.core.util.Dispatcher;

import java.util.Iterator;

/**
 * The HostService is a binding always-available backgrounded wrapper
 * for a set of Modules, to prevent the re-initialization of any Module.
 */
public class HostService extends Service {
	private static final String TAG = HostService.class.getSimpleName();

	/**
	 * @see Service
	 */
	@Override @Nullable
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * @see Service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/**
	 * Begins the Module lifecycle by instantiating any that were found in
	 * the Android Manifest meta-data tags.
	 *
	 * @see Service
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Dispatcher.UTILITY.run(() -> {

			// Parse the meta-data for the "modules" tag and retrieve
			// the list of strings it contains.
			String[] classes = new String[]{};
			try {
				ComponentName myService = new ComponentName(this, getClass());
				Bundle data = getPackageManager().getServiceInfo(myService, PackageManager.GET_META_DATA).metaData;
				classes = data.getString("modules", "").split("\\|");
			} catch(Exception e) {
				Log.e(TAG, "Could not parse meta-data.");
			}

			// If the class is prefixed with a . add the package to it.
			for (String name : classes) {
				if (name.startsWith("."))
					name = getPackageName() + name;

				// Lookup the Class for this string, ensuring it is a Module.
				Class clazz;
				try {
					clazz = Class.forName(name);
					if (!Module.class.isAssignableFrom(clazz))
						throw new RuntimeException();
				} catch(Exception e) {
					Log.e(TAG, name + " is not a Module!");
					continue;
				}

				// Register and start the Module lifecycle.
				// Invokes onStart in a different thread priority.
				Log.i(TAG, "Discovered Module: " + clazz.getName());
				Module m = Module.start(clazz);
				Dispatcher.DEFAULT.run(m::onStart);
			}
		});
	}

	/**
	 * Stops all activity and ceases any Modules.
	 *
	 * @see Service
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Dispatcher.UTILITY.run(() -> {

			// Iterate and remove all Modules when we're destroyed.
			// Invokes onStop in a different thread priority.
			for(Iterator<Module> i = Module._modules.iterator(); i.hasNext();) {
				Module m = i.next();
				Dispatcher.DEFAULT.run(m::onStop);
				i.remove();
			}
		});
	}
}
