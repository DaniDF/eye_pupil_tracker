package it.dani.model

/**
 * @author Daniele
 *
 * This class is a wrapper for all key - value pairs for each label.
 * Labels are stored as key identifier value and displayed string
 *
 * @constructor Creates a new label map
 * @param[map] The map of key value labels
 */

data class LabelMap(val map : Map<String,String>)