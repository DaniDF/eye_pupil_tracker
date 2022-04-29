package it.dani

import it.dani.csv.fiftyone.CsvLabelDetectionsReader
import it.dani.csv.fiftyone.CsvLabelMapReader
import it.dani.xml.XmlProducer
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Main function, can be invoked with the following syntax:
 * <detection_file> <labels_file> <input_dir> [<output_dir>]
 *
 * <output_dir> can be omitted so <output_dir> = <input_dir>
 */
fun main(args: Array<String>) {

    val detections: String
    val labels: String
    val inputDir: String
    val outputDir: String

    when(args.size) {
        3 -> {
            detections = args[0]
            labels = args[1]
            inputDir = args[2]
            outputDir = args[2]
        }
        4 -> {
            detections = args[0]
            labels = args[1]
            inputDir = args[2]
            outputDir = args[3]
        }
        else -> throw IllegalArgumentException("Error: invalid arguments: use <detections_file> <labels_file> <input_dir> [<output_dir>]")
    }

    println("Loading labels...")

    val labelMapReader = CsvLabelMapReader(
        BufferedReader(
            InputStreamReader(FileInputStream(labels))
        ).also { it.readLine() }
    )

    println("${labelMapReader.labelMap.map.keys.size} labels found!")

    println("Loading detections...")

    val csvLabelDetectionsReader = CsvLabelDetectionsReader(
        BufferedReader(
            InputStreamReader(FileInputStream(detections))   //Detections
        ).also { it.readLine() }
    ).apply { this.filterBoxes = { labelMapReader.labelMap.map[it.labelName] == "Human eye" } }

    for(ld in csvLabelDetectionsReader){
        println("${ld.keys.size} detections found!")

        println("Producing xml files...")

        XmlProducer(ld,labelMapReader.labelMap,
            File(inputDir), File(outputDir)
        ).produce()
    }

    println("Done.")
}