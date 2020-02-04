package es.gob.oaw.rastreador2.pdf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import es.inteco.common.logging.Logger;

/**
 * Clase para el manejo de las descripciones de las comprobaciones desde un fichero externo de recursos para poder ser modificado fácilmente.
 *
 * @author miguel.garcia
 */
public final class CheckDescriptionsManager {
	/** The Constant DEFAULT_PROPERTIES. */
	private static final String DEFAULT_PROPERTIES = "/default.check.descriptions";
	/** The properties. */
	private final Properties properties;

	/**
	 * Obtiene una instancia de esta clase utilizando como fichero de recursos la entrada file.descriptions del fichero de configuración propertiesmanager.properties.
	 *
	 */
	public CheckDescriptionsManager() {
		this.properties = new Properties(getDefaultDescriptions());
		loadDescriptions(getConfiguredDescriptionsFilePath());
	}

	/**
	 * Obtiene una instancia de esta clase utilizando como fichero de recursos el fichero indicado por el path.
	 *
	 * @param path cadena al fichero de recursos. Si empieza por el protocolo file:/ entonces se considera absoluta si no se considera relativa al classpath.
	 * @throws IOException si no existe el fichero o no se ha podido leer.
	 */
	public CheckDescriptionsManager(final String path) throws IOException {
		this.properties = new Properties(getDefaultDescriptions());
		loadDescriptions(path);
	}

	/**
	 * Load descriptions.
	 *
	 * @param descriptionsPath the descriptions path
	 */
	private void loadDescriptions(final String descriptionsPath) {
		try (Reader reader = getFileReader(descriptionsPath)) {
			this.properties.load(reader);
		} catch (IOException e) {
			Logger.putLog("No se ha podido leer el fichero de descripciones de los checks", CheckDescriptionsManager.class, Logger.LOG_LEVEL_WARNING, e);
		}
	}

	/**
	 * Gets the configured descriptions file path.
	 *
	 * @return the configured descriptions file path
	 */
	private String getConfiguredDescriptionsFilePath() {
		final Properties pmg = new Properties();
		try (InputStream is = CheckDescriptionsManager.class.getResourceAsStream("/propertiesmanager.properties")) {
			pmg.load(is);
		} catch (IOException e) {
			Logger.putLog("No se ha podido leer la propiedad en el propertiesmanager.properties del fichero de descripciones de los checks", CheckDescriptionsManager.class, Logger.LOG_LEVEL_WARNING,
					e);
		}
		return pmg.getProperty("file.descriptions");
	}

	/**
	 * Gets the default descriptions.
	 *
	 * @return the default descriptions
	 */
	private Properties getDefaultDescriptions() {
		final Properties defaultDescriptions = new Properties();
		try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(DEFAULT_PROPERTIES))) {
			defaultDescriptions.load(reader);
		} catch (IOException ioe) {
			Logger.putLog("No se ha podido leer las descripciones por defecto de los checks", CheckDescriptionsManager.class, Logger.LOG_LEVEL_ERROR, ioe);
		}
		return defaultDescriptions;
	}

	/**
	 * Gets the file reader.
	 *
	 * @param file the file
	 * @return the file reader
	 */
	private Reader getFileReader(final String file) {
		if (file != null && file.startsWith("file:/")) {
			// Check encoding
			final String encoding = "UTF-8";
			try {
				return new InputStreamReader(new URL(file).openStream(), encoding);
			} catch (IOException e) {
				Logger.putLog("No se ha podido leer el fichero", CheckDescriptionsManager.class, Logger.LOG_LEVEL_WARNING, e);
				return new InputStreamReader(CheckDescriptionsManager.class.getResourceAsStream(DEFAULT_PROPERTIES));
			}
		} else {
			return new InputStreamReader(CheckDescriptionsManager.class.getResourceAsStream(file));
		}
	}

	/**
	 * Obtiene el valor de una propiedad.
	 *
	 * @param key el identificador de una propiedad.
	 * @return el valor para esa propiedad o null si no existe.
	 */
	public String getString(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Obtiene el mensaje de error de una comprobación.
	 *
	 * @param idCheck el identificador de la comprobación.
	 * @return el mensaje de error para esa comprobación o null si no existe.
	 */
	public String getErrorMessage(final String idCheck) {
		return properties.getProperty("check." + idCheck + ".error");
	}

	/**
	 * Obtiene la descripción larga de una comprobación.
	 *
	 * @param idCheck el identificador de la comprobación.
	 * @return la descripción para esa comprobación o null si no existe.
	 */
	public String getRationaleMessage(final String idCheck) {
		return properties.getProperty("check." + idCheck + ".rationale");
	}
}
