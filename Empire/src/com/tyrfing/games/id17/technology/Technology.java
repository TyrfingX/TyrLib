package com.tyrfing.games.id17.technology;

import java.io.Serializable;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.effects.IEffect;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.technology.TechMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.networking.TechnologyEvent;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.ScaledVector2;

public class Technology implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7455851107831132576L;
	
	public transient Technology[] pre;
	public transient int scienceMax;
	public transient int funds;
	public final int ID;
	public transient String name;
	
	public transient String researchDesc;
	public transient String discoverDesc;
	
	public transient IEffect[] researchEffects;
	public transient IEffect[] discoverEffects;
	
	public transient ScaledVector2 iconPos;
	
	private boolean discovered;
	
	public static int COUNT_TECHS = 0;
	
	public static final int INTELLECTUAL_DISCOVERY = 30;
	public static final float INTELLECTUAL_DISCOVERY_DECAY = -1f / World.DAYS_PER_YEAR;
	
	public static final int INTELLECTUAL_RESEARCH = 5;
	public static final float INTELLECTUAL_RESEARCH_DECAY = -1f / World.DAYS_PER_SEASON;
	
	public Technology(	String name, int scienceMax, int funds, Technology[] pre, 
						String researchDesc,String discoverDesc,
						IEffect[] researchEffects,
						IEffect[] discoverEffects, ScaledVector2 iconPos) {
		this.update(name, scienceMax, funds, pre, 
					researchDesc, discoverDesc, researchEffects, discoverEffects, iconPos);
		this.ID = COUNT_TECHS++;
		final Technology tech = this;
		LinkManager.getInstance().registerLink(new ILink() {
			@Override
			public void onCall() {
				Mail back = World.getInstance().getMainGUI().mailboxGUI.getCurrentMail();
				Mail mail = TechMail.createMail(tech, World.getInstance().getPlayerController().getHouse());
				if (back != null) {
					mail.setBackMail(back);
				}
			}
		}, name);
	}
	
	public void update(	String name, int scienceMax, int funds, Technology[] pre, 
			String researchDesc,String discoverDesc,
			IEffect[] researchEffects,
			IEffect[] discoverEffects, ScaledVector2 iconPos) {
		
		this.name = name;
		this.scienceMax = scienceMax;
		this.funds = funds;
		this.pre = pre;
		this.researchDesc = researchDesc;
		this.discoverDesc = discoverDesc;
		this.researchEffects = researchEffects;
		this.discoverEffects = discoverEffects;
		this.iconPos = iconPos;
	}
	
	public void onApply(House house, boolean discover) {
		house.research(this);
		
		for (int i = 0; i < researchEffects.length; ++i) {
			researchEffects[i].apply(house);
		}
		
		if (discover) {
			for (int i = 0; i < discoverEffects.length; ++i) {
				discoverEffects[i].apply(house);
			}
			
			discovered = true;
			house.discovered[ID] = true;
		}
	}
	
	public void onResarch(House house) { 
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TechnologyEvent(ID, house.id, -1));
		}
	
		house.addStatModifier(new VaryingStatModifier("Research", House.PROGRESS, house, -1, INTELLECTUAL_RESEARCH, INTELLECTUAL_RESEARCH_DECAY, 0));
		house.addStatModifier(new VaryingStatModifier("Research", House.WEALTH,house, -1, INTELLECTUAL_RESEARCH, INTELLECTUAL_RESEARCH_DECAY, 0));
		
		
		house.research(this);
		
		for (int i = 0; i < researchEffects.length; ++i) {
			researchEffects[i].apply(house);
		}
		
		String text = "We have completed our research on\nthe technology " + this.name + ".";
		String title = "Research completed:\n" + getLinkedName();
		
		if (!discovered){
			onDiscover(house);
			text += "\nAs we are the first to\nuncover this technology, we\nobtain the bonus:\n" + discoverDesc;
			title = "New Technology discovered by\nus!";
		} 
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (house == World.getInstance().getPlayerController().getHouse() && EmpireFrameListener.state != GameState.SELECT) {
				HeaderedMail mail = new HeaderedMail(	title, 
														text, 
														house, 
														World.getInstance().getPlayerController().getHouse());
				mail.setIconName("Technology");
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
	}
	
	public void onSpread(House from, House house) { 
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TechnologyEvent(ID, house.id, from.id));
		}
		
		house.research(this);
		
		for (int i = 0; i < researchEffects.length; ++i) {
			researchEffects[i].apply(house);
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (house == World.getInstance().getPlayerController().getHouse() && EmpireFrameListener.state != GameState.SELECT) {
				HeaderedMail mail = new HeaderedMail(	"Technology spread to us\n" + name, 
														"We have learned of the secrets\nbehind the technology " + getLinkedName() + ".\nThe technology spread from House\n" + from.getLinkedName() + ".", 
														from, 
														World.getInstance().getPlayerController().getHouse());
				mail.setIconName("Technology");
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
		
	}
	
	public void onDiscover(House house) { 
		for (int i = 0; i < discoverEffects.length; ++i) {
			discoverEffects[i].apply(house);
		}
		
		discovered = true;
		house.discovered[ID] = true;
		
		house.addStatModifier(new VaryingStatModifier("Discovery", House.PROGRESS, house, -1, INTELLECTUAL_DISCOVERY, INTELLECTUAL_DISCOVERY_DECAY, 0));
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (house != World.getInstance().getPlayerController().getHouse() && EmpireFrameListener.state != GameState.SELECT) {
				HeaderedMail mail = new HeaderedMail(	"New Technology discovered by\na different House!", 
														"House " + house.getLinkedName() + " has discovered\nthe new technology known as\n" + getLinkedName() + ".", 
														house, 
														World.getInstance().getPlayerController().getHouse());
				mail.setIconName("Technology");
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			} 
		}
	}
	
	public String getLinkedName() {
		return "<link=" +  name + ">" + name + "\\l<img TECH " + name + ">";
	}
}
