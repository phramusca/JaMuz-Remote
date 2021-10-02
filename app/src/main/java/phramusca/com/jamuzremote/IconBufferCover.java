 /*
  * Copyright (C) 2021 phramusca ( https://github.com/phramusca/JaMuz/ )
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */

 package phramusca.com.jamuzremote;

 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.util.Log;

 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;

 /**
  *
  * @author phramusca ( https://github.com/phramusca/JaMuz/ )
  */

//FIXME Use this instead of coverMap in ActivityMain, made for remote (Warning: make sure coverHash are thee same)
 public class IconBufferCover {
     private static final String TAG = IconBufferCover.class.getName();

     enum IconSize {
         COVER(500),
         THUMB(120);

         private final int size;

         IconSize(int size) {
             this.size = size;
         }
     }

     /**
      * Get cover icon from cache
      * @param track The track.
      * @return The cover icon.
      */
     public static Bitmap getCoverIcon(Track track, IconSize size, boolean readIfNotFound) {
         Bitmap icon;
         icon = readIconFromCache(track.getCoverHash(), size);
         if(icon != null) {
             return icon;
         }
         if(readIfNotFound) {
             Bitmap trackCover = track.readCover();
             Bitmap cover = writeIconToCache(track.getCoverHash(), IconSize.COVER, trackCover);
             Bitmap thumb = writeIconToCache(track.getCoverHash(), IconSize.THUMB, cover);
             if (size == IconSize.COVER) {
                 icon = cover;
             } else if (size == IconSize.THUMB) {
                 icon = thumb;
             }
         }
         return icon;
     }

     //TODO: Does this really need to be synchronized ?
     private synchronized static Bitmap writeIconToCache(String coverHash, IconSize iconSize, Bitmap cover) {
         Bitmap icon = null;
         if(cover != null) {
             //FIXME: Keep ratio for IconSize.COVER !
             icon = Bitmap.createScaledBitmap(cover, iconSize.size, iconSize.size, false);
             if(icon != null) {
                 try {
                     File cacheFile = getCacheFile(coverHash, iconSize);
                     FileOutputStream fOut = new FileOutputStream(cacheFile);
                     icon.compress(Bitmap.CompressFormat.PNG, 90, fOut);
                     fOut.flush();
                     fOut.close();
                 } catch (IOException e) {
                     Log.e(TAG, "Error writing thumb to cache", e);
                     e.printStackTrace();
                 }
             }
         }
         return icon;
     }

     //TODO: Offer at least a cache cleanup function (better would be a smart auto cleanup)
     //Until then, can delete cache folder (or only audio)
     private static Bitmap readIconFromCache(String coverHash, IconSize iconSize) {
         File file = getCacheFile(coverHash, iconSize);
         if(file.exists()) {
             return BitmapFactory.decodeFile(file.getAbsolutePath());
         }
         return null;
     }

     private static File getCacheFile(String coverHash, IconSize iconSize) {
         String filename = coverHash.equals("")?"NA":coverHash+ iconSize.name();
         return getFile(filename+".png", "..", "cache", "cover-icons");
     }

     //TODO: Move this to a generic class if to be used elsewhere
     private static File getFile(String filename, String... args) {
         File file = ActivityMain.getAppDataPath();
         for (String subFolder : args) {
             file = new File(file, subFolder);
             //noinspection ResultOfMethodCallIgnored
             file.mkdirs();
         }
         file = new File(file, filename);
         return file;
     }
 }
