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
	public final Pitch pitch;
	public final int octave;
	public final int frequency;
	
	public Note(int value, Pitch pitch, int octave) {
		if (value < 1) throw new IllegalArgumentException("Value must be greater or equal to 1");
		if (octave < 0 || octave > 9) throw new IllegalArgumentException("Octave must be between 0 and 9");
		
		this.value = value;
		this.pitch = pitch;
		this.octave = octave;
		frequency = getFrequency();
	}
	
	public Note(String str) {
		int i = 0;
		
		// Parse value
		double value = 0;
		try {
			for (i = 0; Character.isDigit(str.charAt(i)); ++i) {
				value = value * 10 + Integer.parseInt(""+str.charAt(i));
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(PARSE_ERROR);
		}
		
		// Parse dot
		if (str.charAt(i) == '.') {
			value *= (3.0/4.0);
			++i;
		}
		this.value = value;
		
		// Parse pitch
		Pitch pitch = null;
		for (Pitch p : Pitch.values()) {
			if (str.substring(i).length() > 1 && p.label.equals(str.substring(i,i+2).toLowerCase())
			|| (str.substring(i).length() > 0 && p.label.equals(str.substring(i,i+1)))) {
				 pitch = p;
			 }
		}
		if (pitch == null) throw new IllegalArgumentException(PARSE_ERROR);
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
		sb.append(pitch);
		if (pitch != Pitch.REST) sb.append(octave);
		return sb.toString();
	}
	
}
