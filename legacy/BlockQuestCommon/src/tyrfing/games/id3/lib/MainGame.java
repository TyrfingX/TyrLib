package tyrfing.games.id3.lib;

import android.content.Context;
import tyrfing.common.factory.FactoryManager;
import tyrfing.common.game.Game2D;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.WindowManager;


public class MainGame extends Game2D {
	
	
	public static boolean ADFREE = false;
	
	public MainGame(Context context) {
		super(context);
		WindowManager.init(new PaperSkin());
	}

	@Override
	public void go() {

		float tileSize = MainLogic.calcTileSize();		
		Ressources.loadRes("bg", R.drawable.bg,  new Vector2(TargetMetrics.width, TargetMetrics.height));
		Ressources.loadRes("roomBg", R.drawable.roombg, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("roomBgRed", R.drawable.roombgred, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("roomBgDarkRed", R.drawable.roombgdarkred, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("roomBgViolet", R.drawable.roombgviolet, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("cursedBg", R.drawable.cursedbg, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("curseEffect", R.drawable.curse, new Vector2((tileSize+1)*14, (tileSize+1)*2));
		Ressources.loadRes("monster1", R.drawable.monster1, new Vector2(tileSize, tileSize));
		Ressources.loadRes("monster2", R.drawable.monster2, new Vector2(tileSize, tileSize));
		Ressources.loadRes("demon", R.drawable.demon, new Vector2(tileSize, tileSize));		
		Ressources.loadRes("grave", R.drawable.grave);
		Ressources.loadRes("heal", R.drawable.heal, new Vector2(tileSize, tileSize));
		Ressources.loadRes("heart", R.drawable.heart);
		Ressources.loadRes("attackUp", R.drawable.attackup, new Vector2(tileSize, tileSize));	
		Ressources.loadRes("sword", R.drawable.sword);
		Ressources.loadRes("shield", R.drawable.shield);
		Ressources.loadRes("money", R.drawable.money, new Vector2(tileSize, tileSize));
		Ressources.loadRes("hero", R.drawable.hero);
		Ressources.loadRes("stairsup", R.drawable.stairsup, new Vector2(tileSize, tileSize));
		Ressources.loadRes("stairsdown", R.drawable.stairsdown, new Vector2(tileSize, tileSize));
		Ressources.loadRes("potion", R.drawable.potion);
		Ressources.loadRes("roomBgBlue", R.drawable.roomblue, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("roomBgDarkBlue", R.drawable.roomdarkblue, new Vector2(tileSize+1, tileSize+1));
		Ressources.loadRes("Mirror", R.drawable.mirror, new Vector2(tileSize*2, tileSize*2));
		Ressources.loadRes("Rotate", R.drawable.rotate, new Vector2(tileSize*2, tileSize*2));
		
		SceneManager.RENDER_THREAD.setBg(Ressources.getBitmap("bg"));
		
		MainMenu mainMenu = new MainMenu();
		mainMenu.display();
		
		FactoryManager.init();
		
		//SceneManager.RENDER_THREAD.addFrameListener(new FPSCounter(new Node(30,30)));				


	}
	
	public void kill()
	{
		
		try {
			//MainLogic.save();
		} catch (Exception e) {
			
		}
		/*
		 WindowManager.destroyAllWindows();
		 
		if (SceneManager.RENDER_THREAD != null)
		{
			SceneManager.RENDER_THREAD.clear();
		}
		Ressources.free();
		*/
	}

}
