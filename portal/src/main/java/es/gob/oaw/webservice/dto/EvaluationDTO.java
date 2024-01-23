package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class EvaluationDTO {
	private String filename;
	private String entity;
	private Long tracker;
	private CSSResourceDTO[] cssResourcesDTO;
}