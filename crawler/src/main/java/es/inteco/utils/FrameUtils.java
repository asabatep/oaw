package es.inteco.utils;

import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.common.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static es.inteco.utils.CrawlerDOMUtils.*;

/**
 * Clase para incorporar el contenido de frames e iframes a la página que los declara.
 */
public final class FrameUtils {

    /**
	 * Instantiates a new frame utils.
	 */
    private FrameUtils() {
    }

    /**
	 * Gets the frame content.
	 *
	 * @param url the url
	 * @return the frame content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
    private static String getFrameContent(final String url) throws IOException {
        HttpURLConnection connection = CrawlerUtils.getConnection(url, null, false);
        InputStream markableInputStream = CrawlerUtils.getMarkableInputStream(connection);
        String textContent = StringUtils.getContentAsString(markableInputStream, CrawlerUtils.getCharset(connection, markableInputStream));

        textContent = CrawlerUtils.removeHtmlComments(textContent);

        textContent = CrawlerDOMUtils.getOnlyBody(textContent);

        return textContent;
    }

    /**
	 * Gets the frames source.
	 *
	 * @param rootUrl     the root url
	 * @param textContent the text content
	 * @return the frames source
	 * @throws Exception the exception
	 */
    public static String getFramesSource(final String rootUrl, final String textContent) throws Exception {
        Document document = getDocument(textContent);
        List<Element> frames = getElementsByTagName(document, "frame");

        final StringBuilder framesSources = new StringBuilder();
        for (Element frame : frames) {
            try {
                if (hasAttribute(frame, "src") && StringUtils.isNotEmpty(getAttribute(frame, "src"))) {
                    String frameUrl = new URL(new URL(rootUrl), getAttribute(frame, "src")).toString();
                    String frameSource = "<!-- Código HTML del frame localizado en " + frameUrl + " -->\n\n" +
                            getFrameContent(frameUrl) +
                            "<!-- Fin del Código HTML del frame localizado en " + frameUrl + " -->\n\n";

                    frameSource = createAbsoluteHrefs(frameSource, frameUrl);

                    framesSources.append(frameSource);
                }
            } catch (Exception e) {
                Logger.putLog("Error al recuperar el contenido del FRAME: ", CrawlerUtils.class, Logger.LOG_LEVEL_INFO, e);
            }
        }

        return framesSources.toString();
    }

    /**
	 * Append frames source.
	 *
	 * @param textContent  the text content
	 * @param framesSource the frames source
	 * @return the string
	 * @throws Exception the exception
	 */
    public static String appendFramesSource(final String textContent, final String framesSource) throws Exception {
        Document document = getDocument(textContent);

        List<Element> noframes = getElementsByTagName(document, "noframes");
        if (noframes.isEmpty()) {
            Document frameDocument = getDocument("<NOFRAMES>\n<BODY>\n" + framesSource + "</BODY>\n</NOFRAMES>\n");
            NodeList childNodes = document.getChildNodes();
            for (int i = childNodes.getLength() - 1; i > 0; i--) {
                if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    addFrameDocument((Element) childNodes.item(i), frameDocument);
                    break;
                }
            }
        } else {
            Element noframe = noframes.get(0);
            NodeList bodies = noframe.getElementsByTagName("BODY");
            if (bodies.getLength() == 0) {
                // Si tiene NOFRAMES sin BODY, lo creamos y lo metemos dentro
                Document frameDocument = getDocument("<BODY>\n" + CrawlerDOMUtils.serializeNodeList(noframe.getChildNodes()) + framesSource + "</BODY>\n");

                Node child = noframe.getFirstChild();
                while (child != null) {
                    Node childToRemove = child;
                    child = child.getNextSibling();
                    noframe.removeChild(childToRemove);
                }

                addFrameDocument(noframe, frameDocument);
            } else {
                // Si tiene NOFRAMES con BODY, lo metemos dentro
                Element body = (Element) bodies.item(0);
                Document frameDocument = getDocument(framesSource);
                addFrameDocument(body, frameDocument);
            }
        }

        return CrawlerDOMUtils.serializeDocument(document);
    }

    /**
	 * Adds the frame document.
	 *
	 * @param element       the element
	 * @param frameDocument the frame document
	 */
    private static void addFrameDocument(final Element element, final Document frameDocument) {
        final NodeList frameChildren = frameDocument.getChildNodes();

        for (int i = 0; i < frameChildren.getLength(); i++) {
            if (frameChildren.item(i).getNodeType() == Node.ELEMENT_NODE || frameChildren.item(i).getNodeType() == Node.TEXT_NODE) {
                element.appendChild(element.getOwnerDocument().importNode(frameChildren.item(i), true));
            }
        }
    }

    /**
	 * Adds the frame document sibling.
	 *
	 * @param parentElement the parent element
	 * @param element       the element
	 * @param frameDocument the frame document
	 */
    private static void addFrameDocumentSibling(final Element parentElement, final Element element, final Document frameDocument) {
        final NodeList frameChildren = frameDocument.getChildNodes();

        for (int i = 0; i < frameChildren.getLength(); i++) {
            if (frameChildren.item(i).getNodeType() == Node.ELEMENT_NODE || frameChildren.item(i).getNodeType() == Node.TEXT_NODE) {
                parentElement.insertBefore(element.getOwnerDocument().importNode(frameChildren.item(i), true), element);
            }
        }
    }

    /**
	 * Append iframes source.
	 *
	 * @param rootUrl     the root url
	 * @param textContent the text content
	 * @return the string
	 * @throws Exception the exception
	 */
    public static String appendIframesSource(String rootUrl, String textContent) throws Exception {
        final Document document = getDocument(textContent);
        final List<Element> iframes = getElementsByTagName(document, "iframe");
        if (!iframes.isEmpty()) {
            for (Element iframe : iframes) {
                try {
                    if (hasAttribute(iframe, "src") && !"#".equals(getAttribute(iframe, "src")) && !getAttribute(iframe, "src").endsWith(".gif")) {
                        final String frameUrl = new URL(new URL(rootUrl), getAttribute(iframe, "src")).toString();
                        String frameSource = "<!-- Código HTML del iframe localizado en " + frameUrl + " -->\n\n" +
                                getFrameContent(frameUrl) +
                                "<!-- Fin del Código HTML del iframe localizado en " + frameUrl + " -->\n\n";

                        frameSource = createAbsoluteHrefs(frameSource, frameUrl);

                        Document frameDocument = getDocument(frameSource);

                        addFrameDocumentSibling((Element) iframe.getParentNode(), iframe, frameDocument);
                    }
                } catch (Exception e) {
                    Logger.putLog("Error al recuperar el contenido del IFRAME: ", CrawlerUtils.class, Logger.LOG_LEVEL_INFO, e);
                }
            }

            return CrawlerDOMUtils.serializeDocument(document);
        } else {
            return textContent;
        }
    }


    /**
	 * Creates the absolute hrefs.
	 *
	 * @param textContent the text content
	 * @param frameUrl    the frame url
	 * @return the string
	 * @throws Exception the exception
	 */
    private static String createAbsoluteHrefs(String textContent, String frameUrl) throws Exception {
        Document frameDocument = getDocument(textContent);
        List<Element> links = getElementsByTagName(frameDocument, "a");
        for (Element link : links) {
            if (hasAttribute(link, "href")) {
                try {
                    link.setAttribute("href", new URL(new URL(frameUrl), getAttribute(link, "href")).toString());
                } catch (Exception e) {
                    Logger.putLog(String.format("Error al crear la URL absoluta de %s y %s", getAttribute(link, "href"), frameUrl), CrawlerUtils.class, Logger.LOG_LEVEL_WARNING, e);
                }
            }
        }
        PropertiesManager pmgr = new PropertiesManager();
        int maxNumElements = Integer.parseInt(pmgr.getValue("crawler.core.properties", "max.num.descendants.to.serialize"));
        if (frameDocument.getElementsByTagName("*").getLength() < maxNumElements) {
            return serializeDocument(frameDocument);
        } else {
            return "";
        }
    }

}
