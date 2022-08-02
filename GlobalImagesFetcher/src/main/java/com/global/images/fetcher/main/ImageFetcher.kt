package com.global.images.fetcher.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.global.images.fetcher.models.Folder
import com.global.images.fetcher.models.FolderSortOrder
import com.global.images.fetcher.models.ImagesSortOrder
import java.io.File
import java.text.Collator

class ImageFetcher(context: Context) {

    private var mContext = context
    private var allImages: MutableList<File> = mutableListOf()
    private var isFetching = false

    fun getAllImages(order: ImagesSortOrder? = null, callback: (MutableList<File>?) -> Unit) {
        if (checkPermissionForReadExternalStorage() || globalPermissionCheck()) {

            val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED
            )
            try {
                mContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, null
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val file = File(cursor.getString(dataColumn))
                        if (file.length() > 100) {
                            allImages.add(file)
                        }
                    }
                }
            } catch (e: Exception) {
            } catch (e: java.lang.Exception) {
            } catch (e: java.lang.IllegalArgumentException) {
            } finally {
                if (allImages.isNotEmpty()) {
                    val collator = Collator.getInstance()
                    when (order) {
                        null -> {
                            callback(allImages)
                        }
                        ImagesSortOrder.NameAscending -> {
                            allImages = allImages.sortedWith { a1, a2 -> collator.compare(a1.nameWithoutExtension, a2.nameWithoutExtension) } as MutableList<File>
                            callback(allImages)
                        }
                        ImagesSortOrder.NameDescending -> {
                            allImages = allImages.sortedWith { a1, a2 -> collator.compare(a2.nameWithoutExtension, a1.nameWithoutExtension) } as MutableList<File>
                            callback(allImages)
                        }
                        ImagesSortOrder.SizeAscending -> {
                            allImages = allImages.sortedByDescending { it.length() } as MutableList<File>
                            callback(allImages)
                        }
                        ImagesSortOrder.SizeDescending -> {
                            allImages = allImages.sortedBy { it.length() } as MutableList<File>
                            callback(allImages)
                        }
                        ImagesSortOrder.LastModifiedAscending -> {
                            allImages = allImages.sortedByDescending { it.lastModified() } as MutableList<File>
                            callback(allImages)
                        }
                        ImagesSortOrder.LastModifiedDescending -> {
                            allImages = allImages.sortedBy { it.lastModified() } as MutableList<File>
                            callback(allImages)
                        }
                    }
                } else {
                    callback(null)
                }
            }

        } else {
            callback(null)
        }
    }

    fun getDataAndFolders(imageSortOrder: ImagesSortOrder? = null, foldersSortOrder: FolderSortOrder? = null, callback: (list: MutableList<Folder>?) -> Unit) {
        getAllImages(imageSortOrder) { files ->
            if (files != null) {
                var grouped = allImages.groupBy { it.parentFile }.map {
                    it.key?.let { it1 ->
                        Folder(
                            it1.nameWithoutExtension,
                            it.value as MutableList<File>
                        )
                    }
                }
                when (foldersSortOrder) {
                    null -> {
                        callback(grouped as MutableList<Folder>)
                    }
                    FolderSortOrder.NameAscending -> {
                        val collator = Collator.getInstance()
                        grouped = grouped.sortedWith { c1, c2 -> collator.compare(c1?.name, c2?.name) }
                        callback(grouped as MutableList<Folder>)
                    }
                    FolderSortOrder.NameDescending -> {
                        val collator = Collator.getInstance()
                        grouped = grouped.sortedWith { c1, c2 -> collator.compare(c2?.name, c1?.name) }
                        callback(grouped as MutableList<Folder>)
                    }
                    FolderSortOrder.LengthAscending -> {
                        grouped = grouped.sortedBy {
                            it?.folderLength
                        }
                        callback(grouped as MutableList<Folder>)
                    }
                    FolderSortOrder.LengthDescending -> {
                        grouped = grouped.sortedByDescending {
                            it?.folderLength
                        }
                        callback(grouped as MutableList<Folder>)
                    }
                }
            } else {
                callback(null)
            }
        }
    }

    private fun checkPermissionForReadExternalStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        } else false
    }

    private fun globalPermissionCheck(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (checkPermissionForReadExternalStorage()) {
                return true
            }
        } else {
            if (Environment.isExternalStorageManager()) {
                return true
            }
        }
        return false
    }

    companion object {
        const val TAG = "IMAGEFETCHERTAG"
    }
}