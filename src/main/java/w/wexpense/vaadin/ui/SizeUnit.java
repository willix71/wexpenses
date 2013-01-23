package w.wexpense.vaadin.ui;

public enum SizeUnit {
	PIXELS(0,"px"),
	POINTS(1,"pt"),
	PICAS(2,"pc"),
	EM(3,"em"),
	EX(4,"ex"),
	MM(5,"mm"),
	CM(6,"cm"),
	INCH(7,"in"),
	PERCENTAGE(8,"%");
    
	private int value;
	private String abreviation;
	
	private SizeUnit(int value, String abreviation) {
		this.value = value;
		this.abreviation = abreviation;
	}

	public int getValue() {
		return value;
	}

	public String getAbreviation() {
		return abreviation;
	}
	
	public static SizeUnit fromValue(int value) {
		for(SizeUnit unit: SizeUnit.values()) {
			if (unit.value==value) return unit;
		}
		throw new IllegalArgumentException("Unknown size unit value " + value);
	}
	
	public static SizeUnit fromAbreviation(String abreviation) {
		for(SizeUnit unit: SizeUnit.values()) {
			if (unit.abreviation.equals(abreviation)) return unit;
		}
		throw new IllegalArgumentException("Unknown size unit abreviation " + abreviation);
	}
}

