package com.sciencesquad.health.data;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupHelper;
import android.app.backup.FileBackupHelper;
import io.realm.Realm;

/**
 * Backup agent to back up all the realm files.
 */
public class RealmBackupAgent extends BackupAgentHelper {
    private static final String TAG = RealmBackupAgent.class.getSimpleName();

    private final String realmList = Realm.getDefaultInstance().getPath() + ", nutrition.realm";

    @Override
    public void onCreate() {
        super.onCreate();
        BackupHelper helper = new FileBackupHelper(this, realmList);
        addHelper("realms", helper);
    }
}
