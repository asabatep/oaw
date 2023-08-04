package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertTAnalysisRequestDTO {
    protected Long idAnalysis;
    protected String[] elements;
    
}
