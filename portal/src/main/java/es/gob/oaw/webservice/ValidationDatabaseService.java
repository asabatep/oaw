package es.gob.oaw.webservice;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.jfree.util.Log;

import ca.utoronto.atrc.tile.accessibilitychecker.Evaluation;
import ca.utoronto.atrc.tile.accessibilitychecker.Evaluator;
import es.gob.oaw.css.CSSImportedResource;
import es.gob.oaw.css.CSSResource;
import es.gob.oaw.webservice.dto.CSSResourceDTO;
import es.gob.oaw.webservice.dto.IncrementChecksOkRequestDTO;
import es.gob.oaw.webservice.dto.InsertTAnalysisRequestDTO;
import es.gob.oaw.webservice.dto.SaveDocumentsRequestDTO;
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
            TAnalisisAccesibilidadDAO.insertUrls(DataBaseManager.getConnection(), insertTAnalysisRequestDTO.getIdAnalysis(), Arrays.asList(insertTAnalysisRequestDTO.getElements()));
            return "Se han insertado correctamente los enlaces de accesibilidad";
        }
        catch (Exception e){
            return "Ha ocurrido un error durante la inserción de los enlaces de accesibilidad";
        }
    }

    public String saveDocumentsRequest(SaveDocumentsRequestDTO saveDocumentsRequestDTO){
        try{
            for (Entry<String,String> document :saveDocumentsRequestDTO.getElements().entrySet()) {
                TAnalisisAccesibilidadDAO.saveDocumentUrl(DataBaseManager.getConnection(), saveDocumentsRequestDTO.getIdAnalysis(), document.getKey(), document.getValue());
            }
            return "Se han insertado correctamente los documentos de accesibilidad";
        }
        catch (Exception e){
            return "Ha ocurrido un error durante la inserción de los documentos de accesibilidad";
        }
    }

    public String incrementChecksOkRequest(IncrementChecksOkRequestDTO incrementChecksOkRequestDTO){
        try{
            for (String element : incrementChecksOkRequestDTO.getElements()) {
                TAnalisisAccesibilidadDAO.incrementCheckOk(DataBaseManager.getConnection(), incrementChecksOkRequestDTO.getIdAnalysis(), element);
            }
            return "Se han incrementado correctamente los checks";
            
        }
        catch (Exception e){
            return "Ha ocurrido un error al incrementar los checks";
        }
    }
    public int setAnalysisDbRequest(SetAnalysisDBRequestDTO setAnalysisDBRequestDTO){
        Log.warn("Insertar analisis en BBDD");
        Evaluation evaluation = new Evaluation();
        CheckAccessibility checkAccessibility = new CheckAccessibility();
        checkAccessibility.setContent(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getContent());
        checkAccessibility.setIdRastreo(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getIdRastreo());
        checkAccessibility.setGuidelineFile(setAnalysisDBRequestDTO.getCheckAccessibilityDTO().getGuidelineFile());
        evaluation.setEntidad(setAnalysisDBRequestDTO.getEvaluationDTO().getEntity());
        CSSResourceDTO[] resources = setAnalysisDBRequestDTO.getEvaluationDTO().getCssResourcesDTO();
        List<CSSResourceDTO> resourceDTOList = Arrays.asList(resources);
        List<CSSResource> resourceList = new ArrayList<>();
        for (CSSResourceDTO cssResourceDTO : resourceDTOList) {
            CSSImportedResource resource = new CSSImportedResource();
            resource.setContent(cssResourceDTO.getContent());
            resource.setSource(cssResourceDTO.getSource());
            resourceList.add(resource);
        }
        evaluation.setCssResources(resourceList);
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
    
}


  
