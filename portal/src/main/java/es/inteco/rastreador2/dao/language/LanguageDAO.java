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
package es.inteco.rastreador2.dao.language;

import es.inteco.common.logging.Logger;
import es.inteco.rastreador2.actionform.rastreo.LenguajeForm;
import es.inteco.rastreador2.utils.DAOUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class LanguageDAO.
 */
public final class LanguageDAO {

    /**
	 * Instantiates a new language DAO.
	 */
    private LanguageDAO() {
    }

    /**
	 * Load languages.
	 *
	 * @param c the c
	 * @return the list
	 */
    public static List<LenguajeForm> loadLanguages(Connection c) {
        List<LenguajeForm> lenguajeFormList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = c.prepareStatement("SELECT * FROM languages");
            rs = ps.executeQuery();

            while (rs.next()) {
                LenguajeForm lenguajeForm = new LenguajeForm();
                lenguajeForm.setId(rs.getLong("id_language"));
                lenguajeForm.setCodice(rs.getString("codice"));
                lenguajeForm.setKeyName(rs.getString("key_name"));
                lenguajeFormList.add(lenguajeForm);
            }

        } catch (Exception e) {
            Logger.putLog("Exception: ", LanguageDAO.class, Logger.LOG_LEVEL_ERROR, e);
        } finally {
            DAOUtils.closeQueries(ps, rs);
        }

        return lenguajeFormList;
    }

}
