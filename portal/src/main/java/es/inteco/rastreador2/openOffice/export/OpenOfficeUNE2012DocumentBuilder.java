/*******************************************************************************
* Copyright (C) 2017 MINHAFP, Ministerio de Hacienda y Función Pública, 
* This program is licensed and may be used, modified and redistributed under the terms
* of the European Public License (EUPL), either version 1.2 or (at your option) any later 
* version as soon as they are approved by the European Commission.
* Unless required by applicable law or agreed to in writing, software distributed under the 
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
* ANY KIND, either express or implied. See the License for the specific language governing 
* permissions and more details.
* You should have received a copy of the EUPL1.2 license along with this program; if not, 
* you may find it at http://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32017D0863
******************************************************************************/
package es.inteco.rastreador2.openOffice.export;

import static es.inteco.common.Constants.CRAWLER_PROPERTIES;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import es.inteco.common.Constants;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.intav.form.ObservatoryEvaluationForm;
import es.inteco.rastreador2.actionform.observatorio.ModalityComparisonForm;
import es.inteco.rastreador2.actionform.semillas.CategoriaForm;
import es.inteco.rastreador2.utils.CrawlerUtils;
import es.inteco.rastreador2.utils.GraphicData;
import es.inteco.rastreador2.utils.ResultadosAnonimosObservatorioUNE2012Utils;

/**
 * Clase encargada de construir el documento OpenOffice con los resultados del observatorio usando la metodología UNE 2012.
 */
public class OpenOfficeUNE2012DocumentBuilder extends OpenOfficeDocumentBuilder {
	/** The Constant LEVEL_I_VERIFICATIONS. */
	public static final List<String> LEVEL_I_VERIFICATIONS = Arrays.asList(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_111_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_112_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_113_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_114_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_115_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_116_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_117_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_121_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_122_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_123_VERIFICATION);
	/** The Constant LEVEL_II_VERIFICATIONS. */
	public static final List<String> LEVEL_II_VERIFICATIONS = Arrays.asList(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_211_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_212_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_213_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_214_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_215_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_216_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_217_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_221_VERIFICATION,
			Constants.OBSERVATORY_GRAPHIC_EVOLUTION_222_VERIFICATION, Constants.OBSERVATORY_GRAPHIC_EVOLUTION_223_VERIFICATION);

	/**
	 * Instantiates a new open office UNE 2012 document builder.
	 *
	 * @param executionId      the execution id
	 * @param observatoryId    the observatory id
	 * @param tipoObservatorio the tipo observatorio
	 */
	public OpenOfficeUNE2012DocumentBuilder(final String executionId, final String observatoryId, final Long tipoObservatorio) {
		super(executionId, observatoryId, tipoObservatorio);
		numImg = 8;
		numSection = 5;
	}

	/**
	 * Builds the document.
	 *
	 * @param request           the request
	 * @param graphicPath       the graphic path
	 * @param date              the date
	 * @param evolution         the evolution
	 * @param pageExecutionList the page execution list
	 * @param categories        the categories
	 * @return the odf text document
	 * @throws Exception the exception
	 */
	public OdfTextDocument buildDocument(final HttpServletRequest request, final String graphicPath, final String date, final boolean evolution,
			final List<ObservatoryEvaluationForm> pageExecutionList, final List<CategoriaForm> categories) throws Exception {
		final MessageResources messageResources = CrawlerUtils.getResources(request);
		ResultadosAnonimosObservatorioUNE2012Utils.generateGraphics(messageResources, executionId, Long.parseLong(request.getParameter(Constants.ID)), observatoryId, graphicPath,
				Constants.MINISTERIO_P, true);
		final OdfTextDocument odt = getOdfTemplate();
		final OdfFileDom odfFileContent = odt.getContentDom();
		final OdfFileDom odfStyles = odt.getStylesDom();
		replaceText(odt, odfFileContent, "[fecha]", date);
		replaceText(odt, odfStyles, "[fecha]", date, "text:span");
		replaceSection41(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		replaceSection42(messageResources, odt, odfFileContent, graphicPath, categories, pageExecutionList);
		replaceSection43(messageResources, odt, odfFileContent, graphicPath, categories, pageExecutionList);
		replaceSection441(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		replaceSection442(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		replaceSection451(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		replaceSection452(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		replaceSection46(messageResources, odt, odfFileContent, graphicPath, pageExecutionList);
		for (CategoriaForm category : categories) {
			final List<ObservatoryEvaluationForm> pageExecutionListCat = ResultadosAnonimosObservatorioUNE2012Utils.getGlobalResultData(executionId, Long.parseLong(category.getId()),
					pageExecutionList);
			replaceSectionCat1(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat2(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat31(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat32(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat41(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat42(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			replaceSectionCat5(messageResources, odt, odfFileContent, graphicPath, category, pageExecutionListCat);
			numSection++;
		}
		if (evolution) {
			final Map<Date, List<ObservatoryEvaluationForm>> pageObservatoryMap = ResultadosAnonimosObservatorioUNE2012Utils.resultEvolutionData(Long.valueOf(observatoryId),
					Long.valueOf(executionId));
			final Map<Date, Map<String, BigDecimal>> resultsByAspect = new HashMap<>();
			for (Map.Entry<Date, List<ObservatoryEvaluationForm>> entry : pageObservatoryMap.entrySet()) {
				resultsByAspect.put(entry.getKey(), ResultadosAnonimosObservatorioUNE2012Utils.aspectMidsPuntuationGraphicData(messageResources, entry.getValue()));
			}
			ResultadosAnonimosObservatorioUNE2012Utils.generateEvolutionSuitabilityChart(observatoryId, executionId, graphicPath + "EvolucionNivelConformidadCombinada.jpg", pageObservatoryMap);
			replaceSectionEvolutionSuitabilityLevel(messageResources, odt, odfFileContent, graphicPath, pageObservatoryMap);
			replaceImg(odt, graphicPath + "EvolucionNivelConformidadCombinada.jpg", "image/jpg");
			replaceSectionEvolutionAverageScore(messageResources, odt, odfFileContent, graphicPath, pageObservatoryMap);
			ResultadosAnonimosObservatorioUNE2012Utils.generateEvolutionAverageScoreByVerificationChart(messageResources, graphicPath + "EvolucionPuntuacionMediaVerificacionNAICombinada.jpg",
					pageObservatoryMap, LEVEL_I_VERIFICATIONS);
			ResultadosAnonimosObservatorioUNE2012Utils.generateEvolutionAverageScoreByVerificationChart(messageResources, graphicPath + "EvolucionPuntuacionMediaVerificacionNAIICombinada.jpg",
					pageObservatoryMap, LEVEL_II_VERIFICATIONS);
			replaceSectionEvolutionScoreByVerification(messageResources, odt, odfFileContent, graphicPath, pageObservatoryMap);
			replaceImg(odt, graphicPath + "EvolucionPuntuacionMediaVerificacionNAICombinada.jpg", "image/jpg");
			replaceImg(odt, graphicPath + "EvolucionPuntuacionMediaVerificacionNAIICombinada.jpg", "image/jpg");
			ResultadosAnonimosObservatorioUNE2012Utils.generateEvolutionAverageScoreByAspectChart(messageResources, graphicPath + "EvolucionPuntuacionMediaAspectoCombinada.jpg", pageObservatoryMap);
			replaceSectionEvolutionScoreByAspect(messageResources, odt, odfFileContent, graphicPath, resultsByAspect);
			replaceImg(odt, graphicPath + "EvolucionPuntuacionMediaAspectoCombinada.jpg", "image/jpg");
		}
		return odt;
	}

	/**
	 * Gets the embeded id image.
	 *
	 * @param tipoObservatorio the tipo observatorio
	 * @param name             the name
	 * @return the embeded id image
	 */
	@Override
	protected String getEmbededIdImage(final Long tipoObservatorio, final String name) {
		return OpenOfficeUNE2012ImageUtils.getEmbededIdImage(tipoObservatorio, name);
	}

	/**
	 * Replace section 41.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection41(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.accessibility.level.allocation.name") + ".jpg", "image/jpeg");
		numImg++;
		Map<String, Integer> result = ResultadosAnonimosObservatorioUNE2012Utils.getResultsBySiteLevel(pageExecutionList);
		List<GraphicData> labelValueBean = ResultadosAnonimosObservatorioUNE2012Utils.infoGlobalAccessibilityLevel(messageResources, result);
		replaceText(odt, odfFileContent, "-41.t1.b2-", labelValueBean.get(0).getPercentageP());
		replaceText(odt, odfFileContent, "-41.t1.b3-", labelValueBean.get(1).getPercentageP());
		replaceText(odt, odfFileContent, "-41.t1.b4-", labelValueBean.get(2).getPercentageP());
		replaceText(odt, odfFileContent, "-41.t1.c2-", labelValueBean.get(0).getNumberP());
		replaceText(odt, odfFileContent, "-41.t1.c3-", labelValueBean.get(1).getNumberP());
		replaceText(odt, odfFileContent, "-41.t1.c4-", labelValueBean.get(2).getNumberP());
		return numImg;
	}

	/**
	 * Replace section 42.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param categories        the categories
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection42(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final List<CategoriaForm> categories,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		final Map<Integer, List<CategoriaForm>> resultLists = ResultadosAnonimosObservatorioUNE2012Utils.createGraphicsMap(categories);
		for (Integer i : resultLists.keySet()) {
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.global.puntuation.allocation.segments.mark.name") + i + ".jpg", "image/jpeg");
			numImg++;
		}
		final Map<CategoriaForm, Map<String, BigDecimal>> res = ResultadosAnonimosObservatorioUNE2012Utils.calculatePercentageResultsBySegmentMap(executionId, pageExecutionList, categories);
		int tableNum = 1;
		for (CategoriaForm category : categories) {
			List<LabelValueBean> results = ResultadosAnonimosObservatorioUNE2012Utils.infoComparisonBySegment(messageResources, res.get(category));
			replaceText(odt, odfFileContent, "-42.t" + tableNum + ".b2-", results.get(0).getValue() + "%");
			replaceText(odt, odfFileContent, "-42.t" + tableNum + ".b3-", results.get(1).getValue() + "%");
			replaceText(odt, odfFileContent, "-42.t" + tableNum + ".b4-", results.get(2).getValue() + "%");
			++tableNum;
		}
		return numImg;
	}

	/**
	 * Replace section 43.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param categories        the categories
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection43(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final List<CategoriaForm> categories,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		final Map<Integer, List<CategoriaForm>> resultLists = ResultadosAnonimosObservatorioUNE2012Utils.createGraphicsMap(categories);
		for (Integer i : resultLists.keySet()) {
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.global.puntuation.allocation.segment.strached.name") + i + ".jpg", "image/jpeg");
			numImg++;
		}
		final Map<CategoriaForm, Map<String, BigDecimal>> res = ResultadosAnonimosObservatorioUNE2012Utils.calculateMidPuntuationResultsBySegmentMap(executionId, pageExecutionList, categories);
		int tableNum = 1;
		for (CategoriaForm category : categories) {
			List<LabelValueBean> results = ResultadosAnonimosObservatorioUNE2012Utils.infoComparisonBySegmentPuntuation(messageResources, res.get(category));
			replaceText(odt, odfFileContent, "-43.t" + tableNum + ".b2-", results.get(0).getValue());
			replaceText(odt, odfFileContent, "-43.t" + tableNum + ".b3-", results.get(1).getValue());
			replaceText(odt, odfFileContent, "-43.t" + tableNum + ".b4-", results.get(2).getValue());
			++tableNum;
		}
		return numImg;
	}

	/**
	 * Replace section 441.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection441(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.verification.mid.comparation.level.1.name") + ".jpg", "image/jpeg");
		numImg++;
		final Map<String, BigDecimal> resultL1 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_1);
		final List<LabelValueBean> labelsL1 = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelIVerificationMidsComparison(messageResources, resultL1);
		replaceText(odt, odfFileContent, "-441.t1.b2-", labelsL1.get(0).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b3-", labelsL1.get(1).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b4-", labelsL1.get(2).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b5-", labelsL1.get(3).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b6-", labelsL1.get(4).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b7-", labelsL1.get(5).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b8-", labelsL1.get(6).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b9-", labelsL1.get(7).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b10-", labelsL1.get(8).getValue());
		replaceText(odt, odfFileContent, "-441.t1.b11-", labelsL1.get(9).getValue());
		return numImg;
	}

	/**
	 * Replace section 442.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection442(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.verification.mid.comparation.level.2.name") + ".jpg", "image/jpeg");
		numImg++;
		final Map<String, BigDecimal> resultL2 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_2);
		final List<LabelValueBean> labelsL2 = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelIIVerificationMidsComparison(messageResources, resultL2);
		replaceText(odt, odfFileContent, "-442.t1.b2-", labelsL2.get(0).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b3-", labelsL2.get(1).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b4-", labelsL2.get(2).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b5-", labelsL2.get(3).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b6-", labelsL2.get(4).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b7-", labelsL2.get(5).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b8-", labelsL2.get(6).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b9-", labelsL2.get(7).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b10-", labelsL2.get(8).getValue());
		replaceText(odt, odfFileContent, "-442.t1.b11-", labelsL2.get(9).getValue());
		return numImg;
	}

	/**
	 * Replace section 451.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection451(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.1.name") + ".jpg", "image/jpeg");
		numImg++;
		final Map<String, BigDecimal> results1 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_1);
		final List<ModalityComparisonForm> res = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelVerificationModalityComparison(results1);
		replaceText(odt, odfFileContent, "-451.t1.b2-", res.get(0).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c2-", res.get(0).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b3-", res.get(1).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c3-", res.get(1).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b4-", res.get(2).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c4-", res.get(2).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b5-", res.get(3).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c5-", res.get(3).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b6-", res.get(4).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c6-", res.get(4).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b7-", res.get(5).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c7-", res.get(5).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b8-", res.get(6).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c8-", res.get(6).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b9-", res.get(7).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c9-", res.get(7).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b10-", res.get(8).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c10-", res.get(8).getRedPercentage());
		replaceText(odt, odfFileContent, "-451.t1.b11-", res.get(9).getGreenPercentage());
		replaceText(odt, odfFileContent, "-451.t1.c11-", res.get(9).getRedPercentage());
		return numImg;
	}

	/**
	 * Replace section 452.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection452(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.2.name") + ".jpg", "image/jpeg");
		numImg++;
		final Map<String, BigDecimal> results2 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_2);
		final List<ModalityComparisonForm> res = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelVerificationModalityComparison(results2);
		replaceText(odt, odfFileContent, "-452.t1.b2-", res.get(0).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c2-", res.get(0).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b3-", res.get(1).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c3-", res.get(1).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b4-", res.get(2).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c4-", res.get(2).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b5-", res.get(3).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c5-", res.get(3).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b6-", res.get(4).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c6-", res.get(4).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b7-", res.get(5).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c7-", res.get(5).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b8-", res.get(6).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c8-", res.get(6).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b9-", res.get(7).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c9-", res.get(7).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b10-", res.get(8).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c10-", res.get(8).getRedPercentage());
		replaceText(odt, odfFileContent, "-452.t1.b11-", res.get(9).getGreenPercentage());
		replaceText(odt, odfFileContent, "-452.t1.c11-", res.get(9).getRedPercentage());
		return numImg;
	}

	/**
	 * Replace section 46.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSection46(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.aspect.mid.name") + ".jpg", "image/jpeg");
		numImg++;
		final Map<String, BigDecimal> result = ResultadosAnonimosObservatorioUNE2012Utils.aspectMidsPuntuationGraphicData(messageResources, pageExecutionList);
		final List<LabelValueBean> labels = ResultadosAnonimosObservatorioUNE2012Utils.infoAspectMidsComparison(messageResources, result);
		replaceText(odt, odfFileContent, "-46.t1.b2-", labels.get(0).getValue());
		replaceText(odt, odfFileContent, "-46.t1.b3-", labels.get(1).getValue());
		replaceText(odt, odfFileContent, "-46.t1.b4-", labels.get(2).getValue());
		replaceText(odt, odfFileContent, "-46.t1.b5-", labels.get(3).getValue());
		replaceText(odt, odfFileContent, "-46.t1.b6-", labels.get(4).getValue());
		return numImg;
	}

	/**
	 * Replace section cat 1.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat1(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<String, Integer> resultsMap = ResultadosAnonimosObservatorioUNE2012Utils.getResultsBySiteLevel(pageExecutionList);
			final List<GraphicData> labelValueBean = ResultadosAnonimosObservatorioUNE2012Utils.infoGlobalAccessibilityLevel(messageResources, resultsMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.accessibility.level.allocation.segment.name", category.getOrden()) + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.b2-", labelValueBean.get(0).getPercentageP());
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.c2-", labelValueBean.get(0).getNumberP());
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.b3-", labelValueBean.get(1).getPercentageP());
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.c3-", labelValueBean.get(1).getNumberP());
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.b4-", labelValueBean.get(2).getPercentageP());
			replaceText(odt, odfFileContent, "-" + numSection + "1.t1.c4-", labelValueBean.get(2).getNumberP());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section cat 2.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat2(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.mark.allocation.segment.name", category.getOrden()) + ".jpg", "image/jpeg");
			numImg++;
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section cat 31.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat31(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		final Map<String, BigDecimal> resultL1 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_1);
		if (!pageExecutionList.isEmpty()) {
			final List<LabelValueBean> labelsL1 = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelIVerificationMidsComparison(messageResources, resultL1);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.verification.mid.comparation.level.1.name") + category.getOrden() + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b2-", labelsL1.get(0).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b3-", labelsL1.get(1).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b4-", labelsL1.get(2).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b5-", labelsL1.get(3).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b6-", labelsL1.get(4).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b7-", labelsL1.get(5).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b8-", labelsL1.get(6).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b9-", labelsL1.get(7).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b10-", labelsL1.get(8).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "31.t1.b11-", labelsL1.get(9).getValue());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section cat 32.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat32(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		final Map<String, BigDecimal> resultL2 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_2);
		if (!pageExecutionList.isEmpty()) {
			final List<LabelValueBean> labelsL2 = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelIIVerificationMidsComparison(messageResources, resultL2);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.verification.mid.comparation.level.2.name") + category.getOrden() + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b2-", labelsL2.get(0).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b3-", labelsL2.get(1).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b4-", labelsL2.get(2).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b5-", labelsL2.get(3).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b6-", labelsL2.get(4).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b7-", labelsL2.get(5).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b8-", labelsL2.get(6).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b9-", labelsL2.get(7).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b10-", labelsL2.get(8).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "32.t1.b11-", labelsL2.get(9).getValue());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section cat 41.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat41(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<String, BigDecimal> results1 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_1);
			final List<ModalityComparisonForm> labels = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelVerificationModalityComparison(results1);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.1.name") + category.getOrden() + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b2-", labels.get(0).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c2-", labels.get(0).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b3-", labels.get(1).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c3-", labels.get(1).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b4-", labels.get(2).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c4-", labels.get(2).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b5-", labels.get(3).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c5-", labels.get(3).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b6-", labels.get(4).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c6-", labels.get(4).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b7-", labels.get(5).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c7-", labels.get(5).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b8-", labels.get(6).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c8-", labels.get(6).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b9-", labels.get(7).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c9-", labels.get(7).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b10-", labels.get(8).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c10-", labels.get(8).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.b11-", labels.get(9).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "41.t1.c11-", labels.get(9).getRedPercentage());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section cat 42.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat42(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<String, BigDecimal> results2 = ResultadosAnonimosObservatorioUNE2012Utils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_2);
			final List<ModalityComparisonForm> labels = ResultadosAnonimosObservatorioUNE2012Utils.infoLevelVerificationModalityComparison(results2);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.2.name") + category.getOrden() + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b2-", labels.get(0).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c2-", labels.get(0).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b3-", labels.get(1).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c3-", labels.get(1).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b4-", labels.get(2).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c4-", labels.get(2).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b5-", labels.get(3).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c5-", labels.get(3).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b6-", labels.get(4).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c6-", labels.get(4).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b7-", labels.get(5).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c7-", labels.get(5).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b8-", labels.get(6).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c8-", labels.get(6).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b9-", labels.get(7).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c9-", labels.get(7).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b10-", labels.get(8).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c10-", labels.get(8).getRedPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.b11-", labels.get(9).getGreenPercentage());
			replaceText(odt, odfFileContent, "-" + numSection + "42.t1.c11-", labels.get(9).getRedPercentage());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section evolution suitability level.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionEvolutionSuitabilityLevel(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final Map<Date, List<ObservatoryEvaluationForm>> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<Date, Map<Long, Map<String, Integer>>> evolutionResult = ResultadosAnonimosObservatorioUNE2012Utils.getEvolutionObservatoriesSitesByType(observatoryId, executionId,
					pageExecutionList);
			final PropertiesManager pmgr = new PropertiesManager();
			final DateFormat df = new SimpleDateFormat(pmgr.getValue(CRAWLER_PROPERTIES, "date.format.evolution"));
			final Map<String, BigDecimal> resultDataA = ResultadosAnonimosObservatorioUNE2012Utils.calculatePercentageApprovalSiteLevel(evolutionResult, Constants.OBS_A, df);
			final Map<String, BigDecimal> resultDataAA = ResultadosAnonimosObservatorioUNE2012Utils.calculatePercentageApprovalSiteLevel(evolutionResult, Constants.OBS_AA, df);
			final Map<String, BigDecimal> resultDataNV = ResultadosAnonimosObservatorioUNE2012Utils.calculatePercentageApprovalSiteLevel(evolutionResult, Constants.OBS_NV, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.accesibility.evolution.approval.AA.name") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "1.t1", resultDataAA, true);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.accesibility.evolution.approval.A.name") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "2.t1", resultDataA, true);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.accesibility.evolution.approval.NV.name") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "3.t1", resultDataNV, true);
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
			numImg++;
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section evolution average score.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionEvolutionAverageScore(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final Map<Date, List<ObservatoryEvaluationForm>> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<String, BigDecimal> resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateEvolutionPuntuationDataSet(pageExecutionList);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.mid.puntuation.name") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "4.t1", resultData);
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
			numImg++;
		}
		return numImg;
	}

	/**
	 * Replace section evolution score by verification.
	 *
	 * @param messageResources   the message resources
	 * @param odt                the odt
	 * @param odfFileContent     the odf file content
	 * @param graphicPath        the graphic path
	 * @param pageObservatoryMap the page observatory map
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionEvolutionScoreByVerification(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final Map<Date, List<ObservatoryEvaluationForm>> pageObservatoryMap) throws Exception {
		if (pageObservatoryMap != null && !pageObservatoryMap.isEmpty()) {
			Map<String, BigDecimal> resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_111_VERIFICATION,
					pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.1") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "5.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_112_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.2") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "6.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_113_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.3") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "7.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_114_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.4") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "8.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_115_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.5") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "9.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_116_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.6") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "10.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_117_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.1.7") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "11.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_121_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.2.1") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "12.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_122_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.2.2") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "13.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_123_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "1.2.3") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "14.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_211_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.1") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "15.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_212_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.2") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "16.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_213_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.3") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "17.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_214_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.4") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "18.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_215_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.5") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "19.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_216_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.6") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "20.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_217_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.1.7") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "21.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_221_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.2.1") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "22.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_222_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.2.2") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "23.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateVerificationEvolutionPuntuationDataSet(Constants.OBSERVATORY_GRAPHIC_EVOLUTION_223_VERIFICATION, pageObservatoryMap);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.verification.mid.puntuation.name", "2.2.3") + ".jpg", "image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "24.t1", resultData);
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			for (int i = 5; i < 25; i++) {
				replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
				numImg++;
			}
		}
		return numImg;
	}

	/**
	 * Replace section evolution score by aspect.
	 *
	 * @param messageResources the message resources
	 * @param odt              the odt
	 * @param odfFileContent   the odf file content
	 * @param graphicPath      the graphic path
	 * @param resultsByAspect  the results by aspect
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionEvolutionScoreByAspect(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath,
			final Map<Date, Map<String, BigDecimal>> resultsByAspect) throws Exception {
		if (resultsByAspect != null && !resultsByAspect.isEmpty()) {
			final PropertiesManager pmgr = new PropertiesManager();
			final DateFormat df = new SimpleDateFormat(pmgr.getValue(CRAWLER_PROPERTIES, "date.format.evolution"));
			Map<String, BigDecimal> resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateAspectEvolutionPuntuationDataSet(messageResources.getMessage("observatory.aspect.general"),
					resultsByAspect, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.aspect.mid.puntuation.name", Constants.OBSERVATORY_GRAPHIC_ASPECT_GENERAL_ID) + ".jpg",
					"image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "25.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateAspectEvolutionPuntuationDataSet(messageResources.getMessage("observatory.aspect.presentation"), resultsByAspect, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.aspect.mid.puntuation.name", Constants.OBSERVATORY_GRAPHIC_ASPECT_PRESENTATION_ID) + ".jpg",
					"image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "26.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateAspectEvolutionPuntuationDataSet(messageResources.getMessage("observatory.aspect.structure"), resultsByAspect, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.aspect.mid.puntuation.name", Constants.OBSERVATORY_GRAPHIC_ASPECT_STRUCTURE_ID) + ".jpg",
					"image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "27.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateAspectEvolutionPuntuationDataSet(messageResources.getMessage("observatory.aspect.navigation"), resultsByAspect, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.aspect.mid.puntuation.name", Constants.OBSERVATORY_GRAPHIC_ASPECT_NAVIGATION_ID) + ".jpg",
					"image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "28.t1", resultData);
			resultData = ResultadosAnonimosObservatorioUNE2012Utils.calculateAspectEvolutionPuntuationDataSet(messageResources.getMessage("observatory.aspect.alternatives"), resultsByAspect, df);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.evolution.aspect.mid.puntuation.name", Constants.OBSERVATORY_GRAPHIC_ASPECT_ALTERNATIVE_ID) + ".jpg",
					"image/jpeg");
			numImg++;
			replaceEvolutionTextCellTables(odt, odfFileContent, numSection + "29.t1", resultData);
		} else {
			PropertiesManager pmgr = new PropertiesManager();
			for (int i = 25; i < 30; i++) {
				replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
				numImg++;
			}
		}
		return numImg;
	}

	/**
	 * Replace section cat 5.
	 *
	 * @param messageResources  the message resources
	 * @param odt               the odt
	 * @param odfFileContent    the odf file content
	 * @param graphicPath       the graphic path
	 * @param category          the category
	 * @param pageExecutionList the page execution list
	 * @return the int
	 * @throws Exception the exception
	 */
	private int replaceSectionCat5(final MessageResources messageResources, final OdfTextDocument odt, final OdfFileDom odfFileContent, final String graphicPath, final CategoriaForm category,
			final List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		if (pageExecutionList != null && !pageExecutionList.isEmpty()) {
			final Map<String, BigDecimal> result = ResultadosAnonimosObservatorioUNE2012Utils.aspectMidsPuntuationGraphicData(messageResources, pageExecutionList);
			final List<LabelValueBean> labels = ResultadosAnonimosObservatorioUNE2012Utils.infoAspectMidsComparison(messageResources, result);
			replaceImg(odt, graphicPath + messageResources.getMessage("observatory.graphic.aspect.mid.name") + category.getOrden() + ".jpg", "image/jpeg");
			numImg++;
			replaceText(odt, odfFileContent, "-" + numSection + "5.t1.b2-", labels.get(0).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "5.t1.b3-", labels.get(1).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "5.t1.b4-", labels.get(2).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "5.t1.b5-", labels.get(3).getValue());
			replaceText(odt, odfFileContent, "-" + numSection + "5.t1.b6-", labels.get(4).getValue());
		} else {
			final PropertiesManager pmgr = new PropertiesManager();
			replaceImg(odt, pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.graphic.noResults"), "image/jpeg");
			numImg++;
		}
		return numImg;
	}

	/**
	 * Gets the odf template.
	 *
	 * @return the odf template
	 * @throws Exception the exception
	 */
	private OdfTextDocument getOdfTemplate() throws Exception {
		final PropertiesManager pmgr = new PropertiesManager();
		if (tipoObservatorio == Constants.OBSERVATORY_TYPE_AGE) {
			return (OdfTextDocument) OdfDocument.loadDocument(pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.template.une.2012.AGE"));
		} else if (tipoObservatorio == Constants.OBSERVATORY_TYPE_CCAA) {
			return (OdfTextDocument) OdfDocument.loadDocument(pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.template.une.2012.CCAA"));
		} else if (tipoObservatorio == Constants.OBSERVATORY_TYPE_EELL) {
			return (OdfTextDocument) OdfDocument.loadDocument(pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.template.une.2012.EELL"));
		} else {
			return (OdfTextDocument) OdfDocument.loadDocument(pmgr.getValue(CRAWLER_PROPERTIES, "export.open.office.template.une.2012.AGE"));
		}
	}

	/**
	 * Builds the document filtered.
	 *
	 * @param request               the request
	 * @param filePath              the file path
	 * @param graphicPath           the graphic path
	 * @param date                  the date
	 * @param evolution             the evolution
	 * @param pageExecutionList     the page execution list
	 * @param categories            the categories
	 * @param tagsToFilter          the tags to filter
	 * @param tagsToFilterFixed     the tags to filter fixed
	 * @param grpahicConditional    the grpahic conditional
	 * @param exObsIds              the ex obs ids
	 * @param idBaseTemplate        the id base template
	 * @param idSegmentTemplate     the id segment template
	 * @param idComplexityTemplate  the id complexity template
	 * @param idSegmentEvolTemplate the id segment evol template
	 * @param reportTitle           the report title
	 * @return the odf text document
	 * @throws Exception the exception
	 */
	@Override
	public OdfTextDocument buildDocumentFiltered(HttpServletRequest request, String filePath, String graphicPath, String date, boolean evolution, List<ObservatoryEvaluationForm> pageExecutionList,
			List<CategoriaForm> categories, String[] tagsToFilter, final String[] tagsToFilterFixed, Map<String, Boolean> grpahicConditional, String[] exObsIds, Long idBaseTemplate,
			Long idSegmentTemplate, Long idComplexityTemplate, final Long idSegmentEvolTemplate, String reportTitle) throws Exception {
		return buildDocument(request, graphicPath, date, evolution, pageExecutionList, categories);
	}
}
