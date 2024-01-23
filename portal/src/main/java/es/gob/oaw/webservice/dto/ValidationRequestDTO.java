package es.gob.oaw.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ValidationRequestDTO {
	private String htmlContent;
	private String url;
	private String methodology;
	private boolean brokenLinks;
}
