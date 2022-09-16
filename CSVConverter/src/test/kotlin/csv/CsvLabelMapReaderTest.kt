package csv

import it.dani.csv.fiftyone.CsvLabelMapReader
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

fun main(args: Array<String>) {
    val reader = CsvLabelMapReader(
        BufferedReader(
            InputStreamReader(FileInputStream("C:\\Users\\Daniele\\Desktop\\validation\\metadata\\classes.csv"))
        ).also { it.readLine() }
    )

    reader.labelMap.map.forEach {
        println("${it.key} -> ${it.value}")
    }
}