import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.event.EventListenerList;

public class Tune {
	
	public static final int DEFAULT_TEMPO = 240;
	private static final int SAMPLE_RATE = 44100;
	private static final int SAMPLE_SIZE = 2;
	private static double DELTA = 1.0/SAMPLE_RATE;
	private static final int FREQ = 10000;
	
	public final int tempo;
	private final List<Note> notes = new LinkedList<>();
	private final long numSamples;
	
	private Note note = null;
	public Note getNote() { return note; }
	
	private int noteIndex = -1;
	public int getNoteIndex() { return noteIndex; }
	
	public boolean isPlaying() { return noteIndex > -1; }
	
	private boolean shouldStop;
	
	private List<ActionListener> listeners = new LinkedList<>();
	
	public Tune(int tempo, Note[] notes) {
		this.tempo = tempo;
		for (Note note : notes) this.notes.add(note);
		numSamples = numSamples();
	}
	public Tune(Note[] notes) { this(DEFAULT_TEMPO, notes); }
	
	public Tune(int tempo, Note note, Note... notes) {
		this.tempo = tempo;
		this.notes.add(note);
		for (Note n : notes) this.notes.add(n);
		numSamples = numSamples();
	}
	public Tune(Note note, Note... notes) { this(DEFAULT_TEMPO, note, notes); }
	
	public Tune(int tempo, Collection<Note> notes) {
		this.tempo = tempo;
		for (Note note : notes) this.notes.add(note);
		numSamples = numSamples();
	}
	public Tune(Collection<Note> notes) { this(DEFAULT_TEMPO, notes); }
	
	public Tune(int tempo, String notes) {
		this.tempo = tempo;
		notes = notes.trim().replaceAll("\\s+", " ");
		String[] tokens = notes.split(" ");
		for (String token : tokens) this.notes.add(new Note(token));
		numSamples = numSamples();
	}
	
	public void play() {
		for (ActionListener l : listeners) l.actionPerformed(new ActionEvent(this, 0, "Playing"));
		new Thread() {
				
			@Override
			public void run() {
				double t = 0;
		
				AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				SourceDataLine line = null;
		
				try {
					line = (SourceDataLine)AudioSystem.getLine(info);
					line.open(format);  
					line.start();
				} catch (LineUnavailableException e) { e.printStackTrace(); }
		
				ByteBuffer buf = ByteBuffer.allocate(line.getBufferSize());   
		
				long totalSamples = numSamples;
				Iterator<Note> iter = notes.iterator();
				note = null;
				long samples = 0;
				long k = 0;
				
				while (!shouldStop && totalSamples > 0) {
					buf.clear();
					
					int samplesThisPass = line.available()/SAMPLE_SIZE;
					for (int i = 0; i < samplesThisPass; ++i) {
						if (samples <= 0) {
							if (iter.hasNext()) {
								note = iter.next();
								k = 0;
								++noteIndex;
								samples += numSamplesForNote(note);
							}
							else {
								note = null;
								noteIndex = -1;
							}
						}
						
						short s = 0;
						short a = 10000;
						if (k++ < 25) a -= 400 * (25-k);
						if (samples-- < 200) a -= 200 * (200-samples);
						if (a < 0) a = 0;
						if (note != null && note.pitch != Note.Pitch.REST) s = (short)(a*getWave(note.frequency,t));
						
			            buf.putShort(s);
			
			            t += DELTA/2;
			            if (t > 1) t -= 1;
					}
					if (note == null) for (int i = 0; i < 2000; ++i) buf.putShort((short)0);
		
			        line.write(buf.array(), 0, buf.position());            
			        totalSamples -= samplesThisPass;
		
			        while (line.getBufferSize()/2 < line.available()) {
						try { Thread.sleep(1); }
						catch (InterruptedException e) { e.printStackTrace(); }                                             
			        }
				}
		
				if (shouldStop) {
					line.flush();
					shouldStop = false;
				}
				else line.drain();                                         
			    line.close();
			    for (ActionListener l : listeners) l.actionPerformed(new ActionEvent(this, 1, "Stopped"));
			}
		}.start();
	}
	
	public void stop() {
		shouldStop = true;
	}
	
	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}
	
	private long numSamples() {
		long result = 0;
		for (Note note : notes) result += numSamplesForNote(note);
		return result;
	}
	
	private long numSamplesForNote(Note note) {
		return (long)((240.0/(tempo*(note.value/(note.dotted ? 1.5 : 1)))) * SAMPLE_RATE);
	}
	
	private static double getWave(double f, double t) {
		f*=4;
		t %= f;
		double s = (Math.sin(-Math.tan(Math.PI*f*((0.376505*t) % (1/(1.328*f)))-1.3615))+1)/2;
		double m = 2*Math.PI*FREQ*(t % (2.0/f));
		return s * Math.sin(m);
	}
}
