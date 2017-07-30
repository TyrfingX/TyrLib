package tyrfing.games.id3.lib.rooms.content;



import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;

public class AttackUp extends StaticContent {

	
	public static final int DURATION = 10;
	public static final int POWER_UP = 50;
	
	
	public AttackUp() {
		this.entity = SceneManager.createImage(Ressources.getBitmap("attackUp"), node);
		this.entity.setPriority(10);
	}
	
	public String toString()
	{
		String res   = "attackUp\n";
		res			+= super.toString();
		return res;
	}
	
}
