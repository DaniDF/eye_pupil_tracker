package it.dani.csv

import it.dani.model.Box
import it.dani.model.Data
import java.io.BufferedReader
import kotlin.collections.HashMap

/**
 * @author Daniele
 *
 * This class is a reader for csv dataset file.
 * This class iterates and fetch eagerly the data.
 *
 * @constructor Creates a new reader and pre lode a batch of lines
 * @param[reader] Reader of file csv
 */

abstract class CsvDetectionsReader(private val reader : BufferedReader) : Iterator<Map<String, Data>> {

    /**
     * @property[detectionsMap] Stores a computed batch of lines
     */
    protected var detectionsMap : MutableMap<String,Data> = HashMap()

    /**
     * @property[batchSize] The size of the batch, default 10000
     */
    var batchSize = 10000

    /**
     * @property[filterBoxes] Filter for incoming boxes
     */
    var filterBoxes : (Box) -> Boolean = { true }

    /**
     * @property[Function] Function that returns the pair of the last red image
     */
    protected lateinit var last : () -> Pair<String,Data>
    protected var hasNext = true

    /**
     * @return Returns true while can read successfully at least one line
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
    protected fun readData() {
        return this.readData(this.batchSize)
    }

    /**
     * Reads maximum [limit] lines
     *
     * @param[limit] Batch dimension
     */
    protected open fun readData(limit : Int) {

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
    protected abstract fun computeLine(line : String)
}