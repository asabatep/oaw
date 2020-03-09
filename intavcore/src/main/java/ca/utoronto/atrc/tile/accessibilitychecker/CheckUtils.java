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
package ca.utoronto.atrc.tile.accessibilitychecker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.inteco.common.IntavConstants;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.common.utils.StringUtils;
import es.inteco.intav.form.CheckedLinks;
import es.inteco.intav.utils.EvaluatorUtils;

/**
 * The Class CheckUtils.
 */
public final class CheckUtils {
	/** The Constant WHITE_LIST. */
	private static final List<String> WHITE_LIST = Arrays.asList(".w3.org", ".w3c.es", ".tawdis.net", ".twitter.com", ".facebook.com", ".flicker.com", ".tuenti.com", ".google.com", ".google.es",
			".youtube.com", ".pinterest.com");

	/**
	 * Instantiates a new check utils.
	 */
	private CheckUtils() {
	}

	/**
	 * Comprueba si un nodo DOM Node es de tipo Element y con un determinado nombre.
	 *
	 * @param node el nodo DOM a comprobar
	 * @param name el nombre que debe coincidir si el nodo es de tipo Element
	 * @return true si el nodo es Element y su coincide con el indicado o false en caso contrario
	 */
	public static boolean isElementTagName(final Node node, final String name) {
		return node != null && node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase(name);
	}

	/**
	 * Obtiene el primer nodo de tipo Element que es hijo de un determinado nodo Element.
	 *
	 * @param element el nodo Element del que obtener el primer hijo de tipo Element
	 * @return el nodo Element si se encuentra o null en caso contrario
	 */
	public static Element getFirstChildElement(final Element element) {
		Node node = element.getFirstChild();
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
			node = node.getNextSibling();
		}
		if (node != null) {
			return (Element) node;
		} else {
			return null;
		}
	}

	/**
	 * Obtiene el primer nodo de tipo Element que es hermano de un determinado nodo Element.
	 *
	 * @param element el nodo Element del que obtener el primer hermano de tipo Element
	 * @return el nodo Element si se encuentra o null en caso contrario
	 */
	public static Element getFirstSiblingElement(final Element element) {
		Node node = element.getNextSibling();
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
			node = node.getNextSibling();
		}
		if (node != null) {
			return (Element) node;
		} else {
			return null;
		}
	}

	/**
	 * Obtiene la diferencia de nivel existente entre dos encabezados.
	 *
	 * @param previousHeading encabezado precedente.
	 * @param currentHeading  encabezado actual.
	 * @return la diferencia entre niveles siendo positiva si aumentamos el nivel (h1->h3) o negativa si disminuimos el nivel (h3->h1).
	 */
	public static int compareHeadingsLevel(final Element previousHeading, final Element currentHeading) {
		if (previousHeading != null && currentHeading != null) {
			final int previousHeadingLevel = Integer.parseInt(previousHeading.getTagName().substring(1));
			final int currentHeadingLevel = Integer.parseInt(currentHeading.getTagName().substring(1));
			return currentHeadingLevel - previousHeadingLevel;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the remote document.
	 *
	 * @param documentUrlStr the document url str
	 * @param remoteUrlStr   the remote url str
	 * @param redirects      the redirects
	 * @return the remote document
	 * @throws IOException  Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static Document getRemoteDocument(final String documentUrlStr, final String remoteUrlStr, int redirects) throws IOException, SAXException {
		final int maxNumRedirections = 5;
		try {
			final HttpURLConnection connection = EvaluatorUtils.getConnection(remoteUrlStr, "GET", true);
			connection.setRequestProperty("referer", documentUrlStr);
			connection.connect();
			final int responseCode = connection.getResponseCode();
			String type = connection.getHeaderField("Content-Type");
			if (responseCode == HttpURLConnection.HTTP_OK && !type.contains("application/pdf")) {
				final DOMParser parser = new CheckerParser(true);
				parser.parse(new InputSource(connection.getInputStream()));
				return parser.getDocument();
			} else {
				if ((redirects < maxNumRedirections)
						&& (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER)) {
					// Obtenemos la redirección
					String newUrl = connection.getHeaderField("Location");
					connection.disconnect();
					int countRedirects = redirects + 1;
					return getRemoteDocument(encodeUrl(newUrl), encodeUrl(newUrl), countRedirects);
				} else {
					return null;
				}
			}
		} catch (RuntimeException t) {
			Logger.putLog("Error conectarse a la url: " + remoteUrlStr + " - " + t.getMessage(), CheckUtils.class, Logger.LOG_LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * Gets the remote document renderer.
	 *
	 * @param documentUrlStr the document url str
	 * @param remoteUrlStr   the remote url str
	 * @return the remote document renderer
	 * @throws IOException  Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static Document getRemoteDocumentRenderer(final String documentUrlStr, final String remoteUrlStr) throws IOException, SAXException {
		try {
			final HttpURLConnection connection = EvaluatorUtils.getRendererConnection(remoteUrlStr, "GET", true);
			connection.setRequestProperty("referer", documentUrlStr);
			connection.connect();
			final int responseCode = connection.getResponseCode();
			String type = connection.getHeaderField("Content-Type");
			if (responseCode == HttpURLConnection.HTTP_OK && !type.contains("application/pdf")) {
				final DOMParser parser = new CheckerParser(true);
				parser.parse(new InputSource(connection.getInputStream()));
				return parser.getDocument();
			} else {
				return null;
			}
		} catch (RuntimeException t) {
			Logger.putLog("Error conectarse a la url: " + remoteUrlStr + " - " + t.getMessage(), CheckUtils.class, Logger.LOG_LEVEL_ERROR);
			return null;
		}
	}

	/**
	 * Gets the element text.
	 *
	 * @param checkedNode the checked node
	 * @param backward    the backward
	 * @param inlineTags  the inline tags
	 * @return the element text
	 */
	public static String getElementText(Node checkedNode, boolean backward, final List<String> inlineTags) {
		String text = "";
		while (checkedNode != null && (StringUtils.isEmpty(text) || StringUtils.isOnlyBlanks(text))) {
			if (backward) {
				checkedNode = checkedNode.getPreviousSibling();
			} else {
				checkedNode = checkedNode.getNextSibling();
			}
			if (checkedNode != null && checkedNode.getTextContent() != null
					&& (checkedNode.getNodeType() == Node.TEXT_NODE || (checkedNode.getNodeType() == Node.ELEMENT_NODE && inlineTags.contains(checkedNode.getNodeName().toUpperCase())))) {
				text = checkedNode.getTextContent().trim();
			}
		}
		return text;
	}

	/**
	 * Se mira que el anterior o el siguiente nodo al elemento verificado cumpla la expresión regular para ser un posible candidato a elemento falso de lista. Hay que tener en cuenta el caso
	 * siguiente: "<br/>
	 * <strong>*uno</strong>": con espacios en blanco y una etiqueta en línea se añadiría un nodo de tipo texto vacío que habría que ignorar.
	 *
	 * @param checkedElement the checked element
	 * @param inlineTags     the inline tags
	 * @param pattern        the pattern
	 * @param backward       the backward
	 * @return true, if is false br node
	 */
	public static boolean isFalseBrNode(final Element checkedElement, final List<String> inlineTags, final Pattern pattern, boolean backward) {
		final String text = getElementText(checkedElement, backward, inlineTags);
		final Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	/**
	 * Gets the base url.
	 *
	 * @param documentElement the document element
	 * @return the base url
	 */
	public static String getBaseUrl(final Element documentElement) {
		final NodeList bases = documentElement.getElementsByTagName("base");
		for (int i = bases.getLength() - 1; i >= 0; i--) {
			final Element base = (Element) bases.item(i);
			if (base.hasAttribute("href") && StringUtils.isNotEmpty(base.getAttribute("href"))) {
				return base.getAttribute("href");
			}
		}
		return null;
	}

	/**
	 * Checks if is valid url.
	 *
	 * @param elementRoot the element root
	 * @param nodeNode    the node node
	 * @return true, if is valid url
	 */
	public static boolean isValidUrl(final Element elementRoot, final Node nodeNode) {
		URL remoteUrl = null;
		URL documentUrl = null;
		CheckedLinks checkedLinks = null;
		try {
			if (!isAbsolute(nodeNode.getTextContent().trim())) {
				final String base = CheckUtils.getBaseUrl(elementRoot);
				documentUrl = base != null ? new URL(base) : new URL((String) elementRoot.getUserData("url"));
				remoteUrl = new URL(documentUrl, encodeUrl(nodeNode.getTextContent().trim()));
			} else {
				remoteUrl = new URL(encodeUrl(nodeNode.getTextContent().trim()));
			}
			if (!remoteUrl.getProtocol().startsWith("http")) {
				return true;
			}
			// Consideramos que cualquier enlace a W3C (Internacional o España) o al portal TAW funcionan siempre además
			// de enlaces a las redes sociales más famosas (twitter, facebook, flickr, tuenti,...).
			if (WHITE_LIST.contains(getDomainFromHost(remoteUrl.getHost()))) {
				return true;
			}
			final PropertiesManager pmgr = new PropertiesManager();
			final List<String> allowedPorts = Arrays.asList(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "broken.links.allowed.ports").split(";"));
			if (allowedPorts.contains(String.valueOf(remoteUrl.getPort()))) {
				checkedLinks = (CheckedLinks) elementRoot.getUserData("checkedLinks");
				if (checkedLinks == null) {
					checkedLinks = new CheckedLinks();
					elementRoot.setUserData("checkedLinks", checkedLinks, null);
				}
				if (checkedLinks.getCheckedLinks().contains(remoteUrl.toString())) {
					// Los enlaces ya verificados los damos por buenos, no puntúan mal varias veces
					return true;
				} else if (!checkedLinks.getBrokenLinks().contains(remoteUrl.toString()) && !checkedLinks.getAvailablelinks().contains(remoteUrl.toString())) {
					Logger.putLog("Verificando que existe la URL " + nodeNode.getTextContent() + " --> " + remoteUrl.toString(), Check.class, Logger.LOG_LEVEL_DEBUG);
					final HttpURLConnection connection = EvaluatorUtils.getConnection(remoteUrl.toString(), "GET", true);
					if (documentUrl != null) {
						connection.setRequestProperty("referer", documentUrl.toString());
					}
					connection.connect();
					final int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
						checkedLinks.getBrokenLinks().add(remoteUrl.toString());
						Logger.putLog("Encontrado enlace roto: " + nodeNode.getTextContent() + " --> " + remoteUrl.toString(), Check.class, Logger.LOG_LEVEL_DEBUG);
						return false;
					} else {
						checkedLinks.getAvailablelinks().add(remoteUrl.toString());
						return true;
					}
				} else {
					if (checkedLinks.getBrokenLinks().contains(remoteUrl.toString())) {
						Logger.putLog("Encontrado enlace roto: " + nodeNode.getTextContent() + " --> " + remoteUrl.toString(), Check.class, Logger.LOG_LEVEL_DEBUG);
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (UnknownHostException e) {
			// Si no se puede conectar porque no se reconoce el Host la url no es válida
			return false;
		} catch (MalformedURLException e) {
			// Si la url no está bien formada porque usa protocolos no http (javascript:, tel:, mailto:...) se considera igualmente válida
			return true;
		} catch (Exception e) {
			if ("Already connected".equals(e.getMessage())) {
				return true;
			}
			Logger.putLog("Error al verificar si el elemento " + remoteUrl + " está roto:" + e.getMessage(), CheckUtils.class, Logger.LOG_LEVEL_WARNING);
			return false;
		} finally {
			if (remoteUrl != null && checkedLinks != null) {
				checkedLinks.getCheckedLinks().add(remoteUrl.toString());
			}
		}
		return true;
	}

	/**
	 * Checks if is absolute.
	 *
	 * @param url the url
	 * @return true, if is absolute
	 */
	private static boolean isAbsolute(final String url) {
		return url.startsWith("http");
	}

	/**
	 * Encode url.
	 *
	 * @param path the path
	 * @return the string
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private static String encodeUrl(String path) throws UnsupportedEncodingException {
		// Remover tildes
		path = path.replaceAll("[ \\+]", "%20");
		String[] pathArray = path.split("[:\\./?&=#(%20)]");
		for (String aPathArray : pathArray) {
			path = path.replaceAll(aPathArray, URLEncoder.encode(aPathArray, "UTF-8"));
		}
		return path;
	}

	/**
	 * Checks for content.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	private static boolean hasContent(final Node node) {
		final PropertiesManager pmgr = new PropertiesManager();
		final List<String> elements = Arrays.asList(pmgr.getValue("intav.properties", "content.tags").split(";"));
		if (node.getNodeType() == Node.TEXT_NODE && StringUtils.isNotEmpty(node.getTextContent()) && !StringUtils.isOnlyBlanks(node.getTextContent())) {
			return true;
		} else if (node.getNodeType() == Node.ELEMENT_NODE && elements.contains(node.getNodeName().toUpperCase())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is previous header.
	 *
	 * @param str1 the str 1
	 * @param str2 the str 2
	 * @return true, if is previous header
	 */
	private static boolean isPreviousHeader(final String str1, final String str2) {
		try {
			if (!str2.substring(0, 1).equalsIgnoreCase("h")) {
				return false;
			} else if (Long.valueOf(str2.substring(1, str2.length())) != null && Long.valueOf(str2.substring(1, str2.length())) > Long.valueOf(str1.substring(1, str1.length()))) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Checks if is empty descendent content.
	 *
	 * @param node         the node
	 * @param elementGiven the element given
	 * @return the int
	 */
	public static int isEmptyDescendentContent(Node node, final Element elementGiven) {
		final PropertiesManager pmgr = new PropertiesManager();
		final List<String> noContentTags = Arrays.asList(pmgr.getValue("intav.properties", "ignored.tags").split(";"));
		while (node != null) {
			if (!node.getNodeName().equalsIgnoreCase(elementGiven.getNodeName()) && !isPreviousHeader(node.getNodeName(), elementGiven.getNodeName())) {
				// Si tiene contenido devuelves false
				if (node.getNodeType() == Node.TEXT_NODE) {
					if (StringUtils.isNotEmpty(node.getTextContent()) && !StringUtils.isOnlyBlanks(node.getTextContent())) {
						return IntavConstants.IS_NOT_EMPTY;
					}
				} else if (!noContentTags.contains(node.getNodeName().toUpperCase())) {
					if (CheckUtils.hasContent(node)) {
						return IntavConstants.IS_NOT_EMPTY;
					} else {
						int hasChildContent = CheckUtils.hasChildContent(node, elementGiven);
						if (hasChildContent == IntavConstants.HAS_CONTENT) {
							return IntavConstants.IS_NOT_EMPTY;
						} else if (hasChildContent == IntavConstants.EQUAL_HEADER_TAG) {
							return IntavConstants.EQUAL_HEADER_TAG;
						}
					}
				}
			} else {
				return IntavConstants.EQUAL_HEADER_TAG;
			}
			node = node.getNextSibling();
		}
		return IntavConstants.IS_EMPTY;
	}

	/**
	 * Checks for child content.
	 *
	 * @param node         the node
	 * @param elementGiven the element given
	 * @return the int
	 */
	private static int hasChildContent(final Node node, final Element elementGiven) {
		final NodeList nodeList = node.getChildNodes();
		final PropertiesManager pmgr = new PropertiesManager();
		final List<String> elements = Arrays.asList(pmgr.getValue("intav.properties", "ignored.tags").split(";"));
		if (node.getNodeName().equalsIgnoreCase(elementGiven.getNodeName()) && isPreviousHeader(node.getNodeName(), elementGiven.getNodeName())) {
			return IntavConstants.EQUAL_HEADER_TAG;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equalsIgnoreCase(elementGiven.getNodeName()) || isPreviousHeader(node.getNodeName(), elementGiven.getNodeName())) {
				return IntavConstants.EQUAL_HEADER_TAG;
			} else if (!elements.contains(nodeList.item(i).getNodeName().toUpperCase())) {
				if (CheckUtils.hasContent(nodeList.item(i))) {
					return IntavConstants.HAS_CONTENT;
				} else {
					int hasContentChild = hasChildContent(nodeList.item(i), elementGiven);
					if (hasContentChild == IntavConstants.HAS_CONTENT) {
						return IntavConstants.HAS_CONTENT;
					} else if (hasContentChild == IntavConstants.EQUAL_HEADER_TAG) {
						return IntavConstants.EQUAL_HEADER_TAG;
					}
				}
			}
		}
		return IntavConstants.HAS_NOT_CONTENT;
	}

	/**
	 * Gets the image reader.
	 *
	 * @param img the img
	 * @param url the url
	 * @return the image reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ImageReader getImageReader(final Element img, final URL url) throws IOException {
		if (img.getUserData(IntavConstants.GIF_VERIFICATED) == null && img.getUserData(IntavConstants.GIF_READER) == null) {
			img.setUserData(IntavConstants.GIF_VERIFICATED, Boolean.TRUE, null);
			Logger.putLog("Descargando la imagen " + url + " para analizar su contenido", CheckUtils.class, Logger.LOG_LEVEL_INFO);
			final HttpURLConnection connection = EvaluatorUtils.getConnection(url.toString(), "GET", true);
			final PropertiesManager pmgr = new PropertiesManager();
			connection.setConnectTimeout(Integer.parseInt(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "gif.connection.timeout")));
			connection.setReadTimeout(Integer.parseInt(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "gif.connection.timeout")));
			ImageInputStream imageInputStream = new MemoryCacheImageInputStream(connection.getInputStream());
			final java.util.Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(imageInputStream);
				img.setUserData(IntavConstants.GIF_READER, reader, null);
				return reader;
			} else {
				return null;
			}
		} else {
			return (ImageReader) img.getUserData(IntavConstants.GIF_READER);
		}
	}

	/**
	 * Método para determinar si un enlace pertenece al mismo dominio o no que la página. Se realiza una comprobación básica sobre la sintaxis de la url. No se comprueba si mediante redirección se
	 * sale o no del dominio
	 *
	 * @param url  la url de la página
	 * @param link atributo href no vacío de un enlace (etiqueta a)
	 * @return true si el enlace link pertenece al mismo dominio que el enlace url false en caso contrario
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean checkLinkInDomain(final String url, final String link) throws IOException {
		if (URI.create(CheckUtils.encodeUrl(link)).isAbsolute()) {
			final String domain = getDomainFromHost(new URL(url).getHost());
			final String newHost = getDomainFromHost(new URL(new URL(url), CheckUtils.encodeUrl(link)).getHost());
			return newHost.equalsIgnoreCase(domain);
		} else {
			// Si el enlace es relativo pertenece fijo
			return true;
		}
	}

	/**
	 * Gets the domain from host.
	 *
	 * @param host the host
	 * @return the domain from host
	 */
	private static String getDomainFromHost(final String host) {
		int index = host.lastIndexOf('.');
		if (index != -1) {
			// Comprobamos si la url tiene dominios superiores a nivel 2
			// (ej http://www.algo.es o sólo http://algo.es)
			index = host.lastIndexOf('.', index - 1);
			if (index != -1) {
				return host.substring(index);
			} else {
				// Si la url comenzó directamente con el dominio de 2º nivel
				// http://algo.es le anteponemos el caracter '.' para indicar
				// comienzo de dominio
				return "." + host;
			}
		}
		return host;
	}
}
