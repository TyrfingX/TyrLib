package tyrfing.common.graph;

import java.util.Vector;

import tyrfing.common.math.Vector2;

public class Graph<V extends Object> {
	
	Vector<Vertex<V>> nodes;
	
	
	public Graph()
	{
		nodes = new Vector<Vertex<V>>();
	}
	
	public Graph<V> clone()
	{
		Graph<V> g = new Graph<V>();
		
		for (Vertex<V> node : nodes)
		{
			g.nodes.add(node.clone());
		}
		
		for (int i = 0; i < this.countVertices(); i++)
		{
			Vertex<V> node1 = this.getVertex(i);
			for (int j = 0; j < this.countVertices(); j++)
			{
				Vertex<V> node2 = this.getVertex(j);
				if (node1 != node2)
				{
					if (node1.isNeighbour(node2))
					{
						g.getVertex(i).addEdge(g.getVertex(j));
					}
				}
			}
		}
		
		return g;
	}
	
	public void addVertex(Vertex<V> vertex)
	{
		nodes.add(vertex);
	}
	
	public void addEdge(Vertex<V> vertex1, Vertex<V> vertex2)
	{
		if (!vertex1.isNeighbour(vertex2)) {
			vertex1.addEdge(vertex2);
			vertex2.addEdge(vertex1);
		}
	}
	
	public void addEdge(int index1, int index2)
	{
		this.addEdge(nodes.get(index1), nodes.get(index2));
	}
	
	public boolean hasEdge(V content1, V content2)
	{
		for (Vertex<V> v1 : nodes)
		{
			if (v1.getContent() == content1)
			{
				for (Vertex<V> v2 : nodes)
				{
					if (v2.getContent() == content2)
					{
						return v1.isNeighbour(v2);
					}
				}
				
				return false;
				
			}
		}
		
		return false;
			
	}
	
	public void addEdge(V content1, V content2)
	{
		for (Vertex<V> v1 : nodes)
		{
			if (v1.getContent() == content1)
			{
				for (Vertex<V> v2 : nodes)
				{
					if (v2.getContent() == content2)
					{
						this.addEdge(v1, v2);
						return;
					}
				}
			}
		}		
	}
	
	public void removeEdge(Vertex<V> vertex1, Vertex<V> vertex2) {
		vertex1.removeEdge(vertex2);
		vertex2.removeEdge(vertex1);
	}
	
	public void removeEdge(int index1, int index2) {
		this.removeEdge(nodes.get(index1), nodes.get(index2));
	}
	
	public void removeEdge(V content1, V content2)
	{
		for (Vertex<V> v1 : nodes)
		{
			if (v1.getContent() == content1)
			{
				for (Vertex<V> v2 : nodes)
				{
					if (v2.getContent() == content2)
					{
						this.removeEdge(v1, v2);
						return;
					}
				}
			}
		}		
	}
	
	public Vertex<V> getVertex(V content)
	{
		for (Vertex<V> v : nodes)
		{
			if (v.getContent() == content)
			{
				return v;
			}
		}			
		
		return null;
	}
	
	public int countVertices()
	{
		return nodes.size();
	}
	
	public Vertex<V> getVertex(int index)
	{
		return nodes.get(index);
	}
	
	public void removeVertex(Vertex<V> vertex)
	{
		nodes.remove(vertex);
		this.isolateNode(vertex);
	}
	
	public void removeAllEdges()
	{
		for (Vertex<V> node : nodes)
		{
			node.removeAllEdges();
		}
	}
	
	public void isolateNode(Vertex<V> node)
	{
		for (int i = 0; i < node.getDegree(); i++)
		{
			node.getNeighbour(i).removeEdge(node);
		}
		node.removeAllEdges();
	}
	
	public void isolateNode(int index)
	{
		this.isolateNode(nodes.get(index));
	}
	
	public void removeVertex(int index)
	{
		this.removeVertex(nodes.get(index));
	}
	

	@SuppressWarnings("unchecked")
	public void distancesTo(Vertex<V> node)
	{
		for (Vertex<V> n : nodes)
		{
			n.setDistance(0);
			n.setParent(null);
		}
		Dijkstra.apply((Vertex<Object>) node);
	}
	
	public void distancesTo(int index)
	{
		this.distancesTo(nodes.get(index));
	}
	
	public int getCountNodes()
	{
		return nodes.size();
	}
	
	public Vertex<V> getNode(int index)
	{
		return nodes.get(index);
	}
	
	public void addAll(Graph<V> g)
	{
		nodes.addAll(g.nodes);
	}
	
	public void translate(Vector2 translation)
	{
		for (Vertex<V> vertex : nodes)
		{
			vertex.setX(vertex.getX() + translation.x);
			vertex.setY(vertex.getY() + translation.y);
		}
	}
	
}
