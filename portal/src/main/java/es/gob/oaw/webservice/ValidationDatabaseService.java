package es.gob.oaw.webservice;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import es.inteco.common.logging.Logger;
import es.inteco.common.utils.StringUtils;

import org.jfree.util.Log;

import ca.utoronto.atrc.tile.accessibilitychecker.Evaluation;
import ca.utoronto.atrc.tile.accessibilitychecker.Evaluator;
import es.gob.oaw.css.CSSImportedResource;
import es.gob.oaw.css.CSSResource;
import es.gob.oaw.webservice.dto.CSSResourceDTO;
import es.gob.oaw.webservice.dto.InsertTAnalysisRequestDTO;
import es.gob.oaw.webservice.dto.SetAnalysisDBRequestDTO;
import es.gob.oaw.webservice.dto.SetAnalysisSuccessRequestDTO;
import es.gob.oaw.webservice.dto.SetIncidenceListRequestDTO;
import es.inteco.common.CheckAccessibility;
import es.inteco.intav.dao.TAnalisisAccesibilidadDAO;
import es.inteco.intav.datos.AnalisisDatos;
import es.inteco.intav.datos.IncidenciaDatos;
import es.inteco.plugin.dao.DataBaseManager;

public class ValidationDatabaseService {

    public String insertTAnalysisRequest(InsertTAnalysisRequestDTO insertTAnalysisRequestDTO){
        try{
            TAnalisisAccesibilidadDAO.insertUrls(DataBaseManager.getConnection(), insertTAnalysisRequestDTO.getIdAnalysis(),Arrays.asList(insertTAnalysisRequestDTO.getElements()));
            return "Se han insertado correctamente los enlaces de accesibilidad";
        }
        catch (Exception e){
            return "Ha ocurrido un error durante la inserción de los enlaces de accesibilidad";
        }
    }

    public String saveDocumentsRequest(Long idAnalysis, Map<String, String> documents){
        try{
            for (Entry<String,String> document :documents.entrySet()) {
                TAnalisisAccesibilidadDAO.saveDocumentUrl(DataBaseManager.getConnection(), idAnalysis, document.getKey(), document.getValue());
            }
            return "Se han insertado correctamente los documentos de accesibilidad";
        }
        catch (Exception e){
            return "Ha ocurrido un error durante la inserción de los documentos de accesibilidad";
        }
    }

    public String incrementChecksOkRequest(Long idAnalysis, List<String> elements){
        try{
            for (String element : elements) {
                TAnalisisAccesibilidadDAO.incrementCheckOk(DataBaseManager.getConnection(), idAnalysis, element);
            }
            return "Se han incrementado correctamente los checks";
            
        }
        catch (Exception e){
            Logger.putLog(e.getMessage(), ValidationDatabaseService.class, Logger.LOG_LEVEL_ERROR, e);
            return "Ha ocurrido un error al incrementar los checks: " + e.getMessage();
        }
    }
    public int setAnalysisDbRequest(SetAnalysisDBRequestDTO setAnalysisDBRequestDTO){
        Log.warn("Insertar analisis en BBDD");
        Evaluation evaluation = new Evaluation();
        CheckAccessibility checkAccessibility = new CheckAccessibility();
        if(StringUtils.isBase64(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getContent())){
            checkAccessibility.setContent(new String(Base64.getDecoder().decode(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getContent())));
        }
    
        else checkAccessibility.setContent(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getContent());
        Logger.putLog("CONTEEENT: "+ checkAccessibility.getContent(), ValidationDatabaseService.class, Logger.LOG_LEVEL_ERROR);
        checkAccessibility.setIdRastreo(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getIdRastreo());
        checkAccessibility.setGuidelineFile(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getGuidelineFile());
        evaluation.setEntidad(setAnalysisDBRequestDTO.getEvaluationDTO().getEntity());
        evaluation.setFilename(setAnalysisDBRequestDTO.getEvaluationDTO().getFilename());
        CSSResourceDTO[] resources = setAnalysisDBRequestDTO.getEvaluationDTO().getCssResourcesDTO();
        List<CSSResource> resourceList = new ArrayList<>();
        if(resources != null){
            List<CSSResourceDTO> resourceDTOList = Arrays.asList(resources);
        for (CSSResourceDTO cssResourceDTO : resourceDTOList) {
            CSSImportedResource resource = new CSSImportedResource();
            resource.setContent(cssResourceDTO.getContent());
            resource.setSource(cssResourceDTO.getSource());
            resourceList.add(resource);
        }
        evaluation.setCssResources(resourceList);
        
    }
        else evaluation.setCssResources(null);
        return Evaluator.setDbId(evaluation, checkAccessibility);
    }

    public String setIncidenceListRequest(SetIncidenceListRequestDTO setIncidenceListRequestDTO){
        Log.info("Cargar incidencias en BBDD");
        try (Connection conn = DataBaseManager.getConnection()) {
            IncidenciaDatos.saveIncidenceList(conn, setIncidenceListRequestDTO.getIdAnalysis(), Arrays.asList(setIncidenceListRequestDTO.getIncidences()));
            return "Se ha insertado la lista de incidencias con éxito";
        
        }
        catch (Exception e){
            return "Se ha producido un error al guardar la lista de incidencias";
        }
    }

    public String setAnalysisSuccessRequest(SetAnalysisSuccessRequestDTO setAnalysisSuccessRequestDTO){
        Log.warn("insert analysis success");
        Evaluation evaluation = new Evaluation();
        evaluation.setChecksExecutedStr(setAnalysisSuccessRequestDTO.getChecksExecuted());
        evaluation.settevaluation(setAnalysisSuccessRequestDTO.getTAnalisis());
        evaluation.setIdAnalisis(setAnalysisSuccessRequestDTO.getIdAnalisis());
        AnalisisDatos.endAnalysisSuccess(evaluation);
        return "Analisis finalizado con exito";
        
    }

    public String setAnalysisErrorRequest(CheckAccessibility checkAccessibility){
        Log.warn("Insert Analysis Error");
        AnalisisDatos.setAnalysisError(checkAccessibility);
        return "Análisis fallido insertado con exito";
    }
    
    
}


  
