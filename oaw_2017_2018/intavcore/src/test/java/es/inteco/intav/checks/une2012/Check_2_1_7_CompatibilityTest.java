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
package es.inteco.intav.checks.une2012;

import ca.utoronto.atrc.tile.accessibilitychecker.Evaluation;
import ca.utoronto.atrc.tile.accessibilitychecker.EvaluatorUtility;
import es.inteco.common.CheckAccessibility;
import es.inteco.intav.TestUtils;
import es.inteco.intav.utils.EvaluatorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public final class Check_2_1_7_CompatibilityTest {

    public static final String MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7 = "minhap.observatory.2_0.subgroup.2.1.7";

    /* Documento tenga un DTD válido */
    private static final int DOCTYPE_VALID = 323;
    /* Código HTML sea parseable (apertura y cierre de etiquetas y anidamiento correcto de elementos) */
    private static final int HTML_PARSEABLE = 440;
    /* Se verifica que no se repite el mismo atributo con diferente valor en el mismo elemento */
    private static final int DUPLICATED_ATTRIBUTES = 441;
    /* Se verifica que los valores de los atributos están entrecomillados */
    private static final int QUOTED_ATTRIBUTES = 439;
    /* Se verifica que el valor de los atributos que deben tener un valor único por página (“id”, “accesskey”) efectivamente tienen un valor único */
    private static final int IDS_UNIQUES = 438;
    /* Se verifica que el código CSS es parseable (bien formado, sin errores de sintaxis) */
    private static final int CSS_PARSEABLE = 450;

    private static final String DOCTYPE_HTML5 = "<!DOCTYPE html>" + System.lineSeparator();
    private static final String DOCTYPE_HTML4 = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + System.lineSeparator();

    private CheckAccessibility checkAccessibility;

    @Before
    public void setUp() throws Exception {
        EvaluatorUtility.initialize();
        checkAccessibility = TestUtils.getCheckAccessibility("observatorio-une-2012", true);
    }

    @Test
    public void evaluateNoDoctype() throws Exception {
        checkAccessibility.setContent("<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateValidDoctype() throws Exception {
        checkAccessibility.setContent(DOCTYPE_HTML4 + "<html><body><p id=\"lorem\">Lorem ipsum</p><p>Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);

        checkAccessibility.setContent(DOCTYPE_HTML5 + "<html><body><p id=\"lorem\">Lorem ipsum</p><p>Lorem ipsum</p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateInvalidDoctype() throws Exception {
        checkAccessibility.setContent("<!DOCTYPE HTML PUBLIC \"4.01\">" + System.lineSeparator() + "<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//ES\" \"http://www.w3.org/TR/html4/strict.dtd\">" + System.lineSeparator() + "<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateXHTMLRDFaDoctype() throws Exception {
        checkAccessibility.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\"\n" +
                "  \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">" +
                "<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
    }

    @Test
    public void evaluateIDsUniques() throws Exception {
        checkAccessibility.setContent("<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        checkAccessibility.setTemplateContent("<html><body><p id=\"lorem\">Lorem ipsum</p><p id=\"lorem\">Lorem ipsum</p></body></html>");
        checkAccessibility.setWebService(true);
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), IDS_UNIQUES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateNoQuotedSlashCharAttribute() throws Exception {
        checkAccessibility.setContent("<html><body><p>Lorem ipsum <img src=/images/blank.png></p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateNoQuotedWhiteSpaceAttribute() throws Exception {
        checkAccessibility.setContent("<html><body><p id=lorem ipsum>Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateSingleAttribute() throws Exception {
        checkAccessibility.setContent("<html><body><p single>Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateDuplicatedAttribute() throws Exception {
        checkAccessibility.setContent("<html><body><p class=\"single\" class=\"doble\">Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateDuplicatedAttributeSameValue() throws Exception {
        checkAccessibility.setContent("<html><body><p class=\"single\" class=\"single\">Lorem ipsum</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSS() throws Exception {
        checkAccessibility.setContent("<html><style>.main { color: #FFF background-color: #FFF;}</style><p>Lorem ipsum</p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<html><style>#heading .company-title a{margin 0 auto}</style><p>Lorem ipsum</p></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSSTwoOpenBrackets() throws Exception {
        checkAccessibility.setContent("<html><style type=\"text/css\">.main { color: #FFF; a { background-color: #FFF;}</style><p>Lorem ipsum</p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSSPropietaryProperty() throws Exception {
        checkAccessibility.setContent(DOCTYPE_HTML4 + "<html><style>.main { -moz-border-radius: 5px;}</style><p>Lorem ipsum</p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), HTML_PARSEABLE));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), IDS_UNIQUES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateCSSWrongValue() throws Exception {
        checkAccessibility.setContent("<html><style>.main { color: bold;}</style><p>Lorem ipsum</p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
    }

    @Test
    public void evaluateCSS404() throws Exception {
        checkAccessibility.setContent(DOCTYPE_HTML4 + "<html lang=\"es\"><head><title>Foo</title><link href=\"http://www.noexiste.no/css/no_existe.css\" rel=\"stylesheet\" type=\"text/css\"/></head><body><p>Lorem</p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), HTML_PARSEABLE));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), IDS_UNIQUES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateMediaQueries() throws Exception {
        checkAccessibility.setContent("<html><head><title>Foo</title>" +
                "<style type=\"text/css\" media=\"screen\">" +
                " @media screen and (max-width : 400px) and (color) { a {color: #000; background-color: #000} }"+
                "</style>" +
                "</head><body><p>Lorem</p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        //Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), 448 /*ColorContrast*/));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
    }

    @Test
    public void evaluateCSSSyntaxMediaQueries() throws Exception {
        checkAccessibility.setContent("<html><head><title>Foo</title>" +
                "<style type=\"text/css\" media=\"screen\">" +
                " @media screen and (max-width : 400px) and (color) { a {color: #000 background-color: #000} }"+
                "</style>" +
                "</head><body><p>Lorem</p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_PARSEABLE));
        TestUtils.printProblems(evaluation.getProblems(), CSS_PARSEABLE);
    }

    @Test
    public void evaluateScapingStyleContent() throws Exception {
        checkAccessibility.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">" + System.lineSeparator() +
                "<html><head><title>Foo</title></head><style type=\"text/css\" media=\"all\">\n" +
                "<!--/*--><![CDATA[/*><!--*/\n" +
                "\n" +
                "/*]]>*/-->\n" +
                "</style><body><p>Lorem</p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), HTML_PARSEABLE));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), IDS_UNIQUES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateURL() throws Exception {
        checkAccessibility.setUrl("http://sepes.es/");
        final Evaluation evaluation = EvaluatorUtils.evaluate(checkAccessibility, "es");
        TestUtils.printHtml(evaluation.getHtmlDoc());
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DOCTYPE_VALID));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), HTML_PARSEABLE));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), IDS_UNIQUES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), DUPLICATED_ATTRIBUTES));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), QUOTED_ATTRIBUTES));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_1_7, TestUtils.OBS_VALUE_GREEN_ONE);
    }
}
