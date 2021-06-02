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
package es.inteco.rastreador2.action.semillas;

import es.inteco.common.Constants;
import es.inteco.rastreador2.actionform.semillas.CargarSemillaWebsForm;
import es.inteco.rastreador2.utils.CrawlerUtils;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The Class CargarSemillaWebsAction.
 */
public class CargarSemillaWebsAction extends Action {

    /**
	 * Execute.
	 *
	 * @param mapping  the mapping
	 * @param form     the form
	 * @param request  the request
	 * @param response the response
	 * @return the action forward
	 */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        try {
            ActionErrors errors = new ActionErrors();
            if (CrawlerUtils.hasAccess(request, "web.list.seed")) {

                CargarSemillaWebsForm cargarSemillaWebsForm = (CargarSemillaWebsForm) form;
                cargarSemillaWebsForm.setArchivo(request.getParameter(Constants.ARCHIVO_D));

                String urlsString = "";
                List<String> urls = new ArrayList<>();

                if (request.getParameter(Constants.URLS_STRING) != null) {
                    try {
                        URL url = new URL(request.getParameter(Constants.NUEVO_TERMINO));
                        URLConnection ur = url.openConnection();
                        ur.connect();
                    } catch (Exception e) {
                        errors.add("errorObligatorios", new ActionMessage("url.invalida"));
                        saveErrors(request, errors);
                        return mapping.findForward(Constants.VOLVER);
                    }

                    urlsString = request.getParameter(Constants.URLS_STRING) + ";" + request.getParameter(Constants.NUEVO_TERMINO);

                    StringTokenizer st = new StringTokenizer(urlsString, ";");
                    while (st.hasMoreTokens()) {
                        String t = st.nextToken();
                        urls.add(t);
                    }
                }

                cargarSemillaWebsForm.setUrls(urls);
                cargarSemillaWebsForm.setUrls_string(urlsString);

                return mapping.findForward(Constants.EXITO);
            } else {
                return mapping.findForward(Constants.NO_PERMISSION);
            }
        } catch (Exception e) {
            CrawlerUtils.warnAdministrators(e, this.getClass());
            return mapping.findForward(Constants.ERROR_PAGE);
        }
    }

}