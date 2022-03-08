package it.dani.model

/**
 * @author Daniele
 *
 * This class represents all the data about an image in a set of images
 *
 * @constructor Creates a new data
 * @param[imageID] Unique identifier of an image
 * @param[source] Source of the image (useless)
 */

data class Data(val imageID : String, val source : String) {
    val boxes : MutableList<Box> = ArrayList()
}

/**
 * @author Daniele
 *
 * This class represents the bounding box outside an object
 *
 * @constructor Creates a new bounding box
 * @param[labelName] Name of bounding box
 * @param[confidence] Confidence of bounding box
 * @param[xMin] Left side of bounding box
 * @param[xMax] Right side of bounding box
 * @param[yMin] Top side of bounding box
 * @param[yMax] Bottom side of bounding box
 * @param[isOccluded]
 * @param[isTruncated]
 * @param[isGroupOf]
 * @param[isDepiction]
 * @param[isInside]
 */

data class Box(val labelName : String,
               val confidence : String,
               val xMin : Float,
               val xMax : Float,
               val yMin : Float,
               val yMax : Float,
               val isOccluded : String,
               val isTruncated : String,
               val isGroupOf : String,
               val isDepiction : String,
               val isInside : String)