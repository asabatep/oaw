package es.gob.oaw.webservice.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ProblemDTO {
	private String title;
	private String description;
	private String help;
	private String type;
	private SpecificProblemDTO[] specificProblems;

	public void setSpecificProblems(List<SpecificProblemDTO> specificProblems) {
		this.specificProblems = specificProblems.toArray(new SpecificProblemDTO[specificProblems.size()]);
	}
}
