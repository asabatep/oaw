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
package es.inteco.rastreador2.ws.commons;

/**
 * The Interface Constants.
 */
public interface Constants {
    
    /** The Constant CRAWL_SCHEDULED. */
    public static final int CRAWL_SCHEDULED = 2;
    
    /** The Constant CRAWL_LAUNCHED. */
    public static final int CRAWL_LAUNCHED = 1;
    
    /** The Constant CRAWL_QUEUED. */
    public static final int CRAWL_QUEUED = 0;

    /** The Constant CRAWL_SCHEDULING_ERROR. */
    public static final int CRAWL_SCHEDULING_ERROR = -1;
    
    /** The Constant CRAWL_SCHEDULING_PAST_DATE. */
    public static final int CRAWL_SCHEDULING_PAST_DATE = -2;

    /** The Constant BASIC_SERVICE_FORM. */
    public static final String BASIC_SERVICE_FORM = "basicServiceForm";
}
