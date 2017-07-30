package tyrfing.common.renderables;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import android.graphics.Canvas;

public class Text extends Primitive {

	
	private String[] text;
	
	public Text(String text, int color) {
		super(color);
		this.text = text.split("\n");
		paint.setTextSize(TargetMetrics.fontSize);
		paint.setAntiAlias(true);
	}
	
	public Text(String text, int color, Node parent) {
		super(color, parent);
		this.text = text.split("\n");
		paint.setTextSize(TargetMetrics.fontSize);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
	}
	
	
	public void setSize(int size)
	{
		paint.setTextSize(size);
	}
	
	public void setText(String text){
		this.text = text.split("\n");
	}
	
	public String getText()
	{
		String res = "";
		for (String item : text)
		{
			res += item;
		}
		return res;
	}

	@Override
	public void onRender(Canvas target, float time) {
		super.onRender(target, time);
		float x = parent.getX() * (TargetMetrics.xdpi / 160);
		float y = parent.getY() * (TargetMetrics.ydpi / 160);
		
		float offset = 0;
		
		for (String item : text)
		{
			target.drawText(item, x, y + offset, paint);
			offset += paint.getTextSize();
		}
	}
	
	
	
}
