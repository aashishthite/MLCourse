import java.util.ArrayList;
import java.util.List;


public class Instance 
{
	
	public String label;
	public List<String> attributes = null;

	
	public void addAttribute(String s) 
	{
		if (attributes == null) 
		{
			attributes = new ArrayList<String>();
		}
		attributes.add(s);
	}
	
	
	public void setLabel(String thislabel) 
	{
		label = thislabel;
	}
}
