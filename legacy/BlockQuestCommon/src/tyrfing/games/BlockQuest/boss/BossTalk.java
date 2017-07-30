package tyrfing.games.BlockQuest.boss;

import java.util.ArrayList;
import java.util.List;

import tyrfing.common.game.objects.IUpdateable;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfingx.games.BlockQuest.lib.R;
import android.graphics.Bitmap;
import android.graphics.Color;

public class BossTalk implements IUpdateable {
	
	private Node node;
	private List<Image> imageList;
	private List<Text> textList;
	
	public BossTalk(Node node, float bubbleSize)
	{
		this.node = node;
		Ressources.loadRes("speechBubble1Small", R.drawable.speechbubble1, new Vector2(bubbleSize, bubbleSize));
		Ressources.loadRes("speechBubble1Big", R.drawable.speechbubble1, new Vector2(bubbleSize*3/2, bubbleSize*3/2));
	
		imageList = new ArrayList<Image>();
		textList = new ArrayList<Text>();
	
	}
	
	public void clean()
	{
		Ressources.freeRes("speechBubble1Small");
		Ressources.freeRes("speechBubble1Big");
		
		for (int i = 0; i < imageList.size(); ++i)
		{
			SceneManager.RENDER_THREAD.removeRenderable(imageList.get(i));
			SceneManager.RENDER_THREAD.removeRenderable(textList.get(i));
		}
	}
	
	public void addSpeech(String speech, float x)
	{
		
		Bitmap bitmap = null;
		if (speech.length() < 5)
		{
			bitmap = Ressources.getBitmap("speechBubble1Small");
		}
		else
		{
			bitmap = Ressources.getBitmap("speechBubble1Big");
		}
		
		Node speechNode = node.createChild(x, 0);
		
		Image image = SceneManager.createImage(bitmap, speechNode);
		Text text = SceneManager.createText(speech, Color.BLACK, speechNode.createChild(bitmap.getWidth()*0.2f, bitmap.getHeight()/2));
		
		image.fadeOut(new Vector2((float)Math.random()*4, (float)Math.random()*4), 6f);
		text.fadeOut(new Vector2((float)Math.random()*4, (float)Math.random()*4), 6f);
	
		imageList.add(image);
		textList.add(text);
	}

	@Override
	public void onUpdate(float time) {
		for (int i = 0; i < imageList.size(); ++i)
		{
			if (!imageList.get(i).getVisible())
			{
				SceneManager.RENDER_THREAD.removeRenderable(imageList.get(i));
				SceneManager.RENDER_THREAD.removeRenderable(textList.get(i));
				
				imageList.remove(i);
				textList.remove(i);
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
