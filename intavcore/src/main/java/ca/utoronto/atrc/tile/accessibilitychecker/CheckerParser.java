/*******************************************************************************
* Copyright (C) 2012 INTECO, Instituto Nacional de Tecnologías de la Comunicación, 
* This program is licensed and may be used, modified and redistributed under the terms
* of the European Public License (EUPL), either version 1.2 or (at your option) any later 
* version as soon as they are approved by the European Commission.
* Unless required by applicable law or agreed to in writing, software distributed under the 
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
* ANY KIND, either express or implied. See the License for the specific language governing 
* permissions and more details.
* You should have received a copy of the EUPL1.2 license along with this program; if not, 
* you may find it at http://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32017D0863
* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
* Modificaciones: MINHAFP (Ministerio de Hacienda y Función Pública) 
* Email: observ.accesibilidad@correo.gob.es
******************************************************************************/
/*
Copyright ©2004, University of Toronto. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a 
copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Adaptive Technology Resource Centre, University of Toronto
130 St. George St., Toronto, Ontario, Canada
Telephone: (416) 978-4360
*/

package ca.utoronto.atrc.tile.accessibilitychecker;

import es.inteco.common.IntavConstants;
import es.inteco.common.logging.Logger;
import org.apache.xerces.xni.*;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class CheckerParser extends DOMParser {

    private XMLLocator locator;
    private boolean flagDoctype = false;
    private String stringHtmlXhtml = "";
    private String stringHtmlVersion = "";
    private String doctypePublicId = "";
    private String doctypeSystemId = "";
    private int lineDoctype = 0;
    private Node nodeImgWithLongdesc = null;
    private String longdescValue = "";
    private int headingLevel = 0;
    private int dlinkCounter = 0;
    private int noscriptCounter = 0;
    private int noembedCounter = 0;
    private int formCounter = 0;
    private Node nodeScript = null;
    private Node nodeEmbed = null;
    private Node nodePreviousHeader = null;
    /**
     * Map que guarda el número de veces que aparece un elemento (una etiqueta)
     */
    private Map<String, Integer> hashtableElements = new Hashtable<>();
    private String base = "";
    private String formname = "";
    private List<String> vectorIDs = new ArrayList<>();
    private List<String> vectorFors = new ArrayList<>();
    private int position = 1;
    private boolean inHeading = false;

    public CheckerParser() {
        this(true);
    }

    public CheckerParser(final boolean balanceTags) {
        super();
        try {
            // Activar o no el balanceado de etiquetas
            setFeature("http://cyberneko.org/html/features/balance-tags", balanceTags);
            setFeature("http://xml.org/sax/features/namespaces", false);
            setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe", true);
            setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags", true);
            // No se alteran las mayúsculas ni minúsculas de los nombres de elementos y atributos
            setProperty("http://cyberneko.org/html/properties/names/elems", "no-change");
            setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
        } catch (org.xml.sax.SAXException e) {
            Logger.putLog("Exception: ", CheckerParser.class, Logger.LOG_LEVEL_ERROR, e);
        }
    }

    public boolean hasDoctype() {
        return flagDoctype;
    }

    public int getDoctypeLine() {
        return lineDoctype;
    }

    public String getBase() {
        return base;
    }

    public String getHtmlXhtml() {
        return stringHtmlXhtml;
    }

    public String getHtmlVersion() {
        return stringHtmlVersion;
    }

    public String getDoctypePublicId() {
        return doctypePublicId;
    }

    public String getDoctypeSystemId() {
        return doctypeSystemId;
    }

    /*   We override startElement callback  from DocumentHandler */
    public void startElement(QName elementQName, XMLAttributes attrList, Augmentations augs) {
        super.startElement(elementQName, attrList, augs);

        Node node;
        try {
            node = (Node) this.getProperty("http://apache.org/xml/properties/dom/current-element-node");
        } catch (org.xml.sax.SAXException ex) {
            Logger.putLog("Exception: ", CheckerParser.class, Logger.LOG_LEVEL_ERROR, ex);
            throw new XNIException(ex);
        }

        node.setUserData("startLine", String.valueOf(locator.getLineNumber()), null);
        node.setUserData("startColumn", String.valueOf(locator.getColumnNumber()), null);

        // are we looking for a d-link?
        // (We're doing this here before the DOM structure is created because the d-link
        // must follow within 5 elements of the IMG.)

        if (dlinkCounter > 0) {
            if ("a".equalsIgnoreCase(node.getNodeName())) {
                if (EvaluatorUtility.getAttributeNoSession((Element) node, "href").equalsIgnoreCase(longdescValue)) {
                    nodeImgWithLongdesc.setUserData("dlink", "true", null);
                    dlinkCounter = 0;
                    nodeImgWithLongdesc = null;
                } else {
                    dlinkCounter--;
                }
            } else {
                dlinkCounter--;
            }
        }

        // are we looking for a NOSCRIPT?
        // (We're doing this here before the DOM structure is created because the NOSCRIPT
        // must follow within 5 elements of the SCRIPT.)
        if (noscriptCounter > 0) {
            if ("noscript".equalsIgnoreCase(node.getNodeName())) {
                nodeScript.setUserData("noscript", "true", null);
                noscriptCounter = 0;
                nodeScript = null;
            } else {
                noscriptCounter--;
            }
        }

        // are we looking for a NOEMBED?
        // (We're doing this here before the DOM structure is created because the NOEMBED
        // must follow within 5 elements of the EMBED.)
        if (noembedCounter > 0) {
            if ("noembed".equalsIgnoreCase(node.getNodeName())) {
                nodeEmbed.setUserData("noembed", "true", null);
                noembedCounter = 0;
                nodeEmbed = null;
            } else {
                noembedCounter--;
            }
        }

        // check for non-unique ID attribute values
        // (This is done here because it's more efficient.)
        String stringIDvalue = attrList.getValue("id");
        if (stringIDvalue != null) {
            // check the vector to see if this already exists
            boolean bFoundIt = false;
            for (String vectorID : vectorIDs) {
                if (stringIDvalue.equalsIgnoreCase(vectorID)) {
                    node.setUserData("dupeid", "true", null);
                    bFoundIt = true;
                    break;
                }
            }
            if (!bFoundIt) {
                vectorIDs.add(stringIDvalue);
            }
        }

        // store the "action" attribute of the current form (needed for "input" elements)
        if ("form".equalsIgnoreCase(node.getNodeName())) {
            formCounter++;
            // the form identifier is the 'action' attribute
            formname = EvaluatorUtility.getAttributeNoSession((Element) node, "action");
            if ((formname == null) || (formname.length() == 0)) {
                // if no 'action' attribute then form name is form counter number
                formname = "form" + formCounter;
            }
        }

        // input element - associate with its form and its position in the document
        else if ("input".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("form", formname, null);
            node.setUserData("pos", position, null);
            position++;
        }

        // select element - associate with its form and its position in the document
        else if ("select".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("form", formname, null);
            node.setUserData("pos", position, null);
            position++;
        }

        // a element - associate with its position in the document
        else if ("a".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("pos", position, null);
            position++;
        }

        // area area - associate with its position in the document
        else if ("area".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("pos", position, null);
            position++;
        }

        // button element - associate with its position in the document
        else if ("button".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("pos", position, null);
            position++;
        }

        // object element - associate with its position in the document
        else if ("object".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("pos", position, null);
            position++;
        }
        // textarea element - associate its position in the document
        else if ("textarea".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("pos", position, null);
            position++;
        }

        // label element - associate with its form
        else if ("label".equalsIgnoreCase(node.getNodeName())) {
            node.setUserData("form", formname, null);

            // check for non-unique FOR attribute values
            // (This is done here because it's more efficient.)
            String stringForvalue = attrList.getValue("for");
            if (stringForvalue != null) {
                // check the vector to see if this already exists
                boolean bFoundIt = false;
                for (String vectorFor : vectorFors) {
                    if (stringForvalue.compareToIgnoreCase(vectorFor) == 0) {
                        node.setUserData("dupefor", "true", null);
                        bFoundIt = true;
                        break;
                    }
                }
                if (!bFoundIt) {
                    vectorFors.add(stringForvalue);
                }
            }
        }

        // store the 'base' (needed when loading images)
        else if ("base".equalsIgnoreCase(node.getNodeName())) {
            base = EvaluatorUtility.getAttributeNoSession((Element) node, "href");
        }

        // look at heading levels here too
        // store the previous and next heading level on each header
        else if ("h1".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 1);
        } else if ("h2".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 2);
        } else if ("h3".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 3);
        } else if ("h4".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 4);
        } else if ("h5".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 5);
        } else if ("h6".equalsIgnoreCase(node.getNodeName())) {
            adjustHeadingLevels(node, 6);
        }

        // is this an IMG?
        else if ("img".equalsIgnoreCase(node.getNodeName())) {
            parseImg(node);
            if (!((Element) node).getAttribute("alt").trim().isEmpty()) {
                if (nodePreviousHeader != null) {
                    nodePreviousHeader.setUserData("headerHasContents", true, null);
                }
            }
        }

        // is this an SCRIPT (and requires a NOSCRIPT)?
        else if ("script".equalsIgnoreCase(node.getNodeName())) {
            noscriptCounter = 5; // noscript must occur within the next 5 elements
            node.setUserData("noscript", "false", null);
            nodeScript = node;
        }

        // is this an EMBED (and requires a NOEMBED)?
        else if ("embed".equalsIgnoreCase(node.getNodeName())) {
            noembedCounter = 5; // noembed must occur within the next 5 elements
            node.setUserData("noembed", "false", null);
            nodeEmbed = node;
        }

        // count each element
        Integer intCount = hashtableElements.get(node.getNodeName());
        if (intCount == null) {
            intCount = 0;
        }
        intCount = intCount + 1;
        hashtableElements.put(node.getNodeName(), intCount);
        node.setUserData("count", intCount, null);

    } //startElement

    private void adjustHeadingLevels(final Node node, final int nextHeadingLevel) {
        node.setUserData(IntavConstants.PREVIOUS_LEVEL, headingLevel, null);
        headingLevel = nextHeadingLevel;
        inHeading = true;
        if (nodePreviousHeader != null) {
            nodePreviousHeader.setUserData(IntavConstants.NEXT_LEVEL, nextHeadingLevel, null);
            nodePreviousHeader.setUserData("nextheader", node, null);
            node.setUserData("prevheader", nodePreviousHeader, null);
        }
        nodePreviousHeader = node;
    }

    private void parseImg(Node node) {
        // does it require a d-link?
        if (((Element) node).getAttribute("longdesc").length() > 0) {
            nodeImgWithLongdesc = node;
            longdescValue = EvaluatorUtility.getAttributeNoSession((Element) node, "longdesc");
            dlinkCounter = 5; // dlink must occur within the next 5 elements
            node.setUserData("dlink", "false", null);
        }
    }

    /* We override startDocument callback from DocumentHandler */
    public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext, Augmentations augs) {
        super.startDocument(locator, encoding, namespaceContext, augs);
        this.locator = locator;
        Node node = null;
        try {
            node = (Node) this.getProperty("http://apache.org/xml/properties/dom/current-element-node");
        } catch (org.xml.sax.SAXException ex) {
            Logger.putLog("Exception: ", CheckerParser.class, Logger.LOG_LEVEL_ERROR, ex);
        }

        if (node != null) {
            node.setUserData("startLine", String.valueOf(locator.getLineNumber()), null);
            node.setUserData("startColumn", String.valueOf(locator.getColumnNumber()), null);
        }

        stringHtmlXhtml = "html";
        stringHtmlVersion = "";
    } //startDocument

    // override doctypeDecl to find out if document has a doctype declaration
    // get the (x)html type and version here too
    public void doctypeDecl(java.lang.String rootElement,
                            java.lang.String publicId,
                            java.lang.String systemId,
                            org.apache.xerces.xni.Augmentations augs) {

        // store the systemId
        doctypePublicId = publicId;
        doctypeSystemId = systemId;

        if (publicId == null) {
            super.doctypeDecl(rootElement, null, systemId, augs);
            return;
        }

        // is this html or xhtml? and which version?
        boolean bFoundType = false;
        StringTokenizer stringTokenizer = new StringTokenizer(publicId, " ");
        while (stringTokenizer.hasMoreTokens()) {
            String stringTemp = stringTokenizer.nextToken();
            if (bFoundType) {
                stringHtmlVersion = stringTemp;
                break;
            } else {
                if (stringTemp.compareToIgnoreCase("xhtml") == 0) {
                    bFoundType = true;
                    stringHtmlXhtml = "xhtml";
                } else if (stringTemp.compareToIgnoreCase("html") == 0) {
                    bFoundType = true;
                    stringHtmlXhtml = "html";
                }
            }
        }

        super.doctypeDecl(rootElement, publicId, systemId, augs);

        // set the flag
        flagDoctype = true;

        // and line number
        lineDoctype = locator.getLineNumber();
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) {
        super.endElement(qName, augmentations);
        if ("h1".equals(qName.rawname) || "h2".equals(qName.rawname)
                || "h3".equals(qName.rawname) || "h4".equals(qName.rawname)
                || "h5".equals(qName.rawname) || "h6".equals(qName.rawname)) {
            inHeading = false;
        }
    }

    @Override
    public void characters(XMLString xmlString, Augmentations augmentations) {
        super.characters(xmlString, augmentations);
        if (nodePreviousHeader != null && !inHeading && !xmlString.toString().trim().isEmpty()) {
            nodePreviousHeader.setUserData("headerHasContents", true, null);
        }
    }
}
