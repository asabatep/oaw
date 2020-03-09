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
package ca.utoronto.atrc.tile.accessibilitychecker;

import es.inteco.common.IntavConstants;
import es.inteco.common.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

public class Identifier {
    private Map<String, String> hashtableAttributes;
    private String nameElement;
    private String stringIdentifier;
    //private int idCheck;
    private int line; // line number of key element
    //private int column; // column number of key element

    public String getNameElement() {
        return nameElement;
    }

    public String getNamePlus() {
        return nameElement;
    }

    public Map getHashtableAttributes() {
        return hashtableAttributes;
    }

   /* public int getCheckId() {
        return idCheck;
    }*/

    public int getLine() {
        return line;
    }

    public void setLine(int newLine) {
        line = newLine;
    }

/*    public int getColumn() {
        return column;
    }*/

    // Constructor using the parameters given in a request
    public Identifier(HttpServletRequest inReq) {
        hashtableAttributes = new Hashtable<>();

        nameElement = inReq.getParameter(IntavConstants.ELEMENT);
        if (nameElement == null) {
            Logger.putLog("Error: parameter 'element' missing in Identifer constructor!", Identifier.class, Logger.LOG_LEVEL_INFO);
            return;
        }

        Enumeration<Object> enu = inReq.getParameterNames();
        while (enu.hasMoreElements()) {
            final String nameParam = (String) enu.nextElement();

            // ignore some of the attribute values
            if (!nameParam.equals(IntavConstants.FILE) &&
                    !nameParam.equals(IntavConstants.ELEMENT)) {

                hashtableAttributes.put(nameParam, inReq.getParameter(nameParam));
            }
        }
    }

    private String getNumberString(Element elementGiven, String nameAttribute) {
        Integer integerNumber = (Integer) elementGiven.getUserData(nameAttribute);
        if (integerNumber == null) {
            Logger.putLog(" element: " + elementGiven.getNodeName() + " has no user data: " + nameAttribute, Identifier.class, Logger.LOG_LEVEL_WARNING);
            return "";
        }

        String stringNumber = null;
        if (integerNumber < 10) {
            stringNumber = "0" + integerNumber.toString();
        } else {
            stringNumber = integerNumber.toString();
        }

        return stringNumber;
    }


    // Returns true if the given attribute is the same as the attribute on this identifier.
    public boolean equalsAttribute(String nameAttribute, String valueAttribute) {
        // get attribute from hashtable
        String stringValue = (String) hashtableAttributes.get(nameAttribute);
        if (stringValue == null) {
            // this identifier does not have that attribute so return false
            return false;
        }

        // compare the attribute values
        return stringValue.equalsIgnoreCase(valueAttribute);
    }

    public void addToDoc(Document doc, Element elementParent) {
        // name of the element
        elementParent.setAttribute("element", nameElement);

        // attributes of the identifier
        for (Map.Entry<String, String> entry : hashtableAttributes.entrySet()) {
            elementParent.setAttribute(entry.getKey(), entry.getValue());
        }

        /*Enumeration<Object> enu = hashtableAttributes.keys();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            elementParent.setAttribute(key, (String) hashtableAttributes.get(key));
        }*/

        // the display string for this identifier
        Element newElementDisplay = doc.createElement("display");
        elementParent.appendChild(newElementDisplay);

        Text text = doc.createTextNode(stringIdentifier);
        newElementDisplay.appendChild(text);
    }

    public String getDisplayString() {
        return stringIdentifier;
    }

    public String getDisplayStringWithElement() {
        return nameElement + " - " + stringIdentifier;
    }

    public String getStringEncoded() {
        StringBuilder buffer = new StringBuilder("&element=");
        buffer.append(nameElement);

        try {
            if (hashtableAttributes == null) {
                return buffer.toString();
            }
            for (Map.Entry<String, String> entry : hashtableAttributes.entrySet()) {
                buffer.append("&").append(entry.getKey()).append("=");
                buffer.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            Logger.putLog("Warning: Exception caught in Identifier.getStringEncoded", Identifier.class, Logger.LOG_LEVEL_WARNING);
        }
        return buffer.toString();
    }
}

