import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataSet 
{
	public List<String> labels = null;  
	public List<String> attributes = null;  
	public Map<String, List<String> > attributeValues = null;
	public List<Instance> instances = null; 
		
	public void addLabels(String line) 
	{
		
		labels = new ArrayList<String>(2);
		
		String[] splitline = line.split(" ");
		if (splitline.length < 5) 
		{
			System.err.println("Line doesn't contain enough labels");
			return;
		}
		
		
		for (int i = 3; i < splitline.length; i++) {
			String temp = splitline[i].replace(',',' ');
			temp = temp.replace('}', ' ');
			temp = temp.trim();		
			if(temp.length()>0) labels.add(temp);
		}
		//System.out.println(labels);
	}
	
	
	public void addAttribute(String line) 
	{
		if (attributes == null) 
		{
			attributes = new ArrayList<String>();
			attributeValues = new HashMap<String, List<String>>();
		}
		//set.addLabels(line);
		String[] splitline = line.split(" ");
		if (splitline.length < 3) 
		{
			System.err.println("Line doesn't contain enough attributes");
			return;
		}
		
		if(splitline[1].equals("'class'"))
		{	
			addLabels(line);
			return;
		}
		List<String> list = new ArrayList<String>();
		
		
		String tempAttr = splitline[1].substring(1,splitline[1].length()-1);
		attributes.add(tempAttr);
		if(splitline.length >= 4)
		{		
			for (int i = 3; i < splitline.length; i++) 
			{
				String temp = splitline[i].replace(',',' ');//substring(0,splitline[i].length()-1);
				temp = temp.replace('}', ' ');
				temp = temp.trim();
				if(temp.length()>0) list.add(temp);
			}

		}
		else
		{
			//addNumericSplits(line);
			list.add(splitline[2]);
		}
		attributeValues.put(tempAttr, list);
	}

	
	public void addInstance(String line) 
	{
		
		if (instances == null) 
		{
			instances = new ArrayList<Instance>();
		}		
		String[] splitline = line.split(",");
		
		if (splitline.length < 1 + attributes.size()) 
		{ 
			System.err.println("Instance doesn't contain enough attributes");
			System.out.println(line);
			return;
		}
		
		Instance instance = new Instance();
		for(int i = 0; i < splitline.length - 1; i ++)
			instance.addAttribute(splitline[i]);
		instance.setLabel(splitline[splitline.length - 1]);
		instances.add(instance);
	}

}
