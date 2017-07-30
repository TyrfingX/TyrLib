package tyrfing.games.BlockQuest.rooms.content;


import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;

public class StairsDown extends StaticContent {
	
	public StairsDown() {
		this.entity = SceneManager.createImage(Ressources.getBitmap("stairsdown"), node);
		this.entity.setPriority(10);
	}
	
	
	public String toString()
	{
		String res   = "stairsDown\n";
		res			+= super.toString();
		return res;
	}
}
