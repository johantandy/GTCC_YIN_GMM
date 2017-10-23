package learning_algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.jfree.data.function.NormalDistributionFunction2D;

class GMModel{
	KMeans km;
	
	private double[][]_data;
	ArrayList<ArrayList<Integer>> ClusterIndex = new ArrayList<>();
	 
	
	public ArrayList<ArrayList<Integer>> getClusterIndex() {
		return ClusterIndex;
	}

	public GMModel(double[][]data,int cluster,int iter){
		ArrayList<ArrayList<Double>> labels = new ArrayList<>();
		this._data=data;
		
		String fileName = "C:\\Users\\extre\\Downloads\\iris.csv";
		km = new KMeans( fileName, null );
	     km.clustering(cluster, 100, null);
	     int [] label = km.get_label();
		
		//Kmeans Section
		/*km= new KMeans(data, cluster);
		km.clustering(cluster, iter, null);
		int [] label = km.get_label();*/

		//Separate Each data point
		for (int i = 0; i <cluster; i++) {
			ArrayList<Integer> temp = new ArrayList<>();
			for (int j = 0; j < label.length; j++) {
				if (label[j]==i) {
					temp.add(j);
					
				}
				
			}
			ClusterIndex.add(temp);	
		}
		
		
		/*for (int i = 0; i < ClusterIndex.size(); i++) {
			System.out.println("cluster "+i);
			System.out.println("size "+ClusterIndex.get(i).size());
			for (int j = 0; j < ClusterIndex.get(i).size(); j++) {
				System.out.println(ClusterIndex.get(i).get(j));
			}
		}*/
		
		
		
		
		//System.out.println("space");
		//KM.printResults();
		}
	
	public double[][] getMeans(){
		
		return km.get_centroids();
		
	}
	
	
	
	public ArrayList<Integer> getPhi() {
		ArrayList<Integer> numPoint = new ArrayList<>();
		for (int i = 0; i < ClusterIndex.size(); i++) {
			numPoint.add(ClusterIndex.get(i).size());
		}
		return numPoint;
	}
		
	
	public double[][] covariances (double[][]data){
		
		RealMatrix mx = MatrixUtils.createRealMatrix(data);
		RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
		
		return cov.getData();
		
	}
	
	public double[][] getData() {
		return km.get_data();
	}

	public double GaussianFunction(double x_n, double Mu_k , double Sigma_k ){     // return N(x_n|...)
		
		double Prob = 0;
		
		Prob = Math.pow(2*Math.PI*Sigma_k ,-0.5)*Math.exp(-(Math.pow(x_n-Mu_k, 2))/(2*Sigma_k)); 
	    return Prob;
	}
	
	public void gaussianMixtureModel() {
		//gimana ngitung normal distributionnye ? 
		
	
		
		
		/*for (int i = 0; i < covariances.size(); i++) {
			System.out.println(covariances.get(i));
		}*/
	}
	
	public static void main(String[] args) {
		GMModel gm = new GMModel(null, 3, 0);
		gm.gaussianMixtureModel();
		//System.out.println(Arrays.deepToString(this.getMeans()));
		//System.out.println(this.getData().length+" "+this.getData()[0].length);
		ArrayList<ArrayList<Double>> covariances = new ArrayList<>();
		//double[][] covarianss = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
		double[] prob = new double[gm.ClusterIndex.size()];
		for (int i = 0; i < gm.ClusterIndex.size(); i++) {
			double[][] tempdata = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
			double tempprob=0;
			
			//System.out.println(tempdata[0].length +" "+tempdata.length);
			for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
				tempdata[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
			}
			
			tempdata=gm.covariances(tempdata);
			MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(gm.getMeans()[i], tempdata);
			for (int j = 0; j < gm.getData().length; j++) {
				tempprob=tempprob+gm.getPhi().get(i)*mnd.density(gm.getData()[j]); 
			}
			prob[i]=tempprob;
			System.out.println(tempprob);
			
			/*System.out.println(Arrays.deepToString(tempdata));
			for (int j = 0; j < tempdata.length; j++) {
				ArrayList<Double> tempcov = new ArrayList<>();
				for (int j2 = 0; j2 < tempdata[j].length; j2++) {
					tempcov.add(tempdata[j][j2]);
				}
				System.out.println(tempcov.size());
				covariances.add(tempcov);				
			}*/	
		}
		
		
	}

	

 }
