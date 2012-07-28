package tyrfing.common.factory;

import java.util.HashMap;
import java.util.Map;

public class FactoryManager {
	
	private static Map<String, IFactory> factories;
	
	public static void init()
	{
		factories = new HashMap<String, IFactory>();
	}
	
	public static void registerFactory(String name, IFactory factory)
	{
		factories.put(name, factory);
	}
	
	public static void unRegisterFactory(String name)
	{
		factories.remove(name);
	}
	
	public static Object create(String creator)
	{
		return getFactory(creator).create();
	}
	
	public static IFactory getFactory(String name)
	{
		return factories.get(name);
	}
}
