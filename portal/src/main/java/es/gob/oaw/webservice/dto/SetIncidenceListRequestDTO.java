package es.gob.oaw.webservice.dto;

import es.inteco.intav.comun.Incidencia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class SetIncidenceListRequestDTO {
	public int idAnalysis;
	public Incidencia[] incidences;
}
