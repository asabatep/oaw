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
public class SpecificProblemDTO {
	private String line;
	private String column;
	private String code;

	public void setCode(List<String> code) {
		String formatCode = String.join("", code);
		formatCode = formatCode.replace("&lt;", "<");
		formatCode = formatCode.replace("&gt;", ">");
		this.code = formatCode;
	}
}
