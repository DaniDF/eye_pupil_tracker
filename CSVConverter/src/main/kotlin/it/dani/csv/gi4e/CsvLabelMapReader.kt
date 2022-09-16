package it.dani.csv.gi4e

import it.dani.model.LabelMap
import java.io.BufferedReader

/**
 * @author Daniele
 *
 * This class is a reader for gi4e csv labels file.
 * This class creates a map of key labels and represented string
 *
 * @constructor Creates the default association map
 * @param[reader] Reader of file csv
 */

class CsvLabelMapReader(reader: BufferedReader) : it.dani.csv.CsvLabelMapReader(reader) {

    override fun readMap(): LabelMap {
        return LabelMap(LABELS)
    }

    /**
     * Useless method for this class
     */
    override fun computeLine(line: String): Pair<String, String> {
        return LABELS.entries.first().toPair()
    }

    companion object {

        /**
         * @property[LABELS] The default map of label association
         */
        private val LABELS = mapOf(
            "eye_left_leftcorner" to "eye_left_leftcorner",
            "eye_left_center" to "eye_left_center",
            "eye_left_rightcorner" to "eye_left_rightcorner",
            "eye_right_leftcorner" to "eye_right_leftcorner",
            "eye_right_center" to "eye_right_center",
            "eye_right_rightcorner" to "eye_right_rightcorner"
        )
    }
}