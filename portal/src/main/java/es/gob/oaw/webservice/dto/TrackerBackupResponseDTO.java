package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class TrackerBackupResponseDTO {
	private String content;
	private String observations;
	private boolean validExport;
}
