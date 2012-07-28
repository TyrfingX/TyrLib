package tyrfing.common.debug;

import tyrfing.common.graph.Graph;
import tyrfing.common.math.Vector2;
import tyrfing.common.renderables.Circle;
import tyrfing.common.renderables.Line;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.struct.Node;
import android.graphics.Canvas;

public class RenderGraph<V> extends Renderable {

	Graph<V> g;
	
	int nodeColor;
	int edgeColor;
	Node root;
	protected boolean init = false;
	
	boolean visible;
	
	public RenderGraph(int nodeColor, int edgeColor, Graph<V> g, Node parent) {
		this.g = g;
		this.nodeColor = nodeColor;
		this.edgeColor = edgeColor;
		root = parent;
		this.visible = true;
	}

	@Override
	public void onRender(Canvas target, float time) {
		
		Graph<V> h = g.clone();
		
		for (int i = 0; i < h.getCountNodes(); i++)
		{
			tyrfing.common.graph.Vertex<V> node = h.getNode(i);
			Node child = root.createChild(node.getX(), node.getY());
			Circle c = new Circle(5, this.nodeColor, child);
			c.onRender(target,time);
			root.removeChild(child);
			
			Vector2 node1Pos = child.getPos();
			
			for (int j = 0; j < node.getDegree(); j++)
			{
				tyrfing.common.graph.Vertex<V> node2 = node.getNeighbour(j);
				child = root.createChild(node.getX(), node.getY());
				Vector2 node2Pos = new Vector2(node2.getX(), node2.getY());
				Vector2 toVector = node1Pos.vectorTo(node2Pos);
				Line l = new Line(	toVector.x, toVector.y,
									edgeColor, child);
				l.onRender(target,time);
				root.removeChild(child);
			}
		}
		
	}


	
}
