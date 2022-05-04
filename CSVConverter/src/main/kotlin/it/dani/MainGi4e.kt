package it.dani

import it.dani.csv.gi4e.CsvLabelDetectionsReader
import it.dani.csv.gi4e.CsvLabelMapReader
import it.dani.xml.XmlProducer
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Main function, can be invoked with the following syntax:
 * <detection_file> <input_dir> [<output_dir>]
 *
 * <output_dir> can be omitted so <output_dir> = <input_dir>
 */
fun main(args: Array<String>) {

    val detections: String
    val inputDir: String
    val outputDir: String

    when(args.size) {
        2 -> {
            detections = args[0]
            inputDir = args[1]
            outputDir = args[1]
        }
        3 -> {
            detections = args[0]
            inputDir = args[1]
            outputDir = args[2]
        }
        else -> throw IllegalArgumentException("Error: invalid arguments: use <detections_file> <input_dir> [<output_dir>]")
    }

    println("Loading labels...")

    val labelMapReader = CsvLabelMapReader(
        BufferedReader(
            InputStreamReader(ByteArrayInputStream("".toByteArray()))
        ).also { it.readLine() }
    )

    println("${labelMapReader.labelMap.map.keys.size} labels found!")

    println("Loading detections...")


    val csvLabelDetectionsReader = CsvLabelDetectionsReader(
        BufferedReader(
            InputStreamReader(FileInputStream(detections))   //Detections
        ).also { it.readLine() }
    )

    for(ld in csvLabelDetectionsReader){
        println("${ld.keys.size} detections found!")

        println("Producing xml files...")

        XmlProducer(ld,labelMapReader.labelMap,
            File(inputDir), File(outputDir)
        ).produce()
    }

    println("Done.")
}