package tyrfing.games.id3.lib.rooms.content;


import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;

public class Heal extends StaticContent {

	public Heal() {
		this.entity = SceneManager.createImage(Ressources.getBitmap("heal"), node);
		this.entity.setPriority(10);
	}
	
	public String toString()
	{
		String res   = "heal\n";
		res			+= super.toString();
		return res;
	}

}
