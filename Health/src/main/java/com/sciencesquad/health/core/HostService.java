package com.sciencesquad.health.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * The HostService is a binding always-available backgrounded wrapper
 * for a set of Modules, to prevent the re-initialization of any Module.
 */
public class HostService extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
