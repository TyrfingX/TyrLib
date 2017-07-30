package tyrfing.common.ui;

import java.util.HashMap;
import java.util.Map;

public class Event {
	private Window evoker;
	private Map<String, String> params;
	
	public Event(Window evoker)
	{
		this.evoker = evoker;
		this.params = new HashMap<String, String>();
	}
	
	
	public Window getEvoker()
	{
		return evoker;
	}
	
	public void addParam(String name, String value)
	{
		params.put(name, value);
	}
	
	public String getParam(String name)
	{
		return params.get(name);
	}
}
