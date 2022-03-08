package xml

import it.dani.csv.CsvLabelDetectionsReader
import it.dani.csv.CsvLabelMapReader
import it.dani.xml.XmlProducer
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

fun main() {
    var csvLabelDetectionsReader = CsvLabelDetectionsReader(
            BufferedReader(
            InputStreamReader(FileInputStream("C:\\Users\\Daniele\\Desktop\\validation\\labels\\detections.csv"))
        ).also { it.readLine() }
    ).also { it.readData() }

    val labelMapReader = CsvLabelMapReader(
        BufferedReader(
            InputStreamReader(FileInputStream("C:\\Users\\Daniele\\Desktop\\validation\\metadata\\classes.csv"))
        ).also { it.readLine() }
    )

    val xmlProducer = XmlProducer(csvLabelDetectionsReader,labelMapReader.labelMap,
    File("C:\\Users\\Daniele\\Desktop\\validation\\data"), File("C:\\Users\\Daniele\\Desktop\\validation\\data")
    ).produce()
}