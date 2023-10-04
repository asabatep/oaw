package es.inteco.intav.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidatorForm {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2792056465019141849L;
	
	/** The status. */
	private Integer status;
	
	/** The url. */
	private String url;

	/** If the pdf validation is active */
	private Integer pdfActive;
	
    
}
