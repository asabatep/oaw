package es.gob.oaw.webservice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertTAnalysisRequestDTO {
    protected long idAnalysis;
    protected String[] elements;
    
}
