package it.dani.csv.fiftyone

import it.dani.csv.CsvDetectionsReader
import it.dani.model.Box
import it.dani.model.Data
import java.io.BufferedReader
import java.util.*

/**
 * @author Daniele
 *
 * This class is a reader for fiftyone's csv dataset file.
 * This class iterates and fetch eagerly the data.
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

class CsvLabelDetectionsReader(reader: BufferedReader) : CsvDetectionsReader(reader) {

    override fun computeLine(line : String) {
        val strTokenizer = StringTokenizer(line,",")

        var imageId = ""
        var source = ""
        var labelName = ""
        var confidence = ""
        var xMin = Float.NaN
        var xMax = Float.NaN
        var yMin = Float.NaN
        var yMax = Float.NaN
        var isOccluded = ""
        var isTruncated = ""
        var isGroupOf = ""
        var isDepiction = ""
        var isInside = ""

        var count = 0
        var flagStop = false

        val next = { strTokenizer.nextToken() }
        val nextFloat = { next().toFloat() }

        while(!flagStop && strTokenizer.hasMoreTokens()) {
            when(count++) {
                0 -> imageId = next()
                1 -> source = next()
                2 -> labelName = next()
                3 -> confidence = next()
                4 -> xMin = nextFloat()
                5 -> xMax = nextFloat()
                6 -> yMin = nextFloat()
                7 -> yMax = nextFloat()
                8 -> isOccluded = next()
                9 -> isTruncated = next()
                10 -> isGroupOf = next()
                11 -> isDepiction = next()
                12 -> isInside = next()
                else -> flagStop = true
            }
        }

        val box = Box(labelName,
            confidence,
            xMin, xMax,yMin, yMax,
            isOccluded, isTruncated, isGroupOf, isDepiction, isInside)

        val data = this.detectionsMap[imageId] ?: Data(imageId,source).also { this.detectionsMap[imageId] = it }
        data.apply {
            if(this@CsvLabelDetectionsReader.filterBoxes(box)) {
                this.boxes += box
            }
        }

        this.last = { imageId to data }
    }
}