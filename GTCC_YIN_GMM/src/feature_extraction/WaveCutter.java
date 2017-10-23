package feature_extraction;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WaveCutter {

	public static void cutAudio(String sourceFileName, String destinationFileName, int startSecond, int secondsToCopy) {
	    AudioInputStream inputStream = null;
	    AudioInputStream shortenedStream = null;
	    try {
	      File file = new File(sourceFileName);
	      AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	      AudioFormat format = fileFormat.getFormat();
	      inputStream = AudioSystem.getAudioInputStream(file);
	      int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
	      inputStream.skip(startSecond * bytesPerSecond);
	      long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
	      shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
	      File destinationFile = new File(destinationFileName);
	      AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
	    } catch (Exception e) {
	      System.out.println();
	    } finally {
	      if (inputStream != null) try { inputStream.close(); } catch (Exception e) { System.out.println(e); }
	      if (shortenedStream != null) try { shortenedStream.close(); } catch (Exception e) { System.out.println(e); }
	    }
	  }

}
