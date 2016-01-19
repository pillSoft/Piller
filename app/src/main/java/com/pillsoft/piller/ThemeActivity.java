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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pillsoft.piller.Adapter.CustomPagerAdapter;
import com.pillsoft.piller.Adapter.ThemeAdapter;
import com.pillsoft.piller.Gallery;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.ObbManager;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;
import com.pillsoft.piller.Theme;
import com.squareup.picasso.Picasso;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ThemeActivity extends AppCompatActivity {

    private final String LOGTAG = "ThemeActivity";
    private final int INSTALLED = 0;
    private final int UPLOADABLE = 1;
    private final int NOTINSTALLED = 2;

    Shared s = Shared.getIt();
    ObbManager om;
    TextView card_theme_version;
    ImageButton installButton;
    FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private ThemeAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    Theme theme;
    Toolbar toolbar;
    ImageView header;
    String themePath = "";
    String themeFileName = "";
    boolean readyToInstall = false;
    boolean installAlreadyClicked = false;
    int themeState = -1;


    //resurces task
    int resources[];
    ArrayList<ImageView> photos;
    LinearLayout myGallery;
    int imageCounter = 0 ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delete(new File(themePath));
        Log.d(LOGTAG,"delete ondestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        String packageName = getIntent().getStringExtra("PackageName");
        theme = s.getThemeByPackage(packageName);

        PorterDuffColorFilter colorizer=new PorterDuffColorFilter(Color.parseColor(theme.getTheme_color()), PorterDuff.Mode.MULTIPLY);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.getBackground().setColorFilter(colorizer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(theme.getTheme_name());
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setContentScrimColor(Color.parseColor(theme.getTheme_color()));
        toolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(Color.parseColor(theme.getTheme_darkcolor()));
            window.setNavigationBarColor(Color.parseColor(theme.getTheme_color()));
        }
        setSupportActionBar(toolbar);

        TextView card_theme_motto = (TextView) findViewById(R.id.date);
        card_theme_motto.setText(theme.getTheme_motto());

        card_theme_version = (TextView) findViewById(R.id.version);

        themeFileName = theme.getTheme_name().toLowerCase() + ".apk";

        header = (ImageView) findViewById(R.id.header);
        theme.setTheme_name(theme.getTheme_name());
        int backgroundRes = getResources().getIdentifier(theme.getTheme_name().toLowerCase() + "_header", "drawable", getPackageName());
        if (backgroundRes == 0){
            //Bitmap bt = ((BitmapDrawable) getDrawable( R.drawable.no_resource_header)).getBitmap();
            //header.setImageBitmap(Bitmap.createScaledBitmap(bt, s.headerImageWidth, s.headerImageHeight, false));

            header.setImageBitmap(Bitmap.createScaledBitmap(s.decodeSampledBitmapFromResource(getResources(), R.drawable.no_resource_header, 200, 200), s.headerImageWidth, s.headerImageHeight, false));
            header.setColorFilter(colorizer);
        }else{
            //Bitmap bt = ((BitmapDrawable) getDrawable(backgroundRes)).getBitmap();
            //header.setImageBitmap(Bitmap.createScaledBitmap(bt, s.headerImageWidth, s.headerImageHeight, false));
            header.setImageBitmap(Bitmap.createScaledBitmap(s.decodeSampledBitmapFromResource(getResources(), backgroundRes, 500, 500), s.headerImageWidth, s.headerImageHeight, false));

            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        }

        om = ObbManager.createNewInstance(this, MainActivity.key);
        om.mountMain(new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                if (state == MOUNTED) {
                    om.setPathFromMainFile();

                    try (ApkParser apkParser = new ApkParser(om.getFile(theme.getTheme_name() + ".apk"))) {
                        ApkMeta apkMeta = apkParser.getApkMeta();
                        card_theme_version.setText(getString(R.string.version_card_themeactivity) + apkMeta.getVersionName() + " (" + apkMeta.getVersionCode() + ")");

                        if (Shared.isIstalled(apkMeta.getPackageName(), getApplicationContext())) {
                            fab.setImageDrawable(getDrawable(R.drawable.delete));
                            if (apkMeta.getVersionCode() > Shared.getApkVersion(apkMeta.getPackageName(), getApplicationContext())) {
                                Snackbar.make(coordinatorLayout, getString(R.string.update_snackbar_themeactivity), Snackbar.LENGTH_LONG)
                                        .show();
                                fab.setImageDrawable(getDrawable(R.drawable.upload));
                                themeState = UPLOADABLE;
                            } else {
                                themeState = INSTALLED;
                            }
                        } else {
                            fab.setImageDrawable(getDrawable(R.drawable.download));
                            themeState = NOTINSTALLED;
                        }
                    } catch (Exception e) {
                        Log.e(LOGTAG + "APK THEME", getString(R.string.error_parsing_apk) + e.getMessage());
                    }

                    CopyThemeTask copy = new CopyThemeTask();
                    copy.execute(om.getMainPath() + "/" + theme.getTheme_name().toLowerCase() + ".apk");

                } else {
                    Log.e(LOGTAG, getString(R.string.mounting_failed) + state);
                }
            }
        });


        CardView cardGallery = (CardView) findViewById(R.id.card_gallery);
        myGallery = (LinearLayout) findViewById(R.id.gallery_image);
        //card_theme_version.setText(getString(R.string.version_card_themeactivity)+apkMeta.getVersionName()+" ("+apkMeta.getVersionCode()+")");
        System.out.println(theme.getTheme_name());
        System.out.println(theme.getTheme_package());
        resources = s.getResources(theme.getTheme_name());

        if ((resources == null)||(resources.length == 0)){
            myGallery.setVisibility(View.GONE);
            cardGallery.setVisibility(View.GONE);

            Log.e(LOGTAG, theme.getTheme_name() + " images not found");
        }else {
            photos = new ArrayList<>();
            for (int i = 0; i < resources.length; i++) {
                ImageView im = new ImageView(this);
                im.setId(i);
                photos.add(im);

            }
        }

        BitmapWorkerTask b = new BitmapWorkerTask(photos.get(0));
        b.execute(resources[0]);


    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Integer> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
           // Drawable drawable = getDrawable(params[0]);
            //return ((BitmapDrawable) drawable).getBitmap();
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer iii) {
            if (imageViewReference != null && iii != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {

                    imageView.setImageBitmap(Bitmap.createScaledBitmap(s.decodeSampledBitmapFromResource(getResources(), iii, s.galleryImageWidth/3, s.galleryImageHeight/3), s.galleryImageWidth/2, s.galleryImageHeight/2, false));
                    //imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, s.galleryImageWidth / 2, s.galleryImageHeight / 2, false));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), Gallery.class);
                            intent.putExtra("themeName", theme.getTheme_name());
                            intent.putExtra("index", v.getId());
                            intent.putExtra("resources", resources);
                            startActivity(intent);
                        }
                    });
                    myGallery.addView(imageView);

                    imageCounter++;
                    if(imageCounter<resources.length-1){
                        BitmapWorkerTask t = new BitmapWorkerTask(photos.get(imageCounter));
                        t.execute(resources[imageCounter]);
                    }else {
                        if (readyToInstall) {
                            om.unMountMain(true, new OnObbStateChangeListener() {
                                @Override
                                public void onObbStateChange(String path, int state) {
                                    super.onObbStateChange(path, state);
                                    if (state == UNMOUNTED) {
                                        Log.d(LOGTAG, getString(R.string.unmounting_done));
                                    } else {
                                        Log.d(LOGTAG, "un" + getString(R.string.mounting_failed) + state);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public void onFabClick(View v) {
        switch (themeState) {
            case INSTALLED:
                uninstallTheme();
                break;
            case UPLOADABLE:
            case NOTINSTALLED:
                installTheme();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(readyToInstall) {
            try (ApkParser apkParser = new ApkParser(new File(themePath + themeFileName))) {
                ApkMeta apkMeta = apkParser.getApkMeta();
                card_theme_version.setText(getString(R.string.version_card_themeactivity) + apkMeta.getVersionName() + " (" + apkMeta.getVersionCode() + ")");

                if (Shared.isIstalled(apkMeta.getPackageName(), getApplicationContext())) {
                    fab.setImageDrawable(getDrawable(R.drawable.delete));
                    if (apkMeta.getVersionCode() > Shared.getApkVersion(apkMeta.getPackageName(), getApplicationContext())) {
                        Snackbar.make(coordinatorLayout, getString(R.string.update_snackbar_themeactivity), Snackbar.LENGTH_LONG)
                                .show();
                        fab.setImageDrawable(getDrawable(R.drawable.upload));
                        themeState = UPLOADABLE;
                    } else {
                        themeState = INSTALLED;
                    }
                } else {
                    fab.setImageDrawable(getDrawable(R.drawable.download));
                    themeState = NOTINSTALLED;
                }
            } catch (IOException e) {
                Log.e(LOGTAG + "APK THEME", getString(R.string.error_parsing_apk) + e.getMessage());
            }
        }
    }

    public void uninstallTheme() {
        Uri packageUri = Uri.parse("package:" + theme.getTheme_package());
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                packageUri);
        startActivity(uninstallIntent);
    }

    public void installTheme() {
        if (readyToInstall) {
            install();
        } else {
            installAlreadyClicked = true;
        }
    }

    private void install() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(themePath + themeFileName)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    void delete(File file) {

        if (file.isDirectory())
            for (File child : file.listFiles()) {
                File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
                child.renameTo(to);
                delete(child);
            }
        file.delete();  // delete child file or empty directory
    }

    private class CopyThemeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... filename) {
            String response = "";
            InputStream in;
            OutputStream out;
            try {
                themePath = Environment.getExternalStorageDirectory() + "/.Themes/";
                File wallpaperDirectory = new File(themePath);
                wallpaperDirectory.mkdirs();
                in = new FileInputStream(filename[0]);
                out = new FileOutputStream(themePath + themeFileName);
                copyFile(in, out);
                in.close();
                out.close();
            } catch (Exception e) {
                Log.e(LOGTAG, getString(R.string.failed_copy_asset), e);
            }
            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            readyToInstall = true;
            if (installAlreadyClicked) install();

          /*  BitmapWorkerTask t = new BitmapWorkerTask(photos.get(0));
            t.execute(resources[0]);*/
        }
    }

    //region Commented code
//
//
//Caricamento immagini tutte insieme
//            for (int i = 0; i < resources.length; i++) {
//                ImageView im = new ImageView(this);
//                photos.add(im);
//            }
//
//            BitmapWorkerTask task = new BitmapWorkerTask(photos);
//            task.execute(resources);*/
//
//    class BitmapWorkerTask extends AsyncTask<int[], Void, Bitmap[]> {
//        private final ArrayList<WeakReference<ImageView>> imageViewList = new ArrayList<>();
//
//
//        public BitmapWorkerTask(ArrayList<ImageView>  imageViews) {
//            // Use a WeakReference to ensure the ImageView can be garbage collected
//
//            for(ImageView im : imageViews){
//                WeakReference imageViewReference = new WeakReference<ImageView>(im);
//                imageViewList.add(imageViewReference);
//            }
//            System.out.println("Lunghezza " + imageViewList.size());
//        }
//
//        @Override
//        protected Bitmap[] doInBackground(int[]... params) {
//            Bitmap[] bitmaps = new Bitmap[params[0].length];
//            int j = 0;
//            for (int i = 0; i < bitmaps.length-3; i++) {
//                Drawable drawable = getDrawable(params[0][i]);
//                System.out.println(i);
//                bitmaps [j] = ((BitmapDrawable) drawable).getBitmap();
//                j++;
//            }
//            return bitmaps;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap[] bitmaps) {
//
//            for (int i = 0; i < imageViewList.size(); i++) {
//                ImageView im = imageViewList.get(i).get();
//                Bitmap bm = bitmaps[i];
//
//                if (im != null && bm != null) {
//                    im.setImageBitmap(Bitmap.createScaledBitmap(bm, galleryImageWidth / 2, galleryImageHeight / 2, false));
//                    myGallery.addView(im);
//                }
//
//            }
//
//        }
//
//
//
//
//    noinspection SimplifiableIfStatement
//    if (id == R.id.menu_rate) {
//        Rate("https://play.google.com/store/apps/details?id=" +getApplicationContext().getPackageName());
//        Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.rate_thanks), Toast.LENGTH_SHORT).show();
//    }
//    if (id == R.id.menu_share) {
//        Share("https://play.google.com/store/apps/details?id=" +getApplicationContext().getPackageName());
//    }
//    if (id == R.id.menu_developer) {
//        Link(this.getResources().getString(R.string.developer_site));
//    }
//    if (id == R.id.menu_mail) {
//        Mail(this.getResources().getString(R.string.app_name),this.getResources().getString(R.string.email_address));
//
//    }
//    if (id == R.id.community) {
//        Link(this.getResources().getString(R.string.community_link));
//
//    }
//
//    public void Share(String playStoreLink) {
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, playStoreLink);
//        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//    }
//
//    public void Rate(String playStoreLink) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
//        startActivity(browserIntent);
//    }
//
//    public void Mail(String themeName, String email) {
//        Intent mailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
//        mailIntent.putExtra(Intent.EXTRA_SUBJECT, themeName);
//        startActivity(mailIntent);
//    }
//
//    public void Link(String link) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
//        startActivity(browserIntent);
//    }
    //end region



}