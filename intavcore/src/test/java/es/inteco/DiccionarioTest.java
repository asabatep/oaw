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
package es.inteco;

import org.junit.Test;

import es.gob.oaw.language.Diccionario;

import java.io.InputStream;
import java.net.URL;

/**
 *
 */
public class DiccionarioTest {

    @Test
    public void testDiccionarioIsInitialized() throws Exception {
        final InputStream is = this.getClass().getClassLoader().getResourceAsStream("words_en.properties");
        final URL url = this.getClass().getClassLoader().getResource("words_en.properties");

        final InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("/words_en.properties");
        final URL url2 = this.getClass().getClassLoader().getResource("/words_en.properties");

        final InputStream is3 = this.getClass().getClassLoader().getResourceAsStream("/languages/words_en.txt");
        final URL url3 = this.getClass().getClassLoader().getResource("/languages/words_en.txt");

        final InputStream is4 = this.getClass().getClassLoader().getResourceAsStream("languages/words_en.txt");
        final URL url4 = this.getClass().getClassLoader().getResource("languages/words_en.txt");

        final InputStream is5 = this.getClass().getClassLoader().getResourceAsStream("es/ctic/language/words_en.properties");
        final URL url5 = this.getClass().getClassLoader().getResource("es/ctic/language/words_en.properties");

        final InputStream is6 = this.getClass().getClassLoader().getResourceAsStream("/es/ctic/language/words_en.properties");
        final URL url6 = this.getClass().getClassLoader().getResource("/es/ctic/language/words_en.properties");

        final InputStream is7 = this.getClass().getClassLoader().getResourceAsStream("es.gob.oaw.language.words_en.properties");
        final URL url7 = this.getClass().getClassLoader().getResource("es.gob.oaw.language.words_en.properties");

        final InputStream is8 = Diccionario.class.getClassLoader().getResourceAsStream("./es/ctic/language/words_en.properties");
        final URL url8 = Diccionario.class.getClassLoader().getResource("./words_en.properties");
    }

}
