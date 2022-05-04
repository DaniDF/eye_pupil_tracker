package it.dani.labelimg

import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class LabelImgReader(fileIn : String, fileOut: String) {

    private val document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(fileIn)
    private val transformer = TransformerFactory.newInstance().newTransformer()
    private val result = StreamResult(fileOut)

    fun modify(modification : (Node) -> Any) {
        this.document.documentElement.normalize()
        this.modify(this.document.documentElement,modification)

        this.transformer.transform(DOMSource(this.document),this.result)
    }

    private fun modify(element: Node, modification: (Node) -> Any) {
        modification(element)

        for(index in 0 until element.childNodes.length) {
            this.modify(element.childNodes.item(index),modification)
        }
    }
}