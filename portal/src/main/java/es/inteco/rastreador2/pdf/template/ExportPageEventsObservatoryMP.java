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
package es.inteco.rastreador2.pdf.template;

import java.awt.Color;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import es.inteco.common.Constants;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.rastreador2.pdf.utils.ExportPageEventsUtils;

/**
 * Build the header and footer of the document.
 *
 * @author j.delamo
 * @version 1.0
 */
public class ExportPageEventsObservatoryMP extends PdfPageEventHelper {
	/** The print footer. */
	private static boolean printFooter = false;
	/** The foot text. */
	private final String footText;
	/** The date. */
	private final String date;

	/**
	 * Marks the last page of the document if it is true.
	 *
	 * @param printFooter Boolean variable that marks the last page.
	 */
	public static void setPrintFooter(final boolean printFooter) {
		ExportPageEventsObservatoryMP.printFooter = printFooter;
	}

	/**
	 * Instantiates a new export page events observatory MP.
	 *
	 * @param footText the foot text
	 * @param date     the date
	 */
	public ExportPageEventsObservatoryMP(final String footText, final String date) {
		this.footText = footText;
		this.date = date;
	}

	/**
	 * This is the event for headers and footers.
	 *
	 * @param w A PdfWriter object.
	 * @param d A Document object.
	 */
	public final void onEndPage(final PdfWriter w, final Document d) {
		try {
			final PropertiesManager pmgr = new PropertiesManager();
			final PdfContentByte cb = w.getDirectContent();
			final BaseFont baseFont = BaseFont.createFont(pmgr.getValue(Constants.PDF_PROPERTIES, "path.pdf.font"), BaseFont.CP1252, BaseFont.EMBEDDED);
			createHeader(cb, d);
			if (printFooter) {
				createFooter(cb, w, baseFont, d);
			}
		} catch (Exception e) {
			Logger.putLog("Exception", ExportPageEventsObservatoryMP.class, es.inteco.common.logging.Logger.LOG_LEVEL_ERROR, e);
		}
	}

	/**
	 * Creates the header.
	 *
	 * @param pdfContentByte the pdf content byte
	 * @param document       the document
	 * @throws DocumentException the document exception
	 */
	private void createHeader(final PdfContentByte pdfContentByte, final Document document) throws DocumentException {
		PropertiesManager pmgr = new PropertiesManager();
		int posX = Integer.parseInt(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.logo.posX"));
		int posY = Integer.parseInt(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.logo.posY"));
		final Image logoMinisterio = ExportPageEventsUtils.createImage(pmgr.getValue(Constants.PDF_PROPERTIES, "path.ministerio.logo"), 158, 45,
				"Vicepresidencia Tercera del Gobierno - Ministerio de Asuntos Económicos y Transformación Digital ");
		if (logoMinisterio != null) {
			pdfContentByte.beginMarkedContentSequence(PdfName.IMAGE);
			logoMinisterio.setAbsolutePosition(posX, posY);
			pdfContentByte.addImage(logoMinisterio);
			pdfContentByte.endMarkedContentSequence();
		}
		final Image logoObservatorio = ExportPageEventsUtils.createImage(pmgr.getValue(Constants.PDF_PROPERTIES, "path.observatorio.logo"), 233, 45, "Observatorio de Accesibilidad Web");
		if (logoObservatorio != null) {
			pdfContentByte.beginMarkedContentSequence(PdfName.IMAGE);
			logoObservatorio.setAbsolutePosition(document.getPageSize().getWidth() - logoObservatorio.getScaledWidth() - posX, posY);
			pdfContentByte.addImage(logoObservatorio);
			pdfContentByte.endMarkedContentSequence();
		}
	}

	/**
	 * Creates the footer.
	 *
	 * @param pdfContentByte the pdf content byte
	 * @param pdfWriter      the pdf writer
	 * @param baseFont       the base font
	 * @param document       the document
	 * @throws DocumentException the document exception
	 */
	private void createFooter(final PdfContentByte pdfContentByte, final PdfWriter pdfWriter, final BaseFont baseFont, final Document document) throws DocumentException {
		if (pdfWriter.getPageNumber() == 1) {
			final PropertiesManager pmgr = new PropertiesManager();
			ExportPageEventsUtils.addText(pdfContentByte, Float.parseFloat(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.dateX")),
					Float.parseFloat(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.dateY")), date, Color.WHITE, baseFont, Integer.parseInt(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.date.size")));
//            final Image observatorio = ExportPageEventsUtils.createImage(pmgr.getValue(Constants.PDF_PROPERTIES, "path.titlePage.MP"), 600, 98, "Observatorio de Accesibilidad Web");
//            if (observatorio != null) {
//                observatorio.setAbsolutePosition(0, 150);
//                pdfContentByte.addImage(observatorio);
//                pdfContentByte.endMarkedContentSequence();
//            }
			document.add(ExportPageEventsUtils.createRectangle(Integer.parseInt(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.recWidthFooterFirstPage")),
					Integer.parseInt(pmgr.getValue(Constants.PDF_PROPERTIES, "pdf.recHeightFooterFirstPage")), Constants.VERDE_O_MP));
		} else {
			ExportPageEventsUtils.addFooter(document, pdfWriter, pdfContentByte, baseFont, footText, Constants.VERDE_O_MP);
		}
	}
}