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
Copyright ©2006, University of Toronto. All rights reserved.

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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

import es.gob.oaw.w3cvalidator.W3CValidatorProxy;
import es.inteco.common.CheckAccessibility;
import es.inteco.common.CssValidationError;
import es.inteco.common.IntavConstants;
import es.inteco.common.ValidationError;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.common.utils.StringUtils;
import es.inteco.intav.iana.IanaLanguages;
import es.inteco.intav.iana.IanaUtils;
import es.inteco.intav.utils.EvaluatorUtils;

/**
 * The Class EvaluatorUtility.
 */
public final class EvaluatorUtility {
	/** The evaluator. */
	// the evaluator
	private static Evaluator evaluator = null;
	/** The guideline map. */
	// guideline
	private static Map<String, Guideline> guidelineMap = new HashMap<>();
	/** The initialized. */
	// initialization flag
	private static boolean initialized = false;
	/** The all checks. */
	// the collection of all the checks
	private static AllChecks allChecks = new AllChecks();
	/** The iana languages. */
	private static IanaLanguages ianaLanguages = new IanaLanguages();
	/** The concurrent users. */
	private static int concurrentUsers = 0;

	/**
	 * Instantiates a new evaluator utility.
	 */
	private EvaluatorUtility() {
	}

	/**
	 * Checks if is initialized.
	 *
	 * @return true, if is initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Gets the concurrent users.
	 *
	 * @return the concurrent users
	 */
	public static int getConcurrentUsers() {
		return concurrentUsers;
	}

	/**
	 * Sets the concurrent users.
	 *
	 * @param concurrentUsers the new concurrent users
	 */
	public static void setConcurrentUsers(int concurrentUsers) {
		EvaluatorUtility.concurrentUsers = concurrentUsers;
	}

	/**
	 * Initialize.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	// initialize
	public static synchronized boolean initialize() throws Exception {
		if (initialized) {
			return true;
		}
		setSystemProperties();
		evaluator = new Evaluator();
		if (!loadAllChecks()) {
			return false;
		}
		if (!loadIanaLanguages()) {
			return false;
		}
		ImageIO.setUseCache(true);
		// A null value indicates that the system-dependent default temporary-file directory is to be used
		ImageIO.setCacheDirectory(null);
		initialized = true;
		return true;
	}

	/**
	 * Gets the evaluator.
	 *
	 * @return the evaluator
	 */
	public static Evaluator getEvaluator() {
		return evaluator;
	}

	/**
	 * Gets the iana languages.
	 *
	 * @return the iana languages
	 */
	public static IanaLanguages getIanaLanguages() {
		return ianaLanguages;
	}

	/**
	 * Gets the all checks.
	 *
	 * @return the all checks
	 */
	// Returns the object that holds all the possible accessibility checks.
	public static AllChecks getAllChecks() {
		return allChecks;
	}

	/**
	 * Gets the link text.
	 *
	 * @param node the node
	 * @return the link text
	 */
	// Returns the link text for the given element.
	public static String getLinkText(final Node node) {
		// get child text
		String textChild = getElementText(node);
		// get title attribute text
		String textTitle = ((Element) node).getAttribute("title");
		// check if there is an image within the element and get its alt text
		NodeList listImages = ((Element) node).getElementsByTagName("img");
		String textAlt = "";
		for (int x = 0; x < listImages.getLength(); x++) {
			Element elementImage = (Element) listImages.item(x);
			String stringTemp = elementImage.getAttribute("alt");
			// use the longest alt text for any image within the anchor
			if (stringTemp.length() > textAlt.length()) {
				textAlt = stringTemp;
			}
		}
		// return the longest string
		if (textChild.length() >= textTitle.length()) {
			if (textChild.length() >= textAlt.length()) {
				return textChild;
			} else {
				return textAlt;
			}
		} else {
			if (textTitle.length() >= textAlt.length()) {
				return textTitle;
			} else {
				return textAlt;
			}
		}
	}

	/**
	 * Gets the element text.
	 *
	 * @param node                  the node
	 * @param getOnlyInlineTagsText the get only inline tags text
	 * @return the element text
	 */
	public static String getElementText(final Node node, final boolean getOnlyInlineTagsText) {
		List<String> inlineTags = null;
		if (getOnlyInlineTagsText) {
			PropertiesManager pmgr = new PropertiesManager();
			inlineTags = Arrays.asList(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "inline.tags.list").toUpperCase().split(";"));
		}
		final String text = getElementTextLoop(node, inlineTags);
		if (text.length() == 0) {
			return "";
		} else {
			// remove any tabs, lineends, multiple spaces, and leading/trailing whitespace
			return normalizeString(text);
		}
	}

	/**
	 * Normalize string.
	 *
	 * @param text the text
	 * @return the string
	 */
	private static String normalizeString(String text) {
		final StringBuilder stringBuilder = new StringBuilder(text.length());
		boolean space = false;
		for (int x = 0; x < text.length(); x++) {
			if (text.charAt(x) != '\t' && text.charAt(x) != '\n') {
				if (text.charAt(x) == ' ') {
					if (space) {
						continue;
					}
					space = true;
				} else {
					space = false;
				}
				stringBuilder.append(text.charAt(x));
			}
		}
		return stringBuilder.toString().replaceAll("\\s{2,}", " ").trim();
	}

	/**
	 * Gets the element text.
	 *
	 * @param node the node
	 * @return the element text
	 */
	// returns the text that is contained by the given element
	public static String getElementText(Node node) {
		return getElementText(node, false);
	}

	/**
	 * Gets the element text loop.
	 *
	 * @param node       the node
	 * @param inlineTags the inline tags
	 * @return the element text loop
	 */
	// Recursive function that returns a string containing all the text within the node
	private static String getElementTextLoop(Node node, List<String> inlineTags) {
		try {
			StringBuilder buffer = new StringBuilder();
			NodeList childNodes = node.getChildNodes();
			for (int x = 0; x < childNodes.getLength(); x++) {
				Node nodeChild = childNodes.item(x);
				if (nodeChild == null) {
					break;
				}
				if (inlineTags != null && nodeChild.getNodeType() == Node.ELEMENT_NODE && !inlineTags.contains(nodeChild.getNodeName().toUpperCase())) {
					continue;
				}
				// comments within scripts are treated as 'text' nodes so ignore them
				if (nodeChild.getNodeType() == Node.ELEMENT_NODE && nodeChild.getNodeName().equalsIgnoreCase("script")) {
					continue;
				}
				if (nodeChild.getNodeType() == Node.TEXT_NODE) {
					buffer.append(nodeChild.getNodeValue());
				}
				buffer.append(getElementTextLoop(nodeChild, inlineTags));
			}
			return buffer.toString();
		} catch (Exception e) {
			Logger.putLog("Exception: ", EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
		}
		return "";
	}

	/**
	 * Gets the label text.
	 *
	 * @param node the node
	 * @return the label text
	 */
	// returns the text that is contained by the given label element
	public static String getLabelText(final Node node) {
		final String text = getLabelTextLoop(node);
		if (text.length() == 0) {
			return "";
		} else {
			// remove any tabs, lineends, multiple spaces, and leading/trailing whitespace
			return normalizeString(text);
		}
	}

	// Recursive function that returns a string containing all the text within the node.
	/**
	 * Gets the label text loop.
	 *
	 * @param node the node
	 * @return the label text loop
	 */
	// Any text within a 'select' element is ignored.
	private static String getLabelTextLoop(final Node node) {
		final StringBuilder buffer = new StringBuilder();
		final NodeList childNodes = node.getChildNodes();
		for (int x = 0; x < childNodes.getLength(); x++) {
			final Node nodeChild = childNodes.item(x);
			if (nodeChild.getNodeType() == Node.ELEMENT_NODE) {
				// comments within scripts are treated as 'text' nodes so ignore them
				final String nodeName = nodeChild.getNodeName();
				if ("script".equalsIgnoreCase(nodeName) || "select".equalsIgnoreCase(nodeName)) {
					continue;
				}
				if ("abbr".equalsIgnoreCase(nodeName) || "input".equalsIgnoreCase(nodeName)) {
					buffer.append(((Element) nodeChild).getAttribute("title"));
					buffer.append(" ");
				}
				if ("img".equalsIgnoreCase(nodeName)) {
					buffer.append(((Element) nodeChild).getAttribute("alt"));
					buffer.append(" ");
				}
				if ("style".equalsIgnoreCase(nodeName)) {
					continue;
				}
			} else if (nodeChild.getNodeType() == Node.TEXT_NODE) {
				buffer.append(nodeChild.getNodeValue());
				buffer.append(" ");
			}
			buffer.append(getLabelTextLoop(nodeChild));
		}
		return buffer.toString();
	}

	/**
	 * Load all checks.
	 *
	 * @return true, if successful
	 */
	// Loads all the checks
	private static boolean loadAllChecks() {
		final PropertiesManager pmgr = new PropertiesManager();
		// checks should only be loaded once but can be reloaded
		if (!allChecks.isEmpty()) {
			Logger.putLog("Note: Loading checks after they are already loaded.", EvaluatorUtility.class, Logger.LOG_LEVEL_INFO);
			// clear the old collection of guidelines
			allChecks.clear();
		}
		final InputStream inputStream = EvaluatorUtility.class.getClassLoader().getResourceAsStream(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "check.path"));
		loadChecksFile(inputStream);
		checkPrerequisites();
		return true;
	}

	/**
	 * Check prerequisites.
	 */
	private static void checkPrerequisites() {
		// check the prerequisites for each check to make sure they are OK
		final List<Check> checks = allChecks.getChecks();
		for (Check check : checks) {
			final List<Integer> prerequisites = check.getPrerequisites();
			for (Integer prerequisiteId : prerequisites) {
				final Check checkPrerequisite = allChecks.getCheck(prerequisiteId);
				if (checkPrerequisite == null) {
					Logger.putLog("Warning: prerequisite check " + prerequisiteId + " on test " + check.getId() + " does not exist!", EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
				}
			}
		}
	}

	/**
	 * Load checks file.
	 *
	 * @param inputStream the input stream
	 * @return true, if successful
	 */
	// load a file that contains accessibility checks
	private static boolean loadChecksFile(final InputStream inputStream) {
		try {
			if (inputStream == null) {
				Logger.putLog("Error: Can't open checks file", EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
				return false;
			} else {
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();
				final Document docAllChecks = builder.parse(inputStream);
				final Element nodeRoot = docAllChecks.getDocumentElement();
				findChecksInMasterFile(nodeRoot);
			}
		} catch (Exception e) {
			Logger.putLog("Exception: ", EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
			return false;
		}
		return true;
	}

	/**
	 * Find checks in master file.
	 *
	 * @param nodeRoot the node root
	 */
	private static void findChecksInMasterFile(final Element nodeRoot) {
		// find all the checks in the master file
		final NodeList childNodes = nodeRoot.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("check")) {
				// get the check ID number
				final String stringId = ((Element) node).getAttribute("id");
				if ((stringId == null) || (stringId.length() < 1)) {
					Logger.putLog("Warning: Check has no ID in checks file ", EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
					continue;
				}
				int idNumber;
				try {
					idNumber = Integer.parseInt(stringId);
				} catch (NumberFormatException nfe) {
					Logger.putLog("Warning: Check has bad ID (" + stringId + ") in checks file", EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
					continue;
				}
				// have we already created this check?
				Check check = allChecks.getCheck(idNumber);
				if (check == null) {
					// no, so create a new check
					check = new Check();
					// initialize the check and add it to the list of checks
					if (check.initialize((Element) node, idNumber)) {
						allChecks.addCheck(check);
					}
				} else { // we already have the check
					// add new language text to the check
					check.setCheckText(node);
				}
			}
		}
	}

	/**
	 * Load iana languages.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private static boolean loadIanaLanguages() throws Exception {
		final PropertiesManager pmgr = new PropertiesManager();
		Logger.putLog("Cargando los códigos de lenguaje de IANA", EvaluatorUtility.class, Logger.LOG_LEVEL_INFO);
		final String ianaRegistries = IanaUtils.loadIanaRegistries(pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "iana.lang.codes.url"));
		ianaLanguages.setLanguages(IanaUtils.getIanaList(ianaRegistries, pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "iana.language.type")));
		ianaLanguages.setRegions(IanaUtils.getIanaList(ianaRegistries, pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "iana.region.type")));
		ianaLanguages.setVariants(IanaUtils.getIanaList(ianaRegistries, pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "iana.variant.type")));
		return true;
	}

	/**
	 * Load guideline.
	 *
	 * @param filename the filename
	 * @return the guideline
	 */
	// Returns the guideline from the guideline filename.
	public static Guideline loadGuideline(final String filename) {
		final String fullFilename;
		// check for .xml extension
		int indexDotxml = filename.indexOf(".xml");
		if (indexDotxml == -1) {
			// doesn't have .xml extension so add it
			fullFilename = filename + ".xml";
		} else {
			fullFilename = filename;
		}
		if (guidelineMap.containsKey(fullFilename)) {
			return guidelineMap.get(fullFilename);
		} else {
			final Guideline guideline = new Guideline();
			try (InputStream inputStream = EvaluatorUtility.class.getClassLoader().getResourceAsStream("guidelines/" + fullFilename)) {
				if (!guideline.initialize(inputStream, fullFilename)) {
					Logger.putLog("Error: guideline did not initialize: " + fullFilename, EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
					return null;
				} else {
					guidelineMap.put(fullFilename, guideline);
					return guideline;
				}
			} catch (Exception e) {
				Logger.putLog("Excepción: ", EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
				return null;
			}
		}
	}

	/**
	 * Sets the system properties.
	 */
	private static void setSystemProperties() {
		final PropertiesManager pmgr = new PropertiesManager();
		if (pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "http.proxy.active").equalsIgnoreCase(Boolean.TRUE.toString())) {
			System.setProperty("http.proxyHost", pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "http.proxy.host"));
			System.setProperty("http.proxyPort", pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "http.proxy.port"));
			System.setProperty("https.proxyHost", pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "http.proxy.host"));
			System.setProperty("https.proxyPort", pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "http.proxy.port"));
		}
	}

	/**
	 * Checks if is language code.
	 *
	 * @param stringGiven the string given
	 * @return true, if is language code
	 */
	public static boolean isLanguageCode(final String stringGiven) {
		final String[] langArray = stringGiven.split("-");
		if (StringUtils.isEmpty(langArray[0])) {
			// No ha especificado lenguaje
			return false;
		} else if (langArray.length == 1 && ianaLanguages.getLanguages().contains(langArray[0].toLowerCase())) {
			return true;
		} else {
			return langArray.length == 2 && ianaLanguages.getLanguages().contains(langArray[0].toLowerCase())
					&& (ianaLanguages.getRegions().contains(langArray[1].toUpperCase()) || ianaLanguages.getVariants().contains(langArray[1].toLowerCase()));
		}
	}

	/**
	 * Open url.
	 *
	 * @param filename the filename
	 * @return the url
	 */
	public static URL openUrl(String filename) {
		// make sure filename starts with 'http://'
		if (!filename.toLowerCase().startsWith("http://") && !filename.toLowerCase().startsWith("https://")) {
			filename = "http://" + filename;
		}
		try {
			final URI srcUri = new URI(filename);
			return srcUri.toURL();
		} catch (Exception e) {
			Logger.putLog("Note: Can't open file: " + filename, EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
			return null;
		}
	}
	// Loads the file and returns a Document object. Returns null if file can't be loaded.

	/**
	 * Load html file.
	 *
	 * @param checkAccessibility   the check accessibility
	 * @param htmlValidationNeeded the html validation needed
	 * @param cssValidationNeeded  the css validation needed
	 * @param language             the language
	 * @param fromCrawler          the from crawler
	 * @return the document
	 */
	public static Document loadHtmlFile(final CheckAccessibility checkAccessibility, boolean htmlValidationNeeded, boolean cssValidationNeeded, String language, boolean fromCrawler) {
		try {
			final HttpURLConnection connection = EvaluatorUtils.getRendererConnection(checkAccessibility.getUrl(), "GET", true);
			try {
				final long inicio = System.currentTimeMillis();
				// connect to the server
				connection.connect();
				final InputStream content = connection.getInputStream();
				final BufferedInputStream stream = new BufferedInputStream(content);
				// mark InputStream so we can restart it for validator
				if (stream.markSupported()) {
					stream.mark(Integer.MAX_VALUE);
				}
				final String charset = EvaluatorUtils.getResponseCharset(connection, stream);
				long tiempo = System.currentTimeMillis() - inicio;
				// Registramos en el log el tiempo de la conexión
				Logger.putLog("Tiempo tardado en cargar el HTML remoto: " + tiempo + " milisegundos", Evaluator.class, Logger.LOG_LEVEL_INFO);
				return loadHtmlFile(stream, checkAccessibility, htmlValidationNeeded, cssValidationNeeded, language, charset);
			} catch (Exception e) {
				Logger.putLog("Exception: " + checkAccessibility.getUrl(), EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
				return null;
			}
		} catch (Exception e) {
			Logger.putLog("Exception: " + checkAccessibility.getUrl(), EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
			return null;
		}
	}

	/**
	 * Load html file.
	 *
	 * @param inputStream          the input stream
	 * @param checkAccessibility   the check accessibility
	 * @param htmlValidationNeeded the html validation needed
	 * @param cssValidationNeeded  the css validation needed
	 * @param language             the language
	 * @param charset              the charset
	 * @return the document
	 */
	public static Document loadHtmlFile(final InputStream inputStream, final CheckAccessibility checkAccessibility, boolean htmlValidationNeeded, boolean cssValidationNeeded, String language,
			String charset) {
		try {
			// create a DOM of the HTML file
			CheckerParser parser = new CheckerParser();
			Document doc = null;
			Element elementRoot = null;
			String content = StringUtils.getContentAsString(inputStream, charset);
			for (int i = 0; i < 2 && (doc == null || elementRoot == null); i++) {
				content = addFinalTags(content);
				final InputStream newInputStream = new ByteArrayInputStream(content.getBytes(charset));
				if (newInputStream.markSupported()) {
					newInputStream.mark(Integer.MAX_VALUE);
				}
				try {
					// Reseteamos el stream para volver a analizarlo
					newInputStream.reset();
					final InputSource inputSource = new InputSource(newInputStream);
					parser.parse(inputSource);
					doc = parser.getDocument();
					// store the doctype status on the root element
					elementRoot = doc.getDocumentElement();
				} catch (Exception e) {
					parser = new CheckerParser(true);
					Logger.putLog("Error al parsear al documento. Se intentará de nuevo con el balanceo de etiquetas", EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
				}
			}
			if (!content.toUpperCase().contains("<HTML") && doc.getElementsByTagName("html") != null && doc.getElementsByTagName("html").getLength() > 0) {
				doc.getElementsByTagName("html").item(0).setUserData("realHtml", "false", null);
			}
			List<Node> nodeList = new ArrayList<>();
			EvaluatorUtils.generateNodeList(elementRoot, nodeList, 1000);
			if (parser.hasDoctype()) {
				elementRoot.setUserData("doctype", "true", null);
				elementRoot.setUserData("doctypeType", parser.getHtmlXhtml(), null);
				elementRoot.setUserData("doctypeVersion", parser.getHtmlVersion(), null);
				elementRoot.setUserData("doctypeLine", String.valueOf(parser.getDoctypeLine()), null);
				elementRoot.setUserData("doctypePublicId", parser.getDoctypePublicId(), null);
				elementRoot.setUserData("doctypeSystemId", parser.getDoctypeSystemId(), null);
				elementRoot.setUserData("doctypeSource", generateDoctypeSource(doc.getDoctype()), null);
			} else {
				elementRoot.setUserData("doctype", "false", null);
			}
			elementRoot.setUserData("url", checkAccessibility.getUrl(), null);
			// Aprovechamos para guardar el código fuente en una variable
			inputStream.reset();
			elementRoot.setUserData("source", StringUtils.getContentAsString(inputStream, charset), null);
			// count the number of rows and columns in each table
			// store the result on the node
			NodeList listTables = doc.getDocumentElement().getElementsByTagName("table");
			for (int x = 0; x < listTables.getLength(); x++) {
				int countRows = countElements((Element) listTables.item(x), "tr", "table", -1);
				listTables.item(x).setUserData("rows", countRows, null);
				// number of columns in the table
				// count the 'th' and 'td' elements in first row
				int maxCountCols = 0;
				NodeList listRows = ((Element) listTables.item(x)).getElementsByTagName("tr");
				if (listRows.getLength() > 0) {
					for (int i = 0; i < listRows.getLength(); i++) {
						int countCols = 0;
						NodeList listCols = listRows.item(i).getChildNodes();
						for (int y = 0; y < listCols.getLength(); y++) {
							if ((listCols.item(y).getNodeName().equalsIgnoreCase("td")) || (listCols.item(y).getNodeName().equalsIgnoreCase("th"))) {
								countCols++;
								// increment number of columns depending on 'colspan' attribute
								String stringColspan = ((Element) listCols.item(y)).getAttribute("colspan");
								try {
									int additionalCols = Integer.parseInt(stringColspan);
									countCols += additionalCols - 1;
								} catch (NumberFormatException e) {
									Logger.putLog("Exception colspan no válido", EvaluatorUtility.class, Logger.LOG_LEVEL_DEBUG);
								}
							}
						}
						if (countCols > maxCountCols) {
							maxCountCols = countCols;
						}
					}
				}
				listTables.item(x).setUserData("cols", maxCountCols, null);
			}
			// link map elements and area elements with their associated img/object elements
			NodeList listMaps = doc.getDocumentElement().getElementsByTagName("map");
			if (listMaps.getLength() > 0) {
				// create a list of all maps in the document
				for (int x = 0; x < listMaps.getLength(); x++) {
					Element elementMap = (Element) listMaps.item(x);
					String stringNameMap = "#" + elementMap.getAttribute("name");
					if (stringNameMap.length() == 0) {
						continue;
					}
					// find the image or object for this map
					NodeList listImages = doc.getDocumentElement().getElementsByTagName("img");
					boolean bFoundIt = false;
					String stringImageSource = "";
					for (int y = 0; y < listImages.getLength(); y++) {
						String stringUsemap = ((Element) listImages.item(y)).getAttribute("usemap");
						if ((stringUsemap != null) && (stringUsemap.equals(stringNameMap))) {
							stringImageSource = ((Element) listImages.item(y)).getAttribute("src");
							bFoundIt = true;
							break;
						}
					}
					// look through objects if map not found in images
					if (!bFoundIt) {
						NodeList listObjects = doc.getDocumentElement().getElementsByTagName("object");
						for (int y = 0; y < listObjects.getLength(); y++) {
							String stringUsemap = ((Element) listObjects.item(y)).getAttribute("usemap");
							if ((stringUsemap != null) && (stringUsemap.equals(stringNameMap))) {
								stringImageSource = ((Element) listObjects.item(y)).getAttribute("data");
								bFoundIt = true;
								break;
							}
						}
					}
					// associate the image with the map (and map's area elements)
					if (bFoundIt) {
						elementMap.setUserData("map-src", stringImageSource, null);
						// get list of area elements contained by map
						NodeList listAreas = elementMap.getElementsByTagName("area");
						for (int y = 0; y < listAreas.getLength(); y++) {
							listAreas.item(y).setUserData("map-src", stringImageSource, null);
						}
					}
				}
			}
			// reread the file and validate it
			if (htmlValidationNeeded) {
				if (!inputStream.markSupported()) {
					// can't reset stream, so can't validate, just return doc without validation
					Logger.putLog("Warning: Can't validate file: " + checkAccessibility.getUrl(), EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
				} else {
					// Validamos el documento
					long time = System.currentTimeMillis();
					elementRoot.setUserData("validationErrors", getValidationErrors(checkAccessibility, inputStream, charset), null);
					Logger.putLog("Tiempo tardado en validar el HTML: " + (System.currentTimeMillis() - time) + " milisegundos", EvaluatorUtility.class, Logger.LOG_LEVEL_INFO);
				}
			}
			if (cssValidationNeeded) {
				// Validamos los CSS
				validateCss(doc, checkAccessibility, language);
			}
			// Cargamos los CSS
			loadCss(doc);
			// return the document
			return doc;
		} catch (IOException e) {
			Logger.putLog("Exception al evaluar " + checkAccessibility.getUrl() + ": ", EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
			return null;
		}
	}

	/**
	 * Adds the final tags.
	 *
	 * @param inStr the in str
	 * @return the string
	 */
	private static String addFinalTags(String inStr) {
		final PropertiesManager pmgr = new PropertiesManager();
		final String regExpMatcher = pmgr.getValue("intav.properties", "incompleted.tags.reg.exp.matcher");
		final String[] tags = pmgr.getValue("intav.properties", "incompleted.tags").split(";");
		for (String tag : tags) {
			final Pattern pattern = Pattern.compile(regExpMatcher.replace("@", tag), Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			final Matcher matcher = pattern.matcher(inStr);
			String matchedTag;
			while (matcher.find()) {
				matchedTag = matcher.group(1);
				inStr = inStr.replace(matchedTag, matchedTag.subSequence(0, matchedTag.length() - 1) + "/>");
			}
		}
		return inStr;
	}

	/**
	 * Validate css.
	 *
	 * @param doc                the doc
	 * @param checkAccessibility the check accessibility
	 * @param language           the language
	 */
	private static void validateCss(final Document doc, final CheckAccessibility checkAccessibility, final String language) {
		final Element elementRoot = doc.getDocumentElement();
		final List<CssValidationError> cssValidationErrors = new ArrayList<>();
		long time = System.currentTimeMillis();
		if (StringUtils.isNotEmpty(checkAccessibility.getUrl())) {
			cssValidationErrors.addAll(getCssValidationErrors(checkAccessibility.getUrl(), language));
		} else {
			cssValidationErrors.addAll(getCssValidationNotWorksMessage());
			final List<Element> styleSheets = getStyleSheets(doc);
			for (Element styleSheet : styleSheets) {
				cssValidationErrors.addAll(getCssValidationErrors(styleSheet.getAttribute("href"), language));
			}
		}
		Logger.putLog("Tiempo tardado en validar los CSS: " + (System.currentTimeMillis() - time) + " milisegundos", EvaluatorUtility.class, Logger.LOG_LEVEL_INFO);
		elementRoot.setUserData("cssValidationErrors", cssValidationErrors, null);
	}

	/**
	 * Load css.
	 *
	 * @param doc the doc
	 */
	private static void loadCss(final Document doc) {
		final List<Element> styleSheets = getStyleSheets(doc);
		final Element elementRoot = doc.getDocumentElement();
		for (Element styleSheet : styleSheets) {
			loadStyleSheet(styleSheet, (String) elementRoot.getUserData("url"));
		}
	}

	/**
	 * Load style sheet.
	 *
	 * @param styleSheet the style sheet
	 * @param urlRoot    the url root
	 */
	private static void loadStyleSheet(final Element styleSheet, final String urlRoot) {
		try {
			final URL cssUrl;
			if (StringUtils.isNotEmpty(urlRoot) && URI.create(urlRoot).isAbsolute()) {
				cssUrl = new URL(new URL(urlRoot), styleSheet.getAttribute("href"));
			} else {
				cssUrl = new URL(styleSheet.getAttribute("href"));
			}
			final HttpURLConnection connection = EvaluatorUtils.getConnection(cssUrl.toString(), "GET", true);
			// Add accept text/css to prevent some cases that text/html accpet produces 406 on server
			connection.setRequestProperty("Accept", "text/css;text/html");
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
				styleSheet.setUserData("css", StringUtils.getContentAsString(connection.getInputStream()), null);
			}
			connection.disconnect();
		} catch (Exception e) {
			Logger.putLog("Error al cargar la hoja de estilo localizada en " + styleSheet.getAttribute("href") + ": " + e.getMessage(), EvaluatorUtility.class, Logger.LOG_LEVEL_WARNING);
		}
	}

	/**
	 * Gets the style sheets.
	 *
	 * @param doc the doc
	 * @return the style sheets
	 */
	private static List<Element> getStyleSheets(final Document doc) {
		final List<Element> styleSheets = new ArrayList<>();
		final NodeList links = doc.getElementsByTagName("link");
		for (int i = 0; i < links.getLength(); i++) {
			final Element link = (Element) links.item(i);
			if ((link.hasAttribute("rel") && link.getAttribute("rel").equalsIgnoreCase("stylesheet")) || (link.hasAttribute("type") && link.getAttribute("type").equalsIgnoreCase("text/css"))) {
				styleSheets.add(link);
			}
		}
		return styleSheets;
	}

	/**
	 * Gets the css validation not works message.
	 *
	 * @return the css validation not works message
	 */
	private static List<CssValidationError> getCssValidationNotWorksMessage() {
		final List<CssValidationError> cssValidationErrors = new ArrayList<>();
		final CssValidationError cssValidationError = new CssValidationError();
		final PropertiesManager pmgr = new PropertiesManager();
		final String urlHuman = pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "url.w3c.css.validator.human.submit.code");
		final String link = "<a href='" + urlHuman + "' title='Enlace externo'>Validador de hojas de estilo del W3C</a>";
		cssValidationError
				.setCode("En el modo de análisis de código fuente, la validación de las hojas de estilo solo funcionará si éstas se encuentran enlazadas desde algún servidor con acceso externo.");
		cssValidationError.setCode(cssValidationError.getCode() + " Si no dispone de esta posibilidad, puede validar su hoja de estilos mediante el  " + link);
		cssValidationError.setLine(1);
		cssValidationError.setSummary(true);
		cssValidationErrors.add(cssValidationError);
		return cssValidationErrors;
	}

	/**
	 * Gets the validation errors.
	 *
	 * @param checkAccessibility the check accessibility
	 * @param inputStream        the input stream
	 * @param charset            the charset
	 * @return the validation errors
	 */
	// Devuelve los errores de validación del código HTML
	public static List<ValidationError> getValidationErrors(CheckAccessibility checkAccessibility, InputStream inputStream, String charset) {
		List<ValidationError> validationErrors = new ArrayList<>();
		try {
			// Reseteamos el inputStream a su estado inicial para poder convertirlo a texto
			inputStream.reset();
			String contents;
			if (checkAccessibility.isWebService() && StringUtils.isNotEmpty(checkAccessibility.getTemplateContent())) {
				contents = checkAccessibility.getTemplateContent();
			} else {
				contents = StringUtils.getContentAsString(inputStream, charset);
			}
			validationErrors = W3CValidatorProxy.callService(contents);
			// Corregimos el desajuste de una línea al meter el código en una plantilla HTML cuando viene del WS y es un fragmento de código
			if (checkAccessibility.isWebService() && StringUtils.isNotEmpty(checkAccessibility.getTemplateContent())) {
				for (ValidationError validationError : validationErrors) {
					validationError.setLine(validationError.getLine() - 1);
				}
			}
		} catch (Exception e) {
			Logger.putLog("Error al validar el código HTML de " + checkAccessibility.getUrl(), EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
		}
		return validationErrors;
	}

	/**
	 * Adds the validation summary.
	 *
	 * @param validationErrors the validation errors
	 * @param filename         the filename
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private static void addValidationSummary(final List<ValidationError> validationErrors, final String filename) throws UnsupportedEncodingException {
		final ValidationError validationError = new ValidationError();
		final PropertiesManager pmgr = new PropertiesManager();
		final String urlHuman = pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "url.w3c.validator.human").replace("{0}", URLEncoder.encode(filename, "UTF-8"));
		final String link = "<a href='" + urlHuman + "' title='Enlace externo'>Analizador sintáctico del W3C</a>";
		validationError.setCode("Se ha producido un error al tratar de validar el código del HTML. Utilice la siguiente URL para verificar la sintaxis: " + link);
		validationError.setLine(1);
		validationError.setColumn(1);
		validationError.setNumErrors(0);
		validationError.setNumWarnings(0);
		validationError.setSummary(true);
		validationErrors.add(validationError);
	}

	/**
	 * Gets the validation error.
	 *
	 * @param errorNode the error node
	 * @param lines     the lines
	 * @param type      the type
	 * @return the validation error
	 */
	// Este método por ahora no se va a usar, pero puede que más tarde se trabaje en él (no borrar)
	private static ValidationError getValidationError(final Node errorNode, final String[] lines, int type) {
		final ValidationError validationError = new ValidationError();
		validationError.setType(type);
		final NodeList errorInfo = errorNode.getChildNodes();
		for (int j = 0; j < errorInfo.getLength(); j++) {
			final Node nInfo = errorInfo.item(j);
			if (nInfo.getNodeName().equals(IntavConstants.TAG_COL)) {
				try {
					validationError.setColumn(Integer.parseInt(nInfo.getTextContent()));
				} catch (Exception e) {
					// No se hace nada, ya que el validador no tiene en cuenta la columna si es mayor de 80
				}
			} else if (nInfo.getNodeName().equals(IntavConstants.TAG_LINE)) {
				validationError.setLine(Integer.parseInt(nInfo.getTextContent()));
			} else if (nInfo.getNodeName().equals(IntavConstants.TAG_MESSAGE)) {
				validationError.setCode(IntavConstants.MESSAGE_DELIMITER + StringUtils.truncateText(nInfo.getTextContent(), 300) + IntavConstants.MESSAGE_DELIMITER);
			} else if (nInfo.getNodeName().equals(IntavConstants.TAG_MESSAGE_ID)) {
				validationError.setMessageId(nInfo.getTextContent());
			}
		}
		validationError.setSummary(false);
		try {
			String source = lines[validationError.getLine() - 1];
			if (source.length() > 500) {
				source = source.substring(0, 500);
			}
			validationError.setCode(validationError.getCode() + HTMLEntities.htmlAngleBrackets(source));
		} catch (Exception e) {
			// No hacemos nada, al validador a veces se le va la pinza
		}
		return validationError;
	}

	/**
	 * Gets the css validation errors.
	 *
	 * @param filename the filename
	 * @param language the language
	 * @return the css validation errors
	 */
	// Devuelve los errores de validación del código Css
	public static List<CssValidationError> getCssValidationErrors(final String filename, final String language) {
		final List<CssValidationError> cssValidationErrors = new ArrayList<>();
		final PropertiesManager pmgr = new PropertiesManager();
		if (StringUtils.isUrl(filename)) {
			try {
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();
				Document document;
				try {
					final String url = pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "url.w3c.css.validator").replace("{0}", URLEncoder.encode(filename, "UTF-8")).replace("{1}",
							pmgr.getValue("language.mapping", language));
					final HttpURLConnection connection = EvaluatorUtils.getConnection(url, "GET", true);
					connection.connect();
					document = builder.parse(StringUtils.fixBugInCssValidator(connection.getInputStream()));
				} catch (Exception e) {
					addCssValidationSummary(cssValidationErrors, filename, language);
					throw e;
				}
				final NodeList errorNodes = document.getElementsByTagName(IntavConstants.TAG_ERROR_LIST);
				String lastUri = "";
				for (int i = 0; i < errorNodes.getLength(); i++) {
					final NodeList detailNodes = errorNodes.item(i).getChildNodes();
					for (int j = 0; j < detailNodes.getLength(); j++) {
						if (IntavConstants.TAG_URI.equalsIgnoreCase(detailNodes.item(j).getNodeName())) {
							lastUri = detailNodes.item(j).getTextContent();
						}
						if (IntavConstants.TAG_ERROR.equalsIgnoreCase(detailNodes.item(j).getNodeName())) {
							cssValidationErrors.add(getCssValidationError(detailNodes.item(j), lastUri));
						}
					}
				}
			} catch (Exception e) {
				Logger.putLog("Error al validar el código CSS de " + filename, EvaluatorUtility.class, Logger.LOG_LEVEL_ERROR, e);
			}
		}
		return cssValidationErrors;
	}

	/**
	 * Gets the css validation error.
	 *
	 * @param detailNode the detail node
	 * @param cssUri     the css uri
	 * @return the css validation error
	 */
	private static CssValidationError getCssValidationError(final Node detailNode, final String cssUri) {
		final CssValidationError cssValidationError = new CssValidationError();
		final NodeList detailNodeChilds = detailNode.getChildNodes();
		for (int i = 0; i < detailNodeChilds.getLength(); i++) {
			if (IntavConstants.TAG_LINE.equals(detailNodeChilds.item(i).getNodeName())) {
				cssValidationError.setLine(Integer.parseInt(detailNodeChilds.item(i).getTextContent().trim()));
			} else if (IntavConstants.TAG_CONTEXT.equals(detailNodeChilds.item(i).getNodeName())) {
				cssValidationError.setContext(detailNodeChilds.item(i).getTextContent().trim());
			} else if (IntavConstants.TAG_SKIPPED_STRING.equals(detailNodeChilds.item(i).getNodeName())) {
				cssValidationError.setSkippedString(detailNodeChilds.item(i).getTextContent().trim());
			} else if (IntavConstants.TAG_MESSAGE.equals(detailNodeChilds.item(i).getNodeName())) {
				cssValidationError.setMessage(detailNodeChilds.item(i).getTextContent().trim());
			}
		}
		cssValidationError.setCode(IntavConstants.MESSAGE_DELIMITER + cssUri + ": " + cssValidationError.getMessage() + IntavConstants.MESSAGE_DELIMITER + " " + cssValidationError.getContext()
				+ " { ..." + StringUtils.truncateText(cssValidationError.getSkippedString(), 300) + " ... }");
		cssValidationError.setSummary(false);
		return cssValidationError;
	}

	// Returns the number of elements within the given element.
	/**
	 * Count elements.
	 *
	 * @param elementParent     the element parent
	 * @param nameElement       the name element
	 * @param nameElementIgnore the name element ignore
	 * @param childLevel        the child level
	 * @return the int
	 */
	// Ignores any elements of the same type that are children of the given element.
	public static int countElements(final Element elementParent, final String nameElement, final String nameElementIgnore, int childLevel) {
		int count = 0;
		final NodeList listChildren = elementParent.getChildNodes();
		for (int x = 0; x < listChildren.getLength(); x++) {
			if (listChildren.item(x).getNodeType() == Node.ELEMENT_NODE) {
				// don't search through child elements that should be ignored
				if (nameElementIgnore != null && listChildren.item(x).getNodeName().equalsIgnoreCase(nameElementIgnore)) {
					continue;
				}
				if (nameElement.equals("*") || nameElement.equalsIgnoreCase(listChildren.item(x).getNodeName())) {
					count++;
				}
				if (childLevel == -1) {
					count += countElements((Element) listChildren.item(x), nameElement, nameElementIgnore, childLevel);
				} else if (childLevel != 0) {
					count += countElements((Element) listChildren.item(x), nameElement, nameElementIgnore, childLevel - 1);
				}
			}
		}
		return count;
	}

	/**
	 * Adds the css validation summary.
	 *
	 * @param cssValidationErrors the css validation errors
	 * @param filename            the filename
	 * @param language            the language
	 * @throws Exception the exception
	 */
	private static void addCssValidationSummary(final List<CssValidationError> cssValidationErrors, final String filename, final String language) throws Exception {
		if (filename != null && language != null) {
			final CssValidationError cssValidationError = new CssValidationError();
			final PropertiesManager pmgr = new PropertiesManager();
			final String urlHuman = pmgr.getValue(IntavConstants.INTAV_PROPERTIES, "url.w3c.css.validator.human").replace("{0}", URLEncoder.encode(filename, "UTF-8")).replace("{1}",
					pmgr.getValue("language.mapping", language));
			final String link = "<a href='" + urlHuman + "' title='Enlace externo'>Validador de hojas de estilo del W3C</a>";
			cssValidationError.setCode("Se ha producido un error al intentar validar el código CSS de la página. Utilice la siguiente URL para verificar la sintaxis: " + link);
			cssValidationError.setLine(1);
			cssValidationError.setSummary(true);
			cssValidationErrors.add(cssValidationError);
		}
	}

	/**
	 * Gets the language.
	 *
	 * @param inReq the in req
	 * @return the language
	 */
	public static String getLanguage(final HttpServletRequest inReq) {
		if (inReq.getSession().getAttribute(IntavConstants.LANGUAGE) == null) {
			final PropertiesManager pmgr = new PropertiesManager();
			final String language = pmgr.getValue(IntavConstants.LANGUAGE_MAPPING, inReq.getLocale().getLanguage());
			if (language != null) {
				inReq.getSession().setAttribute(IntavConstants.LANGUAGE, language);
			} else {
				inReq.getSession().setAttribute(IntavConstants.LANGUAGE, IntavConstants.ENGLISH_ABB);
			}
		}
		return (String) inReq.getSession().getAttribute(IntavConstants.LANGUAGE);
	}

	/**
	 * Generate doctype source.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	private static String generateDoctypeSource(final DocumentType docType) {
		String doctypeSource = null;
		if (docType != null) {
			if (docType.getSystemId() != null) {
				doctypeSource = "<!DOCTYPE {0} PUBLIC \"{1}\" \"{2}\">";
				doctypeSource = doctypeSource.replace("{0}", docType.getName().toUpperCase());
				doctypeSource = doctypeSource.replace("{1}", docType.getPublicId().toUpperCase());
				doctypeSource = doctypeSource.replace("{2}", docType.getSystemId());
			} else {
				doctypeSource = "<!DOCTYPE {0} PUBLIC \"{1}\">";
				doctypeSource = doctypeSource.replace("{0}", docType.getName().toUpperCase());
				doctypeSource = doctypeSource.replace("{1}", docType.getPublicId().toUpperCase());
			}
		}
		return doctypeSource;
	}

	/**
	 * Gets the absolute.
	 *
	 * @param stringRelative the string relative
	 * @param filenameURL    the filename URL
	 * @return the absolute
	 */
	public static String getAbsolute(final String stringRelative, String filenameURL) {
		// make sure the URL ends with a '/' if appropriate
		int slashLast = filenameURL.lastIndexOf('/') + 1;
		if (slashLast < 9) {
			filenameURL = filenameURL.concat("/");
		} else {
			if (slashLast != filenameURL.length() && filenameURL.lastIndexOf('.') < slashLast) {
				filenameURL = filenameURL.concat("/");
			}
		}
		try {
			// trim stringRelative to make sure there is no whitespace at end of URL
			return new URI(filenameURL).resolve(stringRelative.trim()).toString();
		} catch (Exception e) {
			Logger.putLog("Exception: ", EvaluatorUtility.class, Logger.LOG_LEVEL_DEBUG, e);
		}
		return "";
	}

	/**
	 * Gets the attribute no session.
	 *
	 * @param element       the element
	 * @param attributeName the attribute name
	 * @return the attribute no session
	 */
	// Returns an attribute value that has been stripped of its session ID.
	public static String getAttributeNoSession(final Element element, final String attributeName) {
		final String stringValue = element.getAttribute(attributeName);
		if (stringValue.length() == 0) {
			return "";
		}
		int index = stringValue.indexOf(";jsession");
		if (index == -1) {
			return stringValue;
		}
		int indexEnd = stringValue.indexOf('?');
		if (indexEnd == -1) {
			return stringValue.substring(0, index);
		}
		return stringValue.substring(0, index) + stringValue.substring(indexEnd);
	}
}
