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
/*
Copyright ©2006, University of Toronto. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a 
copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Adaptive Technology Resource Centre, University of Toronto
130 St. George St., Toronto, Ontario, Canada
Telephone: (416) 978-4360
*/

package ca.utoronto.atrc.tile.accessibilitychecker;

import es.inteco.common.logging.Logger;

import java.util.StringTokenizer;

/**
 * Helper class for holding the RGB values of a color.
 */
public class ColorValues {
    int red;
    int green;
    int blue;
    boolean bIsValid;

    private static final String[][] COLOR_NAME_ARRAY = {
            {"aliceblue", "F0F8FF"},
            {"antiquewhite", "FAEBD7"},
            {"aqua", "00FFFF"},
            {"aquamarine", "7FFFD4"},
            {"azure", "F0FFFF"},
            {"beige", "F5F5DC"},
            {"bisque", "FFE4C4"},
            {"black", "000000"},
            {"blanchedalmond", "FFEBCD"},
            {"blue", "0000FF"},
            {"blueviolet", "8A2BE2"},
            {"brown", "A52A2A"},
            {"burlywood", "DEB887"},
            {"cadetblue", "5F9EA0"},
            {"chartreuse", "7FFF00"},
            {"chocolate", "D2691E"},
            {"coral", "FF7F50"},
            {"cornflowerblue", "6495ED"},
            {"cornsilk", "FFF8DC"},
            {"crimson", "DC143C"},
            {"cyan", "00FFFF"},
            {"darkblue", "00008B"},
            {"darkcyan", "008B8B"},
            {"darkgoldenrod", "B8860B"},
            {"darkgray", "A9A9A9"},
            {"darkgreen", "006400"},
            {"darkkhaki", "BDB76B"},
            {"darkmagenta", "8B008B"},
            {"darkolivegreen", "556B2F"},
            {"darkorange", "FF8C00"},
            {"darkorchid", "9932CC"},
            {"darkred", "8B0000"},
            {"darksalmon", "E9967A"},
            {"darkseagreen", "8FBC8F"},
            {"darkslateblue", "483D8B"},
            {"darkslategray", "2F4F4F"},
            {"darkturquoise", "00CED1"},
            {"darkviolet", "9400D3"},
            {"deeppink", "FF1493"},
            {"deepskyblue", "00BFFF"},
            {"dimgray", "696969"},
            {"dodgerblue", "1E90FF"},
            {"firebrick", "B22222"},
            {"floralwhite", "FFFAF0"},
            {"forestgreen", "228B22"},
            {"fuchsia", "FF00FF"},
            {"gainsboro", "DCDCDC"},
            {"ghostwhite", "F8F8FF"},
            {"gold", "FFD700"},
            {"goldenrod", "DAA520"},
            {"gray", "808080"},
            {"green", "008000"},
            {"greenyellow", "ADFF2F"},
            {"honeydew", "F0FFF0"},
            {"hotpink", "FF69B4"},
            {"indianred", "CD5C5C"},
            {"indigo", "4B0082"},
            {"ivory", "FFFFF0"},
            {"khaki", "F0E68C"},
            {"lavender", "E6E6FA"},
            {"lavenderblush", "FFF0F5"},
            {"lawngreen", "7CFC00"},
            {"lemonchiffon", "FFFACD"},
            {"lightblue", "ADD8E6"},
            {"lightcoral", "F08080"},
            {"lightcyan", "E0FFFF"},
            {"lightgoldenrodyellow", "FAFAD2"},
            {"lightgreen", "90EE90"},
            {"lightgrey", "D3D3D3"},
            {"lightpink", "FFB6C1"},
            {"lightsalmon", "FFA07A"},
            {"lightseagreen", "20B2AA"},
            {"lightskyblue", "87CEFA"},
            {"lightslategray", "778899"},
            {"lightsteelblue", "B0C4DE"},
            {"lightyellow", "FFFFE0"},
            {"lime", "00FF00"},
            {"limegreen", "32CD32"},
            {"linen", "FAF0E6"},
            {"magenta", "FF00FF"},
            {"maroon", "800000"},
            {"mediumaquamarine", "66CDAA"},
            {"mediumblue", "0000CD"},
            {"mediumorchid", "BA55D3"},
            {"mediumpurple", "9370DB"},
            {"mediumseagreen", "3CB371"},
            {"mediumslateblue", "7B68EE"},
            {"mediumspringgreen", "00FA9A"},
            {"mediumturquoise", "48D1CC"},
            {"mediumvioletred", "C71585"},
            {"midnightblue", "191970"},
            {"mintcream", "F5FFFA"},
            {"mistyrose", "FFE4E1"},
            {"moccasin", "FFE4B5"},
            {"navajowhite", "FFDEAD"},
            {"navy", "000080"},
            {"oldlace", "FDF5E6"},
            {"olive", "808000"},
            {"olivedrab", "6B8E23"},
            {"orange", "FFA500"},
            {"orangered", "FF4500"},
            {"orchid", "DA70D6"},
            {"palegoldenrod", "EEE8AA"},
            {"palegreen", "98FB98"},
            {"paleturquoise", "AFEEEE"},
            {"palevioletred", "DB7093"},
            {"papayawhip", "FFEFD5"},
            {"peachpuff", "FFDAB9"},
            {"peru", "CD853F"},
            {"pink", "FFC0CB"},
            {"plum", "DDA0DD"},
            {"powderblue", "B0E0E6"},
            {"purple", "800080"},
            {"red", "FF0000"},
            {"rosybrown", "BC8F8F"},
            {"royalblue", "4169E1"},
            {"saddlebrown", "8B4513"},
            {"salmon", "FA8072"},
            {"sandybrown", "F4A460"},
            {"seagreen", "2E8B57"},
            {"seashell", "FFF5EE"},
            {"sienna", "A0522D"},
            {"silver", "C0C0C0"},
            {"skyblue", "87CEEB"},
            {"slateblue", "6A5ACD"},
            {"slategray", "708090"},
            {"snow", "FFFAFA"},
            {"springgreen", "00FF7F"},
            {"steelblue", "4682B4"},
            {"tan", "D2B48C"},
            {"teal", "008080"},
            {"thistle", "D8BFD8"},
            {"tomato", "FF6347"},
            {"turquoise", "40E0D0"},
            {"violet", "EE82EE"},
            {"wheat", "F5DEB3"},
            {"white", "FFFFFF"},
            {"whitesmoke", "F5F5F5"},
            {"yellow", "FFFF00"},
            {"yellowgreen", "9ACD32"}
    };

    // Returns true if the red, green, and blue values are OK.
    public boolean getValid() {
        return bIsValid;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    // default constructor
    public ColorValues() {
        red = 0;
        green = 0;
        blue = 0;
        bIsValid = false;
    }

    // Constructor using string containing color name or value.
    // Call getValid to find out if the given color string was converted to red,
    // green and blue values correctly.
    public ColorValues(String stringColor) {
        red = 0;
        green = 0;
        blue = 0;
        bIsValid = false;

        // remove any whitespace
        stringColor = stringColor.trim();

        try {
            // check if we were given a color name
            for (int x = 0; x < 140; x++) {
                if (stringColor.equalsIgnoreCase(COLOR_NAME_ARRAY[x][0])) {
                    bIsValid = parseColorValues(COLOR_NAME_ARRAY[x][1]);
                    return;
                }
            }

            // not a color name, check if valid RGB value
            bIsValid = parseColorValues(stringColor);
        } catch (Exception e) {
            Logger.putLog("Info: Can't parse color values: '" + stringColor + "'", ColorValues.class, Logger.LOG_LEVEL_INFO);
        }
    }

    // Sets the red, green and blue member variables from the given color string.
    // This does not check if the string is a color name.
    // Returns true if the colors were parsed properly, false if not.
    boolean parseColorValues(String stringColor) {
        // remove any whitespace
        stringColor = stringColor.trim();

        try {
            // is this the unusual rgb form? e.g. rgb(255,255,255)
            if (stringColor.length() > 4) {
                if (stringColor.substring(0, 4).equals("rgb(")) {

                    // remove the "rgb(" and ")" characters
                    stringColor = stringColor.substring(4, stringColor.length() - 1);

                    // get the 3 values
                    StringTokenizer stringTokenizer = new StringTokenizer(stringColor, ", ");
                    if (stringTokenizer.hasMoreTokens()) {
                        red = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        green = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        blue = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    return true;
                }
            }

            // remove any hashmark (#) at start of string
            if (stringColor.charAt(0) == '#') {
                stringColor = stringColor.substring(1, stringColor.length());
            }

            // is this a short form color value (only 3 characters)?
            if (stringColor.length() == 3) {
                // turn the short form into the regular form (6 characters)
                StringBuilder buffer = new StringBuilder(6);
                buffer.append(stringColor.charAt(0));
                buffer.append(stringColor.charAt(0));
                buffer.append(stringColor.charAt(1));
                buffer.append(stringColor.charAt(1));
                buffer.append(stringColor.charAt(2));
                buffer.append(stringColor.charAt(2));

                stringColor = buffer.toString();
            }

            red = Integer.parseInt(stringColor.substring(0, 2), 16);
            green = Integer.parseInt(stringColor.substring(2, 4), 16);
            blue = Integer.parseInt(stringColor.substring(4, 6), 16);

            return true;
        } catch (Exception e) {
            Logger.putLog("Info: Can't parse color values: '" + stringColor + "'", ColorValues.class, Logger.LOG_LEVEL_INFO);
        }
        return false;
    }

    public static String getHexColorFromName(String colorName) {
        for (String[] colorInfo : COLOR_NAME_ARRAY) {
            if (colorInfo[0].equalsIgnoreCase(colorName)) {
                return "#"+ colorInfo[1];
            }
        }
        return "";
    }
}
