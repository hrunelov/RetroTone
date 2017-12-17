import java.util.Iterator;

public class Note {

	private static final String PARSE_ERROR = "Malformed Note";
	
	public enum Pitch {
		REST("-"),
		C("c"),
		CSHARP("#c"),
		D("d"),
		DSHARP("#d"),
		E("e"),
		F("f"),
		FSHARP("#f"),
		G("g"),
		GSHARP("#g"),
		A("a"),
		ASHARP("#a"),
		B("b");
		
		private final String label;
		
		Pitch(String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	public final double value;
	public final boolean dotted;
	public final Pitch pitch;
	public final int octave;
	public final int frequency;
	
	public Note(int value, boolean dotted, Pitch pitch, int octave) {
		if (value < 1 || value > 64) throw new IllegalArgumentException("Value must be between 1 and 64");
		if (octave < 1 || octave > 3) throw new IllegalArgumentException("Octave must be between 1 and 3");
		
		this.value = value;
		this.dotted = dotted;
		this.pitch = pitch;
		this.octave = octave;
		frequency = getFrequency();
	}
	
	public Note(String str) {
		str = str.toLowerCase();
		
		// Validate
		if (!str.matches("^([1-9]|[1-5][0-9]|6[0-4])\\.?(-|(#?c|#?d|e|#?f|#?g|#?a|b)[1-3])$")) {
			throw new IllegalArgumentException(PARSE_ERROR);
		}
		
		int i = 0;
		
		// Parse value
		double value = 0;
		for (i = 0; Character.isDigit(str.charAt(i)); ++i) {
			value = value * 10 + Integer.parseInt(""+str.charAt(i));
		}
		this.value = value;
		
		// Parse dot
		if (str.charAt(i) == '.') {
			dotted = true;
			++i;
		}
		else dotted = false;
		
		// Parse pitch
		Pitch pitch = null;
		for (Pitch p : Pitch.values()) {
			if (str.substring(i).length() > 1 && p.label.equals(str.substring(i,i+2).toLowerCase())
			|| (str.substring(i).length() > 0 && p.label.equals(str.substring(i,i+1)))) {
				 pitch = p;
			 }
		}
		this.pitch = pitch;
		
		// Parse octave
		if (pitch != Pitch.REST) octave = Integer.parseInt(""+str.charAt(str.length()-1));
		else octave = 0;
		
		frequency = getFrequency();
		
	}
	
	private int getFrequency() {
		return (int)Math.round(440 * Math.pow(2,(pitch.ordinal()+12*octave-10)/12.0));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		if (dotted) sb.append(".");
		sb.append(pitch);
		if (pitch != Pitch.REST) sb.append(octave);
		return sb.toString();
	}
	
}
