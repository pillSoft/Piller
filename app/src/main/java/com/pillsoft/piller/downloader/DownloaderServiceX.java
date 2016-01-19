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
package com.pillsoft.piller.downloader;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;


public class DownloaderServiceX extends DownloaderService {

    //todo add your key
    public static final String BASE64_PUBLIC_KEY = "Your rsa key , is provided by google on developer console";
    private static final byte[] SALT = new byte[]{1, 22, -20, 25, -5, 51, -2, -24, 74, 5, -87, -112, 62, 15, -10, -102, -17, 31, -44, 75};

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return DownloaderServiceBroadcastReceiver.class.getName();
    }
}
