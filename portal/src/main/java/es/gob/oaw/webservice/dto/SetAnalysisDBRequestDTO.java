package es.gob.oaw.webservice.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class SetAnalysisDBRequestDTO {

  public CheckAccessibilityDTO checkAccessibilityDTO;
  public EvaluationDTO evaluationDTO;
    
}
