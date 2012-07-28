package tyrfing.common.game.objects;

import android.graphics.Color;
import tyrfing.common.render.SceneManager;
import tyrfing.common.renderables.Rectangle;
import tyrfing.common.struct.Node;

public class Hp {
	
	private int hp;
	private int maxHp;
	private Rectangle bar;
	private float oldWidth;
	
	public Hp(int maxHp, float barWidth, float barHeight, Node node) {
		bar = SceneManager.createRectangle(barWidth, barHeight, Color.GREEN, node);
		bar.setPriority(10000);
		this.maxHp = maxHp;
		this.hp = maxHp;
		this.oldWidth = barWidth;
	}
	
	public Hp(int maxHp, Rectangle bar)
	{
		this.hp = maxHp;
		this.maxHp = maxHp;
		this.bar = bar;
		this.oldWidth = bar.getWidth();
		bar.setColor(Color.GREEN);
	}
	
	public boolean isDead()
	{
		return hp <= 0;
	}
	
	public int getHp()
	{
		return hp;
	}
	
	public int getMaxHp()
	{
		return maxHp;
	}
	
	public void inflictDamage(int dmg)
	{
		hp -= dmg;
		
		if (hp <= 0)
		{
			hp = 0;
			bar.setVisible(false);
		}
		
		float p = (float) hp / maxHp;
		bar.setWidth(oldWidth * p);
		
		if (p < 0.7) bar.setColor(Color.YELLOW);
		if (p < 0.3) bar.setColor(Color.RED);
		
	}
	
	public Rectangle getBar()
	{
		return bar;
	}
	
}
