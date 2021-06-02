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
package es.inteco.rastreador2.pdf;

import static es.inteco.common.Constants.CRAWLER_PROPERTIES;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.itextpdf.text.pdf.PdfReader;

import es.gob.oaw.rastreador2.pdf.PdfGeneratorThread;
import es.gob.oaw.rastreador2.pdf.PdfGeneratorThread2;
import es.gob.oaw.rastreador2.pdf.SourceFilesManager;
import es.inteco.common.Constants;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.intav.datos.AnalisisDatos;
import es.inteco.plugin.dao.DataBaseManager;
import es.inteco.rastreador2.actionform.semillas.DependenciaForm;
import es.inteco.rastreador2.actionform.semillas.SemillaForm;
import es.inteco.rastreador2.dao.login.DatosForm;
import es.inteco.rastreador2.dao.login.LoginDAO;
import es.inteco.rastreador2.dao.observatorio.ObservatorioDAO;
import es.inteco.rastreador2.dao.rastreo.FulFilledCrawling;
import es.inteco.rastreador2.dao.rastreo.RastreoDAO;
import es.inteco.rastreador2.dao.semilla.SemillaDAO;
import es.inteco.rastreador2.pdf.utils.PDFUtils;
import es.inteco.rastreador2.pdf.utils.PrimaryExportPdfUtils;
import es.inteco.rastreador2.pdf.utils.ZipUtils;
import es.inteco.rastreador2.utils.CrawlerUtils;
import es.inteco.utils.FileUtils;

/**
 * The Class PrimaryExportPdfAction.
 */
public class PrimaryExportPdfAction extends Action {
	/**
	 * Execute.
	 *
	 * @param mapping  the mapping
	 * @param form     the form
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
		if (request.getSession().getAttribute(Constants.ROLE) == null || CrawlerUtils.hasAccess(request, "export.observatory")) {
			final String action = request.getParameter(Constants.ACTION);
			if ("downloadFile".equals(action)) {
				return downloadFile(mapping, request, response);
			} else if (Constants.EXPORT_ALL_PDFS.equals(action)) {
				return exportAllPdfs(mapping, request, response);
			} else if (Constants.EXPORT_ALL_PDFS_EMAIL.equals(action)) {
				// Alternative PDF mass generation
				return exportAllPdfsAndSendEmail(mapping, request, response);
			} else {
				return exportSinglePdf(mapping, request, response);
			}
		} else {
			return mapping.findForward(Constants.NO_PERMISSION);
		}
	}

	/**
	 * Export single pdf.
	 *
	 * @param mapping  the mapping
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	private ActionForward exportSinglePdf(final ActionMapping mapping, final HttpServletRequest request, final HttpServletResponse response) {
		// Url de invocacion:
		// http://localhost:8080/oaw/secure/primaryExportPdfAction.do?id={0}&idExObs={1}&idrastreo={2}&id_observatorio={3}&observatorio=si&key={4}
		final String user = (String) request.getSession().getAttribute(Constants.USER);
		final long idRastreo = request.getParameter(Constants.ID_RASTREO) != null ? Long.parseLong(request.getParameter(Constants.ID_RASTREO)) : 0;
		if (userHasAccess(user, idRastreo)) {
			final long idObservatory = request.getParameter(Constants.ID_OBSERVATORIO) != null ? Long.parseLong(request.getParameter(Constants.ID_OBSERVATORIO)) : 0;
			final long idExecutionOb = request.getParameter(Constants.ID_EX_OBS) != null ? Long.parseLong(request.getParameter(Constants.ID_EX_OBS)) : 0;
			final long idRastreoRealizado = request.getParameter(Constants.ID) != null ? Long.parseLong(request.getParameter(Constants.ID)) : 0;
			final boolean regenerate = Boolean.parseBoolean(request.getParameter(Constants.EXPORT_PDF_REGENERATE));
			// final File pdfFile = buildPdf(idObservatory, idExecutionOb,
			// idRastreoRealizado, idRastreo, regenerate,
			// request);
			final List<File> pdfFiles = buildPdfs(idObservatory, idExecutionOb, idRastreoRealizado, idRastreo, regenerate, request);
			for (File pdfFile : pdfFiles) {
				if (pdfFile != null) {
					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
						ZipUtils.generateZipFile(pdfFile.getParent(), outputStream, false);
						CrawlerUtils.returnData(response, outputStream.toByteArray(), pdfFile.getName().replace(".pdf", ".zip"), "application/zip");
					} catch (Exception e) {
						Logger.putLog("Exception al exportar el PDF", PrimaryExportPdfAction.class, Logger.LOG_LEVEL_ERROR, e);
						return mapping.findForward(Constants.ERROR);
					}
				}
			}
		} else {
			return mapping.findForward(Constants.NO_PERMISSION);
		}
		return mapping.findForward(Constants.ERROR);
	}

	/**
	 * User has access.
	 *
	 * @param user      the user
	 * @param idRastreo the id rastreo
	 * @return true, if successful
	 */
	private boolean userHasAccess(final String user, final long idRastreo) {
		try (Connection c = DataBaseManager.getConnection()) {
			return user == null || RastreoDAO.crawlerToUser(c, idRastreo, user) || RastreoDAO.crawlerToClientAccount(c, idRastreo, user);
		} catch (Exception e) {
			Logger.putLog("Exception al comprobar permisos para exportar el PDF", PrimaryExportPdfAction.class, Logger.LOG_LEVEL_ERROR, e);
			return false;
		}
	}

	/**
	 * Builds the pdfs.
	 *
	 * @param idObservatory      the id observatory
	 * @param idExecutionOb      the id execution ob
	 * @param idRastreoRealizado the id rastreo realizado
	 * @param idRastreo          the id rastreo
	 * @param regenerate         the regenerate
	 * @param request            the request
	 * @return the list
	 */
	private List<File> buildPdfs(long idObservatory, long idExecutionOb, long idRastreoRealizado, long idRastreo, boolean regenerate, final HttpServletRequest request) {
		final int countAnalisis = AnalisisDatos.countAnalysisByTracking(idRastreoRealizado);
		if (countAnalisis > 0) {
			try (Connection c = DataBaseManager.getConnection()) {
				final SemillaForm seed = SemillaDAO.getSeedById(c, RastreoDAO.getIdSeedByIdRastreo(c, idRastreo));
				final List<File> pdfFiles = getReportFiles(idObservatory, idExecutionOb, seed);
				if (pdfFiles != null && !pdfFiles.isEmpty()) {
					for (File pdfFile : pdfFiles) {
						final SourceFilesManager sourceFilesManager = new SourceFilesManager(pdfFile.getParentFile());
						// Si el pdf no ha sido creado lo creamos
						if (regenerate || !pdfFile.exists() || !sourceFilesManager.existsSourcesZip()) {
							generatePDF(idObservatory, idExecutionOb, idRastreoRealizado, idRastreo, request, c, seed, pdfFile, sourceFilesManager);
							// If is corrupt, regenerate
							try {
								PdfReader pdfReader = new PdfReader(pdfFile.getPath());
							} catch (Exception e) {
								generatePDF(idObservatory, idExecutionOb, idRastreoRealizado, idRastreo, request, c, seed, pdfFile, sourceFilesManager);
							}
						}
					}
				}
				return pdfFiles;
			} catch (Exception e) {
				Logger.putLog("Exception: ", ExportAction.class, Logger.LOG_LEVEL_ERROR, e);
			}
		}
		return null;
	}

	/**
	 * Generate PDF.
	 *
	 * @param idObservatory      the id observatory
	 * @param idExecutionOb      the id execution ob
	 * @param idRastreoRealizado the id rastreo realizado
	 * @param idRastreo          the id rastreo
	 * @param request            the request
	 * @param c                  the c
	 * @param seed               the seed
	 * @param pdfFile            the pdf file
	 * @param sourceFilesManager the source files manager
	 * @throws SQLException the SQL exception
	 * @throws Exception    the exception
	 */
	private void generatePDF(long idObservatory, long idExecutionOb, long idRastreoRealizado, long idRastreo, final HttpServletRequest request, Connection c, final SemillaForm seed, File pdfFile,
			final SourceFilesManager sourceFilesManager) throws SQLException, Exception {
		final List<Long> evaluationIds = AnalisisDatos.getEvaluationIdsFromRastreoRealizado(idRastreoRealizado);
		final long observatoryType = ObservatorioDAO.getObservatoryForm(c, idObservatory).getTipo();
		PrimaryExportPdfUtils.exportToPdf(idRastreo, idRastreoRealizado, request, pdfFile.getPath(), seed.getNombre(), null, idExecutionOb, observatoryType);
		FileUtils.deleteDir(new File(pdfFile.getParent() + File.separator + "temp"));
		sourceFilesManager.writeSourceFiles(c, evaluationIds);
		sourceFilesManager.zipSources(true);
	}

	/**
	 * Export all pdfs.
	 *
	 * @param mapping  the mapping
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	private ActionForward exportAllPdfs(final ActionMapping mapping, final HttpServletRequest request, final HttpServletResponse response) {
		// Url de invocacion:
		// http://localhost:8080/oaw/secure/primaryExportPdfAction.do?id_observatorio=8&idExObs=33&action=exportAllPdfs
		final long idExecutionOb = request.getParameter(Constants.ID_EX_OBS) != null ? Long.parseLong(request.getParameter(Constants.ID_EX_OBS)) : 0;
		final long idObservatory = request.getParameter(Constants.ID_OBSERVATORIO) != null ? Long.parseLong(request.getParameter(Constants.ID_OBSERVATORIO)) : 0;
		try (Connection c = DataBaseManager.getConnection()) {
			final List<FulFilledCrawling> fulfilledCrawlings = ObservatorioDAO.getFulfilledCrawlingByObservatoryExecution(c, idExecutionOb);
			if (request.getParameter("reverse") != null && request.getParameter("reverse").equalsIgnoreCase(Boolean.TRUE.toString())) {
				Collections.reverse(fulfilledCrawlings);
			}
			int reportsToGenerate = 0;
			for (FulFilledCrawling fulfilledCrawling : fulfilledCrawlings) {
				List<File> pdfFiles = getReportFiles(idObservatory, idExecutionOb, SemillaDAO.getSeedById(c, RastreoDAO.getIdSeedByIdRastreo(c, fulfilledCrawling.getIdCrawling())));
				if (pdfFiles != null && !pdfFiles.isEmpty()) {
					for (File pdfFile : pdfFiles) {
						final SourceFilesManager sourceFilesManager = new SourceFilesManager(pdfFile.getParentFile());
						final List<Long> evaluationIds = AnalisisDatos.getEvaluationIdsFromRastreoRealizado(fulfilledCrawling.getId());
						// Contabilizamos los informes que no están creados
						// entre los
						// portales para los que tenemos resultados (los
						// portales no
						// analizados no cuentan)
						if (!pdfFile.exists() || !sourceFilesManager.existsSourcesZip() && evaluationIds != null && !evaluationIds.isEmpty()) {
							reportsToGenerate++;
						}
					}
				}
			}
			// Truco para poder generar los informes de forma síncrona por si
			// surge algún problema en la generación asíncrona cuando se
			// despliegue en PRO
			if ("false".equalsIgnoreCase(request.getParameter("async"))) {
				reportsToGenerate = 0;
			}
			if (reportsToGenerate < 5) {
				final boolean regenerate = request.getParameter("regenerate") != null && Boolean.parseBoolean(request.getParameter("regenerate"));
				for (FulFilledCrawling fulfilledCrawling : fulfilledCrawlings) {
					buildPdfs(idObservatory, idExecutionOb, fulfilledCrawling.getId(), fulfilledCrawling.getIdCrawling(), regenerate, request);
				}
				final PropertiesManager pmgr = new PropertiesManager();
				return ZipUtils.pdfsZip(mapping, response, idObservatory, idExecutionOb, pmgr.getValue(CRAWLER_PROPERTIES, "path.inteco.exports.observatory.intav"));
			} else {
				request.setAttribute("GENERATE_TIME", String.format("%d", TimeUnit.SECONDS.toMinutes(Math.max(60, reportsToGenerate * 2))));
				final DatosForm userData = LoginDAO.getUserDataByName(c, request.getSession().getAttribute(Constants.USER).toString());
				request.setAttribute("EMAIL", userData.getEmail());
				final PdfGeneratorThread pdfGeneratorThread = new PdfGeneratorThread(idObservatory, idExecutionOb, fulfilledCrawlings, userData.getEmail());
				pdfGeneratorThread.start();
				return mapping.findForward(Constants.GENERATE_ALL_REPORTS);
			}
		} catch (Exception e) {
			Logger.putLog("Exception: ", ExportAction.class, Logger.LOG_LEVEL_ERROR, e);
			return mapping.findForward(Constants.ERROR);
		}
	}

	/**
	 * Export all pdfs in parts and send an email when the porcess complete with links.
	 *
	 * @param mapping  the mapping
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	private ActionForward exportAllPdfsAndSendEmail(final ActionMapping mapping, final HttpServletRequest request, final HttpServletResponse response) {
		// Url de invocacion:
		//
		final long idExecutionOb = request.getParameter(Constants.ID_EX_OBS) != null ? Long.parseLong(request.getParameter(Constants.ID_EX_OBS)) : 0;
		final long idObservatory = request.getParameter(Constants.ID_OBSERVATORIO) != null ? Long.parseLong(request.getParameter(Constants.ID_OBSERVATORIO)) : 0;
		try (Connection c = DataBaseManager.getConnection()) {
			final List<FulFilledCrawling> fulfilledCrawlings = ObservatorioDAO.getFulfilledCrawlingByObservatoryExecution(c, idExecutionOb);
			if (request.getParameter("reverse") != null && request.getParameter("reverse").equalsIgnoreCase(Boolean.TRUE.toString())) {
				Collections.reverse(fulfilledCrawlings);
			}
			int reportsToGenerate = 0;
			for (FulFilledCrawling fulfilledCrawling : fulfilledCrawlings) {
				List<File> pdfFiles = getReportFiles(idObservatory, idExecutionOb, SemillaDAO.getSeedById(c, RastreoDAO.getIdSeedByIdRastreo(c, fulfilledCrawling.getIdCrawling())));
				if (pdfFiles != null && !pdfFiles.isEmpty()) {
					for (File pdfFile : pdfFiles) {
						final SourceFilesManager sourceFilesManager = new SourceFilesManager(pdfFile.getParentFile());
						final List<Long> evaluationIds = AnalisisDatos.getEvaluationIdsFromRastreoRealizado(fulfilledCrawling.getId());
						// Contabilizamos los informes que no están creados
						// entre los
						// portales para los que tenemos resultados (los
						// portales no
						// analizados no cuentan)
						if (!pdfFile.exists() || !sourceFilesManager.existsSourcesZip() && evaluationIds != null && !evaluationIds.isEmpty()) {
							reportsToGenerate++;
						}
					}
				}
			}
			String url = request.getRequestURL().toString();
			String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
			final DatosForm userData = LoginDAO.getUserDataByName(c, request.getSession().getAttribute(Constants.USER).toString());
			final PdfGeneratorThread2 pdfGeneratorThread = new PdfGeneratorThread2(idObservatory, idExecutionOb, fulfilledCrawlings, userData.getEmail(), baseURL);
			pdfGeneratorThread.start();
			request.setAttribute("GENERATE_TIME", String.format("%d", TimeUnit.SECONDS.toMinutes(Math.max(60, reportsToGenerate * 2))));
			request.setAttribute("EMAIL", userData.getEmail());
			return mapping.findForward(Constants.GENERATE_ALL_REPORTS);
		} catch (Exception e) {
			Logger.putLog("Exception: ", ExportAction.class, Logger.LOG_LEVEL_ERROR, e);
			return mapping.findForward(Constants.ERROR);
		}
	}

	/**
	 * Gets the report files.
	 *
	 * @param idObservatory the id observatory
	 * @param idExecutionOb the id execution ob
	 * @param seed          the seed
	 * @return the report files
	 */
	private List<File> getReportFiles(final long idObservatory, final long idExecutionOb, final SemillaForm seed) {
		List<File> pdfFiles = new ArrayList<>();
		final PropertiesManager pmgr = new PropertiesManager();
		try (Connection c = DataBaseManager.getConnection()) {
			List<DependenciaForm> dependenciasSemilla = SemillaDAO.getSeedDependenciasById(c, seed.getId());
			if (dependenciasSemilla != null && !dependenciasSemilla.isEmpty()) {
				for (DependenciaForm dependenciaSemilla : dependenciasSemilla) {
					String dependOn = PDFUtils.formatSeedName(dependenciaSemilla.getName());
					configReport(idObservatory, idExecutionOb, seed, pdfFiles, pmgr, dependOn);
				}
			} else {
				configReport(idObservatory, idExecutionOb, seed, pdfFiles, pmgr, "sin_dependencia");
			}
		} catch (Exception e) {
		}
		return pdfFiles;
	}

	/**
	 * Config report.
	 *
	 * @param idObservatory the id observatory
	 * @param idExecutionOb the id execution ob
	 * @param seed          the seed
	 * @param pdfFiles      the pdf files
	 * @param pmgr          the pmgr
	 * @param dependOn      the depend on
	 */
	private void configReport(final long idObservatory, final long idExecutionOb, final SemillaForm seed, List<File> pdfFiles, final PropertiesManager pmgr, String dependOn) {
		if (dependOn == null || dependOn.isEmpty()) {
			dependOn = Constants.NO_DEPENDENCE;
		}
		final String path = pmgr.getValue(CRAWLER_PROPERTIES, "path.inteco.exports.observatory.intav") + idObservatory + File.separator + idExecutionOb + File.separator + dependOn + File.separator
				+ PDFUtils.formatSeedName(seed.getNombre());
		pdfFiles.add(new File(path + File.separator + PDFUtils.formatSeedName(seed.getNombre()) + ".pdf"));
	}

	/**
	 * Download file.
	 *
	 * @param mapping  the mapping
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	private ActionForward downloadFile(final ActionMapping mapping, final HttpServletRequest request, final HttpServletResponse response) {
		// Url de invocacion:
		// http://localhost:8080/oaw/secure/primaryExportPdfAction.do?id_observatorio=8&idExObs=33&action=exportAllPdfs
		final long idExecutionOb = request.getParameter(Constants.ID_EX_OBS) != null ? Long.parseLong(request.getParameter(Constants.ID_EX_OBS)) : 0;
		final long idObservatory = request.getParameter(Constants.ID_OBSERVATORIO) != null ? Long.parseLong(request.getParameter(Constants.ID_OBSERVATORIO)) : 0;
		final String filename = request.getParameter("file");
		try (Connection c = DataBaseManager.getConnection()) {
			final PropertiesManager pmgr = new PropertiesManager();
			String filePath = pmgr.getValue(CRAWLER_PROPERTIES, "path.inteco.exports.observatory.intav") + idObservatory + File.separator + idExecutionOb + File.separator + filename;
			CrawlerUtils.returnFile(response, filePath, "application/zip", false);
		} catch (Exception e) {
			Logger.putLog("Exception: ", ExportAction.class, Logger.LOG_LEVEL_ERROR, e);
			return mapping.findForward(Constants.ERROR);
		}
		return null;
	}
}