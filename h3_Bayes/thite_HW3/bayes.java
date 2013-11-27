import java.io.*;
import java.util.*;
public class bayes{
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.out.println("usage: bayes <train> <test> <n|t>");
			return;
		}
		DataSet train = createDataSet(args[0]);
		DataSet test = createDataSet(args[1]);	
		
	
		//nbc learing curve:
		//25 -> 0.8214285714
		//50 -> 0.8452380952
		//100 -> 0.8571428571
		if(args[2].equals("n"))
		{
			//Random generator = new Random();
			//double avg_accuracy = 0;
			//for(int ii = 0; ii < 4; ii++)
			//{
				DataSet tempSet = train;//createSubDataSet(train,generator,25);
				nbc classifier = new nbc();
				classifier.train(tempSet);
				System.out.println();
				int num = 0;
				for(Instance inst : test.instances)
				{
					ClassifyResult cr = classifier.classify(inst);
					System.out.println(cr.label + " " + inst.label + " " + cr.posteriorProb);
					if(cr.label.equals(inst.label)) num++;
				}
				System.out.println("\n"+num);
				//avg_accuracy += (double)num/(double)test.instances.size();
			//}
			//System.out.println(avg_accuracy/4);
			
		}
		//tan learning curve
		//25 -> 0.6607142857142858
		//50 -> 0.8035714285714286
		//100 ->0.8333333333333334
		else if( args[2].equals("t"))
		{
			//Random generator = new Random();
			//double avg_accuracy = 0;
			//for(int ii = 0; ii < 4; ii++)
			//{
			DataSet tempSet = train;//createSubDataSet(train,generator,100);
			tan classifier = new tan();
			classifier.train(tempSet);
			classifier.printStruct();
			System.out.println();
			int num = 0;
			for(Instance inst : test.instances)
			{
				ClassifyResult cr = classifier.classify(inst);
				System.out.println(cr.label + " " + inst.label + " " + cr.posteriorProb);
				if(cr.label.equals(inst.label)) num++;
			}
			System.out.println("\n"+num);	
			//avg_accuracy += (double)num/(double)test.instances.size();
			//}
			//System.out.println(avg_accuracy/4);
		}
		else
		{
			System.out.println("Use n or t to choose classifier");
		}
	}
	private static DataSet createDataSet(String file) {
		
		DataSet set = new DataSet();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine();
				char firstChar = line.charAt(0);
				
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
}
