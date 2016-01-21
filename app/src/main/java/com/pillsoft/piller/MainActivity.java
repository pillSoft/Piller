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

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.storage.OnObbStateChangeListener;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.pillsoft.piller.Adapter.MainViewCardAdapter;
import com.pillsoft.piller.Fragment.HomeFragment;
import com.pillsoft.piller.Fragment.OnFragmentInteractionListener;
import com.pillsoft.piller.Fragment.SettingsFragment;
import com.pillsoft.piller.Fragment.ThemeFragment;
import com.pillsoft.piller.downloader.DownloaderServiceX;
import com.pillsoft.piller.downloader.XAPKFile;
import com.pillsoft.piller.xml.XmlAdviseParser;
import com.pillsoft.piller.xml.XmlChangelogParser;
import com.pillsoft.piller.xml.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IDownloaderClient, ActivityCompat.OnRequestPermissionsResultCallback, OnFragmentInteractionListener {

    //region

    public static final String key = "MySecretKeyDontshare";

    private final XAPKFile[] xAPKS = {// variable that identify the .obb file downloaded
            new XAPKFile(
                    true, // is a main file?
                    BuildConfig.VERSION_CODE, // the version of the obb file, must be equal to the application version
                    35848250 // the length of the .obb file in bytes
            )
    };

    //endregion

    public static Context context;
    public static MainActivity mainActivity;
    final int PERMISSION_INTERNET = 1;
    final int PERMISSION_WIFI = 2;
    final int PERMISSION_NETWORK = 3;
    final int PERMISSION_READEXTERNAL = 4;
    final int PERMISSION_WRITEEXTERNAL = 5;
    final int PERMISSION_WAKELOCK = 6;
    final int PERMISSION_MULTIPLE = 7;
    final String TAG_PERMISSION = "PERMISSION";
    private final String nameFile = "Themes.xml";
    private final String LOGTAG = "MainActivity";
    private final String xmlSaveTag = "XmlThemes";
    private final String xmlChangeLogSaveTag = "XmlChangelogs";
    private HomeFragment homeFragment;
    private ThemeFragment cmFragment = null;
    private ThemeFragment rroFragment = null;
    private SettingsFragment settingsFragment = null;
    private Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;

    Shared s = Shared.getIt();
    boolean appLimitedByDownload = false;
    ObbManager om;
    ProgressDialog mProgressDialog;
    private DrawerLayout drawerLayout;
    private View content;
    private RecyclerView mRecyclerView;
    private MainViewCardAdapter mAdapter;
    private IDownloaderService mRemoteService;
    private IStub mDownloaderClientStub;
    private int mState;
    private XmlParser parser = new XmlParser();

    @Override
    protected void onStart() {
        if (null != mDownloaderClientStub) {
            mDownloaderClientStub.connect(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (null != mDownloaderClientStub) {
            mDownloaderClientStub.disconnect(this);
        }
        super.onStop();
    }

    @Override
    public void onServiceConnected(Messenger m) {
        mRemoteService = DownloaderServiceMarshaller.CreateProxy(m);
        mRemoteService.onClientUpdated(mDownloaderClientStub.getMessenger());
    }

    @Override
    public void onDownloadStateChanged(int newState) {
        setState(newState);
        switch (newState) {
            case IDownloaderClient.STATE_IDLE:
                // STATE_IDLE means the service is listening, so it's
                // safe to start making calls via mRemoteService.
                break;
            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                break;
            case IDownloaderClient.STATE_DOWNLOADING:
                break;
            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
                break;
            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                break;
            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
                break;
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                break;
            case IDownloaderClient.STATE_COMPLETED:
                isDownloaded();//called at the end of the download
                return;
            default:
        }
    }

    @Override
    public void onDownloadProgress(DownloadProgressInfo progress) {
    }


    private void isDownloaded() {
        om.mountMain(new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                if (state == MOUNTED) {
                    om.setPathFromMainFile();
                    List<String> namelist = om.getMainFiles();
                    parser = new XmlParser();
                    s.checkThemeLists(namelist);
                    om.unMountMain(false, new OnObbStateChangeListener() {
                        @Override
                        public void onObbStateChange(String path, int state) {
                            super.onObbStateChange(path, state);
                            if (state == UNMOUNTED) {
                            } else {
                                Log.e(LOGTAG, getString(R.string.cant_mount_obb) + "state");
                            }
                        }
                    });
                    appLimitedByDownload = false;
                } else {
                    Log.e(LOGTAG, "Mounting main file failed with state = " + state);
                }
            }
        });
    }

    private void startObbManager() {
        om = ObbManager.createNewInstance(this, key);
        mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, DownloaderServiceX.class);
        if (!expansionFilesDelivered()) {
            appLimitedByDownload = true;
            try {
                Intent launchIntent = this.getIntent();
                Intent intentToLaunchThisActivityFromNotification = new Intent(this, this.getClass());
                intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentToLaunchThisActivityFromNotification.setAction(launchIntent.getAction());
                if (launchIntent.getCategories() != null) {
                    for (String category : launchIntent.getCategories()) {
                        intentToLaunchThisActivityFromNotification.addCategory(category);
                    }
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT);
                // Request to start the download
                int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, DownloaderServiceX.class);
                switch (startResult) {
                    case DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED:
                        Log.i(LOGTAG, getString(R.string.no_required_download));
                        break;
                    case DownloaderClientMarshaller.LVL_CHECK_REQUIRED:
                        Log.i(LOGTAG, getString(R.string.lvl_required));
                        break;
                    case DownloaderClientMarshaller.DOWNLOAD_REQUIRED:
                        Log.i(LOGTAG, getString(R.string.required_download));
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOGTAG, getString(R.string.cant_find_package), e);
            }

        } else {
            isDownloaded();
        }
    }

    private boolean saveXmlThemes(String xml) {
        SharedPreferences shared = getSharedPreferences(s.themesPrefs, MODE_PRIVATE);
        if (parser.parseThemesString(xml) == null) {
            if (shared.getString(xmlSaveTag, null) == null) {
                Toast.makeText(this, getString(R.string.cant_load_res), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.cant_load_new_res), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        shared.edit().putString(xmlSaveTag, xml).apply();
        //Log.i(LOGTAG, shared.getString(xmlSaveTag, null));
        Toast.makeText(this, getString(R.string.success_load_res), Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean saveXmlThemesAsString() {
        InputStream input;
        String xml = "";
        try {
            input = getAssets().open("Themes.xml");
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                xml = new String(data, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return saveXmlThemes(xml);
    }

    private boolean readXmlFromString() {
        SharedPreferences shared = getSharedPreferences(s.themesPrefs, MODE_PRIVATE);
        String xml = shared.getString(xmlSaveTag, null);
        if (xml != null) {
            List<Theme> themes = parser.parseThemesString(xml);
            if (themes != null) {
                s.setThemeList(themes);
                return true;
            }
        }
        return false;
    }

    boolean expansionFilesDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
            if (!Helpers.doesFileExist(this, fileName, xf.mFileSize, false))
                return false;
        }
        return true;
    }

    private void setState(int newState) {
        if (mState != newState) {
            mState = newState;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        if (Build.VERSION.SDK_INT >= 23) {
            Shared.marsh = true;
            requestMultiplePermission();
        }
        context = this.getApplicationContext();
        s.context = context;
        initToolbar();
        setupDrawerLayout();
        content = findViewById(R.id.content);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        saveXmlThemesAsString();
        readXmlFromString();
        getChangelogs();
        getThemeAdvise();

        homeFragment = HomeFragment.newInstance(mainActivity);
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.twice_back_click, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.home_item);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                String selectedItem = menuItem.getTitle().toString();
                switch (menuItem.getItemId()) {
                    case R.id.home_item:
                        if (homeFragment == null)
                            homeFragment = HomeFragment.newInstance(mainActivity);
                        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
                        break;
                    case R.id.cm_item:
                        if (!appLimitedByDownload) {
                            if (cmFragment == null)
                                cmFragment = ThemeFragment.newInstance(mainActivity, s.CMTAG);
                            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, cmFragment).commit();
                        } else {
                            Snackbar.make(content, "File non ancora scaricati",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.rro_item:
                        if (!appLimitedByDownload) {
                            if (rroFragment == null)
                                rroFragment = ThemeFragment.newInstance(mainActivity, s.RROTAG);
                            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, rroFragment).commit();
                        } else {
                            Snackbar.make(content, "File non ancora scaricati",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        Snackbar.make(content, getString(R.string.no_rro_theme),
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.about_item:
                        if (settingsFragment == null)
                            settingsFragment = SettingsFragment.newInstance(mainActivity);
                        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, settingsFragment).commit();
                        break;
                }
                toolbar.setTitle(selectedItem);
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getChangelogs() {
        s.mainCardViewList.clear();
        XmlChangelogParser parser = new XmlChangelogParser();
        AssetManager am = getApplicationContext().getAssets();
        s.changelogList = parser.parseChangelogString(am);
        Collections.sort(s.changelogList);
        if (s.changelogList != null) {
            if (s.changelogList.size() != 0) {
                Changelog c = s.changelogList.get(0);
                MainViewCard mainViewCard = new MainViewCard(getString(R.string.changelogs_card_mainactivity), c.getDescription().toString().replaceAll("\\\\n", "\n"), "change");
                s.mainCardViewList.add(0, mainViewCard);
                return true;
            } else {
                MainViewCard mainViewCard = new MainViewCard(getString(R.string.changelogs_card_mainactivity), getString(R.string.no_changelogs), "report");
                s.mainCardViewList.add(0, mainViewCard);
            }
        }
        return false;
    }

    private boolean saveChangelogs(String xml) {
        SharedPreferences shared = getSharedPreferences(s.themesPrefs, MODE_PRIVATE);
        if (parser.parseThemesString(xml) == null) {
            if (shared.getString(xmlChangeLogSaveTag, null) == null) {
                Toast.makeText(this, getString(R.string.cant_load_res), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.cant_load_new_res), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        shared.edit().putString(xmlChangeLogSaveTag, xml).apply();
        Toast.makeText(this, getString(R.string.success_load_res), Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean getThemeAdvise() {
        XmlAdviseParser parser = new XmlAdviseParser();
        AssetManager am = getApplicationContext().getAssets();
        List<MainViewCard> adviseList = parser.parseChangelogString(am);
        if (adviseList != null) {
            for (MainViewCard card : adviseList) {
                s.mainCardViewList.add(card);
            }
        }
        return false;
    }

    //region Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_INTERNET) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "Internet Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "Internet Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_WIFI) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "WIFI Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "WIFI Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_NETWORK) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "NETWORK Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "NETWORK Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_READEXTERNAL) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "READ EXTERNAL Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "READ EXTERNAL Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_WRITEEXTERNAL) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "WRITE EXTERNAL Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "WRITE EXTERNAL Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_WAKELOCK) {
            if (Shared.verifyPermissions(grantResults)) {
                Snackbar.make(content, "Wakelock Permission granted",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(content, "Wakelock Permission NOT granted",
                        Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_MULTIPLE) {
            Log.d(TAG_PERMISSION, "Received response for MULTIPLE permission request.");
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "Permission Granted: " + permissions[i]);
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Log.d("Permissions", "Permission Denied: " + permissions[i]);
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        startObbManager();
    }

    public void requestMultiplePermission() {
        Log.d(TAG_PERMISSION,
                "Multiple request");
        String internetPermission = Manifest.permission.INTERNET;
        String networkPermission = Manifest.permission.ACCESS_NETWORK_STATE;
        String wifiPermission = Manifest.permission.ACCESS_WIFI_STATE;
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String wakePermission = Manifest.permission.WAKE_LOCK;

        int hasInternetPermission = checkSelfPermission(Manifest.permission.INTERNET);
        int hasNetworkPermission = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        int hasWifiPermission = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
        int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasWakePermission = checkSelfPermission(Manifest.permission.WAKE_LOCK);

        List<String> permissions = new ArrayList<String>();
        if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(internetPermission);
        }
        if (hasNetworkPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(networkPermission);
        }
        if (hasWifiPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(wifiPermission);
        }
        if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readPermission);
        }
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(writePermission);
        }
        if (hasWakePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(wakePermission);
        }
        if (!permissions.isEmpty()) {
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, PERMISSION_MULTIPLE);
        } else {
            startObbManager();
            // We already have permission, so handle as normal
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onClick_card(View v) {
        Log.d("Setting", "Cliccato");
        startBrowserIntent(v.getTag().toString());
    }

    public static void startBrowserIntent(String package_app) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(package_app));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.context.startActivity(browserIntent);
    }


}
