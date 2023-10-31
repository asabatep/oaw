package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncrementChecksOkRequestDTO {
    protected Long idAnalysis;
    protected String[] elements;
    
}
