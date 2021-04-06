/*******************************************************************************
* Copyright (C) 2017 MINHAFP, Ministerio de Hacienda y Función Pública, 
* This program is licensed and may be used, modified and redistributed under the terms
* of the European Public License (EUPL), either version 1.2 or (at your option) any later 
* version as soon as they are approved by the European Commission.
* Unless required by applicable law or agreed to in writing, software distributed under the 
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
* ANY KIND, either express or implied. See the License for the specific language governing 
* permissions and more details.
* You should have received a copy of the EUPL1.2 license along with this program; if not, 
* you may find it at http://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32017D0863
******************************************************************************/
package es.gob.oaw.language;

import es.inteco.common.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.util.*;
import java.util.Map.Entry;

/**
 * Port del codigo utilizado por KDE para identificar el idioma
 *
 * see http://code.google.com/p/guess-language/
 */
public class GuessLanguage {
    
    /** The Constant UNKNOWN_LANGUAGE. */
    // Valor utilizado para idiomas que no se pueden detectar
    public static final String UNKNOWN_LANGUAGE = "unknown";

    /** The Constant ACTIVE_MODELS. */
    private static final String[] ACTIVE_MODELS = new String[]{"en", "eu",
            "ca", "es", "fr", "de", "it", "pt", "ru", "uk", "ar", "fa", "ps",
            "ur", "pt_BR", "pt_PT", "ast"};

    /** The Constant BASIC_LATIN. */
    private final static List<String> BASIC_LATIN;
    
    /** The Constant EXTENDED_LATIN. */
    private final static List<String> EXTENDED_LATIN;
    
    /** The Constant ALL_LATIN. */
    private final static List<String> ALL_LATIN;
    
    /** The Constant CYRILLIC. */
    private final static List<String> CYRILLIC;
    
    /** The Constant ARABIC. */
    private final static List<String> ARABIC;
    
    /** The Constant DEVANAGARI. */
    private final static List<String> DEVANAGARI;
    
    /** The Constant SINGLETONS. */
    private final static Map<UnicodeBlock, String> SINGLETONS;
    
    /** The Constant PT. */
    private final static List<String> PT;

    static {
        BASIC_LATIN = Arrays.asList("en", "eu");
        EXTENDED_LATIN = Arrays.asList("ca", "es", "fr", "de", "it", "pt", "ast");
        ALL_LATIN = new ArrayList<>(BASIC_LATIN.size()
                + EXTENDED_LATIN.size());
        ALL_LATIN.addAll(BASIC_LATIN);
        ALL_LATIN.addAll(EXTENDED_LATIN);
        CYRILLIC = Arrays.asList("ru", "uk");
        ARABIC = Arrays.asList("ar", "fa", "ps", "ur");
        DEVANAGARI = Collections.emptyList();
        SINGLETONS = new HashMap<>();
        SINGLETONS.put(UnicodeBlock.ARMENIAN, "hy");
        SINGLETONS.put(UnicodeBlock.HEBREW, "he");
        SINGLETONS.put(UnicodeBlock.GREEK, "el");
        PT = Arrays.asList("pt_BR", "pt_PT");
    }

    /** The models. */
    private final Map<String, Map<String, Integer>> models;

    /** The Constant MIN_LENGTH. */
    // Longitud mínima de texto para poder aplicar el algoritmo con garantías
    private static final int MIN_LENGTH = 20;
    
    /** The Constant MAXGRAMS. */
    // Número de n-gramas usados
    private static final int MAXGRAMS = 300;

    /**
	 * Instantiates a new guess language.
	 */
    public GuessLanguage() {
        models = new HashMap<>();
        if (models.isEmpty()) {
            loadModels();
        }
    }

    /**
	 * Guess language.
	 *
	 * @param text the text
	 * @return the string
	 */
    public String guessLanguage(final String text) {
        if (text.length() < MIN_LENGTH) {
            return UNKNOWN_LANGUAGE;
        }

        return identify(text, findRuns(text));
    }

    /**
	 * Checks if is supported language.
	 *
	 * @param language the language
	 * @return true, if is supported language
	 */
    public static boolean isSupportedLanguage(final String language) {
        if (language == null) {
            return true;
        }

        for (String lang : ACTIVE_MODELS) {
            if (lang.equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }

    /**
	 * Find runs.
	 *
	 * @param text the text
	 * @return the list
	 */
    private List<UnicodeBlock> findRuns(final String text) {
        final Map<UnicodeBlock, Integer> runTypes = new HashMap<>();
        int count = 0;
        int totalCount = 0;
        UnicodeBlock previousBlock = null;
        for (int i = 0; i < text.length(); i++) {
            final int c = text.codePointAt(i);
            UnicodeBlock block = Character.UnicodeBlock.of(c);
            if (Character.isLetter(c)) {
                count++;
                totalCount++;
                if (previousBlock != block) {
                    if (block.equals(UnicodeBlock.LATIN_1_SUPPLEMENT)
                            || block.equals(UnicodeBlock.LATIN_EXTENDED_A)
                            || block.equals(UnicodeBlock.IPA_EXTENSIONS)) {
                        block = UnicodeBlock.LATIN_EXTENDED_A;
                    }
                    previousBlock = block;
                    Integer aux = runTypes.get(block);
                    if (aux != null) {
                        runTypes.put(block, aux + count);
                    } else {
                        runTypes.put(block, count);
                    }
                    count = 0;
                }
            }
        }
        // add last count
        Integer aux = runTypes.get(previousBlock);
        if (aux != null) {
            runTypes.put(previousBlock, aux + count);
        } else {
            runTypes.put(previousBlock, count);
        }
        // relevant return types
        final List<UnicodeBlock> relevantRuns = new LinkedList<>();
        // return run types that used for 40% or more of the string
        // always return basic latin if found more than 15%.
        for (Entry<UnicodeBlock, Integer> entry : runTypes.entrySet()) {
            if ((entry.getValue() * 100 / totalCount) >= 40) {
                relevantRuns.add(entry.getKey());
            } else {
                if (entry.getKey().equals(UnicodeBlock.BASIC_LATIN)
                        && (entry.getValue() * 100 / totalCount) >= 15) {
                    relevantRuns.add(entry.getKey());
                }
            }
        }

        return relevantRuns;
    }

    /**
	 * Identify.
	 *
	 * @param sample  the sample
	 * @param scripts the scripts
	 * @return the string
	 */
    private String identify(final String sample,
                            final List<UnicodeBlock> scripts) {
        if (sample.length() < 3) {
            return UNKNOWN_LANGUAGE;
        }

        if (scripts.contains(UnicodeBlock.HANGUL_SYLLABLES)
                || scripts.contains(UnicodeBlock.HANGUL_JAMO)
                || scripts.contains(UnicodeBlock.HANGUL_COMPATIBILITY_JAMO)) {
            return "ko";
        }

        if (scripts.contains(UnicodeBlock.GREEK)) {
            return "el";
        }

        if (scripts.contains(UnicodeBlock.KATAKANA)
                || scripts.contains(UnicodeBlock.HIRAGANA)
                || scripts.contains(UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS)) {
            return "ja";
        }

        if (scripts.contains(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                || scripts.contains(UnicodeBlock.BOPOMOFO)
                || scripts.contains(UnicodeBlock.BOPOMOFO_EXTENDED)
                || scripts.contains(UnicodeBlock.KANGXI_RADICALS)
                || scripts.contains(UnicodeBlock.ARABIC_PRESENTATION_FORMS_A)) {
            return "zh";
        }

        if (scripts.contains(UnicodeBlock.CYRILLIC)) {
            return check(sample, CYRILLIC);
        }

        if (scripts.contains(UnicodeBlock.ARABIC)
                || scripts.contains(UnicodeBlock.ARABIC_PRESENTATION_FORMS_A)
                || scripts.contains(UnicodeBlock.ARABIC_PRESENTATION_FORMS_B)) {
            return check(sample, ARABIC);
        }

        if (scripts.contains(UnicodeBlock.DEVANAGARI)) {
            return check(sample, DEVANAGARI);
        }

        // Try languages with unique scripts
        for (Entry<UnicodeBlock, String> unicode : SINGLETONS.entrySet()) {
            if (scripts.contains(unicode.getKey())) {
                return unicode.getValue();
            }
        }

        if (scripts.contains(UnicodeBlock.LATIN_EXTENDED_ADDITIONAL)) {
            return "vi";
        }

        if (scripts.contains(UnicodeBlock.LATIN_EXTENDED_A)) {
            final String latinLang = check(sample, ALL_LATIN);
            if (latinLang.equals("pt")) {
                return check(sample, PT);
            } else {
                return latinLang;
            }
        }
        if (scripts.contains(UnicodeBlock.BASIC_LATIN)) {
            return check(sample, ALL_LATIN);
        }

        return UNKNOWN_LANGUAGE;
    }

    /**
	 * Check.
	 *
	 * @param sample the sample
	 * @param langs  the langs
	 * @return the string
	 */
    private String check(final String sample, final List<String> langs) {
        if (sample.length() < MIN_LENGTH) {
            return UNKNOWN_LANGUAGE;
        }

        final Map<Integer, String> scores = new TreeMap<>();
        final Map<Integer, List<String>> model = createOrderedModel(sample);

        for (String key : langs) {
            if (models.containsKey(key)) {
                scores.put(distance(model, models.get(key)), key);
            }
        }

        if (scores.isEmpty()) {
            return UNKNOWN_LANGUAGE;
        }

        // we want the lowest score, less distance = greater chance of match
        final Iterator<Entry<Integer, String>> itr = scores.entrySet().iterator();
        return itr.next().getValue();
    }

    /**
	 * Creates the ordered model.
	 *
	 * @param content the content
	 * @return the map
	 */
    private Map<Integer, List<String>> createOrderedModel(final String content) {
        final HashMap<String, Integer> trigrams = new HashMap<>();
        // Map ORDENADO por KEY
        final Map<Integer, List<String>> otrigrams = new TreeMap<>();

        for (int i = 0; i < (content.length() - 2); ++i) {
            final String tri = content.substring(i, i + 3).toLowerCase();
            final Integer integer = trigrams.get(tri);
            if (integer == null) {
                trigrams.put(tri, 1);
            } else {
                trigrams.put(tri, integer + 1);
            }
        }

        for (Entry<String, Integer> entry : trigrams.entrySet()) {
            // iterator QHash::insertMulti ( const Key & key, const T & value )
            // Inserts a new item with the key key and a value of value.
            // If there is already an item with the same key in the hash, this
            // function will simply create a new one. (This behavior is
            // different from insert(), which overwrites the value of an
            // existing item.)
            // See also insert() and values().
            // Otrigrams.insertMulti( - trigrams[key], key);
            List<String> trigram = otrigrams.get(-entry.getValue());
            if (trigram == null) {
                trigram = new LinkedList<>();
                otrigrams.put(-entry.getValue(), trigram);
            }
            trigram.add(entry.getKey());
        }

        return otrigrams;
    }

    /**
	 * Distance.
	 *
	 * @param model      the model
	 * @param knownModel the known model
	 * @return the int
	 */
    private int distance(final Map<Integer, List<String>> model,
                         final Map<String, Integer> knownModel) {
        int counter = -1;
        int dist = 0;

        for (List<String> values : model.values()) {
            for (String value : values) {
                if (!value.contains("  ")) {
                    if (knownModel.containsKey(value.toLowerCase())) {
                        dist += Math.abs(counter++
                                - knownModel.get(value.toLowerCase()));
                    } else {
                        dist += MAXGRAMS;
                    }
                }
                if (counter == (MAXGRAMS - 1)) {
                    break;
                }
            }
        }

        return dist;
    }

    /**
	 * Load models.
	 */
    private void loadModels() {
        final ClassLoader loader = this.getClass().getClassLoader();
        for (String trigram : ACTIVE_MODELS) {
            try {
                // Se lee el fichero
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(loader
                                .getResourceAsStream("languages/trigrams/" + trigram),
                                "utf-8"));
                // Asignamos una capacidad adecuada para que no haya operaciones
                // de rehash (300/.75=400)
                final HashMap<String, Integer> model = new HashMap<>(405);
                String linea;
                while ((linea = reader.readLine()) != null) {
                    model.put(linea.substring(0, 3).toLowerCase(), Integer
                            .parseInt(linea.substring(3).trim()));
                }
                models.put(trigram, model);
                reader.close();
            } catch (IOException e) {
                Logger.putLog("No se ha podido cargar el idioma " + trigram, GuessLanguage.class, Logger.LOG_LEVEL_ERROR, e);
            }
        }
    }

}