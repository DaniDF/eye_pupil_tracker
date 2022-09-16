package it.dani

import it.dani.csv.fiftyone.CsvLabelDetectionsReader
import it.dani.csv.fiftyone.CsvLabelMapReader
import it.dani.label.tensorflow.LabelMapReader
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
    val labelFileFilter : String
    val outputDir: String

    when(args.size) {
        4 -> {
            detections = args[0]
            labels = args[1]
            inputDir = args[2]
            labelFileFilter = args[3]
            outputDir = args[2]
        }
        5 -> {
            detections = args[0]
            labels = args[1]
            inputDir = args[2]
            labelFileFilter = args[3]
            outputDir = args[4]
        }
        else -> throw IllegalArgumentException("Error: invalid arguments: use <detections_file> <labels_file> <input_dir> <label_file(could be empty)> [<output_dir>]")
    }

    println("Loading labels...")

    val labelFileFilterReader = LabelMapReader(
        BufferedReader(
            InputStreamReader(FileInputStream(labelFileFilter))
        )
    )

    println("${labelFileFilterReader.labels.size} filters selected")

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
    ).apply { this.filterBoxes = { box ->  labelMapReader.labelMap.map[box.labelName] in labelFileFilterReader.labels.map { it.label } } }

    for(ld in csvLabelDetectionsReader){
        println("${ld.keys.size} detections found!")

        println("Producing xml files...")

        XmlProducer(ld,labelMapReader.labelMap,
            File(inputDir), File(outputDir)
        ).produce()
    }

    println("Done.")
}