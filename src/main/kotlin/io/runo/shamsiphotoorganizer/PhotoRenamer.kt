package io.runo.shamsiphotoorganizer

import java.io.File

class PhotoRenamer(inputDir: String, outputDir: String, shouldRename: Boolean) {
    private var list: List<Photo> = Utils.getPhotosList(File(inputDir))

    init {
        val count = "Number of valid Photos: " + list.size
        println(count)
        info(count)
        for (photo in list) {
            info("Photos meta: " + photo.metadata)
            Utils.rename(photo, outputDir, shouldRename)
        }
    }
}