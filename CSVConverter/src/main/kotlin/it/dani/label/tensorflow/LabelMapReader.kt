package it.dani.label.tensorflow

import it.dani.label.LabelReader
import it.dani.model.Label
import java.io.BufferedReader
import java.util.regex.Pattern

/**
 * @author Daniele
 *
 * This class implements [LabelReader] for label_map.pbtxt file
 * according to Tensorflow documentation
 */

class LabelMapReader(reader: BufferedReader) : LabelReader(reader) {
    override fun computeLine(line: String): Label? {
        var result : Label? = null
        val patternName = Pattern.compile("name\\s*:\\s*'[a-zA-Z\\d\\s]+'")
        val matcherName = patternName.matcher(line)

        if(matcherName.find()) {
            val labelName = matcherName.group()
                .split(":")[1]
                .trim()
                .replace("'","")

            result = Label(labelName)
        }

        return result
    }
}