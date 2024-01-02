package es.gob.oaw.webservice.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProblemDTO {

  @XmlElement(name = "description", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  protected String description;

  @XmlElement(name = "help", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  protected String help;

  @XmlElement(name = "specificProblems", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  @Builder.Default
  protected List<SpecificProblemDTO> specificProblems = new ArrayList<>();

  @XmlElement(name = "type", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  protected String type;

  @XmlElement(name = "title", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  protected String title;

  @XmlElement(name = "problemNumber", namespace = "http://dto.webservice.oaw.gob.es/xsd")
  protected int problemNumber;
}
