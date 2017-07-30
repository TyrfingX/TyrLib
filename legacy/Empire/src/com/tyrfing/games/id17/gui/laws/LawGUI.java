package com.tyrfing.games.id17.gui.laws;

import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.Law;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class LawGUI {

	public final Law law;
	public final static ScaledVector2 BUTTON_SIZE = new ScaledVector2(HeaderedMail.ACCEPT_SIZE.x/1.25f, HeaderedMail.ACCEPT_SIZE.y/1.05f,2);
	public final PaperButton[] buttons;
	
	private House displayed;
	
	public final Label label;
	
	public LawGUI(final Law law, ScaledVector2 pos, Window parent) {
		this.law = law;
		
		String name = "LAW/" +  law.name;
		
		label = (Label) WindowManager.getInstance().createLabel(name + "/LABEL", pos, law.name);
		label.setColor(Color.BLACK.copy());
		parent.addChild(label);
		
		buttons = new PaperButton[law.options.length];
		
		Vector2 absPos = new ScaledVector2(pos.x, pos.y + HoldingGUI.WINDOW_SIZE.y * 0.15f, 2).get();
		for (int i = 0; i < law.options.length; ++i) {
			final int index = i;
			final PaperButton button = new PaperButton(name + "/" + law.options[i].optionName + "/PAPER_BUTTON",
														absPos, BUTTON_SIZE.get(0), HeaderedMail.ACCEPT_BORDER_SIZE.get(0).x, law.options[i].optionName);
			parent.addChild(button);
			WindowManager.getInstance().addTextTooltip(button, law.options[i].tooltip);
			button.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					
					if (World.getInstance().getMainGUI().mailboxGUI != null) {
					
						if (displayed.getLawSetting(law.ID) != index) {
							
							HeaderedMail mail = new HeaderedMail(	"Change Law:\n" + law.name, 
																	"Change " +  law.name + " to " + law.options[index].optionName + "?\n\n" + 
																	law.desc, 
																	displayed, displayed) {
									@Override
									public void onAccept() {
										law.selectOption(index, displayed);
										remove();
										
										for (int i = 0; i < law.options.length; ++i) {
											if (i == displayed.getLawSetting(law.ID)) {
												buttons[i].highlight();
											} else {
												buttons[i].unhighlight();
											}
										}
										
										World.getInstance().mainGUI.houseGUI.lawGUI.showCategory(World.getInstance().mainGUI.houseGUI.lawGUI.currentCategory);
										
									}
							};
							mail.addAcceptButton();
							World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
						}
					}
				}
			});
			
			button.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					if (index == displayed.getLawSetting(law.ID)) {
						button.highlight();
					}
				}
			});
			
			buttons[i] = button;
			
			absPos.x += new ScaledVector1(BUTTON_SIZE.x, ScaleDirection.X, 0).get();
		}
	}
	
	public void show(House house) {
		this.displayed = house;
		
		for (int i = 0; i < law.options.length; ++i) {
			if (i < house.getLawSetting(law.ID) - 1 || i > house.getLawSetting(law.ID) + 1) {
				buttons[i].disable();
				buttons[i].setReceiveTouchEvents(true);
			} else if (!house.canPassLaws()) {
				buttons[i].disable();
				buttons[i].setReceiveTouchEvents(true);
			} else {
				buttons[i].enable();
				
				if (i == house.getLawSetting(law.ID)) {
					buttons[i].highlight();
				} else {
					buttons[i].unhighlight();
				}
			}
		}
		
	}
	
	public void hide() {
		for (int i = 0; i < law.options.length; ++i) {
			buttons[i].disable();
		}
	}
	
}
