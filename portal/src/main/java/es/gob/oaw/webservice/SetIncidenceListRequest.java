package es.gob.oaw.webservice;

import es.inteco.intav.comun.Incidencia;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "setIncidenceListRequest", namespace = "http://ws.apache.org/axis2/oaw/")
public class SetIncidenceListRequest {
  @XmlElement(name = "idAnalysis", required = true)
  protected int idAnalysis;

  @XmlElement(name = "incidencias", required = true)
  protected Incidencia[] incidences;
}
