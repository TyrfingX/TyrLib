package tyrfing.common.ui.widgets;

import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.WindowManager;

public class ConfirmMessageBox extends MessageBox {

	private Button confirm;
	
	public ConfirmMessageBox(String name, float x, float y, float w, float h,String text) {
		super(name, x, y, w, h, text);
		confirm = WindowManager.createButton(name + "/confirm", 0.25f*w, 0.7f*h, 0.5f*w, 0.2f*h, "Ok");
		frame.addChild(confirm);
	}
	
	public void addClickListener(ClickListener clickListener)
	{
		confirm.addClickListener(clickListener);
	}
	
}
