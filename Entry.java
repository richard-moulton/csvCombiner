package CSVCombiner;

import java.util.HashMap;

public class Entry
{
	private HashMap<String,String> values;
	
	public Entry()
	{
		this.values = new HashMap<String,String>();
	}
	
	public String get(String key)
	{
		return values.get(key);
	}
	
	public String set(String key, String value)
	{
		return values.put(key, value);
	}
	
	public boolean containsKey(String key)
	{
		return values.containsKey(key);
	}	
}
