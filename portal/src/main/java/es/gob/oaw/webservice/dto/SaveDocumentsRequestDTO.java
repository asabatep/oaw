package es.gob.oaw.webservice.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveDocumentsRequestDTO {
    protected Long idAnalysis;
    protected Map<String,String> elements;
    
}
