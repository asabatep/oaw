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
package es.inteco.rastreador2.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * The Class BaseDAO.
 */
public abstract class BaseDAO {
	/**
	 * Salvar en base de datos.
	 *
	 * @param session the session
	 * @param object  the object
	 * @return the object
	 * @throws HibernateException the hibernate exception
	 */
	public static Object save(Session session, Object object) throws HibernateException {
		session.save(object);
		return object;
	}

	/**
	 * Actualizar en base de datos.
	 *
	 * @param session the session
	 * @param object  the object
	 * @return the object
	 * @throws HibernateException the hibernate exception
	 */
	public static Object update(Session session, Object object) throws HibernateException {
		session.update(object);
		return object;
	}

	/**
	 * Borrar en base de datos.
	 *
	 * @param session the session
	 * @param object  the object
	 * @return the object
	 * @throws HibernateException the hibernate exception
	 */
	public static Object delete(Session session, Object object) throws HibernateException {
		session.delete(object);
		return object;
	}
}
