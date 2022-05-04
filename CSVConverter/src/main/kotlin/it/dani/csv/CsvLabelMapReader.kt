package it.dani.csv

import it.dani.model.LabelMap
import java.io.BufferedReader
import kotlin.collections.HashMap

/**
 * @author Daniele
 *
 * This class is a reader for csv labels file.
 * This class creates a map of key labels and represented string
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

abstract class CsvLabelMapReader(private val reader: BufferedReader) {
    /**
     * @property[labelMap] The computed map of labels (see [LabelMap])
     */
    val labelMap = this.readMap()

    /**
     * This method read the csv file and generate the according map of labels
     *
     * @return The labelMap
     */
    protected open fun readMap(): LabelMap {
        val result = HashMap<String, String>()
        var line: String? = this.reader.readLine()

        while (line != null) {
            result += this.computeLine(line)
            line = this.reader.readLine()
        }

        return LabelMap(result)
    }

    /**
     * This method converts a line in string format into a pair key - value
     *
     * @return The computed pair key - value
     */
    protected abstract fun computeLine(line: String): Pair<String, String>
}