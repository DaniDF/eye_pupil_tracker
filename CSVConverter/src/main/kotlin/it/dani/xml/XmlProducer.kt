package it.dani.xml

import it.dani.model.Box
import it.dani.model.Data
import it.dani.model.ImageData
import it.dani.model.LabelMap
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.lang.StringBuilder
import javax.imageio.IIOException
import javax.imageio.ImageIO

/**
 * @author Daniele
 *
 * This class from a detection map and a label map generate the according xml file, one for each image.
 * Each file contains all labels of that image.
 * This class creates xml files only for images contained into [inputDir] folder
 *
 * @param[detectionsMap] The detection map
 * @param[labelMap] The map that associate key label to the string representation
 * @param[inputDir] The folder that contains the images
 * @param[outputDir] The folder where will be saved the xml files
 */

class XmlProducer(private val detectionsMap: Map<String, Data>, private val labelMap : LabelMap,
                  private val inputDir : File, private val outputDir : File) {

    init {
        if(!this.outputDir.isDirectory) {
            throw IllegalArgumentException("Error: outputDir[${this.outputDir.absolutePath}] must be a directory")
        }
        if(!this.inputDir.isDirectory) {
            throw IllegalArgumentException("Error: inputDir[${this.inputDir.absolutePath}] must be a directory")
        }
    }

    /**
     * This method produce a xml file for any images into [inputDir] folder according the [detectionsMap] and [labelMap]
     */
    fun produce() {
        val errorHandler : (File,String) -> Unit = { file, filename ->
            System.err.println("Error at: ${file.absolutePath}")
            File("${this.outputDir.absolutePath}${File.separator}${filename}.xml").delete()
        }

        this.inputDir.listFiles { _, name -> name.endsWith(".jpg") || name.endsWith(".png") }?.forEach { file ->
            val filename = file.name.substring(0,file.name.length - 4)

            try {
                this.detectionsMap[filename]?.let { data ->
                    PrintWriter(FileOutputStream("${this.outputDir.absolutePath}${File.separator}${filename}.xml"))
                        .use { fileOut ->

                            val measuredImage = this.measureImage(file)
                            val imageData = ImageData(file.name,measuredImage.first,measuredImage.second,3)

                            fileOut.print(this.genAnnotationString(this.inputDir.name,
                                file.name,this.inputDir.absolutePath,
                                imageData,data.boxes,this.labelMap))
                        }
                }
            } catch (e : IIOException) {
                errorHandler(file,filename)
            } catch (e : IllegalArgumentException) {
                errorHandler(file,filename)
            }
        }
    }

    /**
     * Generates the annotation tag of xml
     */
    private fun genAnnotationString(folderName : String,
                                    filename: String,
                                    path : String,
                                    imageData: ImageData,
                                    boxes: List<Box>,
                                    labelMap: LabelMap) : String {
        val result = StringBuilder()

        result.append("<annotation>\n")

        result.append("\t${this.genFolderString(folderName)}\n")
        result.append("\t${this.genFilenameString(filename)}\n")
        result.append("\t${this.genPathString(path,filename)}\n")
        result.append("\t${this.genSourceString().tab()}\n")
        result.append("\t${this.genSizeString(imageData).tab()}\n")
        result.append("\t${this.genSegmentedString()}\n")
        result.append("\t${this.genObjectsString(imageData,boxes, labelMap).tab()}\n")

        result.append("</annotation>\n")

        return result.toString()
    }

    /**
     * Generates the folder tag of xml
     */
    private fun genFolderString(folderName : String) : String {
        return "<folder>$folderName</folder>"
    }

    /**
     * Generates the filename tag of xml
     */
    private fun genFilenameString(filename : String) : String {
        return "<filename>$filename</filename>"
    }

    /**
     * Generates the path tag of xml
     */
    private fun genPathString(path : String, filename : String) : String {
        return "<path>$path${File.separator}$filename</path>"
    }


    /**
     * Generates the source tag of xml
     */
    private fun genSourceString() : String {
        return "<source>\n" +
                "\t<database>Unknown</database>\n" +
                "</source>"
    }

    /**
     * Generates the image size tag of xml
     */
    private fun genSizeString(imageData: ImageData) : String {
        return "<size>\n" +
                "\t<width>${imageData.width}</width>\n" +
                "\t<height>${imageData.height}</height>\n" +
                "\t<depth>${imageData.depth}</depth>\n" +
                "</size>"
    }

    /**
     * Generates the segmented tag of xml
     */
    private fun genSegmentedString() : String {
        return "<segmented>0</segmented>"
    }

    /**
     * Generates the objects tag of xml, one for each label
     */
    private fun genObjectsString(imageData: ImageData, boxes: List<Box>, labelMap: LabelMap) : String {
        val result = StringBuilder()

        boxes.forEach {
            val labelName = labelMap.map[it.labelName]

            result.append("<object>\n")

            result.append("\t<name>$labelName</name>\n")
            result.append("\t<pose>Unspecified</pose>\n")
            result.append("\t<truncated>0</truncated>\n")
            result.append("\t<difficult>0</difficult>\n")


            result.append("\t<bndbox>\n")
            result.append("\t\t<xmin>${(imageData.width * it.xMin).toInt()}</xmin>\n")
            result.append("\t\t<ymin>${(imageData.height * it.yMin).toInt()}</ymin>\n")
            result.append("\t\t<xmax>${(imageData.width * it.xMax).toInt()}</xmax>\n")
            result.append("\t\t<ymax>${(imageData.height * it.yMax).toInt()}</ymax>\n")
            result.append("\t</bndbox>\n")

            result.append("\t</object>\n")
        }

        return result.toString()
    }

    /**
     * Calc the dimensions (width and height) of given image
     *
     * @param[file] The path of the image
     * @return The dimension pair (first: width, second: height)
     */
    private fun measureImage(file : File) : Pair<Int,Int> {
        val image = ImageIO.read(file)
        return image.width to image.height
    }

    /**
     * Inset a tab character in front of every line
     */
    private fun String.tab() : String {
        return this.replace("\n\t","\n\t\t")
    }
}