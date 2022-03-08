package csv

import it.dani.csv.CsvLabelDetectionsReader
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


fun main(args : Array<String>) {
    val reader = CsvLabelDetectionsReader(BufferedReader(
            InputStreamReader(FileInputStream("C:\\Users\\Daniele\\Desktop\\validation\\labels\\detections.csv"))
        ).also { it.readLine() }
    )

    println()
}