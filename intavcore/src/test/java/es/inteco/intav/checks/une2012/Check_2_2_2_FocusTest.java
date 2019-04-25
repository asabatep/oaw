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
public final class Check_2_2_2_FocusTest {

    public static final String MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2 = "minhap.observatory.2_0.subgroup.2.2.2";

    private static final int TABINDEX_USSAGE_LOW = 434;
    private static final int TABINDEX_USSAGE_EXCESSIVE = 435;
    private static final int CSS_OUTLINE = 451;

    private CheckAccessibility checkAccessibility;

    @Before
    public void setUp() throws Exception {
        EvaluatorUtility.initialize();
        checkAccessibility = TestUtils.getCheckAccessibility("observatorio-une-2012");
    }

    @Test
    public void evaluateZeroTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a>Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateIgnorableTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a tabindex=\"0\">Lorem ipsum</a><a tabindex=\"0\">Lorem ipsum</a><a tabindex=\"-1\">Lorem ipsum</a><a tabindex=\"-1\">Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }


    @Test
    public void evaluateOneTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a tabindex=\"1\">Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateThreeTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a tabindex=\"1\">Lorem ipsum</a>, <a tabindex=\"2\">Lorem ipsum</a>, <a tabindex=\"3\">Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateFourTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a tabindex=\"1\">Lorem ipsum</a>, <a tabindex=\"2\">Lorem ipsum</a>, <a tabindex=\"3\">Lorem ipsum</a>, <a tabindex=\"4\">Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(4, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ZERO);
    }

    @Test
    public void evaluateTenTabindex() throws Exception {
        checkAccessibility.setContent("<html><p><a tabindex=\"1\">Lorem ipsum</a>, <a tabindex=\"-1\">Lorem ipsum</a>, <a tabindex=\"2\">Lorem ipsum</a>, <a tabindex=\"3\">Lorem ipsum</a>, <a tabindex=\"4\">Lorem ipsum</a></p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(4, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ZERO);
    }

    @Test
    public void evaluateMoreTenTabindex() throws Exception {
        checkAccessibility.setContent("<html><p>" +
                "<a tabindex=\"1\">Lorem ipsum</a>," +
                "<a tabindex=\"2\">Lorem ipsum</a>," +
                "<a tabindex=\"3\">Lorem ipsum</a>," +
                "<a tabindex=\"4\">Lorem ipsum</a>," +
                "<a tabindex=\"5\">Lorem ipsum</a>," +
                "<a tabindex=\"6\">Lorem ipsum</a>," +
                "<a tabindex=\"7\">Lorem ipsum</a>," +
                "<a tabindex=\"8\">Lorem ipsum</a>," +
                "<a tabindex=\"9\">Lorem ipsum</a>," +
                "<a tabindex=\"10\">Lorem ipsum</a>," +
                "<a tabindex=\"11\">Lorem ipsum</a>" +
                "</p></html>");
        final Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");

        Assert.assertEquals(11, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_LOW));
        Assert.assertEquals(11, TestUtils.getNumProblems(evaluation.getProblems(), TABINDEX_USSAGE_EXCESSIVE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_RED_ZERO);
    }

    @Test
    public void evaluateCSSOutline() throws Exception {
        checkAccessibility.setContent("<html><style>.main:focus { outline: 10px solid none }</style><p><a class=\"main\">Lorem</a></p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(1, TestUtils.getNumProblems(evaluation.getProblems(), CSS_OUTLINE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_RED_ZERO);

        checkAccessibility.setContent("<html><style>.main:focus { outline: 10px solid none; border: 1px solid #000; }</style><p><a class=\"main\">Lorem</a></p></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_OUTLINE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);

        checkAccessibility.setContent("<html><style>.main:focus { outline: 10px solid none; background-color: #000; }</style><p><a class=\"main\">Lorem</a></p></html>");
        evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_OUTLINE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }

    @Test
    public void evaluateBoxShadow() throws Exception {
        checkAccessibility.setContent("<html><style>.main:focus { outline: 0; box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(82, 168, 236, 0.6); }</style><p><a class=\"main\">Lorem</a></p></html>");
        Evaluation evaluation = EvaluatorUtils.evaluateContent(checkAccessibility, "es");
        Assert.assertEquals(0, TestUtils.getNumProblems(evaluation.getProblems(), CSS_OUTLINE));
        TestUtils.checkVerificacion(evaluation, MINHAP_OBSERVATORY_2_0_SUBGROUP_2_2_2, TestUtils.OBS_VALUE_GREEN_ONE);
    }
}

