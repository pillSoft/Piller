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

public class XAPKFile {
    public final boolean mIsMain;
    public final int mFileVersion;
    public final long mFileSize;

    public XAPKFile(boolean isMain, int fileVersion, long fileSize) {
        mIsMain = isMain;
        mFileVersion = fileVersion;
        mFileSize = fileSize;
    }
}
