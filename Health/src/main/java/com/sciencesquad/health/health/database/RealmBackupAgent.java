package com.sciencesquad.health.health.database;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupHelper;
import android.app.backup.FileBackupHelper;

import io.realm.Realm;

/**
 * Created by danielmiller on 2/18/16.
 *
 * Backup agent to back up all the realm files.
 */
public class RealmBackupAgent extends BackupAgentHelper {

    private final String realmList = Realm.getDefaultInstance().getPath() + ", nutrition.realm";

    @Override
    public void onCreate(){
        super.onCreate();
        BackupHelper helper = new FileBackupHelper(this, realmList);

        addHelper("realms", helper);
    }
}
