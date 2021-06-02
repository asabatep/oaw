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
package es.inteco.intav.form;

/**
 * The Class PageForm.
 */
public class PageForm {
    
    /** The title. */
    private String title;
    
    /** The path. */
    private String path;
    
    /** The style class. */
    private String styleClass;
    
    /** The active. */
    private boolean active;

    /**
	 * Instantiates a new page form.
	 */
    public PageForm() {
        super();
    }

    /**
	 * Instantiates a new page form.
	 *
	 * @param title      the title
	 * @param path       the path
	 * @param styleClass the style class
	 * @param active     the active
	 */
    public PageForm(String title, String path, String styleClass, boolean active) {
        this.title = title;
        this.path = path;
        this.styleClass = styleClass;
        this.active = active;
    }

    /**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
    public boolean isActive() {
        return active;
    }

    /**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * Gets the style class.
	 *
	 * @return the style class
	 */
    public String getStyleClass() {
        return styleClass;
    }

    /**
	 * Sets the style class.
	 *
	 * @param styleClass the new style class
	 */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
	 * Gets the title.
	 *
	 * @return the title
	 */
    public String getTitle() {
        return title;
    }

    /**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
	 * Gets the path.
	 *
	 * @return the path
	 */
    public String getPath() {
        return path;
    }

    /**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
    public void setPath(String path) {
        this.path = path;
    }

}
