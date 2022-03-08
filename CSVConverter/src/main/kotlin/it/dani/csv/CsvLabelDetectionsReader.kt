package it.dani.csv

import it.dani.model.Box
import it.dani.model.Data
import java.io.BufferedReader
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Daniele
 *
 * This class is a reader for fiftyone's csv dataset file.
 * This class iterates and fetch eagerly the data.
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

class CsvLabelDetectionsReader(private val reader: BufferedReader) : Iterator<Map<String,Data>> {

    /**
     * @property[detectionsMap] Stores a computed batch of lines
     */
    var detectionsMap : MutableMap<String,Data> = HashMap()

    /**
     * @property[batchSize] The size of the batch, default 10000
     */
    var batchSize = 10000

    /**
     * @property[Function] Function that returns the pair of the last red image
     */
    private lateinit var last : () -> Pair<String,Data>
    private var hasNext = true

    init {
        this.readData()
    }

    /**
     * @return Returns true if can read successfully at least one line
     */
    override fun hasNext(): Boolean {
        return this.hasNext
    }

    /**
     * This method returns the previews batch of data, read a new one and insert into that one the pair of the last red image
     * (see [last])
     *
     * @return Batch of data
     */
    override fun next(): Map<String, Data> {
        if(this.detectionsMap.isEmpty()) {
            this.readData()
        }

        val result = this.detectionsMap

        this.detectionsMap = HashMap()
        this.detectionsMap[this.last().first] = this.last().second

        this.readData()

        return result
    }

    /**
     * Reads a batch of lines
     */
    private fun readData() {
        return this.readData(this.batchSize)
    }

    /**
     * Reads maximum [limit] lines
     *
     * @param[limit] Batch dimension
     */
    private fun readData(limit : Int) {

        var line : String? = this.reader.readLine()
        var count = 0

        while(count++ < limit && line != null) {
            this.computeLine(line)
            if(count < limit) {
                line = this.reader.readLine()
            }
        }

        this.hasNext = count >= limit
    }

    /**
     * Computes a [line] in String format and convert it a [Data] object that will be stored in [detectionsMap]
     */
    private fun computeLine(line : String) {
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
                else -> flagStop = true //throw ParseException("Error: to much fields $count - $line",count)
            }
        }

        /*
        if(count != 13) {
            throw ParseException("Error: not enough fields $count",count)
        }
        */

        val box = Box(labelName,
            confidence,
            xMin, xMax,yMin, yMax,
            isOccluded, isTruncated, isGroupOf, isDepiction, isInside)

        val data = this.detectionsMap[imageId] ?: Data(imageId,source).also { this.detectionsMap[imageId] = it }
        data.apply { this.boxes += box }
        this.last = { imageId to data }
    }
}