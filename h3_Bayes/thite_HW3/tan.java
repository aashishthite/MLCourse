import java.util.*;
class TreeNode
{
	int attrNum;
	int parentAttr;
	List<TreeNode> children;
	public TreeNode(int num, int parent)
	{
		children = new ArrayList<TreeNode>();
		attrNum = num;
		parentAttr = parent;
	}
	public void addChild(TreeNode node)
	{
		children.add(node);
	}
}
public class tan
{
	Map<String,Map<String,Integer>> mapPos,mapNeg;
	int countPos,countNeg;
	double[][] treeFinal;
	DataSet trainingSet;
	CPT[] allCPTs;
	int[] parent;
	TreeNode root;
	public void train(DataSet train)
	{
		treeFinal = new double[train.attributes.size()][train.attributes.size()];
		trainingSet = train;
		countPos=0;
		countNeg=0;
		mapPos = new HashMap<String, Map<String,Integer>>();
		mapNeg = new HashMap<String, Map<String,Integer>>();
		//System.out.println(train.labels.size());
		for(String attr: train.attributes)
		{
			Map<String,Integer> tempMapPos = new HashMap<String,Integer>();
			Map<String,Integer> tempMapNeg = new HashMap<String,Integer>();
			List<String> aValues = train.attributeValues.get(attr);
			//System.out.println(attr + " " + "class");
			for(String attrVal : aValues)
			{
			//	System.out.println(attr + " " +attrVal);
				tempMapPos.put(attrVal,0);
				tempMapNeg.put(attrVal,0);
			}
			mapPos.put(attr,tempMapPos);
			mapNeg.put(attr,tempMapNeg);
		}
		for(Instance inst : train.instances)
		{
			
			if(inst.label.equals(train.labels.get(0)))
			{
				countPos++;
				int i=0; 
				for(String attr : train.attributes)
				{
					Map<String,Integer> tempMap = mapPos.get(attr);
					tempMap.put(inst.attributes.get(i),tempMap.get(inst.attributes.get(i))+1);
					mapPos.put(attr,tempMap);
					i++;
				}
			}
			else
			{
				countNeg++;		
				int i=0; 
				for(String attr : train.attributes)
				{
					Map<String,Integer> tempMap = mapNeg.get(attr);
					tempMap.put(inst.attributes.get(i),tempMap.get(inst.attributes.get(i))+1);
					mapNeg.put(attr,tempMap);
					i++;
				}
			}	
		}
		constructTree();
		parent = new int[trainingSet.attributes.size()];
		parent[0] = -1;
		root = new TreeNode(0,-1);
		Queue<TreeNode> q = new LinkedList<TreeNode>();
		q.offer(root);
		while(!q.isEmpty())
		{
			TreeNode temp = q.poll();
			for(int ii = 1; ii < treeFinal.length; ii++)
			{
				if(treeFinal[temp.attrNum][ii] > 0)
				{
					parent[ii] = temp.attrNum;
					TreeNode child = (new TreeNode(ii,temp.attrNum));
					temp.addChild(child);
					q.offer(child);
				}
			}
		}
		allCPTs = new CPT[trainingSet.attributes.size()];
		for(int ii = 1; ii < trainingSet.attributes.size(); ii++)
		{
			String attr1 = trainingSet.attributes.get(ii);
			String attr2 = trainingSet.attributes.get(parent[ii]);
			allCPTs[ii] = new CPT(attr1, attr2);

			List<String> attr1Vals = trainingSet.attributeValues.get(attr1);
			List<String> attr2Vals = trainingSet.attributeValues.get(attr2);
			
			int ind1 = trainingSet.attributes.indexOf(attr1);
			int ind2 = trainingSet.attributes.indexOf(attr2);
			for(int jj = 0; jj < attr1Vals.size(); jj++)
				for(int kk = 0; kk < attr2Vals.size(); kk++)
				{
					String attr1Val = attr1Vals.get(jj);
					String attr2Val = attr2Vals.get(kk);
					int pos = 0, neg = 0, totPos=0, totNeg=0;
					for(Instance inst : trainingSet.instances)
					{
						if(inst.attributes.get(ind2).equals(attr2Val))
						{
							if(inst.label.equals(trainingSet.labels.get(0)))
							{
								totPos++;
								if(inst.attributes.get(ind1).equals(attr1Val))
									pos++;
							}
							else
							{
								totNeg++;
								if(inst.attributes.get(ind1).equals(attr1Val))
									neg++;
							}


						}

					}
					allCPTs[ii].cpt[jj][kk][0]=(double)(pos+1)/(double)(totPos + attr1Vals.size());//TODO: check laplace estimate
					allCPTs[ii].cpt[jj][kk][1]=(double)(neg+1)/(double)(totNeg + attr1Vals.size());//TODO: check laplace estimates
				}


		}
		
		/*
		int cc = 12;
		for(int ii = 0; ii < trainingSet.attributeValues.get(trainingSet.attributes.get(cc)).size(); ii++)
		{
			String attr2 = trainingSet.attributes.get(parent[cc]);
			List<String> attr2Vals = trainingSet.attributeValues.get(attr2);
			for(int jj = 0; jj < attr2Vals.size(); jj++)
			{
				System.out.println(allCPTs[cc].cpt[ii][jj][0] + " " + allCPTs[cc].cpt[ii][jj][1]);
			}
		}*/
		//for(int ii = 0; ii < trainingSet.attributes.size();ii++)
	//		System.out.println(cc+ " " +parent[cc]);
		//q.

		//System.out.println("prob of 18 = 1 " + priorP(trainingSet.labels.get(1)));
	}
	public void constructTree()
	{
		double[][] graph = new double[trainingSet.attributes.size()][trainingSet.attributes.size()];	
		for(int ii = 0; ii < graph.length; ii++)
			for(int jj = ii; jj < graph[ii].length; jj++)
			{
				graph[ii][jj] = graph[jj][ii] = condMutualInfo(trainingSet.attributes.get(ii),trainingSet.attributes.get(jj));
			}
		
		int[] visited = new int [trainingSet.attributes.size()];
		double[] maxDist = new double[trainingSet.attributes.size()];
		int current=0;
		//maxDist[current] = 0;
		visited[current] = 1;
		int total = 1;
		treeFinal[0][0] = graph[0][0];
		//double maxCost = -1;
		while(total < trainingSet.attributes.size())
		{
			double maxCost = -1;
			int maxRow = 0,maxCol =0;
			for(int ii = 0; ii < graph.length; ii++)
			{
				if(visited[ii] != 0)
					for(int jj =0; jj <graph[ii].length; jj ++)
					{
						if(visited[jj] == 0)
							if(graph[ii][jj] > maxCost)
							{
								maxCost = graph[ii][jj];
								maxRow = ii; maxCol = jj;
							}
					}
			}
			visited[maxCol] = 1;
			treeFinal[maxRow][maxCol] = graph[maxRow][maxCol];
			total++;

		}
/*
		for(int ii = 0; ii < graph.length; ii++){
			for(int jj = 0; jj < graph[ii].length; jj++)
				System.out.print(treeFinal[ii][jj] + " ");
			System.out.print("\n");
		}
*/		
		//System.out.println(visited[0]);

	}
	public double condMutualInfo(String attr1, String attr2)
	{
		if(attr1.equals(attr2))
			return -1.0;

		List<String> attr1Vals = trainingSet.attributeValues.get(attr1);
		List<String> attr2Vals = trainingSet.attributeValues.get(attr2);
		int ind1 = trainingSet.attributes.indexOf(attr1);
		int ind2 = trainingSet.attributes.indexOf(attr2);
		double CMI = 0;
		for(int ii = 0; ii< attr1Vals.size(); ii++)
			for(int jj = 0; jj < attr2Vals.size(); jj++)
			{
				String attr1Val = attr1Vals.get(ii);
				String attr2Val = attr2Vals.get(jj);
				int pos =0, neg =0;
				for(Instance inst: trainingSet.instances)
				{
					if(inst.attributes.get(ind1).equals(attr1Val) && inst.attributes.get(ind2).equals(attr2Val))
					{
						if(inst.label.equals(trainingSet.labels.get(0))) pos++;
						else neg++;
					}
				}
				double jointPPos = (double)(pos+1)/(double)(trainingSet.instances.size()+attr1Vals.size()*attr2Vals.size()*2);//TODO: check laplace estimates
				double jointPNeg = (double)(neg+1)/(double)(trainingSet.instances.size()+attr1Vals.size()*attr2Vals.size()*2);//TODO: check laplace estimates
				double condPPos = (jointPPos)/(priorP(trainingSet.labels.get(0)));
				double condPNeg = (jointPNeg)/(priorP(trainingSet.labels.get(1)));
				CMI += jointPPos * Math.log(condPPos/likelihood(attr1,attr1Val,trainingSet.labels.get(0)) /likelihood(attr2,attr2Val,trainingSet.labels.get(0)))/Math.log(2);
				CMI += jointPNeg * Math.log(condPNeg/likelihood(attr1,attr1Val,trainingSet.labels.get(1)) /likelihood(attr2,attr2Val,trainingSet.labels.get(1)))/Math.log(2);
			}
		return CMI;

	}
	public double priorP(String label)
	{
		if(label.equals(trainingSet.labels.get(0)))
			return (double) (countPos+1)/(double)(countPos + countNeg + 2);
		else
			return (double) (countNeg+1)/(double)(countPos + countNeg + 2);
	}
	public double likelihood(String attribute, String attrValue, String label)
	{
		double temp;
		if(label.equals(trainingSet.labels.get(0)))
			temp = (double)(mapPos.get(attribute).get(attrValue)+1)/(double) (countPos + trainingSet.attributeValues.get(attribute).size());
		else
			temp = (double)(mapNeg.get(attribute).get(attrValue)+1)/(double) (countNeg + trainingSet.attributeValues.get(attribute).size());
		return temp;
	}
	public void printStruct()
	{
		System.out.println(trainingSet.attributes.get(0) + " class");
		for(int ii = 1; ii < trainingSet.attributes.size(); ii++)
			System.out.println(trainingSet.attributes.get(ii) + " " + trainingSet.attributes.get(parent[ii]) + " class");	
	}
	public ClassifyResult classify(Instance inst)
	{
		ClassifyResult cr = new ClassifyResult();
		double logProbPos = Math.log(this.priorP(trainingSet.labels.get(0)));
		double logProbNeg = Math.log(this.priorP(trainingSet.labels.get(1)));
		int i = 0;
		for(String attr: inst.attributes)
		{
			if(i==0)
			{
				logProbPos += Math.log(this.likelihood(trainingSet.attributes.get(i),attr,trainingSet.labels.get(0)));
				logProbNeg += Math.log(this.likelihood(trainingSet.attributes.get(i),attr,trainingSet.labels.get(1))); 
			}
			else
			{
				String attr1 = trainingSet.attributes.get(i);
				String attr2 = trainingSet.attributes.get(parent[i]);
				int ind1 = trainingSet.attributeValues.get(attr1).indexOf(attr);
				int ind2 = trainingSet.attributeValues.get(attr2).indexOf(inst.attributes.get(parent[i]));
				logProbPos += Math.log(allCPTs[i].cpt[ind1][ind2][0]);
				logProbNeg += Math.log(allCPTs[i].cpt[ind1][ind2][1]);

			}
			i++;
		}
		if(logProbPos >= logProbNeg)
		{
			cr.label = trainingSet.labels.get(0);
			cr.posteriorProb = Math.exp(logProbPos)/(Math.exp(logProbPos)+Math.exp(logProbNeg));
		}
		else
		{
			cr.label = trainingSet.labels.get(1);
			cr.posteriorProb =  Math.exp(logProbNeg)/(Math.exp(logProbPos)+Math.exp(logProbNeg));
		}
		return cr;
	}
	class CPT{
		double[][][] cpt;
		String attr1, attr2;
		public CPT( String a1, String a2)
		{
			attr1 = a1;
			attr2 = a2;
			cpt = new double[trainingSet.attributeValues.get(attr1).size()][trainingSet.attributeValues.get(attr2).size()][2];
		}
	
	}//public double likelihood(String attr1, String attr1Val, String attr2, String attr2Val, String 
}
