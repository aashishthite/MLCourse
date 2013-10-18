import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;
import java.util.Collections;



public class DecisionTree 
{
	
	TreeNode rootNode;
	DataSet trainingSet;
	int stoppingPoint;
	
	DecisionTree() 
	{
	}
	
	
	DecisionTree(DataSet train, int m) 
	{
		trainingSet   = train;
		rootNode      = new TreeNode("", "", "Root", false);
		stoppingPoint = m;
		buildTree(train,rootNode);
		
	}

	
		
	public String classify(Instance instance) 
	{
		TreeNode temp = rootNode;
		while(!temp.terminal)
		{
			if(!temp.isNumeric)
			{
				String question = temp.attribute;
				int index = trainingSet.attributes.indexOf(question);
				String attribValue  = instance.attributes.get(index);				
				for (int i=0;i<temp.children.size();i++)
				{
					TreeNode childNode = temp.children.get(i);
					if (childNode.parentAttributeValue.equals(attribValue))
					{
						temp = childNode;
						break;
					}
				}
			}
			else
			{
				String question = temp.attribute;
				int index = trainingSet.attributes.indexOf(question);
				double attribValue  = Double.parseDouble(instance.attributes.get(index));
				if(temp.threshold >= attribValue)
				{
					temp = temp.children.get(0);
				}
				else
				{
					temp = temp.children.get(1);
				}
			}	
		}		
		return temp.label;
		
	}

	
	public void print() 
	{		
		rootNode.print(0);
	}

		 
	public double calcTestAccuracy(DataSet testingSet)
	{
		double testAccuracy = 0;
		 for (Instance instance : testingSet.instances){
			 String label = classify(instance);
			 if (label.equals(instance.label))
				 testAccuracy++;
		 }
		 testAccuracy = testAccuracy/testingSet.instances.size();
		 return testAccuracy;	
	}
	
	
	private int buildTree(DataSet train, TreeNode root)
	{
		
		int a=0,b=0;
		for(int ii = 0; ii < train.instances.size(); ii++)
		{
			if(train.instances.get(ii).label.equals(train.labels.get(0)))
			{
				a++;
			}
			else
			{
				b++;
			}
		}
		root.neg = a;
		root.pos = b;
		if (train.instances.size() < stoppingPoint)
		{
			root.label    = train.labels.get(majorityVote(train));
			root.terminal = true;
			return 0;
		}
		else if( check_same_label(train))
		{
			
			root.label    = train.labels.get(majorityVote(train));
			root.terminal = true;
			return 0;		
		}
		else if (train.attributes.size() == 0)
		{
			String label 	  = train.labels.get(majorityVote(train));
			root.label 	  = label;
			root.terminal = true;
			return 0;			
		}
		else 
		{
			if (check_same_label(train))
			{
				root.label = train.instances.get(0).label;
				root.terminal = true;
				return 0;
			}
			else
			{
				Feature tempF	   = getBestAttribute(train);
				String attribute   = tempF.featureName;
				root.isNumeric = tempF.isNumeric;
				root.threshold = tempF.threshold;
				root.attribute = attribute;
				root.terminal  = false;
				root.label     =  train.labels.get(majorityVote(train));
				int noOfAttribVal  = root.isNumeric?2:train.attributeValues.get(attribute).size();
				//System.out.println(root.isNumeric+" "+root.attribute + " " + root.threshold );
				
				for (int i=0; i < noOfAttribVal; i++)
				{
					
					if( !root.isNumeric)
					{	
						//System.out.println(train.attributeValues.get(attribute).size());
						String parentAttributeValue = train.attributeValues.get(attribute).get(i);
						TreeNode childNode = new TreeNode("", "", parentAttributeValue , false);
						root.addChild(childNode);
						DataSet newSet = splitDataSet(train, attribute, parentAttributeValue);
						//System.out.println(parentAttributeValue);
						//rootNode.print(0);
						buildTree(newSet, childNode);
					}
					else
					{
						
						String parentAttributeValue = String.valueOf(root.threshold);
						TreeNode childNode = new TreeNode("", "", parentAttributeValue , false);
						root.addChild(childNode);
						DataSet newSet = splitDataSetNumeric(train, attribute, root.threshold, i>0?true:false);
						//rootNode.print(0);
						buildTree(newSet, childNode);
					}	
				}
			}
		}
		return 0;
		
	}
	private DataSet splitDataSetNumeric(DataSet dataset, String attribute, double threshold, boolean greaterThan)
	{
		DataSet newSet 	= new DataSet();
		List<String> newLabels  = new ArrayList<String>(2);
		for (int i = 0; i < dataset.labels.size(); i++)
		{
			newLabels.add(dataset.labels.get(i));
		}
	    	newSet.labels = newLabels;
		
	    	List<String> newAttributes = new ArrayList<String>();
		for (int i= 0; i<dataset.attributes.size();i++)
		{
			newAttributes.add(dataset.attributes.get(i));
		}
	    	newSet.attributes = newAttributes;
		
	    	Map<String, List<String> > newAttributeValues = new HashMap<String, List<String>>();
		newAttributeValues.putAll(dataset.attributeValues);
		newSet.attributeValues = newAttributeValues;
		
		List<Instance> newInstances = new ArrayList<Instance>();
		for (int i=0; i < dataset.instances.size(); i++)
		{
			Instance newInst   = new Instance();
			newInst.attributes = new ArrayList<String>();
			Instance oldInst   = dataset.instances.get(i);
			newInst.label 	   = oldInst.label;

			for (int j=0; j < oldInst.attributes.size(); j++)
			{
				newInst.attributes.add(oldInst.attributes.get(j));
			}

			newInstances.add(newInst);
		}
		int indexOfAttribute = newSet.attributes.indexOf(attribute);

		newSet.instances = newInstances;		
		//newSet.attributes.remove(attribute);
		//newSet.attributeValues.remove(attribute);
		
	
		int newSetSize = newSet.instances.size();
		int[] delInstance = new int[newSetSize];
		int k = 0;

		for (int i=0; i< newSetSize; i++)
		{
			Instance inst = newSet.instances.get(i);
			if(greaterThan)
			{
				if (Double.parseDouble(inst.attributes.get(indexOfAttribute)) <= threshold)
				{
					delInstance[k++] = i;
				}
			}
			else
			{
				if (Double.parseDouble(inst.attributes.get(indexOfAttribute)) > threshold)
				{				
					delInstance[k++] = i;
				}
			}
		}

		for (int i = k-1; i >= 0; i--)
		{
			newSet.instances.remove(delInstance[i]);
		}
		
		return newSet;		
	}

	
	private DataSet splitDataSet(DataSet dataset, String attribute, String attribValue)
	{
		DataSet newSet 	= new DataSet();
		List<String> newLabels  = new ArrayList<String>(2);
		for (int i = 0; i < dataset.labels.size(); i++)
		{
			newLabels.add(dataset.labels.get(i));
		}
	    	newSet.labels = newLabels;
		
	    	List<String> newAttributes = new ArrayList<String>();
		for (int i= 0; i<dataset.attributes.size();i++)
		{
			newAttributes.add(dataset.attributes.get(i));
		}
	    	newSet.attributes = newAttributes;
		
	    	Map<String, List<String> > newAttributeValues = new HashMap<String, List<String>>();
		newAttributeValues.putAll(dataset.attributeValues);
		newSet.attributeValues = newAttributeValues;
		
		List<Instance> newInstances = new ArrayList<Instance>();
		for (int i=0; i < dataset.instances.size(); i++)
		{
			Instance newInst   = new Instance();
			newInst.attributes = new ArrayList<String>();
			Instance oldInst   = dataset.instances.get(i);
			newInst.label 	   = oldInst.label;

			for (int j=0; j < oldInst.attributes.size(); j++)
			{
				newInst.attributes.add(oldInst.attributes.get(j));
			}

			newInstances.add(newInst);
		}
		int indexOfAttribute = newSet.attributes.indexOf(attribute);

		newSet.instances = newInstances;		
		newSet.attributes.remove(attribute);
		newSet.attributeValues.remove(attribute);
	
		int newSetSize = newSet.instances.size();
		int[] delInstance = new int[newSetSize];
		int k = 0;

		for (int i=0; i< newSetSize; i++)
		{
			Instance inst = newSet.instances.get(i);
			
			if (inst.attributes.get(indexOfAttribute).equals(attribValue))
			{
				inst.attributes.remove(indexOfAttribute);
			}	
			else 
			{
				delInstance[k++] = i;
			}
		}

		for (int i = k-1; i >= 0; i--)
		{
			newSet.instances.remove(delInstance[i]);
		}
		//System.out.println(newSet.attributes.size()+" "+newSet.instances.size());
		return newSet;		
	}

	
	private boolean check_same_label(DataSet dataset)
	{
		
		String label = dataset.instances.get(0).label;
		
		Iterator<Instance> itr = dataset.instances.iterator();
		boolean is_same = true;
		while(itr.hasNext())
		{
			if(!itr.next().label.equals(label))
			{	
				is_same = false;
				break;
			}
		}
		return is_same;
	}


	private int majorityVote(DataSet dataset)
	{
		int count1=0,count2=0;
		for(int i=0;i<dataset.instances.size();i++)
		{
			if(dataset.instances.get(i).label.equals(dataset.labels.get(0)))
			{
				count1++;
			}
			else
			{
				count2++;
			}
		}
		if(count1 >= count2)
			return 0;
		else 
			return 1;
	}
	
	private Feature getBestAttribute(DataSet dataset)
	{
		
		Feature bestAttribute = new Feature();
		int a = 0, b = 0;
        	for(int i = 0; i < dataset.instances.size(); i++)
        	{
            
           		if(dataset.instances.get(i).label.equals(dataset.labels.get(0)))
          		{
                		a++;
            		}
            		else if(dataset.instances.get(i).label.equals(dataset.labels.get(1)))
            		{
                		b++;
            		}	                
        	}
		double classEntropy = this.calc_entropy(a, b);

		double maxMutualInfo = 0;
		//System.out.println(dataset.attributes);
		for(int i = 0; i < dataset.attributes.size(); i++)
		{
			double mutualInfo = classEntropy;
			//System.out.println(dataset.attributeValues.get(dataset.attributes.get(i)));
			if(dataset.attributeValues.get(dataset.attributes.get(i)).size() > 1)
			{
				
				for(int j = 0; j < dataset.attributeValues.get(dataset.attributes.get(i)).size(); j++)
				{
					int _a = 0, _b = 0;
					for(int k = 0; k < dataset.instances.size(); k++)
					{
						String attVal =  dataset.attributeValues.get(dataset.attributes.get(i)).get(j);
		    
		   				if(dataset.instances.get(k).attributes.get(i).equals(attVal) && dataset.instances.get(k).label.equals(dataset.labels.get(0)))
		  				{
		        				_a++;
		    				}
		    				else if(dataset.instances.get(k).attributes.get(i).equals(attVal) && dataset.instances.get(k).label.equals(dataset.labels.get(1)))
		    				{
		        				_b++;
		    				}	                
					}
					mutualInfo -= (double)(_a + _b) * calc_entropy(_a, _b)/ (double)(dataset.instances.size());
					
				}
				//System.out.println(mutualInfo);
				if(mutualInfo > maxMutualInfo)
				{
					maxMutualInfo = mutualInfo;
					bestAttribute.featureName = dataset.attributes.get(i);
					bestAttribute.isNumeric = false;  
					bestAttribute.threshold = 0;
				}
			}
			else
			{
				//TODO: numeric split stuff
				
				List<Double> Values = new ArrayList<Double>();
				//System.out.println(dataset.instances.get(0).attributes);
				//System.out.println(i);
				for(int k = 0; k < dataset.instances.size(); k++)
				{
					//System.out.println(k);
					if(!Values.contains(Double.parseDouble(dataset.instances.get(k).attributes.get(i))))
					{
						Values.add(Double.parseDouble(dataset.instances.get(k).attributes.get(i)));
					}
				}
				Collections.sort(Values);
				
				for(int j = 0; j < Values.size()-1; j++)
				{
					mutualInfo = classEntropy;
					double tempThresh = (Values.get(j) + Values.get(j+1))/2;
					boolean posLeft = false, posRight = false, negLeft = false, negRight = false;
					for(int k = 0; k < dataset.instances.size(); k++)
					{
						
						if(Double.parseDouble(dataset.instances.get(k).attributes.get(i))==Values.get(j))
						{
							if(dataset.instances.get(k).label.equals(dataset.labels.get(0)))
							{
								negLeft = true;
							}
						}
						if(Double.parseDouble(dataset.instances.get(k).attributes.get(i))==Values.get(j+1))
						{
							if(dataset.instances.get(k).label.equals(dataset.labels.get(0)))
							{
								negRight = true;
							}
						}
						if(Double.parseDouble(dataset.instances.get(k).attributes.get(i))==Values.get(j))
						{
							if(dataset.instances.get(k).label.equals(dataset.labels.get(1)))
							{
								posLeft = true;
							}
						}
						if(Double.parseDouble(dataset.instances.get(k).attributes.get(i))==Values.get(j+1))
						{
							if(dataset.instances.get(k).label.equals(dataset.labels.get(1)))
							{
								posRight = true;
							}
						}
						if((posRight && negLeft) || (negRight && posLeft))
						{
							break;
						}						
						
					}

					if((posRight && negLeft) || (negRight && posLeft))
					{
						
						int _a = 0, _b = 0, _a_ = 0, _b_ = 0;
						for(int k = 0; k < dataset.instances.size(); k++)
						{
						
    							
   							if(Double.parseDouble(dataset.instances.get(k).attributes.get(i)) <= tempThresh)
  							{
								if(dataset.instances.get(k).label.equals(dataset.labels.get(0)))
									_a++;
								else
									_b++;
    							}
							
    							else if(Double.parseDouble(dataset.instances.get(k).attributes.get(i)) > tempThresh)
    							{
								if(dataset.instances.get(k).label.equals(dataset.labels.get(0)))
									_a_++;
								else
									_b_++;
    							}	                
						}
						mutualInfo -= (double)(_a + _b) * calc_entropy(_a, _b)/ (double)(dataset.instances.size());
						mutualInfo -= (double)(_a_ + _b_) * calc_entropy(_a_, _b_)/ (double)(dataset.instances.size());
					
						//System.out.println(mutualInfo);
						
						if(mutualInfo > maxMutualInfo)
						{
							maxMutualInfo = mutualInfo;
							bestAttribute.featureName = dataset.attributes.get(i);
							bestAttribute.threshold = tempThresh;
							bestAttribute.isNumeric = true;
						}
							
					}
				}				
			}
			
		}
		//System.out.println(maxMutualInfo);
		return bestAttribute;
	}
	
	public double calc_entropy(int a, int b)
	{
		if(a==0 && b==0)
			return 0;
		double pa=(double)(a)/(double)(a + b);
		double pb=(double)(b)/(double)(a + b);
		if(pa != 0 && pb != 0)
		{
			return (-pa * Math.log(pa) - pb * Math.log(pb))/ Math.log(2);
		}
		if(pa != 0 && pb == 0)
		{
			return (-pa * Math.log(pa))/ Math.log(2);
		}
		if(pa == 0 && pb != 0)
		{
			return (-pb * Math.log(pb))/ Math.log(2);
		}		
		return -1;
	}	
}

