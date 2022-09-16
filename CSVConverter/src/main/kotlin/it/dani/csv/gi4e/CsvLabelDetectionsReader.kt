package it.dani.csv.gi4e

import it.dani.csv.CsvDetectionsReader
import it.dani.model.Box
import it.dani.model.Data
import java.io.BufferedReader
import java.util.*

/**
 * @author Daniele
 *
 * This class is a reader for gi4e csv dataset file.
 * This class iterates and fetch eagerly the data.
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

class CsvLabelDetectionsReader(reader: BufferedReader) : CsvDetectionsReader(reader) {

    override fun computeLine(line: String) {
        val strTokenizer = StringTokenizer(line, ";")

        var imageId = ""
        var x0 = Float.NaN
        var y0 = Float.NaN
        var x1 = Float.NaN
        var y1 = Float.NaN
        var x2 = Float.NaN
        var y2 = Float.NaN
        var x3 = Float.NaN
        var y3 = Float.NaN
        var x4 = Float.NaN
        var y4 = Float.NaN
        var x5 = Float.NaN
        var y5 = Float.NaN

        var count = 0
        var flagStop = false

        val next = { strTokenizer.nextToken() }
        val nextFloat = { next().toFloat() }

        while (!flagStop && strTokenizer.hasMoreTokens()) {
            when (count++) {
                0 -> imageId = next()
                1 -> x0 = nextFloat() / IMAGE_WIDTH
                2 -> y0 = nextFloat() / IMAGE_HEIGHT
                3 -> x1 = nextFloat() / IMAGE_WIDTH
                4 -> y1 = nextFloat() / IMAGE_HEIGHT
                5 -> x2 = nextFloat() / IMAGE_WIDTH
                6 -> y2 = nextFloat() / IMAGE_HEIGHT
                7 -> x3 = nextFloat() / IMAGE_WIDTH
                8 -> y3 = nextFloat() / IMAGE_HEIGHT
                9 -> x4 = nextFloat() / IMAGE_WIDTH
                10 -> y4 = nextFloat() / IMAGE_HEIGHT
                11 -> x5 = nextFloat() / IMAGE_WIDTH
                12 -> y5 = nextFloat() / IMAGE_HEIGHT
                else -> flagStop = true
            }
        }

        val boxes = arrayOf(
            Box("eye_left_leftcorner", "1", x0, x0, y0, y0, "0", "0", "0", "0", "0"),
            Box("eye_left_center", "1", x1, x1, y1, y1, "0", "0", "0", "0", "0"),
            Box("eye_left_rightcorner", "1", x2, x2, y2, y2, "0", "0", "0", "0", "0"),
            Box("eye_right_leftcorner", "1", x3, x3, y3, y3, "0", "0", "0", "0", "0"),
            Box("eye_right_center", "1", x4, x4, y4, y4, "0", "0", "0", "0", "0"),
            Box("eye_right_rightcorner", "1", x5, x5, y5, y5, "0", "0", "0", "0", "0")
        )

        val data = this.detectionsMap[imageId] ?: Data(imageId, imageId).also { this.detectionsMap[imageId] = it }
        data.apply {
            this.boxes += boxes.filter(filterBoxes)
        }

        this.last = { imageId to data }
    }

    companion object {
        /**
         * @property[IMAGE_WIDTH] default database image width
         */
        private const val IMAGE_WIDTH = 800

        /**
         * @property[IMAGE_WIDTH] default database image height
         */
        private const val IMAGE_HEIGHT = 600
    }
}