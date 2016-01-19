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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.pillsoft.piller.Adapter.CustomPagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Shared {


    private static Shared instance = null;
    public final String themesPrefs = "ThemesPreference";
    private final String LOGTAG = "SHARED_LOG";
    public static final String CMTAG = "Cm";
    public static final String RROTAG = "Rro";
    public static boolean marsh = false;
    public Map<String, int[]> resourcesList = new HashMap<>();
    public List<Theme> themeList;
    public List<Changelog> changelogList;
    public List<MainViewCard> mainCardViewList = new ArrayList<>();
    public Context context;
    private List<Theme> cmThemeList = new ArrayList<>();
    private List<Theme> rroThemeList = new ArrayList<>();
    public CustomPagerAdapter customPagerAdapter;

    public static List<WeakReference<ImageView>> galleryList = new ArrayList<>();

    public final int galleryImageWidth = 1132;
    public final int galleryImageHeight = 1920;
    public final int headerImageWidth = 1598;
    public final int headerImageHeight = 1052;

    private Shared() {
    }

    public static synchronized Shared getIt() {
        if (instance == null) instance = new Shared();
        return instance;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        Log.d("PERMISSION", "Richiesta di verifica per ->" + grantResults);
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean     isIstalled(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    public static int getApkVersion(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public void setThemeList(List<Theme> themes) {
        themeList = themes;
        checkThemes();

        populateResources();
        populateThemeLists();
    }

    private void checkThemes(){
        Map<String,Integer> m = new HashMap<>();
        for (Theme t: themeList){
            if (m.containsKey(t.getTheme_package()))
                m.put(t.getTheme_package(), m.get(t.getTheme_package())+1);
            else
                m.put(t.getTheme_package(), +1);
        }
        Collection<Integer> s = m.values();
        while(s.contains(1)){
            s.remove(1);
        }
        System.out.println(s.toString());
        if (!s.isEmpty()){
            Log.e(LOGTAG, "Duplicate package found");
        }

    }

    public void checkThemeLists(List<String> namelist) {
        for (int i = 0; i < themeList.size(); i++) {
            String t = themeList.get(i).getTheme_name();
            if (!namelist.contains(t.toLowerCase() + ".apk")) {
                themeList.remove(themeList.get(i));
            }
        }
    }

    private void populateResources() {
        resourcesList.clear();
        for (Theme theme : themeList) {
            String name = theme.getTheme_name().toLowerCase();
            int i = -1;
            String num = "";
            List<Integer> list = new ArrayList<>();
            int pos = 0;
            int drawableId;
            do {
                if (i == -1) num = "";
                else num = "" + i;
                drawableId = context.getResources().getIdentifier(name + num, "drawable", context.getPackageName());
                Log.i("", "" + drawableId);
                if (drawableId != 0) {
                    list.add(drawableId);
                }
                i++;
            } while (i < 2 || drawableId != 0);
            int[] res = new int[list.size()];
            for (Integer in : list) {
                int n = in.intValue();
                res[pos] = in;
                pos++;
            }
            resourcesList.put(theme.getTheme_name(), res);
        }
    }

    public int[] getResources(String themeName) {
        return resourcesList.get(themeName);
    }

    public void populateThemeLists() {
        cmThemeList.clear();
        rroThemeList.clear();
        for (Theme t : themeList) {
            switch (t.getTheme_type()) {
                case CMTAG:
                    cmThemeList.add(t);
                    break;
                case RROTAG:
                    rroThemeList.add(t);
                    break;
                default:
                    Log.e(LOGTAG,"Unknown theme type insert");
            }
        }
    }

    public List<Theme> getThemesByType(String type) {
        switch (type) {
            case CMTAG:
                return cmThemeList;
            case RROTAG:
                return rroThemeList;
            default:
                return null;
        }
    }

    public Theme getThemeByPackage(String packageName) {
        for (Theme t : themeList){
            if (t.getTheme_package().equals(packageName)) return t;
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}



