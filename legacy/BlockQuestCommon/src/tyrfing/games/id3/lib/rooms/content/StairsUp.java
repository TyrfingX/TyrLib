package tyrfing.games.id3.lib.rooms.content;



import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;

public class StairsUp extends StaticContent {

	public StairsUp() {
		this.entity = SceneManager.createImage(Ressources.getBitmap("stairsup"), node);
		this.entity.setPriority(10);
	}
	
	public boolean countable()
	{
		return false;
	}

	public String toString()
	{
		String res   = "stairsUp\n";
		res			+= super.toString();
		return res;
	}
}
