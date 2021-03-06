import java.util.*;
public class nbc{
	Map<String,Map<String,Integer>> mapPos,mapNeg;
	int countPos,countNeg;
	DataSet trainingSet;
	public void train(DataSet train)
	{
		
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
			System.out.println(attr + " " + "class");
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
		//System.out.println(countPos+ " " + countNeg);
		//System.out.println(mapNeg.get(train.attributes.get(0)));	
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
	public ClassifyResult classify(Instance inst)
	{
		ClassifyResult cr = new ClassifyResult();
		double logProbPos = Math.log(this.priorP(trainingSet.labels.get(0)));
		double logProbNeg = Math.log(this.priorP(trainingSet.labels.get(1)));
		int i = 0;
		for(String attr: inst.attributes)
		{
			logProbPos += Math.log(this.likelihood(trainingSet.attributes.get(i),attr,trainingSet.labels.get(0)));
			logProbNeg += Math.log(this.likelihood(trainingSet.attributes.get(i),attr,trainingSet.labels.get(1))); 
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
}
