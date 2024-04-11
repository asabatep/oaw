package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertTAnalysisRequestDTO {
	protected long idAnalysis;
	protected String[] elements;
}
