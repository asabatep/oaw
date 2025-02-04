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
package es.inteco.accesibilidad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.http.HTTPException;

import org.jfree.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; 

import ca.utoronto.atrc.tile.accessibilitychecker.EvaluatorUtility;
import es.inteco.common.CheckAccessibility;
import es.inteco.common.IntavConstants;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.intav.comun.Incidencia;
import es.inteco.intav.dao.ValidatorDAO;
import es.inteco.intav.datos.AnalisisDatos;
import es.inteco.intav.datos.IncidenciaDatos;
import es.inteco.intav.form.ValidatorForm;
import es.inteco.intav.persistence.Analysis;
import es.inteco.intav.utils.CacheUtils;
import es.inteco.intav.utils.EvaluatorUtils;
import es.inteco.plugin.Cartucho;
import es.inteco.plugin.dao.DataBaseManager;
import org.apache.commons.codec.binary.Base64;
import java.net.SocketTimeoutException;

/**
 * Implementación de un cartucho que analiza las urls, así como el contenido de las páginas y clasificarlas como maliciosas o no.
 */
public class CartuchoAccesibilidad extends Cartucho {
	/**
	 * Analyzer.
	 *
	 * @param datos the datos
	 */
	@Override
	public void analyzer(final Map<String, Object> datos) {
		Logger.putLog("Iniciando evaluación de accesibilidad desde el rastreador de la url: " + datos.get("url"), CartuchoAccesibilidad.class, Logger.LOG_LEVEL_INFO);
		
		final PropertiesManager pmgr = new PropertiesManager();
		final CheckAccessibility checkAccesibility = new CheckAccessibility();
		checkAccesibility.setEntity((String) datos.get("entity"));
		checkAccesibility.setGuideline(datos.get("guidelineFile").toString().substring(0, datos.get("guidelineFile").toString().lastIndexOf('.')).replace("-nobroken", ""));
		checkAccesibility.setGuidelineFile(datos.get("guidelineFile").toString());
		checkAccesibility.setLevel(pmgr.getValue("crawler.core.properties", "check.accessibility.default.level"));
		checkAccesibility.setUrl((String) datos.get("url"));
		checkAccesibility.setIdRastreo((Long) datos.get("idFulfilledCrawling"));
		checkAccesibility.setIdObservatory((Long) datos.get("idObservatory"));
		checkAccesibility.setContent((String) datos.get("contenido"));
		// Propagar el charset
		checkAccesibility.setCharset((String) datos.get("charset"));
		boolean isLast = (Boolean) datos.get("isLast");
		try {
				
			    Connection c = DataBaseManager.getConnection();
				ValidatorForm validator = ValidatorDAO.getValidator(c);
				if(validator.getStatus() == 1){
					if(!checkAccesibility.getGuideline().contains("pdf") && (checkAccesibility.getUrl().contains(".pdf") || new String(Base64.decodeBase64(checkAccesibility.getContent())).contains("%PDF"))){
						DataBaseManager.closeConnection(c);
					}
					else{
					URL url = new URL(validator.getUrl());
					Proxy nProxy = Proxy.NO_PROXY;
					HttpURLConnection con = (HttpURLConnection) url.openConnection(nProxy);
					DataBaseManager.closeConnection(c);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setReadTimeout(1200000);
				con.setChunkedStreamingMode(0);
				con.setDoOutput(true);
				Gson gson = new GsonBuilder().create();
				String json = gson.toJson(checkAccesibility);
				try(OutputStream os = con.getOutputStream()) {
					byte[] input = json.getBytes("utf-8");
					os.write(input, 0, input.length);			
				}
				int statusCode = con.getResponseCode();
					if (statusCode != HttpURLConnection.HTTP_OK) {
					    throw new HTTPException(statusCode);
					}
				try(BufferedReader br = new BufferedReader(
  					new InputStreamReader(con.getInputStream(), "utf-8"))) {
    				StringBuilder response = new StringBuilder();
    				String responseLine = null;
    				while ((responseLine = br.readLine()) != null) {
        				response.append(responseLine.trim());
    														}
    				Log.warn(response.toString());
					}

				}
				}
				else {
					DataBaseManager.closeConnection(c);
					if ((checkAccesibility.getUrl() != null && !checkAccesibility.getUrl().contains(".pdf")) && !(new String(Base64.decodeBase64(checkAccesibility.getContent())).contains("%PDF"))) {
						EvaluatorUtils.evaluateContent(checkAccesibility, pmgr.getValue("crawler.core.properties", "check.accessibility.default.language"));
					}
				}
				
				
				
				
			
		} catch (Exception e) {
			Log.error("EXCEPTION: " + e.getMessage());
			Logger.putLog("Excepcion: ", CartuchoAccesibilidad.class, Logger.LOG_LEVEL_ERROR, e);
			if (e instanceof IOException || e instanceof SocketTimeoutException){
				throw new HTTPException(500);
			}
		}
		if (isLast) {
			CacheUtils.removeFromCache(IntavConstants.CHECKED_LINKS_CACHE_KEY + checkAccesibility.getIdRastreo());
			Logger.putLog("Realizando tareas post-analisis", CartuchoAccesibilidad.class, Logger.LOG_LEVEL_DEBUG);
			// Calculamos el resultado de la comprobacion titulos diferentes ya
			// que requiere haber realizado el rastreo completo
			if (checkAccesibility.getGuidelineFile().startsWith(IntavConstants.GUIDELINE_FILENAME_START_2012)
					|| checkAccesibility.getGuidelineFile().startsWith(IntavConstants.GUIDELINE_FILENAME_START_2012_B)
					|| checkAccesibility.getGuidelineFile().startsWith(IntavConstants.GUIDELINE_FILENAME_START_2019)) {
				final long idRastreo = (Long) datos.get("idFulfilledCrawling");
				final List<Long> evaluationIds = AnalisisDatos.getEvaluationIdsFromRastreoRealizado(idRastreo);
				processDiferentTitlesCheck(idRastreo, evaluationIds);
			}
		}
	}

	/**
	 * Process diferent titles check.
	 *
	 * @param idRastreo     the id rastreo
	 * @param evaluationIds the evaluation ids
	 */
	private void processDiferentTitlesCheck(final long idRastreo, final List<Long> evaluationIds) {
		final Set<String> distribucionTitulos = new HashSet<>();
		try (Connection connection = DataBaseManager.getConnection()) {
			for (Long evaluationId : evaluationIds) {
				final List<Incidencia> incidencias = IncidenciaDatos.getIncidenciasByAnalisisAndComprobacion(connection, evaluationId, 462);
				for (Incidencia incidencia : incidencias) {
					distribucionTitulos.add(incidencia.getCodigoFuente());
				}
			}
			// Se verifica que todos los títulos no sean idénticos (para tamaños
			// de muestra >= 10).
			if (evaluationIds.size() < 10 || distribucionTitulos.size() > 1) {
				// Si hay menos de 10 páginas o hay más de 1 título se borran
				// las incidencias
				for (Long evaluationId : evaluationIds) {
					final Analysis analysis = AnalisisDatos.getAnalisisFromId(connection, evaluationId);
					final String updatedChecks = analysis.getChecksExecutedStr().replace(",462", "");
					AnalisisDatos.updateChecksEjecutados(updatedChecks, idRastreo);
					IncidenciaDatos.deleteIncidenciasByAnalisisAndComprobacion(connection, evaluationId, 462);
				}
			}
		} catch (Exception e) {
			Logger.putLog("Exception al intentar comprobar titulos diferentes: ", CartuchoAccesibilidad.class, Logger.LOG_LEVEL_ERROR, e);
		}
	}

	/**
	 * Sets the config.
	 *
	 * @param idRastreo the new config
	 */
	public void setConfig(final long idRastreo) {
		if (!EvaluatorUtility.isInitialized()) {
			try {
				EvaluatorUtility.initialize();
			} catch (Exception e) {
				Logger.putLog("Exception: ", CartuchoAccesibilidad.class, Logger.LOG_LEVEL_ERROR, e);
			}
		}
	}
}