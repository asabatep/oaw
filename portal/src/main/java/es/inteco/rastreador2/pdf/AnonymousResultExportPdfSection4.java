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

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPTable;

import es.inteco.common.Constants;
import es.inteco.common.ConstantsFont;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.intav.form.ObservatoryEvaluationForm;
import es.inteco.rastreador2.actionform.semillas.CategoriaForm;
import es.inteco.rastreador2.pdf.utils.PDFUtils;
import es.inteco.rastreador2.pdf.utils.SpecialChunk;
import es.inteco.rastreador2.utils.CrawlerUtils;
import es.inteco.rastreador2.utils.GraphicData;
import es.inteco.rastreador2.utils.ResultadosAnonimosObservatorioIntavUtils;

/**
 * The Class AnonymousResultExportPdfSection4.
 */
public final class AnonymousResultExportPdfSection4 {
	
	/**
	 * Instantiates a new anonymous result export pdf section 4.
	 */
	private AnonymousResultExportPdfSection4() {
	}

	/**
	 * Creates the chapter 4.
	 *
	 * @param request the request
	 * @param chapter the chapter
	 */
	protected static void createChapter4(HttpServletRequest request, Chapter chapter) {
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.4.p1"), ConstantsFont.PARAGRAPH, chapter);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.4.p2"), ConstantsFont.PARAGRAPH, chapter);
	}

	/**
	 * Creates the section 41.
	 *
	 * @param request         the request
	 * @param section         the section
	 * @param graphicPath     the graphic path
	 * @param res             the res
	 * @param observatoryType the observatory type
	 * @throws Exception the exception
	 */
	protected static void createSection41(HttpServletRequest request, Section section, String graphicPath, Map<String, Integer> res, long observatoryType) throws Exception {
		if (observatoryType == Constants.OBSERVATORY_TYPE_AGE) {
			PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.41.p1.AGE"), ConstantsFont.PARAGRAPH, section);
		} else if (observatoryType == Constants.OBSERVATORY_TYPE_CCAA) {
			PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.41.p1.CCAA"), ConstantsFont.PARAGRAPH, section);
		}
		PDFUtils.addImageToSection(section, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.accessibility.level.allocation.name") + ".jpg",
				CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.41.img.alt"), 75);
		java.util.List<GraphicData> labelValueBean = ResultadosAnonimosObservatorioIntavUtils.infoGlobalAccessibilityLevel(CrawlerUtils.getResources(request), res);
		PDFUtils.createTitleTable(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.41.tableTitle"), section, 380);
		float[] widths = { 33f, 33f, 33f };
		PdfPTable table = new PdfPTable(widths);
		table.addCell(PDFUtils.createTableCell(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.level"), Constants.VERDE_C_MP, ConstantsFont.labelCellFont, Element.ALIGN_CENTER, 0));
		table.addCell(PDFUtils.createTableCell(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.porc.portales"), Constants.VERDE_C_MP, ConstantsFont.labelCellFont,
				Element.ALIGN_CENTER, 0));
		table.addCell(PDFUtils.createTableCell(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.num.portales"), Constants.VERDE_C_MP, ConstantsFont.labelCellFont,
				Element.ALIGN_CENTER, 0));
		for (GraphicData label : labelValueBean) {
			table.addCell(PDFUtils.createTableCell(label.getAdecuationLevel(), Color.white, ConstantsFont.noteCellFont, Element.ALIGN_CENTER, 0));
			table.addCell(PDFUtils.createTableCell(label.getPercentageP(), Color.white, ConstantsFont.noteCellFont, Element.ALIGN_CENTER, 0));
			table.addCell(PDFUtils.createTableCell(label.getNumberP(), Color.white, ConstantsFont.noteCellFont, Element.ALIGN_CENTER, 0));
		}
		table.setSpacingBefore(ConstantsFont.LINE_SPACE);
		section.add(table);
	}

	/**
	 * Creates the section 42.
	 *
	 * @param request           the request
	 * @param section           the section
	 * @param graphicPath       the graphic path
	 * @param id_execution      the id execution
	 * @param pageExecutionList the page execution list
	 * @param categories        the categories
	 * @param observatoryType   the observatory type
	 * @throws Exception the exception
	 */
	protected static void createSection42(HttpServletRequest request, Section section, String graphicPath, String id_execution, java.util.List<ObservatoryEvaluationForm> pageExecutionList,
			java.util.List<CategoriaForm> categories, long observatoryType) throws Exception {
		Map<CategoriaForm, Map<String, BigDecimal>> res = ResultadosAnonimosObservatorioIntavUtils.calculatePercentageResultsBySegmentMap(id_execution, pageExecutionList, categories);
		PropertiesManager pmgr = new PropertiesManager();
		int numBarByGrapg = Integer.parseInt(pmgr.getValue(CRAWLER_PROPERTIES, "num.max.bar.graph"));
		int numGraph = categories.size() / numBarByGrapg;
		if (categories.size() % numBarByGrapg != 0) {
			numGraph++;
		}
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.p1"), ConstantsFont.PARAGRAPH, section);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.p2"), ConstantsFont.PARAGRAPH, section);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.p5"), ConstantsFont.PARAGRAPH, section);
		if (observatoryType == Constants.OBSERVATORY_TYPE_AGE) {
			PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.p6"), ConstantsFont.PARAGRAPH, section);
		}
		for (int i = 1; i <= numGraph; i++) {
			PDFUtils.addImageToSection(section, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.global.puntuation.allocation.segments.mark.name") + i + ".jpg",
					CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.img.alt"), 75);
		}
		section.newPage();
		for (CategoriaForm category : categories) {
			PDFUtils.createTitleTable(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.tableTitle", category.getName()), section, 380);
			java.util.List<LabelValueBean> results = ResultadosAnonimosObservatorioIntavUtils.infoComparisonBySegment(CrawlerUtils.getResources(request), res.get(category));
			for (LabelValueBean label : results) {
				label.setValue(label.getValue() + "%");
			}
			java.util.List<String> headers = new ArrayList<>();
			headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.level"));
			headers.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.42.resT"));
			section.add(PDFUtils.createResultTable(results, headers));
		}
	}

	/**
	 * Creates the section 43.
	 *
	 * @param request           the request
	 * @param section           the section
	 * @param graphicPath       the graphic path
	 * @param execution_id      the execution id
	 * @param observatory_id    the observatory id
	 * @param pageExecutionList the page execution list
	 * @param categories        the categories
	 * @throws Exception the exception
	 */
	protected static void createSection43(HttpServletRequest request, Section section, String graphicPath, String execution_id, String observatory_id,
			java.util.List<ObservatoryEvaluationForm> pageExecutionList, java.util.List<CategoriaForm> categories) throws Exception {
		Map<CategoriaForm, Map<String, BigDecimal>> res = ResultadosAnonimosObservatorioIntavUtils.calculateMidPuntuationResultsBySegmentMap(execution_id, pageExecutionList, categories);
		PropertiesManager pmgr = new PropertiesManager();
		int numBarByGrapg = Integer.parseInt(pmgr.getValue(CRAWLER_PROPERTIES, "num.max.bar.graph"));
		int numGraph = categories.size() / numBarByGrapg;
		if (categories.size() % numBarByGrapg != 0) {
			numGraph++;
		}
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.p1"), ConstantsFont.PARAGRAPH, section);
		Map<Integer, SpecialChunk> anchorMap = new HashMap<>();
		SpecialChunk anchor = new SpecialChunk(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.p4.anchor"), CrawlerUtils.getResources(request).getMessage("anchor.PMPO"),
				false, ConstantsFont.PARAGRAPH_ANCHOR_FONT);
		anchorMap.put(1, anchor);
		section.add(PDFUtils.createParagraphAnchor(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.p4"), anchorMap, ConstantsFont.PARAGRAPH));
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.p5"), ConstantsFont.PARAGRAPH, section);
		for (int i = 1; i <= numGraph; i++) {
			PDFUtils.addImageToSection(section, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.global.puntuation.allocation.segment.strached.name") + i + ".jpg",
					CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.img.alt"), 75);
		}
		section.newPage();
		final MessageResources resources = CrawlerUtils.getResources(request);
		for (CategoriaForm category : categories) {
			PDFUtils.createTitleTable(resources.getMessage("ob.resAnon.intav.report.43.tableTitle", category.getName()), section, 300);
			java.util.List<LabelValueBean> results = ResultadosAnonimosObservatorioIntavUtils.infoComparisonBySegmentPuntuation(resources, res.get(category));
			java.util.List<String> headers = new ArrayList<>();
			headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.level"));
			headers.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.43.resT"));
			section.add(PDFUtils.createResultTable(results, headers));
		}
	}

	/**
	 * Creates the section 44.
	 *
	 * @param request           the request
	 * @param section           the section
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @throws Exception the exception
	 */
	protected static void createSection44(HttpServletRequest request, Section section, String graphicPath, java.util.List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		Map<String, BigDecimal> resultL1 = ResultadosAnonimosObservatorioIntavUtils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_1);
		Map<String, BigDecimal> resultL2 = ResultadosAnonimosObservatorioIntavUtils.getVerificationResultsByPoint(pageExecutionList, Constants.OBS_PRIORITY_2);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.p1"), ConstantsFont.PARAGRAPH, section);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.p2"), ConstantsFont.PARAGRAPH, section);
		Map<Integer, SpecialChunk> anchorMap = new HashMap<>();
		SpecialChunk anchor = new SpecialChunk(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.p7.anchor"), CrawlerUtils.getResources(request).getMessage("anchor.PMV"),
				false, ConstantsFont.PARAGRAPH_ANCHOR_FONT);
		anchorMap.put(1, anchor);
		section.add(PDFUtils.createParagraphAnchor(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.p7"), anchorMap, ConstantsFont.PARAGRAPH));
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.p8"), ConstantsFont.PARAGRAPH, section);
		section.newPage();
		Section subSection = PDFUtils.createSection(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.sub1"), null, ConstantsFont.CHAPTER_TITLE_MP_FONT_3_L, section, -1, 2);
		PDFUtils.addImageToSection(subSection, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.verification.mid.comparation.level.1.name") + ".jpg",
				CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.img.alt"), 75);
		java.util.List<LabelValueBean> labelsL1 = ResultadosAnonimosObservatorioIntavUtils.infoLevelIVerificationMidsComparison(CrawlerUtils.getResources(request), resultL1);
		PDFUtils.createTitleTable(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.tableTitle1"), subSection, 380);
		java.util.List<String> headers = new ArrayList<>();
		headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.punto.verification"));
		headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.punt.media"));
		PdfPTable table = PDFUtils.createResultTable(labelsL1, headers);
		subSection.add(table);
		Section subSection2 = PDFUtils.createSection(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.sub2"), null, ConstantsFont.CHAPTER_TITLE_MP_FONT_3_L, section, -1, 2);
		PDFUtils.addImageToSection(subSection2, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.verification.mid.comparation.level.2.name") + ".jpg",
				CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.img.alt.2"), 75);
		java.util.List<LabelValueBean> labelsL2 = ResultadosAnonimosObservatorioIntavUtils.infoLevelIIVerificationMidsComparison(CrawlerUtils.getResources(request), resultL2);
		PDFUtils.createTitleTable(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.44.tableTitle2"), subSection2, 380);
		subSection2.add(PDFUtils.createResultTable(labelsL2, headers));
	}

	/**
	 * Creates the section 45.
	 *
	 * @param request           the request
	 * @param section           the section
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @throws Exception the exception
	 */
	protected static void createSection45(HttpServletRequest request, Section section, String graphicPath, java.util.List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		Map<String, BigDecimal> results1 = ResultadosAnonimosObservatorioIntavUtils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_1);
		Map<String, BigDecimal> results2 = ResultadosAnonimosObservatorioIntavUtils.getVerificationResultsByPointAndModality(pageExecutionList, Constants.OBS_PRIORITY_2);
		final MessageResources messageResources = CrawlerUtils.getResources(request);
		PDFUtils.addParagraph(messageResources.getMessage("ob.resAnon.intav.report.45.p1"), ConstantsFont.PARAGRAPH, section);
		PDFUtils.addParagraph(messageResources.getMessage("ob.resAnon.intav.report.45.p2"), ConstantsFont.PARAGRAPH, section);
		PDFUtils.addParagraph(messageResources.getMessage("ob.resAnon.intav.report.45.p3"), ConstantsFont.PARAGRAPH, section);
		section.newPage();
		Section subSection = PDFUtils.createSection(messageResources.getMessage("ob.resAnon.intav.report.45.sub1"), null, ConstantsFont.CHAPTER_TITLE_MP_FONT_3_L, section, -1, 2);
		PDFUtils.addImageToSection(subSection, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.1.name") + ".jpg",
				messageResources.getMessage("ob.resAnon.intav.report.45.img1.alt"), 75);
		PDFUtils.createTitleTable(messageResources.getMessage("ob.resAnon.intav.report.45.tableTitle1"), subSection, 380);
		section.add(PDFUtils.createTableMod(messageResources, ResultadosAnonimosObservatorioIntavUtils.infoLevelVerificationModalityComparison(results1)));
		section.newPage();
		Section subSection2 = PDFUtils.createSection(messageResources.getMessage("ob.resAnon.intav.report.45.sub2"), null, ConstantsFont.CHAPTER_TITLE_MP_FONT_3_L, section, -1, 2);
		PDFUtils.addImageToSection(subSection2, graphicPath + messageResources.getMessage("observatory.graphic.modality.by.verification.level.2.name") + ".jpg",
				messageResources.getMessage("ob.resAnon.intav.report.45.img2.alt"), 75);
		PDFUtils.createTitleTable(messageResources.getMessage("ob.resAnon.intav.report.45.tableTitle2"), subSection2, 380);
		section.add(PDFUtils.createTableMod(messageResources, ResultadosAnonimosObservatorioIntavUtils.infoLevelVerificationModalityComparison(results2)));
	}

	/**
	 * Creates the section 46.
	 *
	 * @param request           the request
	 * @param section           the section
	 * @param graphicPath       the graphic path
	 * @param pageExecutionList the page execution list
	 * @throws Exception the exception
	 */
	protected static void createSection46(HttpServletRequest request, Section section, String graphicPath, java.util.List<ObservatoryEvaluationForm> pageExecutionList) throws Exception {
		Map<String, BigDecimal> result = ResultadosAnonimosObservatorioIntavUtils.aspectMidsPuntuationGraphicData(CrawlerUtils.getResources(request), pageExecutionList);
		Map<Integer, SpecialChunk> anchorMap = new HashMap<>();
		SpecialChunk anchor = new SpecialChunk(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.46.p12.ancla"), CrawlerUtils.getResources(request).getMessage("anchor.PMA"),
				false, ConstantsFont.PARAGRAPH_ANCHOR_FONT);
		anchorMap.put(1, anchor);
		section.add(PDFUtils.createParagraphAnchor(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.46.p12"), anchorMap, ConstantsFont.PARAGRAPH));
		PDFUtils.addImageToSection(section, graphicPath + CrawlerUtils.getResources(request).getMessage("observatory.graphic.aspect.mid.name") + ".jpg",
				CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.46.img.alt"), 75);
		java.util.List<LabelValueBean> labels = ResultadosAnonimosObservatorioIntavUtils.infoAspectMidsComparison(request, result);
		PDFUtils.createTitleTable(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.46.tableTitle"), section, 380);
		// section.add(PDFUtils.addResultsList(request, labels, CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.46.resT")));
		java.util.List<String> headers = new ArrayList<>();
		headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.aspect"));
		headers.add(CrawlerUtils.getResources(request).getMessage("resultados.anonimos.punt.media"));
		section.add(PDFUtils.createResultTable(labels, headers));
	}
}
