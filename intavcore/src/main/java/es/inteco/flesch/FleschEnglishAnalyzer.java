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
package es.inteco.flesch;

import java.util.Arrays;
import java.util.List;

/**
 * The Class FleschEnglishAnalyzer.
 */
public class FleschEnglishAnalyzer implements FleschAnalyzer {

    /**
	 * Calculate flesch value.
	 *
	 * @param numSyllables the num syllables
	 * @param numWords     the num words
	 * @param numPhrases   the num phrases
	 * @return the double
	 */
    public double calculateFleschValue(int numSyllables, int numWords,
                                       int numPhrases) {
        return 206.835 - (84.6 * (double) numSyllables / (double) numWords) - (1.015 * (double) numWords / (double) numPhrases);
    }

    /**
	 * Count phrases.
	 *
	 * @param text the text
	 * @return the int
	 */
    public int countPhrases(String text) {
        return text.split("\\.").length;
    }

    /**
	 * Count syllables.
	 *
	 * @param text the text
	 * @return the int
	 */
    public int countSyllables(String text) {
        String[] words = text.split("[^a-zA-Z]");
        List<String> consonants = Arrays.asList("b", "B", "c", "C", "d", "D", "f", "F", "g", "G", "h", "H",
                "j", "J", "k", "K", "l", "L", "m", "M", "n", "N", "p", "P",
                "q", "Q", "r", "R", "s", "S", "t", "T", "v", "V", "w", "W", "x", "X", "y", "Y", "z", "Z");
        List<String> vowels = Arrays.asList("a", "A", "e", "E", "i", "I", "o", "O", "u", "U");

        int count = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i] != null && words[i].trim().length() > 0) {
                String word = words[i].toLowerCase();
                if (word.length() > 2) {
                    for (int j = 0; j < word.length(); j++) {
                        //Si la palabra acaba en -ED y el sonido es -ID se suma una sílaba si el -T o -D no
                        if ((j == word.length() - 2) && String.valueOf(word.charAt(j)).equals("e") &&
                                String.valueOf(word.charAt(j + 1)).equals("d") && consonants.contains(String.valueOf(word.charAt(j - 1)))) {
                            if (((String.valueOf(word.charAt(j - 1)).equals("d"))
                                    || (String.valueOf(word.charAt(j - 1)).equals("t")))) {
                                count++;
                            }
                        }
                        //Excepciones de palabras acabadas en "e" que forman sílaba
                        else if (((j == word.length() - 1) && String.valueOf(word.charAt(j)).equals("e"))) {
                            // Si la ppalabra acaba en (consonante + "l" + "e", ej: -ble) se suma una sílaba
                            // A excepción de la terminación -ckle
                            if (String.valueOf(word.charAt(j - 1)).equals("l") && consonants.contains(String.valueOf(word.charAt(j - 2))) &&
                                    !(String.valueOf(word.charAt(j - 2)).equals("k") && (word.length() > 3)
                                            && String.valueOf(word.charAt(j - 3)).equals("c"))) {
                                count++;
                            }
                            //Si la palabra termina en -THE se suma sílaba
                            if (String.valueOf(word.charAt(j - 1)).equals("h") && String.valueOf(word.charAt(j - 2)).equals("t")) {
                                count++;
                            }
                            //Si la palabra termina en -IRE se suma una sílaba
                            if (String.valueOf(word.charAt(j - 1)).equals("r") && String.valueOf(word.charAt(j - 2)).equals("i")) {
                                count++;
                            }
                        }
                        //Si encontramos una "Y" seguida de vocal ésta se considera consonante, se suma una sílaba
                        else if (String.valueOf(word.charAt(j)).equals("y") &&
                                (j + 1 < word.length()) && vowels.contains(String.valueOf(word.charAt(j + 1)))) {
                            count++;
                        }
                        //Si nos encontramos al final de la palabra y la penúltima letra es una consonante, entonces sumamos una sílaba
                        //siempre que la última letra sea vocal y no sea una "e"
                        else if ((j == word.length() - 1) && consonants.contains(String.valueOf(word.charAt(j - 1)))) {
                            //... si la última letra es una vocal pero no una "e" o una "y" sumamos una sílaba
                            if ((vowels.contains(String.valueOf(word.charAt(j))) && !String.valueOf(word.charAt(j)).equals("e")) ||
                                    String.valueOf(word.charAt(j)).equals("y")) {
                                count++;
                            }
                        }
                        //Si encontramos "UA" contamos como dos silabas
                        else if (j + 1 < word.length() && String.valueOf(word.charAt(j)).equals("u") &&
                                String.valueOf(word.charAt(j + 1)).equals("a")) {
                            count++;
                        }
                        //Si encontramos una vocal seguida de una consonante añadimos una sílaba
                        else if (vowels.contains(String.valueOf(word.charAt(j))) &&
                                (j + 1 < word.length()) && consonants.contains(String.valueOf(word.charAt(j + 1)))) {
                            count++;
                        }
                    }
                    // Si la palabra tiene una o dos letra se cuenta como sílaba
                } else {
                    count++;
                }
            }
        }

        return count;
    }

    /**
	 * Count words.
	 *
	 * @param text the text
	 * @return the int
	 */
    public int countWords(String text) {
        return FleschUtils.countWords(text);
    }

    /**
	 * Gets the readability level.
	 *
	 * @param fleschValue the flesch value
	 * @return the readability level
	 */
    public int getReadabilityLevel(double fleschValue) {
        if (fleschValue > 90) {
            return FleschAnalyzer.FLESCH_LEVEL_VERY_EASY;
        } else if (fleschValue <= 90 && fleschValue > 80) {
            return FleschAnalyzer.FLESCH_LEVEL_EASY;
        } else if (fleschValue <= 80 && fleschValue > 70) {
            return FleschAnalyzer.FLESCH_LEVEL_QUITE_EASY;
        } else if (fleschValue <= 70 && fleschValue > 60) {
            return FleschAnalyzer.FLESCH_LEVEL_STANDARD;
        } else if (fleschValue <= 60 && fleschValue > 50) {
            return FleschAnalyzer.FLESCH_LEVEL_QUITE_HARD;
        } else if (fleschValue <= 50 && fleschValue > 30) {
            return FleschAnalyzer.FLESCH_LEVEL_HARD;
        } else if (fleschValue <= 30 && fleschValue >= 0) {
            return FleschAnalyzer.FLESCH_LEVEL_VERY_HARD;
        } else {
            return FleschAnalyzer.FLESCH_LEVEL_UNKNOWN;
        }
    }

}
