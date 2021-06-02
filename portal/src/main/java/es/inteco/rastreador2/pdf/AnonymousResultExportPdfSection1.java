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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;

import es.inteco.common.ConstantsFont;
import es.inteco.rastreador2.pdf.utils.PDFUtils;
import es.inteco.rastreador2.utils.CrawlerUtils;

/**
 * The Class AnonymousResultExportPdfSection1.
 */
public final class AnonymousResultExportPdfSection1 {
	
	/**
	 * Instantiates a new anonymous result export pdf section 1.
	 */
	private AnonymousResultExportPdfSection1() {
	}

	/**
	 * Creates the chapter 1.
	 *
	 * @param request the request
	 * @param chapter the chapter
	 */
	protected static void createChapter1(HttpServletRequest request, Chapter chapter) {
		ArrayList<String> boldWords = new ArrayList<>();
		boldWords.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p1.bold"));
		chapter.add(PDFUtils.createParagraphWithDiferentFormatWord(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p1"), boldWords, ConstantsFont.paragraphBoldFont,
				ConstantsFont.PARAGRAPH, true));
		boldWords = new ArrayList<>();
		boldWords.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p2.bold"));
		Paragraph p = PDFUtils.createParagraphWithDiferentFormatWord(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p2"), boldWords, ConstantsFont.paragraphBoldFont,
				ConstantsFont.PARAGRAPH, true);
		Phrase ph = new Phrase(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p2.m1"), ConstantsFont.paragraphUnderlinedFont);
		p.add(ph);
		chapter.add(p);
		boldWords = new ArrayList<>();
		boldWords.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p3.bold"));
		chapter.add(PDFUtils.createParagraphWithDiferentFormatWord(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p3"), boldWords, ConstantsFont.paragraphBoldFont,
				ConstantsFont.PARAGRAPH, true));
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p4"), ConstantsFont.PARAGRAPH, chapter);
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.1.p5"), ConstantsFont.PARAGRAPH, chapter);
	}

	/**
	 * Creates the section 11.
	 *
	 * @param request the request
	 * @param section the section
	 */
	protected static void createSection11(HttpServletRequest request, Section section) {
		ArrayList<String> boldWords = new ArrayList<>();
		boldWords.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.11.p4.bold"));
		section.add(PDFUtils.createParagraphWithDiferentFormatWord(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.11.p4"), boldWords, ConstantsFont.paragraphBoldFont,
				ConstantsFont.PARAGRAPH, true));
		PDFUtils.addParagraph(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.11.p5"), ConstantsFont.PARAGRAPH, section);
		boldWords = new ArrayList<>();
		boldWords.add(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.11.p1.bold"));
		section.add(PDFUtils.createParagraphWithDiferentFormatWord(CrawlerUtils.getResources(request).getMessage("ob.resAnon.intav.report.11.p1"), boldWords, ConstantsFont.paragraphBoldFont,
				ConstantsFont.PARAGRAPH, true));
	}
}