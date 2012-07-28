package tyrfing.common.graph;

import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra {
	
	private static Queue<Vertex<Object>> open;
	private static Vertex<Object> start;
	
	@SuppressWarnings("unchecked")
	public static void apply(Vertex<? extends Object> node)
	{
		
		open = new PriorityQueue<Vertex<Object>>();
		start = (Vertex<Object>) node;
		
		Dijkstra.addNeighbours((Vertex<Object>) node);
		
		while (open.size() != 0)
		{
			Dijkstra.step();
		}
		
		start.setDistance(0);
	}
	
	private static void step()
	{
		Vertex<Object> min = open.poll();
		Dijkstra.addNeighbours(min);
	}
	
	private static void addNeighbours(Vertex<Object> node)
	{
		int degree = node.getDegree();
		for (int i = 0; i < degree; i++)
		{
			Vertex<Object> neighbour = node.getNeighbour(i);
			if (neighbour.getDistance() == 0 && neighbour != start)
			{
				neighbour.setParent(node);
				float distance = node.distanceTo(neighbour) + node.getDistance();
				neighbour.setDistance(distance);
				open.add(neighbour);
			}
		}
	}
	
	
}
