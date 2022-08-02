package com.global.images.fetcher.models

import java.io.File

data class Folder(
    var name: String = "",
    var data: MutableList<File> = mutableListOf(),
) {
    val firstFile: File?
        get() = safeGetFirstFile()

    val folderLength: Double
        get() = safeGetFolderSize()

    private fun safeGetFirstFile(): File? {
        return data.firstOrNull()
    }

    private fun safeGetFolderSize(): Double {
        var size = 0.0
        data.forEach {
            size += it.length()
        }
        return size
    }
}
