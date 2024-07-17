package es.oaw.wcagem.enums;

/**
 * The Enum WcagEmPointKey.
 */
public enum WcagEmPointKey {
	/** The wcag 131. */
	WCAG_1_1_1("9.1.1.1", "WCAG2:non-text-content"),
	/** The wcag9. 1 3 1. */
	WCAG_1_3_1("9.1.3.1", "WCAG2:info-and-relationships"),
	/** The wcag9. 1 3 4. */
	WCAG_1_3_4("9.1.3.4", "WCAG2:orientation"),
	/** The wcag9. 1 3 5. */
	WCAG_1_3_5("9.1.3.5", "WCAG2:identify-input-purpose"),
	/** The wcag9. 1 4 3. */
	WCAG_1_4_3("9.1.4.3", "WCAG2:contrast-minimum"),
	/** The wcag9. 1 4 10. */
	WCAG_1_4_10("9.1.4.10", "WCAG2:reflow"),
	/** The wcag9. 1 4 12. */
	WCAG_1_4_12("9.1.4.12", "WCAG2:text-spacing"),
	/** The wcag9. 2 1 1. */
	WCAG_2_1_1("9.2.1.1", "WCAG2:keyboard"),
	/** The wcag9. 2 2 1. */
	WCAG_2_2_1("9.2.2.1", "WCAG2:timing-adjustable"),
	/** The wcag9. 2 2 2. */
	WCAG_2_2_2("9.2.2.2", "WCAG2:pause-stop-hide"),
	/** The wcag9. 2 4 1. */
	WCAG_2_4_1("9.2.4.1", "WCAG2:bypass-blocks"),
	/** The wcag9. 2 4 2. */
	WCAG_2_4_2("9.2.4.2", "WCAG2:page-titled"),
	/** The wcag9. 2 4 3. */
	WCAG_2_4_3("9.2.4.3", "WCAG2:focus-order"),
	/** The wcag9. 2 4 4. */
	WCAG_2_4_4("9.2.4.4", "WCAG2:link-purpose-in-context"),
	/** The wcag9. 2 4 5. */
	WCAG_2_4_5("9.2.4.5", "WCAG2:multiple-ways"),
	/** The wcag9. 2 4 5. */
	WCAG_2_4_7("9.2.4.7", "WCAG2:focus-visible"),
	/** The wcag9. 2 5 3. */
	WCAG_2_5_3("9.2.5.3", "WCAG2:label-in-name"),
	/** The wcag9. 3 1 1. */
	WCAG_3_1_1("9.3.1.1", "WCAG2:language-of-page"),
	/** The wcag9. 3 1 2. */
	WCAG_3_1_2("9.3.1.2", "WCAG2:language-of-parts"),
	/** The wcag9. 3 2 1. */
	WCAG_3_2_1("9.3.2.1", "WCAG2:on-focus"),
	/** The wcag9. 3 2 2. */
	WCAG_3_2_2("9.3.2.2", "WCAG2:on-input"),
	/** The wcag9. 3 2 3. */
	WCAG_3_2_3("9.3.2.3", "WCAG2:consistent-navigation"),
	/** The wcag9. 3 3 2. */
	WCAG_3_3_2("9.3.3.2", "WCAG2:labels-or-instructions"),
	/** The wcag9. 4 1 1. Deprecated */
	WCAG_4_1_1("9.4.1.1", "WCAG2:parsing"),
	/** The wcag9. 4 1 2. */
	WCAG_4_1_2("9.4.1.2", "WCAG2:name-role-value"),

	WCAG_10_1_1_1("10.1.1.1", "WCAG2:non-text-content"),
	/** The wcag 1 3 1. */
	WCAG_10_1_3_1("10.1.3.1", "WCAG2:info-and-relationships"),

	WCAG_10_2_4_2("10.2.4.2", "WCAG2:page-titled"),
	WCAG_10_3_1_1("10.3.1.1", "WCAG2:language-of-page"),
	WCAG_10_4_1_1("10.4.1.1", "WCAG2:parsing"),
	/** T10_he wcag10. 4 1 2. */
	WCAG_10_4_1_2("10.4.1.2", "WCAG2:name-role-value");

	/** The wcag point. */
	private final String wcagPoint;
	/** The wcag em id. */
	private final String wcagEmId;

	/**
	 * Instantiates a new wcag em point key.
	 *
	 * @param wcagPoint the wcag point
	 * @param wcagEmId  the wcag em id
	 */
	WcagEmPointKey(String wcagPoint, String wcagEmId) {
		this.wcagPoint = wcagPoint;
		this.wcagEmId = wcagEmId;
	}

	/**
	 * Gets the wcag point.
	 *
	 * @return the wcag point
	 */
	public String getWcagPoint() {
		return wcagPoint;
	}

	/**
	 * Gets the wcag em id.
	 *
	 * @return the wcag em id
	 */
	public String getWcagEmId() {
		return wcagEmId;
	}

	/**
	 * Find by point.
	 *
	 * @param point the point
	 * @return the wcag em point key
	 */
	public static WcagEmPointKey findByPoint(String point) {
		for (WcagEmPointKey v : values()) {
			if (v.wcagPoint.equals(point)) {
				return v;
			}
		}
		return null;
	}
}
