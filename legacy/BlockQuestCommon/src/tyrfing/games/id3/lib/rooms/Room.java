package tyrfing.games.id3.lib.rooms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.graphics.Color;
import android.graphics.Paint.Style;
import tyrfing.common.debug.RenderGraph;
import tyrfing.common.game.objects.Algorithm;
import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.game.objects.GameObject;
import tyrfing.common.game.objects.Movement;
import tyrfing.common.graph.Graph;
import tyrfing.common.graph.Vertex;
import tyrfing.common.input.TouchListener;
import tyrfing.common.math.Rotator2;
import tyrfing.common.math.Transformer;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Coord2;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.MainLogic;
import tyrfing.games.id3.lib.rooms.content.Hero;
import tyrfing.games.id3.lib.rooms.content.Monster;
import tyrfing.games.id3.lib.rooms.content.StaticContent;

public class Room extends GameObject implements TouchListener {
	protected ArrayList<RoomElement> roomElements;
	protected Vector<GameObject> content;
	protected Board board;
	protected boolean falling;
	
	protected Movement movement;
	
	public static float fallSpeed = 20;
	public static float SPEED_UP = 14;
	
	protected boolean finished = false;

	protected boolean cleared = false;
	protected Vector2 lastTranslation;
	
	protected Graph<RoomElement> graph;
	protected Graph<RoomElement> moveGraph;
	protected RenderGraph<RoomElement> debugGraph;
	
	protected VisitCounter visits;

	
	protected float touchTime = 0;
	private static final float TIME_MIRROR = 0.25f;
	
	private boolean firstFall = true;
	private RoomState state = RoomState.NORMAL;
	private boolean containsHero = false;
	
	private Text debugVisitCounter;
	
	private Integer lowestCoord;
	
	private Node previewNode;
	private List<Rectangle> previewElements;
	
	public boolean rotate = false;
	
	private boolean moved;
	
	private Direction userMove = null;
	
	public static final int MAX_ITERATIONS = 10000;
	
	public Room(Node node, Board board)
	{
		super(node);
		roomElements = new ArrayList<RoomElement>();
		this.board = board;
		this.movement = new Movement(node, fallSpeed);
		this.content = new Vector<GameObject>();
		graph = new Graph<RoomElement>();
		visits = new VisitCounter();
		debugVisitCounter = SceneManager.createText("", Color.WHITE, node);
		debugVisitCounter.setPriority(10000);
		debugVisitCounter.setVisible(false);
	}
	
	public void setFirstFall(boolean firstFall)
	{
		this.firstFall = firstFall;
		if (!firstFall)
		{
			movement.setSpeed(fallSpeed * SPEED_UP);
		}
	}
	
	public int countRoomElements()
	{
		return roomElements.size();
	}
	
	public void setState(RoomState state)
	{
		
		if (this.state != state)
		{
		
			if (this.state.overwrite(state))
			{
				
				if (state == RoomState.NORMAL)
				{
					for (RoomElement element : roomElements)
					{
						element.onRedRemoved();
					}
				}
				else
				{
					for (RoomElement element : roomElements)
					{
						element.onRedSet(state);
					}
					
					if (state.strenghtenMonsters())
					{
						if (this.state != RoomState.RED && this.state != RoomState.MADERED && this.state != RoomState.VIOLET)
						{
							for (GameObject object : content)
							{
								if (object instanceof Monster)
								{
									((Monster) object).powerUp((int)(((Monster) object).getStats().getStat("Level")*0.5f));
								}
							}
						}
					}
				}
				
				this.state = state;
			}
		
		}
		
		
	}
	
	public boolean isRed()
	{
		return state == RoomState.MADERED || state == RoomState.RED;
	}
	
	public boolean isOriginalRed()
	{
		return state == RoomState.RED;
	}
	
	public RoomState getState()
	{
		return state;
	}
	
	public void addElement(RoomElement element)
	{
		roomElements.add(element);
		element.setRoom(this);
	
		if (lowestCoord == null)
		{
			lowestCoord = board.getBoardCoord(element.getCenter()).y;
		}
		else
		{
			Coord2 elementCoord = board.getBoardCoord(element.getCenter());
			if (elementCoord.y > lowestCoord) lowestCoord = elementCoord.y;
		}
	
	}
	
	public void removeElement(RoomElement element)
	{
		
		for (GameObject object : content)
		{
			if (object instanceof StaticContent)
			{
				if (((StaticContent) object).getRoomElement() == element)
				{
					((StaticContent) object).remove();
					break;
				}
			}
		}
		
		roomElements.remove(element);
		graph.removeVertex(graph.getVertex(element));
		element.setRoom(null);
	
		lowestCoord = null;
		for (RoomElement roomElement : roomElements)
		{
			if (lowestCoord == null)
			{
				lowestCoord = board.getBoardCoord(roomElement.getCenter()).y;
			}
			else
			{
				Coord2 elementCoord = board.getBoardCoord(roomElement.getCenter());
				if (elementCoord.y > lowestCoord) lowestCoord = elementCoord.y;
			}
		
		}
	
	}
	
	public int getCountElements()
	{
		return roomElements.size();
	}
	
	public RoomElement getRoomElement(int index)
	{
		return roomElements.get(index);
	}
	
	public void rotate()
	{
		rotate = false;
		this.transform(Rotator2.Rotator90);
	}
	
	public void transform(Transformer transformer)
	{
		this.writeNullAtPos();
		for (RoomElement element : roomElements)
		{
			Vector2 boardPoint = element.getCenter().add(new Vector2(0, board.getTileSize()));
			if (board.getItem(boardPoint) == element)
			{
				board.setItem(boardPoint, null);
			}
		}
		
		
		for (int i = 0; i < 4; ++i) {
			for (RoomElement element : roomElements)
			{
				element.transform(transformer);
				Vertex<RoomElement> vertex = graph.getVertex(element);
				Vector2 pos = new Vector2(vertex.getX(), vertex.getY());
				pos = transformer.transformVector(pos);
				vertex.setX(pos.x);
				vertex.setY(pos.y);
			}
			
			if (!isInvalidRotation()) {
				break;
			}
		}
		
		
		MainLogic.updateFallPreview();
	}
	
	private boolean isInvalidRotation() {
		if (checkLowCollision()) {
			return true;
		}
		
		for (RoomElement roomElement : roomElements) {
			Vector2 point = roomElement.getCenter();
			if (point.x < 0) return true;
			if (point.y < 0) {
				point.y = 0;
			}
			Coord2 boardCoord = board.getBoardCoord(point);
			
			if (boardCoord.x < 0 || boardCoord.y < 0) {	
				return true;
			}
			
			if (boardCoord.x >= board.getWidth() || boardCoord.y >= board.getHeight()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isInvalidPosition() {
		if (checkCollision()) {
			return true;
		}
		
		for (RoomElement roomElement : roomElements) {
			Vector2 point = roomElement.getCenter();
			if (point.x < 0) return true;
			if (point.y < 0) {
				point.y = 0;
			}
			Coord2 boardCoord = board.getBoardCoord(point);
			
			if (boardCoord.x < 0 || boardCoord.y < 0) {	
				return true;
			}
			
			if (boardCoord.x >= board.getWidth() || boardCoord.y >= board.getHeight()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onUpdate(float time) {
		
		visits.onUpdate(time);

		debugVisitCounter.setText(visits.getVisits()+"");
		
		if (state == RoomState.MADERED || state == RoomState.RED || state == RoomState.VIOLET)
		{
			ArrayList<Room> adjacentRooms = this.getAdjacentRooms(false);
			for (Room room : adjacentRooms)
			{
				if (state != RoomState.MADERED)
				{
					room.setState(RoomState.reducedRedness(state));
				}
				else if (room.getState() == RoomState.BLUE || room.getState() == RoomState.MADEBLUE)
				{
					room.setState(state);
				}
			}
		}
		else if (state == RoomState.BLUE)
		{
			ArrayList<Room> adjacentRooms = this.getAdjacentRooms(false);
			for (Room room : adjacentRooms)
			{
				room.setState(RoomState.reducedBlueness(state));
			}
		}

		
		if (rotate && this.isFalling() && this.firstFall) this.rotate();
		
		float timeTmp = time;
		
		if (userMove != null && this.isFalling() && this.firstFall)
		{
			this.writeNullAtPos();
			for (RoomElement element : roomElements)
			{
				Vector2 boardPoint = element.getCenter().add(new Vector2(0, board.getTileSize()));
				if (board.getItem(boardPoint) == element)
				{
					board.setItem(boardPoint, null);
				}
			}
			
			Vector2 newPos = this.getPos();
			if (userMove == Direction.RIGHT)
			{
				newPos = newPos.sub(new Vector2(board.getTileSize(), 0));
			}
			else
			{
				newPos = newPos.sub(new Vector2(-1 * board.getTileSize(), 0));
			}
			
			Vector2 oldPos = this.getPos();
			
			node.setPos(newPos);
			
			if (!isInvalidPosition())
			{
				movement.clearPath();
			}
			else
			{
				node.setPos(oldPos);
			}
			
			userMove = null;
		}
		
		int iterations = 0;
		while(time > 0 && iterations < MAX_ITERATIONS) {
			if (movement.isFinished()) {
				
				if (!checkNextCollision())
				{
					this.fallToNextLevel();
				}
				else
				{
					if (!finished)
					{
						this.land();
						
					}
					time = 0;
				}
			} else {
				lastTranslation = this.getAbsolutePos();
				movement.onUpdate(time);
				time = movement.getRemainingTime();
				lastTranslation = lastTranslation.vectorTo(this.getAbsolutePos());
				this.setChanged();
				this.notifyObservers();
			}
			
			iterations++;
		}
		
		if (!this.isFalling())
		{
			for (RoomElement element : roomElements)
			{
				if (element.movement.isFinished())
				{
					
					if (!element.justLanded)
					{
						element.justLanded = true;

						graph = new Graph<RoomElement>();
						this.finishConstruction();
					}
					
					Vector2 elementCenter = element.getCenter();
					Coord2 nextPos = new Coord2((int)(elementCenter.x / board.getTileSize()), (int)(elementCenter.y /board.getTileSize()) + 1);
					if (board.checkBounds(nextPos.x, nextPos.y))
					{
						RoomElement nextElement = (RoomElement) board.getItem(nextPos);
						if (nextElement == null)
						{	
							boolean ownRoomBelow = true;
							
								
							Coord2 boardCoord = board.getBoardCoord(element.getCenter()); 
								
							while(true)
							{
								if(!board.checkBounds(boardCoord.x, boardCoord.y))
								{
									break;
								}
								else if (board.getItem(boardCoord) == null)
								{
									break;
								}
								else
								{
									RoomElement rowElement = (RoomElement) board.getItem(boardCoord);
									if (rowElement.getRoom() != this) break;
									if (this.isElementBelow2(rowElement)) ownRoomBelow = false;
								}
								boardCoord.x--;
							}
							
							boardCoord = board.getBoardCoord(element.getCenter()); 
							
							while(true)
							{
								if(!board.checkBounds(boardCoord.x, boardCoord.y))
								{
									break;
								}
								else if (board.getItem(boardCoord) == null)
								{
									break;
								}
								else
								{
									RoomElement rowElement = (RoomElement) board.getItem(boardCoord);
									if (rowElement.getRoom() != this) break;
									if (this.isElementBelow2(rowElement)) ownRoomBelow = false;
								}
								boardCoord.x++;
							}
							
							if (ownRoomBelow)
							{
								boardCoord = board.getBoardCoord(element.getCenter()); 
								if (boardCoord.y >= lowestCoord) ownRoomBelow = false;
							}

							
							
							if (ownRoomBelow)
							{
								MainLogic.updateFallPreview();
								board.setItem(element.getCenter(), null);
								Vector2 boardPos = element.getNode().getParent().getParent().getAbsolutePos();
								Vector2 dest = new Vector2(nextPos.x * board.getTileSize(), nextPos.y * board.getTileSize());
								dest = dest.add(boardPos);
								board.setItem(nextPos, element);
								element.movement.addPoint(dest);
								element.justLanded = false;
							}
						}
						
					}

				}
				else
				{
					lastTranslation = element.getAbsolutePos();
					element.onUpdate(timeTmp);
					lastTranslation = lastTranslation.vectorTo(element.getAbsolutePos());
					this.setChanged();
					this.notifyObservers();
				}
				
			}

		}
		
		

	}
	
	public int getIndexOfRoomElement(RoomElement element)
	{
		int i = 0;
		for (RoomElement element2 : roomElements)
		{
			if (element2 == element) break;
			++i;
		}
		return i;
	}
	
	private boolean isElementBelow(Vector2 elementCenter)
	{
		Coord2 nextPos = new Coord2((int)(elementCenter.x / board.getTileSize()), (int)(elementCenter.y /board.getTileSize()) + 1);
		if (board.checkBounds(nextPos.x, nextPos.y))
		{
			RoomElement nextElement = (RoomElement) board.getItem(nextPos);
			if (nextElement != null && !roomElements.contains(nextElement))
			{
				return true;
			}
			
		}
		else
		{
			return true;
		}
		
		return false;
	}

	private boolean isElementBelow2(RoomElement element)
	{
		Vector2 elementCenter = element.getCenter();
		Coord2 nextPos = new Coord2((int)(elementCenter.x / board.getTileSize()), (int)(elementCenter.y /board.getTileSize()) + 1);
		if (board.checkBounds(nextPos.x, nextPos.y))
		{
			RoomElement nextElement = (RoomElement) board.getItem(nextPos);
			if (nextElement != null)
			{
				return true;
			}
			
		}
		else
		{
			return true;
		}
		
		return false;
	}
	
	private void fallToNextLevel()
	{
		MainLogic.updateFallPreview();
		writeNullAtPos();
		Vector2 currentPos = board.toNearestBoardPoint(this.getPos().add(new Vector2(board.getTileSize()/2,board.getTileSize()/2)));
		Vector2 nextPos = currentPos.add(new Vector2(0, board.getTileSize())).add(this.getNode().getParent().getAbsolutePos());
		movement.addPoint(nextPos);
		
		for (RoomElement element : roomElements)
		{
			board.setItem(element.getCenter().add(new Vector2(0, board.getTileSize())), element);
		}
		
		finished = false;		
	}
	
	private void land()
	{
		
		for (RoomElement element : roomElements)
		{
			Vector2 boardPoint = element.getCenter().add(new Vector2(0, board.getTileSize()));
			if (board.getItem(boardPoint) == element)
			{
				board.setItem(boardPoint, null);
			}
		}
		
		lastTranslation = this.getAbsolutePos();
		Vector2 currentPos = board.toNearestBoardPoint(this.getPos());
		currentPos.x = this.getPos().x;
		this.setPos(currentPos);
		if (!checkNextCollision())
		{
			Vector2 nextPos = currentPos.add(new Vector2(0, board.getTileSize()));
			this.setPos(nextPos);
		}
		
		for (RoomElement element : roomElements)
		{
			Vector2 boardPoint = element.getCenter().add(new Vector2(0, board.getTileSize()));
			if (board.getItem(boardPoint) == element)
			{
				board.setItem(boardPoint, null);
			}
		}
		
		lastTranslation = lastTranslation.vectorTo(this.getAbsolutePos());
		
		if (previewNode != null)
		{
			previewNode.detach();
			previewNode = null;
			for (Rectangle rect : previewElements)
			{
				SceneManager.RENDER_THREAD.removeRenderable(rect);
			}
			previewElements.clear();
		}
		
		this.makePartOfDungeon();
		
		this.setChanged();
		this.notifyObservers();	
	}
	
	public void makePartOfDungeon()
	{
		finished = true;
		firstFall = false;
		movement.setSpeed(fallSpeed * SPEED_UP);
		this.insertIntoBoard();
	}
	
	public Vector2 getLastTranslation()
	{
		return lastTranslation;
	}
	
	public boolean isFalling()
	{
		return !finished;
	}
	
	public boolean isMoving()
	{
		return !movement.isFinished();
	}
	
	public void writeNullAtPos()
	{
		for (RoomElement element : roomElements)
		{
			board.setItem(element.getCenter(), null);
		}
	}
	private boolean checkNextCollision()
	{
		for (RoomElement element : roomElements)
		{
			if (this.isElementBelow(element.getCenter()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean checkCollision()
	{
		for (RoomElement element : roomElements)
		{
			Vector2 elementCenter = element.getCenter();
			Coord2 nextPos = new Coord2((int)(elementCenter.x / board.getTileSize()), (int)(elementCenter.y /board.getTileSize()));
			if (board.checkBounds(nextPos.x, nextPos.y))
			{
				RoomElement nextElement = (RoomElement) board.getItem(nextPos);
				if (nextElement != null)
				{
					return true;
				}
				
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean checkLowCollision()
	{
		for (RoomElement element : roomElements)
		{
			Vector2 elementCenter = element.getCenter().add(new Vector2(0, element.getHeight()*0.5f));
			Coord2 nextPos = new Coord2((int)(elementCenter.x / board.getTileSize()), (int)(elementCenter.y /board.getTileSize()));
			if (board.checkBounds(nextPos.x, nextPos.y))
			{
				RoomElement nextElement = (RoomElement) board.getItem(nextPos);
				if (nextElement != null)
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}
	
	public void insertIntoBoard()
	{
		for (RoomElement element : roomElements)
		{
			board.setItem(element.getCenter(), element);
		}
	}
	
	public Node getNode()
	{
		return node;
	}
	
	public boolean isFinished()
	{
		return (this.countRoomElements() == 0);
	}

	public Vector2 getMin()
	{
		return Algorithm.getMin(roomElements);
	}
	
	@Override
	public boolean onTouchDown(Vector2 point) {
		
		if (point.y < node.getParent().getAbsolutePos().y || point.y > node.getParent().getAbsolutePos().y + board.getHeight() * board.getTileSize())
		{
			movement.setSpeed(fallSpeed * SPEED_UP);
		}
		
		if (touchTime < TIME_MIRROR && touchTime >= 0)
		{
			if (point.y < node.getParent().getAbsolutePos().y || point.y > node.getParent().getAbsolutePos().y + board.getHeight() * board.getTileSize())
			{
				
			}
			else
			{
				if (movement.getSpeed() == fallSpeed)
				{
					updatePos(point);
					rotate = false;
				}
			}
			MainLogic.updateFallPreview();
		}
		
		touchTime = 0;
		return false;
	}

	@Override
	public boolean onTouchUp(Vector2 point) {
		
		if (touchTime < TIME_MIRROR && touchTime >= 0 && !moved)
		{
			
			if (node.getParent() != null) {
			
				if (point.y < node.getParent().getAbsolutePos().y || point.y > node.getParent().getAbsolutePos().y + board.getHeight() * board.getTileSize())
				{
					
				}
				else
				{
					if (movement.getSpeed() == fallSpeed)
					{
						checkRotate(point);
					}
				}
				MainLogic.updateFallPreview();
			
			}
		}
		
		
		movement.setSpeed(fallSpeed);
		moved = false;
		return false;
	}

	@Override
	public boolean onTouchMove(Vector2 point) {
		return false;
	}
	
	public boolean overlaps()
	{
		for (RoomElement element : roomElements)
		{
			if (board.getItem(element.getCenter()) != null) return true;
		}
		return false;
	}
	
	public void updatePos(Vector2 point)
	{
		
		boolean userRotates = checkRotate(point);
		float max = Algorithm.getMax((Collection<RoomElement>)roomElements).x;
		float min = Algorithm.getMin((Collection<RoomElement>)roomElements).x;
		
		boolean outsideRoom;
		
		if (max - min < board.getTileSize() * 2)
		{
			outsideRoom = (point.x < min - board.getTileSize() / 2 || point.x > max + board.getTileSize() / 2);
		}
		else
		{
			outsideRoom = (point.x < min || point.x > max);
		}
		
		if (!userRotates && outsideRoom)
		{
			if (point.x < this.getAbsolutePos().x)
			{
				userMove = Direction.RIGHT;
			}
			else
			{
				userMove = Direction.LEFT;
			}
			
			moved = true;
			
		}
		
	}
	
	public float getSpeed()
	{
		return fallSpeed;
	}
	
	public void setSpeed(float speed)
	{
		fallSpeed = speed;
		movement.setSpeed(speed);
	}	

	
	private boolean checkRotate(Vector2 point)
	{
		if (this.testPointInRoom(point))
		{
			rotate = true;
			return true;
		}
		return false;
	}
	
	public boolean testPointInRoom(Vector2 point)
	{
		float max = Algorithm.getMax((Collection<RoomElement>)roomElements).x;
		float min = Algorithm.getMin((Collection<RoomElement>)roomElements).x;
		
		if (max - min < board.getTileSize() * 2)
		{
			return !(point.x < min - board.getTileSize() / 2 || point.x > max + board.getTileSize() / 2);
		}
		else
		{
			return !(point.x < min || point.x > max);
		}
		
	}

	public void cleared()
	{
		this.cleared = true;
		for (RoomElement element : roomElements)
		{
			element.cleared();
		}
	}
	
	public boolean isCleared()
	{
		return cleared;
	}
	
	public void leave(GameObject object)
	{
		this.deleteObserver(object);
		content.remove(object);
	
		if (object instanceof Hero)
		{
			containsHero = false;
		}
		
		
	
	}
	
	public void enter(GameObject object)
	{
		this.addObserver(object);
		content.add(object);
		if (object instanceof Hero)
		{
			containsHero = true;
			visits.visit();
		}
		
		
		debugVisitCounter.setText(visits.getVisits()+"");
	}
	
	public boolean hasHero()
	{
		return containsHero;
	}
	
	public int getVisits()
	{
		return visits.getVisits();
	}
	
	public int countContent()
	{
		int res = 0;
		for (GameObject object : content)
		{
			if (object instanceof StaticContent)
			{
				if (((StaticContent) object).countable())
				{
						res++;
				}
			}
		}
		return res;
	}
	
	public int countTotalContent()
	{
		return content.size();
	}
	
	public GameObject getContent(int index)
	{
		return content.get(index);
	}
	
	@Override
	public boolean isEnabled() {
		return !finished;
	}
	
	public ArrayList<Room> getAdjacentRooms(boolean heroRelevant)
	{
		ArrayList<Room> rooms = new ArrayList<Room>();
		
		Hero hero = null;
		if (heroRelevant)
		{
			moveGraph = graph.clone();
			if (this.hasHero())
			{
				for (GameObject object : content)
				{
					if (object instanceof Hero)
					{
						hero = (Hero) object;
						moveGraph.distancesTo(moveGraph.getVertex(hero.getCurrentElement()));
						break;
					}
				}
			}
		}
		
		
		for (RoomElement other : roomElements)
		{
			if (other.justLanded && (!heroRelevant || !this.containsHero || moveGraph.getVertex(other).getParent() != null || (hero != null && other == hero.getCurrentElement())))
			{
				Direction[] directions = Direction.values();
				Coord2 testCoord = board.getBoardCoord(other.getCenter());
				for (Direction direction : directions)
				{
					if (other.isOpen(direction))
					{
						Coord2 coord = Direction.translatePoint(direction, testCoord, 1);
						if (board.checkBounds(coord.x, coord.y))
						{
							RoomElement element = (RoomElement) board.getItem(coord);
							if (element != null && element.justLanded && element.isOpen(Direction.getOppositeDirection(direction)))
							{
								Room room = element.getRoom();
								if (room != this && !room.isFalling())
								{
									if (!rooms.contains(room))
									{
										rooms.add(room);
										if (heroRelevant)
										{
											Graph<RoomElement> clone = room.graph.clone();
											Vector2 vectorTo = this.getPos().vectorTo( room.getPos());
											clone.translate(vectorTo);
											moveGraph.addAll(clone);
										}
									}
									if (heroRelevant)
									{
										moveGraph.addEdge(element, other);
									}	
								}
							}
						}
					}
				}
			}
		}
		
		return rooms;
	}
	
	public void createPathToElement(Movement movement, RoomElement current, RoomElement target)
	{
		movement.clearPath();
		if (moveGraph == null) moveGraph = graph.clone();
		
		Vertex<RoomElement> currentVertex = moveGraph.getVertex(current);
		moveGraph.distancesTo(currentVertex);
		Vertex<RoomElement> targetVertex = moveGraph.getVertex(target);
		List<Vector2> path = new ArrayList<Vector2>();
		
		Set<Vertex<RoomElement>> vertices = new HashSet<Vertex<RoomElement>>();
		
		if (targetVertex != null)
		{
			while (targetVertex.getParent() != null && !vertices.contains(targetVertex.getParent()))
			{
				RoomElement element = targetVertex.getContent();
				path.add(0, element.getAbsolutePos());
				vertices.add(targetVertex);
				targetVertex = targetVertex.getParent();
			}
		}
		
		for (Vector2 point : path) {
			movement.addPoint(point);
		}
		
		moveGraph = null;
	
	}
	
	public void finishConstruction()
	{
		for (RoomElement element : roomElements)
		{
			element.finishConstruction();
			Vertex<RoomElement> v1 = new Vertex<RoomElement>(element, element.getPos().x + element.getHeight()/2, element.getPos().y + element.getWidth()/2);
			graph.addVertex(v1);
		}
		
		for (int i = 0; i < graph.countVertices(); i++)
		{
			Vertex<RoomElement> v1 = graph.getVertex(i);
			RoomElement element1 = v1.getContent();
			for (int j = 0; j < graph.countVertices(); j++)
			{
				Vertex<RoomElement> v2 = graph.getVertex(j);
				RoomElement element2 = v2.getContent();
				if (element1 != element2)
				{
					if (this.elementsAdjacent(element1, element2))
					{
						graph.addEdge(v1, v2);
					}
				}
			}
		}
		/*
		if (debugGraph != null) SceneManager.RENDER_THREAD.removeRenderable(debugGraph);
		debugGraph = new RenderGraph<RoomElement>(Color.YELLOW, Color.RED, graph, node);
		debugGraph.setPriority(10000);
		SceneManager.RENDER_THREAD.addRenderable(debugGraph);
		*/
	}

	
	public ArrayList<RoomElement> isAdjacent(RoomElement other, Coord2 coord)
	{
		
		ArrayList<RoomElement> adjacents = new ArrayList<RoomElement>();
		for (RoomElement element : roomElements)
		{
			Coord2 coordElement = board.getBoardCoord(element.getCenter());
			if (coordElement.x == coord.x + 1 && coordElement.y == coord.y && element.isOpen(Direction.LEFT) && other.isOpen(Direction.RIGHT)) adjacents.add(element);
			if (coordElement.x == coord.x - 1 && coordElement.y == coord.y && element.isOpen(Direction.RIGHT) && other.isOpen(Direction.LEFT)) adjacents.add(element);
			if (coordElement.x == coord.x && coordElement.y == coord.y + 1 && element.isOpen(Direction.UP) && other.isOpen(Direction.DOWN)) adjacents.add(element);
			if (coordElement.x == coord.x && coordElement.y == coord.y - 1 && element.isOpen(Direction.DOWN) && other.isOpen(Direction.UP)) adjacents.add(element);
		}
		return adjacents;
	}
	
	public boolean elementsAdjacent(RoomElement element1, RoomElement element2)
	{
		if (element1.getNeighbour(new Vector2(element1.getWidth(), 0)) == element2) return true;	
		if (element1.getNeighbour(new Vector2(-element1.getWidth(), 0)) == element2) return true;	
		if (element1.getNeighbour(new Vector2(0, element1.getHeight())) == element2) return true;	
		if (element1.getNeighbour(new Vector2(0, -element1.getHeight())) == element2) return true;	
		return false;
	}
	
	public RoomElement getRandomRoomElement()
	{
		return roomElements.get((int)(Math.random() * roomElements.size()));
	}
	
	public RoomElement getRandomFreeRoomElement()
	{
		if (this.content.size() == roomElements.size()) {
			return null;
		}
		
		int iterations = 0;
		nextElement: while (iterations < MAX_ITERATIONS)
		{
			iterations++;
			
			RoomElement element = this.getRandomRoomElement();
			for (GameObject object : content)
			{
				if (object instanceof StaticContent)
				{
					StaticContent staticContent = (StaticContent) object;
					iterations++;
					if (staticContent.getRoomElement() == element) continue nextElement;
				}
			}
			
			return element;
		}
		return null;
	}
	
	public void destroy()
	{
		
		if (previewNode != null)
		{
			previewNode.detach();
			previewNode = null;
			for (Rectangle rect : previewElements)
			{
				SceneManager.RENDER_THREAD.removeRenderable(rect);
			}
		}
		
		this.writeNullAtPos();
		
		for (RoomElement element : roomElements)
		{
			element.destroy();
			element.deleteObservers();
		}
		
		while (content.size() != 0)
		{
			GameObject element = content.firstElement();
			element.deleteObservers();
			if (element instanceof StaticContent){
				((StaticContent) element).remove();
			}
			else
			{
				content.remove(element);
			}
		}
		
		node.detach();
		
		
		
	}

	@Override
	public long getPriority() {
		return 0;
	}
	
	public void createFallPreview()
	{
		List<Vector2> centers = new ArrayList<Vector2>();
		
		if (previewNode == null)
		{
			previewElements = new ArrayList<Rectangle>();
			previewNode = node.getParent().createChild();
		}
		else
		{
			for (Rectangle rect : previewElements)
			{
				SceneManager.RENDER_THREAD.removeRenderable(rect);
			}
		}
			
			
		previewNode.setPos(board.toNearestBoardPoint(node.getPos().add(new Vector2(board.getTileSize()*0.5f, board.getTileSize()*0.5f))));
		for (RoomElement roomElement : roomElements)
		{
			Node elementNode = previewNode.createChild(roomElement.getNode().getPos().add(new Vector2(board.getTileSize()*0.25f, board.getTileSize()*0.25f)));
			Rectangle rect = SceneManager.createRectangle(board.getTileSize()*0.5f, board.getTileSize()*0.5f, Color.GRAY, elementNode);
			rect.setPriority(-5);
			rect.getPaint().setStyle(Style.STROKE);
			centers.add(roomElement.getCenter());
			previewElements.add(rect);
		}
		
		int iterations = 0;
		falling: while(iterations < MAX_ITERATIONS)
		{
			for (Vector2 center : centers)
			{
				if (this.isElementBelow(center))
				{
					break falling;
				}
			}
			
			previewNode.translate(new Vector2(0, board.getTileSize()));
		
			for (Vector2 center : centers)
			{
				center.y += board.getTileSize();
			}
			
			iterations++;
		
		}
	}
	
}
