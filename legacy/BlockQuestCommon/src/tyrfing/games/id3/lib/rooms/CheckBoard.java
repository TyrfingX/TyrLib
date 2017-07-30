package tyrfing.games.id3.lib.rooms;



import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.rooms.content.Hero;

public class CheckBoard {
	
	public static int BASE_MONEY_PER_ROW = 10;
	private Board board;
	
	public CheckBoard(Board board)
	{
		this.board = board;
	}
	
	public int clearRows(Hero hero, State state)
	{
		int res = 0;
		nextRow: for (int y = 0; y < board.getHeight(); y++)
		{
			int extra = 0;
			for (int x = 0; x < board.getWidth(); x++)
			{
				RoomElement element = (RoomElement) board.getItem(x, y);
				if (element == null) continue nextRow;
				Room room = element.getRoom();
				if (!room.isCleared()  || room.hasHero() || !element.justLanded 
					|| room.isFalling() || hero.getTargetRoom() == room 
					|| room.getState() == RoomState.CURSED) continue nextRow;
				if (element.getRoom().getState() == RoomState.MADEBLUE || element.getRoom().getState() == RoomState.BLUE)
				{
					extra++;
				}
			}	
			
			res++;
			
			int addedMoney  = res * CheckBoard.BASE_MONEY_PER_ROW + ((int)(2*extra*CheckBoard.BASE_MONEY_PER_ROW/(float)board.getWidth()));
			state.character.setMoney(state.character.getMoney() + addedMoney);
			
			RoomElement center = (RoomElement) board.getItem((int)(board.getWidth()/2), y);
			Node node = new Node(center.getAbsolutePos());
			Image money = SceneManager.createImage(Ressources.getBitmap("money"), node);
			money.fadeOut(new Vector2(0,-20), 10);
			money.setPriority(10000);
			Text text = SceneManager.createText("+" + addedMoney, Color.YELLOW, node.createChild(-center.getSize().x*0.75f, center.getSize().y/2));
			text.fadeOut(new Vector2(0,0),10);
			text.setPriority(10000);
			
			for (int x = 0; x < board.getWidth(); x++)
			{
				RoomElement element = (RoomElement) board.getItem(x, y);
				element.getRoom().removeElement(element);
				element.destroy();
				board.setItem(x, y, null);
			}
		}
		return res;
		
	}
	
	public void checkDoors()
	{
		for (int y = 0; y < board.getHeight(); y++)
		{
			for (int x = 0; x < board.getWidth(); x++)
			{
				RoomElement element = (RoomElement) board.getItem(x, y);
				if (element != null && !element.getRoom().isFalling() && element.justLanded)
				{
					RoomElement other;
					
					other = (RoomElement) board.getItem(x-1,y);
					if (other != null)
					{
						if (other.getRoom() != element.getRoom())
						{
							if (!other.getRoom().isFalling() && other.justLanded)
							{
								if (RoomElement.isDoorOpen(element, other, Direction.LEFT))
								{
									element.openDoor(Direction.LEFT);
									other.openDoor(Direction.RIGHT);
								}
							}
						}
						else
						{
							element.openDoor(Direction.LEFT);
							other.openDoor(Direction.RIGHT);
						}
					}
					else
					{
						element.closeDoor(Direction.LEFT);
					}

					other = (RoomElement) board.getItem(x+1,y);
					if (other != null)
					{
						if (other.getRoom() != element.getRoom())
						{
							if (!other.getRoom().isFalling() && other.justLanded)
							{
								if (RoomElement.isDoorOpen(element, other, Direction.RIGHT))
								{
									element.openDoor(Direction.RIGHT);
									other.openDoor(Direction.LEFT);
								}
							}
						}
						else
						{
							element.openDoor(Direction.RIGHT);
							other.openDoor(Direction.LEFT);
						}
					}
					else
					{
						element.closeDoor(Direction.RIGHT);
					}
					
					other = (RoomElement) board.getItem(x,y-1);
					if (other != null)
					{
						if (other.getRoom() != element.getRoom())
						{
							if (!other.getRoom().isFalling() && other.justLanded)
							{
								if (RoomElement.isDoorOpen(element, other, Direction.UP))
								{
									element.openDoor(Direction.UP);
									other.openDoor(Direction.DOWN);
								}
								else if (!element.isOpen(Direction.UP)  && other.isOpen(Direction.DOWN))
								{
									element.closeDoor(Direction.UP);
								}
							}
						}
						else
						{
							element.openDoor(Direction.UP);
							other.openDoor(Direction.DOWN);							
						}
					}
					else
					{
						element.closeDoor(Direction.UP);
					}
					
					other = (RoomElement) board.getItem(x,y+1);
					if (other != null)
					{
						if (other.getRoom() != element.getRoom())
						{
							if (!other.getRoom().isFalling() && other.justLanded)
							{
								if (RoomElement.isDoorOpen(element, other, Direction.DOWN))
								{
									element.openDoor(Direction.DOWN);
									other.openDoor(Direction.UP);
								}
								else if (!element.isOpen(Direction.DOWN)  && other.isOpen(Direction.UP))
								{
									element.closeDoor(Direction.DOWN);
								}
							}
						}
						else
						{
							element.openDoor(Direction.DOWN);
							other.openDoor(Direction.UP);							
						}
					}
					else
					{
						element.closeDoor(Direction.DOWN);
					}
					
				}
			
			}	
		}
	}
	
	public void destroyRoomsOnBoard()
	{
		for (int y = 0; y < board.getHeight(); y++)
		{
			for (int x = 0; x < board.getWidth(); x++)
			{
				RoomElement element = (RoomElement) board.getItem(x, y);
				if (element != null && element.getRoom() != null)
				{
					Room room = element.getRoom();
					room.deleteObservers();
					room.destroy();
				}
				
				board.setItem(x, y, null);
			}
		}
			
	}
	
	public List<Room> getRoomsOnBoard()
	{
		ArrayList<Room> rooms = new ArrayList<Room>();
		
		for (int y = 0; y < board.getHeight(); y++)
		{
			for (int x = 0; x < board.getWidth(); x++)
			{
				RoomElement element = (RoomElement) board.getItem(x, y);
				if (element != null)
				{
					Room room = element.getRoom();
					if (!rooms.contains(room))
					{
						rooms.add(room);
					}
				}
			}
		}		
		return rooms;
	}
	
}
