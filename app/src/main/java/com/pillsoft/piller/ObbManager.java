/*
This file is part of Piller.
Piller is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
Piller is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with Piller. If not, see <http://www.gnu.org/licenses/>.
Copyright 2015, Giulio Fagioli, Lorenzo Salani
*/
package com.pillsoft.piller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ObbManager {
    private String LOGTAG = "ObbManager";

    private List<String> fileNameQueue = new ArrayList<>();
    private List<File> fileQueue = new ArrayList<>();

    private String packageName;
    private int packageVersion = 1;
    private String mainPath;
    private File mainFile;
    private String patchPath;
    private File patchFile;
    private String key;
    private String obbPath;
    private StorageManager storageManager;
    MountChecker mainChecker;

    public boolean isReadyToGetFile() {
        return readyToGetFile;
    }

    public void setReadyToGetFile(boolean readyToGetFile) {
        this.readyToGetFile = readyToGetFile;
    }

    private boolean readyToGetFile = false;

    private static ObbManager instance;

    private ObbManager(Context context, String key) {
        //get package name
        packageName = context.getPackageName();
        Log.d(LOGTAG, "Package name = " + packageName);
        //get package version
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageVersion = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //define the storage manager to manage the .obb file
        storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        obbPath = Environment.getExternalStorageDirectory() + "/Android/obb";
        mainFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "main." + packageVersion + "." + packageName + ".obb");
        patchFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "patch." + packageVersion + "." + packageName + ".obb");
        this.key = key;
    }

    public boolean mountPatch(OnObbStateChangeListener patchObbStateChangeListener) {
        if (storageManager.isObbMounted(patchFile.getAbsolutePath())) {
            Log.d(LOGTAG, "Patch file already mounted.");
            patchPath = storageManager.getMountedObbPath(patchFile.getAbsolutePath());
            return true;
        } else {
            if (patchFile.exists()) {
                mainChecker = new MountChecker(true,patchObbStateChangeListener);
                (new Timer()).schedule(mainChecker, 8000);
                storageManager.mountObb(patchFile.getAbsolutePath(), key, patchObbStateChangeListener);
                return true;
            } else {
                Log.d(LOGTAG, "Patch file not found");
                return false;
            }
        }
    }

    public boolean mountMain(OnObbStateChangeListener mainObbStateChangeListener) {
        if (storageManager.isObbMounted(mainFile.getAbsolutePath())) {
            Log.d(LOGTAG, "Main file already mounted.");
            mainPath = storageManager.getMountedObbPath(mainFile.getAbsolutePath());
        } else {
            if (mainFile.exists()) {
                mainChecker = new MountChecker(true,mainObbStateChangeListener);
                (new Timer()).schedule(mainChecker, 8000);
                storageManager.mountObb(mainFile.getAbsolutePath(), key, mainObbStateChangeListener);
            } else {
                Log.d(LOGTAG, "Main file not found");
                return false;
            }
        }

        return true;
    }
    public boolean unMountMain(boolean force, OnObbStateChangeListener mainObbStateChangeListener) {
        if (storageManager.isObbMounted(mainFile.getAbsolutePath())) {
            storageManager.unmountObb(mainFile.getAbsolutePath(), force, mainObbStateChangeListener);
            mainChecker.cancel();
        } else {
            return false;
        }
        return true;
    }
    public boolean unMountPatch(boolean force, OnObbStateChangeListener patchObbStateChangeListener) {
        if (storageManager.isObbMounted(patchFile.getAbsolutePath())) {
            storageManager.unmountObb(patchFile.getAbsolutePath(), force, patchObbStateChangeListener);
            mainChecker.cancel();
        } else {
            return false;
        }
        return true;
    }

    public List<String> getMainFiles(){
        List<String> l = new ArrayList();
        File folder = new File(mainPath);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {

            } else {
                if (fileEntry.getName().endsWith(".apk")) l.add(fileEntry.getName().toLowerCase());
            }
        }
        return l;
    }

    public String getMainPath() {
        return mainPath;
    }

    public void setPathFromMainFile() {
        mainPath = storageManager.getMountedObbPath(mainFile.getAbsolutePath());
    }

    public void setPathFromPatchFile() {
        patchPath = storageManager.getMountedObbPath(patchFile.getAbsolutePath());
    }

    public String getPatchPath() {
        return patchPath;
    }

    public static ObbManager createNewInstance(Context context, String key) {
        instance = new ObbManager(context, key);
        return instance;
    }

    public static ObbManager getInstance() {
        return instance;
    }

    public File getFile(String pathToFile) {
        pathToFile = pathToFile.toLowerCase();
        if (mainPath!=null||patchPath!=null) {
            if (!pathToFile.startsWith(File.separator)) {
                pathToFile = File.separator + pathToFile;
            }
            File f = getFileFromPatch(pathToFile);
            if (f != null) if (f.exists()) return f;
            f = getFileFromMain(pathToFile);
            if (f != null) if (f.exists()) return f;
        }else{
            fileNameQueue.add(pathToFile);
            Log.d(LOGTAG,"added" + pathToFile);
        }
        return null;
    }

    public File getFileFromMain(String pathToFile) {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(mainPath + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public File getFileFromPatch(String pathToFile) {
        if (!pathToFile.startsWith(File.separator)) {
            pathToFile = File.separator + pathToFile;
        }
        File file = new File(patchPath + pathToFile);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private void populateFileQueue(){
        if(fileNameQueue.size()>0){
            Log.d(LOGTAG, "populate");
            for (String path : fileNameQueue){
                fileQueue.add(getFile(path));
                System.out.println(path);
            }
            fileNameQueue.clear();
            readyToGetFile = true;
        }
    }

    public List<File> getFileQueue(){
        if (isReadyToGetFile()){
            return fileQueue;
        }
        return null;
    }

    //Class required if the event OnObbStateChange is not called
    private class MountChecker extends TimerTask {
        private boolean isMainFile;
        private OnObbStateChangeListener listener;
        public MountChecker(boolean isMainFile, OnObbStateChangeListener listener) {
            this.isMainFile = isMainFile;
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.d(LOGTAG, "MountChecker: Check if " + (isMainFile ? "main" : "patch") + " file mounted without calling callback: " +
                    storageManager.isObbMounted(mainFile.getAbsolutePath()));
            File file = isMainFile ? mainFile : patchFile;
            if (storageManager != null && file != null && storageManager.isObbMounted(file.getAbsolutePath())) {
                if (isMainFile) {
                    mainPath = storageManager.getMountedObbPath(file.getAbsolutePath());
                    listener.onObbStateChange(mainPath,1);
                } else {
                    patchPath = storageManager.getMountedObbPath(file.getAbsolutePath());
                    listener.onObbStateChange(patchPath,1);
                }
            } else {
                mainChecker = new MountChecker(isMainFile,listener);
                (new Timer()).schedule(mainChecker, 1000);
            }
        }
    }
}
