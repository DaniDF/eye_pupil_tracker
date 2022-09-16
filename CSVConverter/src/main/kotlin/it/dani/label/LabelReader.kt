package it.dani.label

import it.dani.model.Label
import java.io.BufferedReader

/**
 * @author Daniele
 *
 * This class is a reader for label file.
 * This class creates a set of labels
 *
 * @constructor Creates a new reader and lode the file
 * @param[reader] Reader of label file
 */

abstract class LabelReader(private val reader: BufferedReader) {
    val labels : Set<Label> = this.readLabels()

    /**
     * This method read the label file and generate the according set of labels
     *
     * @return The label set
     */
    protected open fun readLabels(): Set<Label> {
        val result = HashSet<Label>()
        var line = this.reader.readLine()

        while (line != null) {

            this.computeLine(line.trim())?.let { label ->
                result += label
            }
            line = this.reader.readLine()
        }

        return result
    }

    /**
     * This method converts a line in string format into a [Label] object
     *
     * @return The computed Label
     */
    protected abstract fun computeLine(line : String) : Label?
}