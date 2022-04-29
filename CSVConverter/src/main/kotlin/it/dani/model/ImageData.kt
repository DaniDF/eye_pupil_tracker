package it.dani.model

/**
 * @author Daniele
 *
 * This class represents the use full information of image
 *
 * @constructor Creates a new image information class
 * @param[fileName] Name of the file not the path
 * @param[width] Width of represented image
 * @param[height] Height of represented image
 * @param[depth] Number of colour channels of represented image
 */
data class ImageData(val fileName : String, val width : Int, val height : Int, val depth : Int)