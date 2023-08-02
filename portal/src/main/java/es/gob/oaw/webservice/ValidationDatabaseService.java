package es.gob.oaw.webservice;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.utoronto.atrc.tile.accessibilitychecker.Evaluation;
import ca.utoronto.atrc.tile.accessibilitychecker.Evaluator;
import es.inteco.common.CheckAccessibility;
import es.inteco.intav.comun.Incidencia;
import es.inteco.intav.dao.TAnalisisAccesibilidadDAO;
import es.inteco.intav.datos.IncidenciaDatos;
import es.inteco.plugin.dao.DataBaseManager;

public class ValidationDatabaseService {

    public String insertTAnalysisRequest(Long idAnalysis, List<String> elements){
        try{
            TAnalisisAccesibilidadDAO.insertUrls(DataBaseManager.getConnection(), idAnalysis, elements);
            return "Se han insertado correctamente los enlaces de accesibilidad";
        }
        catch (Exception e){
            return "Ha ocurrido un error durante la inserción de los enlaces de accesibilidad";
        }
    }

    public String saveDocumentsRequest(Long idAnalysis, Map<String,String> documents){
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
            return "Ha ocurrido un error al incrementar los checks";
        }
    }
    public int setAnalysisDbRequest(CheckAccessibility checkAccessibility, Evaluation evaluation){
        return Evaluator.setDbId(evaluation, checkAccessibility);
    
    }
    public String setIncidenceListRequest(SetIncidenceListRequest request){
        try (Connection conn = DataBaseManager.getConnection()) {
            IncidenciaDatos.saveIncidenceList(conn, request.getIdAnalysis(), Arrays.asList(request.getIncidences()));
            return "Se ha insertado la lista de incidencias con éxito";
        
        }
        catch (Exception e){
            return "Se ha producido un error al guardar la lista de incidencias";
        }
    }
    
}


  
