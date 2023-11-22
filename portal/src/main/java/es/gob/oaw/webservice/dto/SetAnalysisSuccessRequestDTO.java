package es.gob.oaw.webservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class SetAnalysisSuccessRequestDTO {

  public long tAnalisis;
  
  public String checksExecuted;

  public long idAnalisis;
    
}
