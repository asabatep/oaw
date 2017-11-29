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
import ca.utoronto.atrc.tile.accessibilitychecker.Problem;
import es.inteco.common.CheckAccessibility;
import es.inteco.intav.TestUtils;
import es.inteco.intav.utils.EvaluatorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public final class Check_1_2_2_ColorContrast {

    private static final String MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2 = "minhap.observatory.2_0.subgroup.1.2.2";

    private static final int CSS_COLOR_CONTRAST = 448;

    private CheckAccessibility checkAccessibility;

    @BeforeClass
    public static void init() throws Exception {
        EvaluatorUtility.initialize();
    }

    @Before
    public void setUp() throws Exception {
        checkAccessibility = TestUtils.getCheckAccessibility("observatorio-une-2012");
    }

    @Test
    public void evaluateCSSSameColors() throws Exception {
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #FFF;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<html><head><style>u { color: #FFF; background-color: #FFF;}</style><title>Lorem</title></head><body><p>Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<html><head><style>p u { color: #FFF; background-color: #FFF;}</style><title>Lorem</title></head><body><p>Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSSSameColorsName() throws Exception {
        checkAccessibility.setContent("<html><head><style>.main { color: white; background-color: white;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSSMaximumContrast() throws Exception {
        checkAccessibility.setContent("<html><head><style>.main { color: #000; background-color: #FFF;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateCSSMaximumContrastColorName() throws Exception {
        checkAccessibility.setContent("<html><head><style>.main { color: black; background-color: #FFF;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateCSSNoColor() throws Exception {
        checkAccessibility.setContent("<html><head><style>.main { color: black; }</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateCSSBackground() throws Exception {
        //<link href="http://www.thespanisheconomy.com/recursos_tse/css/style.css" rel="stylesheet"/>
        checkAccessibility.setContent("<html><head><link href=\"http://www.thespanisheconomy.com/recursos_tse/css/style.css\" rel=\"stylesheet\" type=\"text/css\"/><title>Lorem</title></head><body><p>Lorem <u>ipsum</u></p></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");


        for (Problem problem : evaluation.getProblems()) {
            if (problem.getCheck().getId()==CSS_COLOR_CONTRAST) {
                System.out.printf("%d:%d  %s%n", problem.getLineNumber(), problem.getColumnNumber(), problem.getNode().getTextContent());
            }
        }
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    /**
     * *************
     * ABB     *
     * **************
     */

        /*
            MET 4.9.1 - Se verifica que las combinaciones de color de primer plano y de color de fondo en una misma regla de las hojas de estilo tienen el contraste suficiente (NUEVA)

            CSS_COLOR_CONTRAST= 448;

        */
    @Test
    public void MET_4_9_1_evaluateContrast() throws Exception {

        /* MET 4.9.1
            Title:      Se verifica que las combinaciones de color de primer plano y de color de fondo en una misma regla de las hojas de estilo tienen el contraste suficiente
            Subject:    estilos (<style>, "style", <link rel="stylesheet">)
            Check:      Combinaciones de primer plano (color) y de fondo (background-color, background) en una misma regla tienen contraste suficiente.
                            3:1 o 4.5:1 según el tamaño del texto si éste es conocido
                            3:1 si no se conoce el tamaño del texto
         */

        // Contraste 3.5:1, tamaño texto indefinido
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #888;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);


        // FALLA: Expected 1, Actual 0
        // Contraste 3.5:1, tamaño texto pequeño
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #999; font-size: 10pt}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);


        // FALLA: Expected 1, Actual 0
        // Contraste 3.5:1, tamaño texto pequeño (14pt)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #999; font-size: 14pt;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);


        // Contraste 3.5:1, tamaño texto grande (18pt)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #888; font-size: 18pt}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);


        // Contraste 3.5:1, tamaño texto grande (14pt+bold)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background-color: #888; font-size: 14pt; font-weight: bold}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);


        // Background property

        // Contraste 21:1, tamaño texto indefinido
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #000 url(\"img_tree.png\") no-repeat right top;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        // FALLA: Expected 1, Actual 0
        // Contraste 1:1, tamaño texto indefinido
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #FFF url(\"img_tree.png\") no-repeat right top;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #000 url(\"img_tree.png\") no-repeat right top;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        // Contraste 3.5:1, tamaño texto indefinido
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #888 url(\"img_tree.png\") no-repeat right top;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        // FALLA: Expected 1, Actual 0
        // Contraste 3.5:1, tamaño texto pequeño
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #888 url(\"img_tree.png\") no-repeat right top; font-size: 10pt}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        // FALLA: Expected 1, Actual 0
        // Contraste 3.5:1, tamaño texto pequeño (14pt)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #888 url(\"img_tree.png\") no-repeat right top; font-size: 14pt;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        // Contraste 3.5:1, tamaño texto grande (18pt)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #888 url(\"img_tree.png\") no-repeat right top; font-size: 18pt}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        // Contraste 3.5:1, tamaño texto grande (14pt+bold)
        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: #888 url(\"img_tree.png\") no-repeat right top; font-size: 14pt; font-weight: bold}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: none no-repeat right top #000;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        checkAccessibility.setContent("<html><head><style>.main { color: #FFF; background: none no-repeat right top #FFF;}</style><title>Lorem</title></head><body><p class=\"main\">Lorem <u>ipsum</u></p></body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_1_2_2, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void testStyle() throws Exception {
        checkAccessibility.setContent("<html><head>" +
                "<style>#cabecera .barraConfiguracion { color: #FFF; background-color: #C80E0E;}</style>"+
                "</head><body><p id=\"cabecera\"><span class=\"barraConfiguracion\">Lorem ipsum</span></body></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
    }

    @Test
    public void testS() throws Exception {
//        checkAccessibility.setContent("<html><head>" +
//                "<style>h2.objetivo {\n" +
//                "        background: url(\"../SiteCollectionImages/fn-objetivo.png\") no-repeat scroll left top rgb(0, 0, 0);" +
//                "        color: #fff;}</style>"+
//                "</head><body><h2 class=\"objetivo\">Foo</h2><p>Lorem ipsum</body></html>");
//        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
//        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
//
//        checkAccessibility.setContent("<html><head>" +
//                "<style>p { display: block; background: url(\"../img/bullet-lista-izq0.png\") 2% 50% no-repeat #f5f9fd; padding: 0.5em 0 0.5em 1.3em; text-decoration: none; color: #7abdde; font-size: 1.1em; font-weight: bold; }</style>"+
//                "</head><body><p>Lorem ipsum</body></html>");
//        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
//        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
//
//        checkAccessibility.setUrl("http://administracionelectronica.gob.es/pae_Home.html");
//        Evaluation evaluation = EvaluatorUtils.evaluate(checkAccessibility, "es");
//        TestUtils.printProblems(evaluation.getProblems(),448);
//        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
    }


    @Test
    public void testMedia() throws Exception {
        checkAccessibility.setContent("<html><head>" +
                "<style>@media print {.main { color: #FFF; background-color: #FFF;} }</style>"+
                "</head><body><p class=\"main\">Lorem ipsum</body></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));

        checkAccessibility.setContent("<html><head>" +
                "<style>@media print {" +
                "  #cabecera { color: #FFF; background-color: #FFF; } " +
                "  @media screen { #main {color: #FFF; background-color: #FFF;} }" +
                "}</style>"+
                "</head><body><p id=\"main\">Lorem ipsum</body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));

        checkAccessibility.setContent("<html><head>" +
                "<style media=\"print\">#cabecera { color: #FFF; background-color: #FFF; }</style>"+
                "</head><body><p id=\"cabecera\">Lorem ipsum</body></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_COLOR_CONTRAST));
    }
}