package it.dani.csv.fiftyone

import java.io.BufferedReader
import java.text.ParseException
import java.util.*

/**
 * @author Daniele
 *
 * This class is a reader for fiftyone's csv labels file.
 * This class creates a map of key labels and represented string
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

class CsvLabelMapReader(private val reader: BufferedReader) : it.dani.csv.CsvLabelMapReader(reader) {

    override fun computeLine(line: String): Pair<String, String> {
        val strTokenizer = StringTokenizer(line, ",")
        val next = { strTokenizer.nextToken() }

        var count = 0

        var id = ""
        var value = ""

        while (strTokenizer.hasMoreTokens()) {
            when (count++) {
                0 -> id = next()
                1 -> value = next()
                else -> throw ParseException("Error: to much fields", count)
            }
        }

        if (count != 2) {
            throw ParseException("Error: not enough fields", count)
        }

        return id to value
    }
}