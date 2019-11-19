package es.gob.oaw.rastreador2.action.diagnostico;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import es.gob.oaw.rastreador2.actionform.diagnostico.ServicioDiagnosticoForm;
import es.inteco.common.Constants;
import es.inteco.common.logging.Logger;
import es.inteco.plugin.dao.DataBaseManager;
import es.inteco.rastreador2.dao.basic.service.DiagnosisDAO;
import es.inteco.rastreador2.utils.CrawlerUtils;

/**
 * Action para manejar la exportación de las estadísticas de uso del servicio de diagnóstico.
 *
 * @author miguel.garcia
 */
public class ServicioDiagnosticoAction extends Action {
	/**
	 * Execute.
	 *
	 * @param mapping  the mapping
	 * @param form     the form
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
		if ("Exportar".equalsIgnoreCase(request.getParameter(Constants.ACCION))) {
			final ServicioDiagnosticoForm servicioDiagnosticoForm = (ServicioDiagnosticoForm) form;
			final ActionErrors errors = servicioDiagnosticoForm.validate(mapping, request);
			validarRangoFechas(errors, servicioDiagnosticoForm);
			if (errors.isEmpty()) {
				try (Connection conn = DataBaseManager.getConnection()) {
					final DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					final Date startDate = format.parse(servicioDiagnosticoForm.getStartDate() + " 00:00:00");
					final Date endDate = convertToIncludingEndDate(format, servicioDiagnosticoForm.getEndDate() + " 23:59:59");
					final String csv = DiagnosisDAO.getBasicServiceRequestCSV(conn, startDate, endDate);
					CrawlerUtils.returnStringAsFile(response, csv, "servicio_diagnostico_" + servicioDiagnosticoForm.getStartDate() + "-" + servicioDiagnosticoForm.getEndDate() + ".csv", "text/csv");
				} catch (Exception e) {
					Logger.putLog("ERROR al intentar exportar datos del servicio de diagnóstico", ServicioDiagnosticoAction.class, Logger.LOG_LEVEL_ERROR, e);
				}
				return null;
			} else {
				saveErrors(request, errors);
				return mapping.findForward(Constants.EXITO);
			}
		}
		request.getSession().setAttribute(Constants.MENU, Constants.MENU_SERVICIO_DIAGNOSTICO);
		request.getSession().setAttribute(Constants.SUBMENU, Constants.SUBMENU);
		return mapping.findForward(Constants.EXITO);
	}

	/**
	 * Convert to including end date.
	 *
	 * @param format  the format
	 * @param endDate the end date
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	private Date convertToIncludingEndDate(final DateFormat format, final String endDate) throws ParseException {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(format.parse(endDate));
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * Comprueba que el rango de fechas de la exportación del servicio de diagnóstico sea correcto (la fecha de inicio no puede ser posterior a la final).
	 *
	 * @param errors                  the errors
	 * @param servicioDiagnosticoForm the servicio diagnostico form
	 */
	private void validarRangoFechas(final ActionErrors errors, final ServicioDiagnosticoForm servicioDiagnosticoForm) {
		final DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			final Date startDate = format.parse(servicioDiagnosticoForm.getStartDate());
			final Date endDate = format.parse(servicioDiagnosticoForm.getEndDate());
			// Si la fecha de inicio es posterior a la final es un fallo
			if (startDate.after(endDate)) {
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("errors.date.range"));
			}
		} catch (ParseException pe) {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("errors.date.range"));
		}
	}
}
