package test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import feature_extraction.GTCC;
import feature_extraction.StdAudio;
import feature_extraction.WaveCutter;
import util.Database;
import yin.Yin;

public class Main {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		// TODO Auto-generated method stub
		String path = "C:\\Users\\extre\\Desktop\\heart audio\\Atraining_normal\\Atraining_normal";
		File dir = new File(path);
		  File[] directoryListing = dir.listFiles();
		  double [] duration = new double[directoryListing.length];
		  int iter =0;
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	//System.out.println(child.getAbsoluteFile());
/*		    	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(child);
		    	AudioFormat format = audioInputStream.getFormat();
		    	long frames = audioInputStream.getFrameLength();
		    	duration[iter] = (frames+0.0) / format.getFrameRate(); 
		    	iter=iter+1;*/
		    	
		    	GTCC gtcc = new GTCC();
		    	double[] data = StdAudio.read(child.getAbsolutePath());
		    	double[] feature = gtcc.GetFeatureVector(data, 0.9, 4000, 16000);
		    	//System.out.println(Arrays.toString(feature));
		    	//Database db = new Database();
		    	//db.insertGtcc("MURMUR",feature);
		    	
		    	
		    	ArrayList<Float> pitch1 = new ArrayList<Float>();
				ArrayList<Float> timesuji = new ArrayList<Float>();
				Yin yin = new Yin(16000);
				try {
					System.out.println(child.getAbsolutePath());
					yin.main(child.getAbsolutePath());
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pitch1=yin.getPitchs();
				timesuji=yin.getTimes();
				System.out.println(pitch1.size());
				System.out.println("MULAI");
				for (int i = 0; i < pitch1.size(); i++) {
					if (pitch1.get(i)!=-1.0) {

						System.out.println(timesuji.get(i)+"  "+pitch1.get(i));	
					}
				}
				System.out.println("selesai");
		    	
		    }
		  } else {
		    
		  }
		  
		  //wc.cutAudio(sourceFileName, destinationFileName, startSecond, secondsToCopy);
	}

}
