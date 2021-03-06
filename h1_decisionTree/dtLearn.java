import java.io.BufferedReader;
import java.io.FileReader;
 import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class dtLearn 
{

	
	public static void main(String[] args) 
	{
		if (args.length < 3) 
		{
			System.out.println("usage: java dt-learn <train-set-file> <test-set-file> <m>");
			System.exit(-1);
		}
		if(!isInteger(args[2]) || Integer.parseInt(args[2])<1)
		{
			System.out.println("Not a valid m value");
			System.exit(-1);
		}
		 
	 	
		
		DataSet trainSet = createDataSet(args[0]);
		DataSet testSet = createDataSet(args[1]);		
		//System.out.println(trainSet == null);
		 
		
		DecisionTree tree = new DecisionTree(trainSet,Integer.parseInt(args[2]));
		/*
		Random generator = new Random();
		double avg_accuracy = 0;
		for(int ii = 0; ii < 10; ii++)
		{
			DataSet tempSet = createSubDataSet(trainSet,generator,100);
			DecisionTree tree = new DecisionTreeImpl(tempSet,Integer.parseInt(args[2]));
			avg_accuracy += tree.calcTestAccuracy(testSet);
			System.out.println(tree.calcTestAccuracy(testSet));
		}
		System.out.println(avg_accuracy/10);
*/
		
		System.out.println(" ");
		tree.print();
		System.out.println(" ");
		
		String ss = "";
		for(String attr : testSet.attributes)
			ss += attr + " ";
		ss += "predicted actual";
		System.out.println(ss);
		int correct = 0;
		for (Instance instance : testSet.instances) 
		{
			String predicted = tree.classify(instance);
			String s = "";
			for(String attr : instance.attributes)
				s += attr + " ";
			s +=  predicted + " " + instance.label;
			System.out.println(s);
			if(instance.label.equals(predicted))
				correct++;
			
		}
		System.out.println(" ");
		System.out.println("Correctly classiified " +correct + " instances out of total " + testSet.instances.size() + " instances");
		 
	}

	
	private static DataSet createDataSet(String file) {
		
		DataSet set = new DataSet();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine();
				//
				char firstChar = line.charAt(0);
				
				//
				
				if (firstChar == '%') 
				{
				} 
				else if (firstChar == '@') 
				{
					if(line.toLowerCase().charAt(1) == 'a')
						set.addAttribute(line);					
				} 
				else if(!line.isEmpty())
				{
					set.addInstance(line);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} 		
		
		return set;
	}

	private static DataSet createSubDataSet(DataSet dataset, Random generator, int _size)
	{
		if(_size >= dataset.instances.size())
		{
			return dataset;
		}
		List<Integer> randomNos = new ArrayList<Integer>();
		for(int ii = 0; ii < _size; ii++ )
		{
			randomNos.add(generator.nextInt(dataset.instances.size()));
		}
	
		DataSet newDataSet 	= new DataSet();
		List<String> newLabels  = new ArrayList<String>(2);
		for (int i = 0; i < dataset.labels.size(); i++)
		{
			newLabels.add(dataset.labels.get(i));
		}
	    	newDataSet.labels = newLabels;
		
	    	List<String> newAttributes = new ArrayList<String>();
		for (int i= 0; i<dataset.attributes.size();i++)
		{
			newAttributes.add(dataset.attributes.get(i));
		}
	    	newDataSet.attributes = newAttributes;
		
	    	Map<String, List<String> > newAttributeValues = new HashMap<String, List<String>>();
		newAttributeValues.putAll(dataset.attributeValues);
		newDataSet.attributeValues = newAttributeValues;
		
		List<Instance> newInstances = new ArrayList<Instance>();
		for (int i=0; i < dataset.instances.size(); i++)
		{
			if(randomNos.contains(i))
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
		}
		newDataSet.instances = newInstances;
		return newDataSet;		
	}
	private static boolean isInteger(String s) 
	{
	        try 
		{ 
	            Integer.parseInt(s); 
	        } 
		catch(NumberFormatException e) 
		{ 
	            return false; 
	        }
	        return true;
	}
}
