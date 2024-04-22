package com.example.grid_image_view.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Environment.isExternalStorageRemovable
import android.util.Log
import android.util.LruCache
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CacheUtils {

    private lateinit var cacheDir: File

    //memory cache
    private lateinit var memoryCache: LruCache<String, Bitmap>

    //Disk cache
    private val DISK_CACHE_SUBDIR = "image-thumbnails"



    fun create(activity: Activity) {
        /* Memory Cache */
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(506250000) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount
            }
        }

        /* Disk Cache */
        //initialise disk cache
        cacheDir = getDiskCacheDir(activity, DISK_CACHE_SUBDIR)
    }

    /* Memory Cache get Image method */
    private fun getImageFromMemory(fileName: String): Bitmap? {
        return memoryCache.get(fileName)
    }

    /* Memory Cache add Image to memory method */
    private fun addImageToMemory(fileName: String, bitmap: Bitmap?) {
        if (memoryCache.get(fileName) == null && bitmap != null) {
            memoryCache.put(fileName, bitmap)
        }
    }

    /* Disk Cache - create directory in local storage */
    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !isExternalStorageRemovable()
            ) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }

        val mediaStorageDir = File(cachePath + File.separator + uniqueName)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return mediaStorageDir
            }
        }

        return mediaStorageDir
    }

    /* Disk Cache save Image method */
    private fun saveImageToStorage(image: Bitmap?, fileName: String) {
        val pictureFile: File = getOutputMediaFile(fileName)
        if (pictureFile == null) {
            Log.d(
                "Image Save",
                "Error creating media file, check storage permissions: "
            ) // e.getMessage());
            return
        }
        try {
            val fos = FileOutputStream(pictureFile)
            image?.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
            Log.e("Test1234", "save image to disk is success")
        } catch (e: FileNotFoundException) {
            Log.d("Image Save", "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d("Image Save", "Error accessing file: " + e.message)
        }
    }

    /* Creating file in specified location for image */
    private fun getOutputMediaFile(fileName: String): File {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaFile: File = File(cacheDir.path + File.separator + fileName)
        return mediaFile
    }

    /* Disk cache get image method */
    private fun getImageFromStorage(fileName: String) : Bitmap? {
        try {
            val f: File = File(cacheDir.path, fileName)
            return BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /* Get image either from disk or memory cache */
    fun getImageSavedInDiskOrMemory(fileName: String) : Bitmap? {
        //fetching from memory cache
        val memoryImage = getImageFromMemory(fileName)
        if(memoryImage != null) {
            Log.e("Test1234", "get image from memory is success")
            return memoryImage
        }
        Log.e("Test1234", "get image from memory is empty")
        //fetching from disk cache if image is not in memory cache
        val storedImage = getImageFromStorage(fileName)
        if(storedImage != null) {
            Log.e("Test1234", "get image from disk is success")
            // image not in memory cache, adding it
            addImageToMemory(fileName, storedImage)
        }
        return storedImage
    }

    /* save image in disk and memory cache */
    fun saveImageInDiskAndMemory(fileName: String, bitmap: Bitmap?) {
        val memoryImage = getImageFromMemory(fileName)
        // additional condition to check whether it exist or not
        if(memoryImage == null) {
            Log.e("Test1234", "save image to memory is success")
            addImageToMemory(fileName, bitmap)
        }

        saveImageToStorage(bitmap, fileName)
    }
}