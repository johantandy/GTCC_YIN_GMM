package learning_algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

class GMModel{
	static KMeans km;
	
	ArrayList<ArrayList<Integer>> ClusterIndex = new ArrayList<>();
	 
	
	public ArrayList<ArrayList<Integer>> getClusterIndex() {
		return ClusterIndex;
	}

	public GMModel(double[][]data,int cluster,int iter){

		
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
	
	
	

	
	public double[] getPhiArray(int size) {
		double[] numPoint = new double[ClusterIndex.size()];
		for (int i = 0; i < ClusterIndex.size(); i++) {
			numPoint[i]=(double)(ClusterIndex.get(i).size())/(double)size;
		}
		return numPoint;
	}
		
	
	public double[][] covariances (double[][]data){
		
		RealMatrix mx = MatrixUtils.createRealMatrix(data);
		RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
		
		return cov.getData();
		
	}
	
	public double[][] getCovariaces(double[][]data){
		
		return data;
		
	}
	
	public double[][] getData() {
		
		return km.get_data();
	}
	
	public ArrayList<ArrayList<Double>> getDataAsList() {
		ArrayList<ArrayList<Double>> data = new ArrayList<>();
		for (int i = 0; i < km.get_data().length; i++) {
			ArrayList<Double> tempdata = new ArrayList<>();
			for (int j = 0; j < km.get_data()[i].length; j++) {
				data.add(tempdata);
				}
		}
		
		return data;
	}

	public double GaussianFunction(double x_n, double Mu_k , double Sigma_k ){     // return N(x_n|...)
		
		double Prob = 0;
		
		Prob = Math.pow(2*Math.PI*Sigma_k ,-0.5)*Math.exp(-(Math.pow(x_n-Mu_k, 2))/(2*Sigma_k)); 
	    return Prob;
	}
	
	
	public static double log2(double num)
	{ if(num==0)
		return 0;
	  else 
	    return (Math.log(num)/Math.log(2));
	}
	
	public static void gmmApache(GMModel gm) {
		
		
		double [][]means = gm.getMeans();
		double [] pi = gm.getPhiArray(gm.getData().length);
			//hitung covariance
			double[][][] cov = new double[gm.ClusterIndex.size()][gm.ClusterIndex.get(0).size()][gm.ClusterIndex.size()];
			double[][][] covtemp = new double[gm.ClusterIndex.size()][gm.ClusterIndex.get(0).size()][gm.ClusterIndex.size()];
			
			for (int i = 0; i < gm.ClusterIndex.size(); i++) {
				double[][] tempdata1 = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
				//ArrayList<ArrayList<Double>> cova = new ArrayList<>();
				for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
					tempdata1[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
					//cova.add(gm.getDataAsList().get(gm.ClusterIndex.get(i).get(j)));
				}
				tempdata1=gm.covariances(tempdata1);
				cov[i] = tempdata1;	
				covtemp[i] = tempdata1;
			}
		
		System.out.println("Phi lama = "+Arrays.toString(pi));	
		System.out.println("cov lama = "+Arrays.deepToString(covtemp));	
		
		System.out.println("Means lama = "+Arrays.deepToString(gm.getMeans()));	
		
		MixtureMultivariateNormalDistribution mmmd = new MixtureMultivariateNormalDistribution(pi, means, cov);
		MultivariateNormalMixtureExpectationMaximization mle = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
		mle.fit(mmmd);
		mmmd = mle.getFittedModel();
		
		for (int i = 0; i < mmmd.getComponents().size(); i++) {
			/*System.out.println("cov "+Arrays.deepToString(mmmd.getComponents().get(i).getValue().getCovariances().getData()));
			System.out.println("means "+Arrays.toString(mmmd.getComponents().get(i).getValue().getMeans()));
			System.out.println("weight "+mmmd.getComponents().get(i).getFirst());*/
		cov[i]= mmmd.getComponents().get(i).getValue().getCovariances().getData();
		means[i]=mmmd.getComponents().get(i).getValue().getMeans();
		pi[i]=mmmd.getComponents().get(i).getFirst();
		}
		
		System.out.println(mle.getLogLikelihood());
		System.out.println("Phi lama = "+Arrays.toString(pi));	
		System.out.println("cov lama = "+Arrays.deepToString(cov));	
		
		System.out.println("Means lama = "+Arrays.deepToString(means));	
		/*for (int i = 0; i < km.getLabel().length; i++) {
			System.out.println("index "+i+" cluster "+km.get_label()[i]);
		}*/
		int match =0;
			for (int i = 0; i < gm.getData().length; i++)  {
			//int check = Integer.parseInt(JOptionPane.showInputDialog("Masukin"));
			double ProbOfXns=0;
			double []prob = new double[gm.ClusterIndex.size()]; 
			 for (int j = 0; j < gm.ClusterIndex.size(); j++){
				ProbOfXns=0;
				
					 
					 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
					// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
						
					 ProbOfXns =  pi[j]*mnd.density(gm.getData()[i]);
				
				
				 prob[j]=ProbOfXns;
			}
			
			
			 double set = prob[0];
			 int index =0;
			for (int i1 = 1; i1 < prob.length; i1++) {
				
				if (prob[i1]>set) {
					set=prob[i1];
					index=i1;
				}
				
			}
/*			System.out.println("Cluster "+index+" Prob= "+prob[index]);
			System.out.println("Cluster Kmeans "+km.get_label()[i]);*/
			
			if (index==km.get_label()[i]) {
				match++;
			}
		}
			System.out.println("Match=" +match);
	}
	
	

	
	
	public static void main(String[] args) {
		GMModel gm = new GMModel(null, 3, 0);
		gmmApache(gm);
		//System.out.println(Arrays.deepToString(this.getMeans()));
		//System.out.println(this.getData().length+" "+this.getData()[0].length);
		//ArrayList<ArrayList<ArrayList<Double>>> covariances = new ArrayList<>();
		//double[][] covarianss = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
		
		
		//while(ChangeInLogLikelihood > 0.01) {
			/*double tempprob=0;
			double[][] tempdata = null;
			for (int i = 0; i < gm.ClusterIndex.size(); i++) {
				 tempdata = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
				
				//System.out.println(tempdata[0].length +" "+tempdata.length);
				for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
					tempdata[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
				}
				tempdata=gm.covariances(tempdata);
			}*/
			
			/*while(ChangeInLogLikelihood > 0.01) {
				double LogLikelihood=0, ProbOfXn=0;
				for (int i = 0; i < tempdata.length; i++) {
					ProbOfXn =0;
					for (int j = 0; j < gm.ClusterIndex.size(); j++) {
						MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(gm.getMeans()[i], gm.get);
						ProbOfXn = ProbOfXn + gm.getPhi().get(j)*mnd.density(vals)
					}
				}
			}*/
				
				/*tempdata=gm.covariances(tempdata);
				
				for (int j = 0; j < gm.getData().length; j++) {
					tempprob=tempprob+gm.getPhi().get(i)*mnd.density(gm.getData()[j]); 
				}
				prob[i]=tempprob;
				System.out.println(tempprob);
				
				System.out.println(Arrays.deepToString(tempdata));
				for (int j = 0; j < tempdata.length; j++) {
					ArrayList<Double> tempcov = new ArrayList<>();
					for (int j2 = 0; j2 < tempdata[j].length; j2++) {
						tempcov.add(tempdata[j][j2]);
					}
					System.out.println(tempcov.size());
					covariances.add(tempcov);				
				}	
			}*/
			
			/*for (int i = 0; i < gm.ClusterIndex.size(); i++) {
				double[][] tempdata1 = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
				double tempprob1=0;
				
				//System.out.println(tempdata[0].length +" "+tempdata.length);
				for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
					tempdata1[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
				}
				
				tempdata1=gm.covariances(tempdata1);
				MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(gm.getMeans()[i], tempdata1);
				for (int j = 0; j < gm.getData().length; j++) {
					tempprob1=tempprob1+gm.getPhi().get(i)*mnd.density(gm.getData()[j]); 
				}
				prob[i]=tempprob1;
				System.out.println(tempprob1);
				
				System.out.println(Arrays.deepToString(tempdata1));
				for (int j = 0; j < tempdata1.length; j++) {
					ArrayList<Double> tempcov = new ArrayList<>();
					for (int j2 = 0; j2 < tempdata1[j].length; j2++) {
						tempcov.add(tempdata1[j][j2]);
					}
					System.out.println(tempcov.size());
					covariances.add(tempcov);				
				}	
			}*/
			
			/*double[][][] cov = new double[gm.ClusterIndex.size()][gm.ClusterIndex.get(0).size()][gm.ClusterIndex.size()];
			for (int i = 0; i < gm.ClusterIndex.size(); i++) {
				double[][] tempdata1 = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
				ArrayList<ArrayList<Double>> cova = new ArrayList<>();
				//System.out.println(tempdata1.length);
				//double tempprob1=0;				
				//System.out.println(tempdata[0].length +" "+tempdata.length);
				for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
					tempdata1[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
					cova.add(gm.getDataAsList().get(gm.ClusterIndex.get(i).get(j)));
				}
				//System.out.println(gm.covariances(cova));
				tempdata1=gm.covariances(tempdata1);
				
				cov[i] = tempdata1;
				//System.out.println("tempdatal "+tempdata1[i].length);
				//covariances.add(tempdata);
				MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(gm.getMeans()[i], tempdata1);
				for (int j = 0; j < gm.getData().length; j++) {
					tempprob1=tempprob1+gm.getPhi().get(i)*mnd.density(gm.getData()[j]); 
				}
			
				System.out.println(Arrays.deepToString(tempdata1));	
			
			}*/
			
		//see all size
/*		System.out.println("cluster index "+gm.ClusterIndex.size());*/
		/*for (int j = 0; j < gm.ClusterIndex.size(); j++) {
			System.out.println("cluster index dalem ["+j+"]= "+gm.ClusterIndex.get(j).size());
			for (int j2 = 0; j2 < gm.ClusterIndex.get(j).size(); j2++) {
				System.out.println("cluster index dalem ["+j+"] dalem ["+j2+"]="+gm.ClusterIndex.get(j).get(j2));
			}
		}*/
		System.out.println(gm.getMeans()[0].length);
		double [][]means = gm.getMeans();
		double [] pi = gm.getPhiArray(gm.getData().length);
			//hitung covariance
			double[][][] cov = new double[gm.ClusterIndex.size()][gm.ClusterIndex.get(0).size()][gm.ClusterIndex.size()];
			double[][][] covtemp = new double[gm.ClusterIndex.size()][gm.ClusterIndex.get(0).size()][gm.ClusterIndex.size()];
			
			for (int i = 0; i < gm.ClusterIndex.size(); i++) {
				double[][] tempdata1 = new double[gm.getClusterIndex().get(i).size()][gm.getClusterIndex().size()];
				//ArrayList<ArrayList<Double>> cova = new ArrayList<>();
				for (int j = 0; j < gm.ClusterIndex.get(i).size(); j++) {
					tempdata1[j] = gm.getData()[gm.ClusterIndex.get(i).get(j)];
					//cova.add(gm.getDataAsList().get(gm.ClusterIndex.get(i).get(j)));
				}
				tempdata1=gm.covariances(tempdata1);
				cov[i] = tempdata1;	
				covtemp[i] = tempdata1;
			}
			double [][] dataa = new double [4][4];
			for (int i = 0; i < 4; i++) {
				dataa[i]=gm.getData()[i];
			}
			
			
			//EM ALL SET & PREDICT
			
			
			

			/*
			double TotalLoglikelihood = 0;
			double 	ChangeInLogLikelihood = 1;
			for (int it = 0; it < 100 ; it++)  {
				//mulai itung
				//while(ChangeInLogLikelihood > 0.01){
					double LogLikelihood=0, ProbOfXn=0;
					for (int i = 0; i < gm.getData().length; i++) {
						ProbOfXn=0;
						 for (int j = 0; j < gm.ClusterIndex.size(); j++) {
							 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
							// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
								
							 ProbOfXn = ProbOfXn + pi[j]*mnd.density(gm.getData()[i]);
						
						}
						 LogLikelihood = LogLikelihood + log2(ProbOfXn);
					}
					
					System.out.println(LogLikelihood+" "+ProbOfXn);
					
					
					
					//E-step
					 double[][] Rspb = new double[gm.getData().length][gm.ClusterIndex.size()];          // Tau_nk: n data points and k component mixture
						for(int i=0; i< gm.getData().length; i++){
							ProbOfXn =0;
							for(int j=0; j< gm.ClusterIndex.size(); j++){
								 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
								 Rspb[i][j]	 = ProbOfXn + pi[j]*mnd.density(gm.getData()[i]);
								 ProbOfXn = ProbOfXn + Rspb[i][j]; 
							}
							
							for (int j = 0; j < gm.ClusterIndex.size(); j++) {
								Rspb[i][j] = Rspb[i][j]/ProbOfXn;
								//System.out.println(Rspb[i][j]);
							}
							
						}
						
					System.out.println("Phi lama = "+Arrays.toString(pi));	
					System.out.println("cov lama = "+Arrays.deepToString(covtemp));	
					
					System.out.println("Means lama = "+Arrays.deepToString(gm.getMeans()));	
						
					//M-Step	
						double[] N_k = new double[gm.ClusterIndex.size()];
			            
						for(int k=0; k< gm.getClusterIndex().size(); k++){             // Calculating N_k's
							for(int n=0; n< gm.getData().length; n++){
								N_k[k] = N_k[k] + Rspb[n][k];
							}
						}
						
						// Calculating new Mu_k's
						for(int k=0; k<gm.ClusterIndex.size(); k++){
							for (int i = 0; i < means[k].length; i++) {
								means[k][i]=0;
								for(int n=0; n< gm.getData().length; n++){
									means[k][i] = means[k][i] + Rspb[n][k]*gm.getData()[n][k];	
								}
								means[k][i] = means[k][i]/N_k[k];
							}
							
							
						}
						
						//UPDATE MEANS
						for (int i = 0; i < means.length; i++) {
							for (int j = 0; j < means[0].length; j++) {
								means[i][j]=0;
							}
						
						
						double sumposterior = 0;
						for (int i1 = 0; i1 < gm.getData().length; i1++) {
							double pw= Rspb[i1][i];
							sumposterior+=pw;
							for (int j = 0; j < means[0].length; j++) {
								means[i][j] += (pw*gm.getData()[i1][j]);
							}
						}
						
						double oosumposterior = 1./sumposterior;
						for (int i1 = 0; i1 < means[0].length; i1++) {
							means[i][i1] *= oosumposterior;
						}
						}
						
						//UPDATE COVARIANCES
						for (int i = 0; i < means.length; i++) {
							double[][]covv = cov[i];
							double[] mean = means[i];
							
							for (int j = 0; j < means[0].length; j++) {
								for (int j2 = 0; j2 < mean.length; j2++) {
									covv[j][j2] =0.;
								}
							}
								
								double sumposterior = 0.;
								for (int l = 0; l < gm.getData().length; l++) {
									double[] d = gm.getData()[l];
									double pw = Rspb[l][i];
									sumposterior +=pw;
									for (int k = 0; k < means[0].length; k++) {
										for (int j = 0; j < means[0].length; j++) {
											covv[k][j] +=(pw * (d[k]-mean[k]) * (d[j]-mean[j]));
										}
										
									}
									
								}
								
								double oosumposterior = 1./sumposterior;
								for (int j = 0; j < means[0].length; j++) {
									for (int j2 = 0; j2 < means[0].length; j2++) {
										covv[j][j2] *=oosumposterior;
									}
								}
								Matrix a = new Matrix(covv);
								cov[i]= a.inverse().getArray();
								
								
							}
						
						
						//UPDATE PI 
						for (int i = 0; i < gm.ClusterIndex.size(); i++) {
							pi[i]=N_k[i]/gm.getData().length;
						}
						
						for (int i = 0; i < means.length; i++) {
							double sum =0;
							for (int j = 0; j < gm.getData().length; j++) {
								sum+=Rspb[j][i];
							}
							pi[i]=sum/gm.getData().length;
						}
					
						
						
						System.out.println("cov baru = "+Arrays.deepToString(cov));
						System.out.println("Means baru = "+Arrays.deepToString(means));
						System.out.println("Phi baru = "+Arrays.toString(pi));
						
						double NewLogLikelihood=0, ProbOfX=0;
						
						for (int i = 0; i < gm.getData().length; i++) {
							ProbOfX=0;
							 for (int j = 0; j < gm.ClusterIndex.size(); j++) {
								 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
								// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
									
								 ProbOfX = ProbOfX + pi[j]*mnd.density(gm.getData()[i]);
							
							}
							 NewLogLikelihood = NewLogLikelihood + log2(ProbOfX);
						}

						ChangeInLogLikelihood = NewLogLikelihood - LogLikelihood;
						TotalLoglikelihood = NewLogLikelihood;
						System.out.println("Total LogLikelihood: "+TotalLoglikelihood);
						System.out.println("Change in LogLikelihood: "+ChangeInLogLikelihood);
								

				
			}
			*/
			
			

			double TotalLoglikelihood = 0;
			double 	ChangeInLogLikelihood = 1;
			while(ChangeInLogLikelihood > 0.0001)  {
				//mulai itung
				//while(ChangeInLogLikelihood > 0.01){
					double LogLikelihood=0, ProbOfXn=0;
					for (int i = 0; i < gm.getData().length; i++) {
						ProbOfXn=0;
						 for (int j = 0; j < gm.ClusterIndex.size(); j++) {
							 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
							// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
								
							 ProbOfXn = ProbOfXn + pi[j]*mnd.density(gm.getData()[i]);
						
						}
						 LogLikelihood = LogLikelihood + log2(ProbOfXn);
					}
					
					System.out.println(LogLikelihood+" "+ProbOfXn);
					
					
					
					//E-step
					 double[][] Rspb = new double[gm.getData().length][gm.ClusterIndex.size()];          // Tau_nk: n data points and k component mixture
						for(int i=0; i< gm.getData().length; i++){
							ProbOfXn =0;
							for(int j=0; j< gm.ClusterIndex.size(); j++){
								 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
								 Rspb[i][j]	 = pi[j]*mnd.density(gm.getData()[i]);
								 ProbOfXn = ProbOfXn + Rspb[i][j]; 
							}
							
							for (int j = 0; j < gm.ClusterIndex.size(); j++) {
								Rspb[i][j] = Rspb[i][j]/ProbOfXn;
								//System.out.println(Rspb[i][j]);
							}
							
						}
						
					System.out.println("Phi lama = "+Arrays.toString(pi));	
					System.out.println("cov lama = "+Arrays.deepToString(covtemp));	
					
					System.out.println("Means lama = "+Arrays.deepToString(gm.getMeans()));	
						
					//M-Step	
						double[] N_k = new double[gm.ClusterIndex.size()];
			            
						for(int k=0; k< gm.getClusterIndex().size(); k++){             // Calculating N_k's
							for(int n=0; n< gm.getData().length; n++){
								N_k[k] = N_k[k] + Rspb[n][k];
							}
						}
						
						// Calculating new Mu_k's
						for(int k=0; k<gm.ClusterIndex.size(); k++){
							for (int i = 0; i < means[k].length; i++) {
								means[k][i]=0;
								for(int n=0; n< gm.getData().length; n++){
									means[k][i] = means[k][i] + Rspb[n][k]*gm.getData()[n][k];	
								}
								means[k][i] = means[k][i]/N_k[k];
							}
							
							
						}
						
						//UPDATE MEANS
						for (int i = 0; i < means.length; i++) {
							for (int j = 0; j < means[0].length; j++) {
								means[i][j]=0;
							}
						
						
						double sumposterior = 0;
						for (int i1 = 0; i1 < gm.getData().length; i1++) {
							double pw= Rspb[i1][i];
							sumposterior+=pw;
							for (int j = 0; j < means[0].length; j++) {
								means[i][j] += (pw*gm.getData()[i1][j]);
							}
						}
						
						double oosumposterior = 1./sumposterior;
						for (int i1 = 0; i1 < means[0].length; i1++) {
							means[i][i1] *= oosumposterior;
						}
						}
						
						//UPDATE COVARIANCES
						for (int i = 0; i < means.length; i++) {
							double[][]covv = cov[i];
							double[] mean = means[i];
							
							for (int j = 0; j < means[0].length; j++) {
								for (int j2 = 0; j2 < means[0].length; j2++) {
									covv[j][j2] =0.;
								}
							}
								
								double sumposterior = 0.;
								for (int l = 0; l < gm.getData().length; l++) {
									double[] d = gm.getData()[l];
									double pw = Rspb[l][i];
									sumposterior +=pw;
									for (int k = 0; k < means[0].length; k++) {
										for (int j = 0; j < means[0].length; j++) {
											covv[k][j] +=(pw * (d[k]-mean[k]) * (d[j]-mean[j]));
										}
										
									}
									
								}
								
								double oosumposterior = 1./sumposterior;
								for (int j = 0; j < means[0].length; j++) {
									for (int j2 = 0; j2 < means[0].length; j2++) {
										covv[j][j2] *=oosumposterior;
									}
								}

								cov[i]=covv;
								
								
							}
						
						
						//UPDATE PI 
						/*for (int i = 0; i < gm.ClusterIndex.size(); i++) {
							pi[i]=N_k[i]/gm.getData().length;
						}*/
						
						for (int i = 0; i < means.length; i++) {
							double sum =0;
							for (int j = 0; j < gm.getData().length; j++) {
								sum+=Rspb[j][i];
							}
							pi[i]=sum/gm.getData().length;
						}
					
						
						
						System.out.println("cov baru = "+Arrays.deepToString(cov));
						System.out.println("Means baru = "+Arrays.deepToString(means));
						System.out.println("Phi baru = "+Arrays.toString(pi));
						
						double NewLogLikelihood=0, ProbOfX=0;
						
						for (int i = 0; i < gm.getData().length; i++) {
							ProbOfX=0;
							 for (int j = 0; j < gm.ClusterIndex.size(); j++) {
								 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
								// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
									
								 ProbOfX = ProbOfX + pi[j]*mnd.density(gm.getData()[i]);
							
							}
							 NewLogLikelihood = NewLogLikelihood + log2(ProbOfX);
						}

						ChangeInLogLikelihood = NewLogLikelihood - LogLikelihood;
						TotalLoglikelihood = NewLogLikelihood;
						System.out.println("Total LogLikelihood: "+TotalLoglikelihood);
						System.out.println("Change in LogLikelihood: "+ChangeInLogLikelihood);
								

				
			}
			
			/*for (int i = 0; i < km.getLabel().length; i++) {
				System.out.println("index "+i+" cluster "+km.get_label()[i]);
			}*/
			int match =0;
				for (int i = 0; i < gm.getData().length; i++)  {
				//int check = Integer.parseInt(JOptionPane.showInputDialog("Masukin"));
				double ProbOfXns=0;
				double []prob = new double[gm.ClusterIndex.size()]; 
				 for (int j = 0; j < gm.ClusterIndex.size(); j++){
					ProbOfXns=0;
					
						 
						 MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means[j], cov[j]);
						// MultivariateNormalMixtureExpectationMaximization s = new MultivariateNormalMixtureExpectationMaximization(gm.getData());
							
						 ProbOfXns = ProbOfXns + pi[j]*mnd.density(gm.getData()[i]);
					
					
					 prob[j]=ProbOfXns;
				}
				
				
				 double set = prob[0];
				 int index =0;
				for (int i1 = 1; i1 < prob.length; i1++) {
					
					if (prob[i1]>set) {
						set=prob[i1];
						index=i1;
					}
					
				}
				/*System.out.println("Cluster "+index+" Prob= "+prob[index]);
				System.out.println("Cluster Kmeans "+km.get_label()[i]);*/
				
				if (index==km.get_label()[i]) {
					match++;
				}
			}
				System.out.println("Match normal=" +match);
	}
	
			
			
			
		}	
		
	//}

	


