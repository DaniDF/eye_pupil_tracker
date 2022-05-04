package it.dani

import it.dani.labelimg.LabelImgReader
import java.io.File

fun main(args: Array<String>) {

    val folderPath: File

    when (args.size) {
        1 -> {
            folderPath = File(args[0])

            if (!folderPath.isDirectory) {
                error("Error, <folder_path> must be a folder")
            } else if (!folderPath.canWrite()) {
                error("Error, this script wants to change the folder content but it is not allowed")
            }
        }
        else -> error("Error, usage: <folder_path>")
    }

    folderPath.list()?.filter { it.endsWith(".xml") }?.forEach {

        var filename = ""

        LabelImgReader("${folderPath.absolutePath}${File.separator}$it", "${folderPath.absolutePath}${File.separator}$it").modify { n ->
            n.normalize()
            if (n.nodeName == "path" && n.hasChildNodes()) {
                n.firstChild.nodeValue = "${folderPath.absolutePath}${File.separator}$filename"
            } else if (n.nodeName == "filename" && n.hasChildNodes()) {
                filename = n.firstChild.nodeValue
            }
        }
    }

    println("DONE.")
}