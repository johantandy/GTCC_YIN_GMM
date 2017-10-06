package feature_extraction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.management.monitor.GaugeMonitorMBean;
import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import plot.XyChart;

public class GTCC {
	private static final double MAX_16_BIT = Short.MAX_VALUE;
	FFT fft = new FFT();
	private static int dataPerFrame;
	static ArrayList<Double> gain;
	//DC REMOVAL - 1st Step GTCC
	public double[] DCRemoval (double[] data)
	{
		double[] result = new double[data.length];
		double avarage=0;
		for (int i=0;i<data.length;i++)
		{
			avarage = avarage + data[i];
		}
		avarage = avarage/data.length;
		//System.out.println(avarage);
		for (int i=0;i<data.length;i++)
		{
			result[i] = data[i]-avarage;
			//System.out.println("y["+i+"]="+data[i]+"-("+avarage+")="+result[i]);
			//System.out.println(result[i]);
		}
		
		return result;
	}
	
	//PREEMPHASIZE 2nd Step GTCC
	public double[] PreEmphasize (double[] data, double alpha)
	{
		double[] result = new double[data.length];
		result[0] = data[0];
		for (int i=1;i<data.length;i++)
		{
			result[i] = data[i] - alpha*data[i-1];
			//System.out.println(result[i]);
		}
		return result;
	}
	
	
	
	//FRAME BLOCKING 3nd Step GTCC
	public static ArrayList<double[]> FrameBlocking (double[] data, int frameSize, int overlap)
	{
		int length = data.length;
		ArrayList<double[]> result = new ArrayList<double[]>();
		int index = 0;
		int bound = length-overlap;
		int iterator =0;
		while (index<bound)
		{
			
			double[] frameData = new double[frameSize];
			for (int i=0;i<frameSize;i++)
			{
				int frameNumber = index+i;
				if (frameNumber>=length)
				{
					frameData[i]=0;
				}
				else
				{
					frameData[i] = data[frameNumber];
				}
			}
	
			index = index + overlap;
			//System.out.println(frameData[1]);
			result.add(frameData);
			iterator+=1;
		}
		
		/*for (int i = 0; i < result.size(); i++) {
			//System.out.println("split");
			for (int j = 0; j < result.get(i).length; j++) {
				System.out.println(result.get(i)[j]);
			}
			
		}*/
		
	
		
		return result;
	}
	
	
	//WINDOWING WITH HAMMING WINDOW 4nd Step GTCC
	public double[] Windowing (double[] data, int frameSize)
	{
		int length = data.length;
		double[] result = new double[length];
		double divider = frameSize-1;
		double pi2 = 2*Math.PI;
		for (int i=0;i<length;i++)
		{ //System.out.println(divider);
		//	System.out.println((0.54-0.46*Math.cos(pi2*i/divider)));
			result[i] = data[i] * (0.54-0.46*Math.cos(pi2*i/divider));
		}
		return result;
	}
	
	//GAMMATONE FILTER BANK 5nd Step GTCC
	public static ArrayList<Double> GammatoneFilterBank(ArrayList<Double> source,double fs) {
		int low_freq=100;
		int high_freq=44100/4;
		int numfbank = 64;
		double ear_q = 9.26449; //# Glasberg and Moore Parameters
		double min_bw = 24.7;
		double T = 1/fs;
		ArrayList<Double> erb_point = new ArrayList<>();
		ArrayList<Double> erb = new ArrayList<>();
		ArrayList<Double> b = new ArrayList<>();
		ArrayList<Double> arg = new ArrayList<>();
		ArrayList<Complex> vec = new ArrayList<>();
		ArrayList<Double> b1 = new ArrayList<>();
		ArrayList<Double> b2 = new ArrayList<>();
		ArrayList<Double> k11 = new ArrayList<>();
		ArrayList<Double> k12 = new ArrayList<>();
		ArrayList<Double> k13 = new ArrayList<>();
		ArrayList<Double> k14 = new ArrayList<>();
		ArrayList<Double> A11 = new ArrayList<>();
		ArrayList<Double> A12 = new ArrayList<>();
		ArrayList<Double> A13 = new ArrayList<>();
		ArrayList<Double> A14 = new ArrayList<>();
		ArrayList<Double> common = new ArrayList<>();
		ArrayList<Complex> gain_arg_temp = new ArrayList<>();
		ArrayList<Complex> gain_arg_temp1 = new ArrayList<>();	
		ArrayList<Complex> gain_arg = new ArrayList<>();
		ArrayList<Complex> c31 = new ArrayList<>();
		ArrayList<Complex> c32 = new ArrayList<>();
		ArrayList<Complex> c33 = new ArrayList<>();
		ArrayList<Complex> c34 = new ArrayList<>();
		ArrayList<Complex> c35 = new ArrayList<>();
		ArrayList<Complex> c36 = new ArrayList<>();
		ArrayList<Complex> c37 = new ArrayList<>();
		ArrayList<Double> gtResponse = new ArrayList<>();
		gain = new ArrayList<>();
		ArrayList<Integer> allfilts = new ArrayList<>();
		ArrayList<ArrayList<Double>> fcoefs = new ArrayList<>();
		
		Complex cdua = new Complex(0, 2);
		Complex csatu = new Complex(0, 1);
		double A0 = T;
		double A2 = 0;
		double B0 = 1;
		double rt_pos = Math.sqrt(3 +Math.pow( 2, 1.5));
		//System.out.println(rt_pos);
		double rt_neg = Math.sqrt(3 - Math.pow(2, 1.5));

		//System.out.println(-ear_q*min_bw+Math.exp((1/numfbank)*(-Math.log(high_freq+ear_q*min_bw)+Math.log(low_freq+ear_q*min_bw)))*(high_freq+ear_q*min_bw));
		
		
		double width =1.0;
		int order = 1;
		for (int i = 1; i <=numfbank; i++) {
			erb_point.add(-ear_q*min_bw+Math.exp(((double)i/numfbank)*(-Math.log(high_freq+ear_q*min_bw)+Math.log(low_freq+ear_q*min_bw)))*(high_freq+ear_q*min_bw));
			//System.out.println(erb_point.get(i-1));
			//System.out.println((double)i/numfbank);
			erb.add(Math.pow(width*(Math.pow((erb_point.get(i-1)/ear_q), order)+Math.pow(min_bw, 1)), 1/order));
			//System.out.println(erb.get(i-1));
			b.add(1.019*2*Math.PI*erb.get(i-1));
			//System.out.println(b.get(i-1));
			//arg.add(2*T*Math.exp(-2*Math.PI*b.get(i)*T));
			arg.add((2*erb_point.get(i-1)*Math.PI*T));			
			//System.out.println(arg.get(i-1));
			Complex c1 = new Complex(arg.get(i-1), 0);
			vec.add(cdua.times(c1).exp());
			//System.out.println(vec.get(i-1));
			b1.add(-2*Math.cos(arg.get(i-1))/Math.exp(b.get(i-1)*T));
			//System.out.println(b1.get(i-1));
			b2.add(Math.exp(-2*b.get(i-1)*T));
			//System.out.println(b2.get(i-1));
			common.add(-T *Math.exp(-(b.get(i-1)*T)));
			//System.out.println(common.get(i-1));
			
			k11.add(Math.cos(arg.get(i-1))+rt_pos*Math.sin(arg.get(i-1)));
			//System.out.println(k11.get(i-1));
			k12.add(Math.cos(arg.get(i-1))-rt_pos*Math.sin(arg.get(i-1)));
			//System.out.println(k12.get(i-1));
			k13.add(Math.cos(arg.get(i-1))+rt_neg*Math.sin(arg.get(i-1)));
			//System.out.println(k13.get(i-1));
			k14.add(Math.cos(arg.get(i-1))-rt_neg*Math.sin(arg.get(i-1)));
			//System.out.println(k14.get(i-1));
			
			A11.add(common.get(i-1)*k11.get(i-1));
			A12.add(common.get(i-1)*k12.get(i-1));
			A13.add(common.get(i-1)*k13.get(i-1));
			A14.add(common.get(i-1)*k14.get(i-1));
			
			Complex c = new Complex(arg.get(i-1), 0);
			Complex c10 = new Complex(b.get(i-1)*T, 0);
			
			gain_arg_temp.add(c);
			gain_arg_temp1.add(c10);
			
			gain_arg.add(csatu.times(gain_arg_temp.get(i-1)).minus(gain_arg_temp1.get(i-1)).exp());
			//System.out.println(gain_arg.get(i-1));
			Complex c3 = new Complex(k11.get(i-1), 0);
			Complex c4 = new Complex(k12.get(i-1), 0);
			Complex c5 = new Complex(k13.get(i-1), 0);
			Complex c6 = new Complex(k14.get(i-1), 0);
			Complex c7 = new Complex(T*Math.exp(b.get(i-1)*T), 0);
			Complex c8 = new Complex(-1/Math.exp(b.get(i-1)*T)+1, 0);
			Complex c9 = new Complex(1-Math.exp(b.get(i-1)*T), 0);
			
			c31.add(c3);
			c32.add(c4);
			c33.add(c5);
			c34.add(c6);
			c35.add(c7);
			c36.add(c8);
			c37.add(c9);
			
			
			gain.add(((vec.get(i-1).minus(gain_arg.get(i-1).times(c31.get(i-1)))).times((
					vec.get(i-1).minus(gain_arg.get(i-1).times(c32.get(i-1))))).times((
					vec.get(i-1).minus(gain_arg.get(i-1).times(c33.get(i-1))))).times((
					vec.get(i-1).minus(gain_arg.get(i-1).times(c34.get(i-1)))))).times((c35.get(i-1)).
							divides((c36.get(i-1).plus((vec.get(i-1).times(c37.get(i-1)))))).power(4)).abs());
			
			
			
			//System.out.println(gain.get(i-1));
			

			//System.out.println(gain.size());
			
			allfilts.add( Collections.frequency(erb_point, erb_point.get(i-1)));
			
			
		ArrayList<Double>fcoefstemp = new ArrayList<>();

			
			fcoefstemp.add(A0*allfilts.get(i-1));
			fcoefstemp.add(A11.get(i-1));
			fcoefstemp.add(A12.get(i-1));
			fcoefstemp.add(A13.get(i-1));
			fcoefstemp.add(A14.get(i-1));
			fcoefstemp.add(A2*allfilts.get(i-1));
			fcoefstemp.add(B0*allfilts.get(i-1));
			fcoefstemp.add(b1.get(i-1));
			fcoefstemp.add(b2.get(i-1));
			fcoefstemp.add(gain.get(i-1));
			
			
			fcoefs.add(fcoefstemp);
			
			
			 
		}
		//counting
		//System.out.println(gain.size());
		double sumOfX = 0;
		for (int j = 0; j < 1; j++) {
			//System.out.println(source.get(j));
			for (int j2 = 0; j2 < gain.size(); j2++) {
				
				
				//System.out.println(source.size());
				sumOfX+=(source.get(j)*gain.get(j2));
			}
			//System.out.println(sumOfX);
			gtResponse.add(sumOfX);
			sumOfX = 0;
			//System.out.println(gtResponse.size());
			//System.out.println(gtResponse);
			//System.out.println("space");
		}
		
		for (int j = 0; j < fcoefs.size(); j++) {
			for (int j2 = 0; j2 < fcoefs.get(0).size(); j2++) {
				System.out.println(fcoefs.get(j).get(j2));
				
			}
			System.out.println("space");
			
		}
		
		//System.out.println(erb_point.get(63));
		
		return gain;
		
	}
	
	public static void EqualLoudnessCurve(ArrayList<Double> centre_frequecy) {
		
		
		
	}
	
	
	
	
	
	
	public double[][] GetFeatureVector(double[] data, double alpha, int size, int overlap){
		double[] dcRemoval = DCRemoval(data);
		double[] preEmphasized = PreEmphasize(dcRemoval,alpha);
		//ArrayList<double[]> frame = FrameBlocking(preEmphasized,frameSize);
		ArrayList<double[]> frame = FrameBlocking(preEmphasized,size,overlap);
		
		ArrayList<double[]> result = new ArrayList<double[]>();
		double[][] gtccFeature = new double[frame.size()][];
		double[][] framedSignal = new double[frame.size()][];
		ArrayList<Double> GTFIlterBank = new ArrayList<>();
		System.out.println(frame.size());
		for (int i = 0; i < frame.size(); i++) {
			
			double[] window = Windowing (frame.get(i),size);
			framedSignal[i] = window;
			
			/*for (int j = 0; j < window.length; j++) {
				System.out.println(window[j]);
			}*/
			Complex[] signal = new Complex[window.length];
			//System.out.println(window.length);
			for (int x=0;x<window.length;x++)
			{
				Complex c = new Complex (window[x],0);
				signal[x] = c;
			}
			
			Complex[] hasil = FFT.fft1D(signal);
			/*for (int j = 0; j < hasil.length; j++) {
				System.out.println(hasil[j]);
			}
			
			System.out.println("split");*/
			
			ArrayList<Double> frequencyValue = new ArrayList<>();
			
			for (int k = 0; k < window.length; k++) {
				double nilaiReal = hasil[k].re();
				double nilaiImag = hasil[k].im();
				
				frequencyValue.add(Math.sqrt(nilaiReal * nilaiReal + nilaiImag * nilaiImag));
				//System.out.println(frequencyValue.si);
				//System.out.println(frequencyValue.get(k));
			}
			
			
			
			
			
			GammatoneFilterBank(frequencyValue, 16000);
			
			
		}
		return null;
	}
	
	public static void main(String[] args) {
	//GammatoneFilterBank(null, 16000);
		String Sound1 = "C:\\Users\\extre\\Desktop\\heart audio\\Atraining_murmur\\Atraining_murmur\\201101051104.wav";
		double[] data = StdAudio.read(Sound1);
		double [] datasample=new double[12];
		for (int i = 0; i <=11; i++) {
			datasample[i]=data[i];
			//System.out.println(datasample[i]);
		}
		System.out.println("oke");
		GTCC gtcc = new GTCC();
		gtcc.GetFeatureVector(datasample, 0.9, 4, 2);
		
		/*for (int i = 2; i <=64; i++) {
			System.out.println(i);
		}*/
		/*XYSeries xys = new XYSeries("plot audio");
		for (int i = 0; i < data.length; i++) {
			xys.add(i, data[i]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(xys);
		XyChart.dataSet=dataset;
		XyChart chart = new XyChart("plot audio", "plot audio");
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chart.setVisible(true);
		GTCC gtcc = new GTCC();
		gtcc.GetFeatureVector(data, 0.9, 4000, 16000);*/
		
		/*Complex c = new Complex(0, 2);
		Complex c1 = new Complex(1.1, 0);
		
		System.out.println((c.times(c1).exp()));*/
		
		//ArrayList<double[]> hasilframeblocking = FrameBlocking(data, 4000,1600);
		
		
		
	}
	
	
	
	
	
	

}
