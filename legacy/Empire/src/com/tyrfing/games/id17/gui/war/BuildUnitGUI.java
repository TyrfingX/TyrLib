package com.tyrfing.games.id17.gui.war;

import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.MailboxGUI;
import com.tyrfing.games.id17.gui.mails.UnitMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class BuildUnitGUI extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.x, ScaleDirection.X, 2);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(ArmyBuilderGUI.BUILD_UNITS_SIZE.y / 3, ScaleDirection.Y, 0);
	
	public static final ScaledVector2 ICON_POS = new ScaledVector2(0.015f, 0.02f, 0);
	public static final ScaledVector2 ICON_SIZE = new ScaledVector2(0.03f, 0.04f, 0);
	
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.05f, 0.02f, 0);
	
	public static final ScaledVector1 COST_LABEL_X = new ScaledVector1(SIZE_X.x * 0.6f, ScaleDirection.X, 2);
	public static final ScaledVector1 COST_ICON_POS_X = new ScaledVector1(SIZE_X.x * 0.825f, ScaleDirection.X, 2);
	
	public static final ScaledVector1 COST_LABEL_Y = new ScaledVector1(NAME_LABEL.y, ScaleDirection.Y, 0);
	public static final ScaledVector1 COST_ICON_POS_Y = new ScaledVector1(NAME_LABEL.y, ScaleDirection.Y, 0);
	
	public static final ScaledVector2 COST_ICON_SIZE= new ScaledVector2(ICON_SIZE.x, ICON_SIZE.y, 0);
	
	private Army army;
	public final String name;
	private int pos;
	private FormationWindow formationWindow;
	private ArmyBuilderGUI gui;
	private UnitType type;
	
	public BuildUnitGUI(String name, ArmyBuilderGUI gui) {
		super("BUILD_GUI" +  "/" + name, new Vector2(SIZE_X.get(), SIZE_Y.get()), "");
	
		this.name = name;
		this.gui = gui;
		
		type = UnitType.valueOf(name);
		
		ImageBox unitIcon = (ImageBox) WindowManager.getInstance().createImageBox("BUILD_GUI/" + name + "/UNIT_ICON", ICON_POS, "UNIT_ICONS", name, ICON_SIZE);
		unitIcon.setInheritsAlpha(true);
		addChild(unitIcon);
		
		Label nameLabel = (Label) WindowManager.getInstance().createLabel("BUILD_GUI/" + name + "/NAME", NAME_LABEL, name);
		nameLabel.setColor(Color.BLACK);
		nameLabel.setInheritsAlpha(true);
		addChild(nameLabel);
		
		float cost = UnitType.UNIT_STATS.get(type).getStat(UnitType.BASE_COST);
		Label costLabel = (Label) WindowManager.getInstance().createLabel("BUILD_GUI/" + name + "/COST", new Vector2(COST_LABEL_X.get(), COST_LABEL_Y.get()), Integer.toString((int)cost));
		costLabel.setColor(Color.BLACK);
		costLabel.setInheritsAlpha(true);
		addChild(costLabel);
		
		ImageBox costIcon = (ImageBox) WindowManager.getInstance().createImageBox("BUILD_GUI/" + name + "/UNIT_COST_ICON", new Vector2(COST_ICON_POS_X.get(), COST_ICON_POS_Y.get()), "MAIN_GUI", "GOLD_ICON", COST_ICON_SIZE);
		costIcon.setInheritsAlpha(true);
		addChild(costIcon);
		
		this.addTextTooltip("");
		this.setSizeRelaxation(new Vector2(1f, 1));
		
	}
	
	public void setInfo(Army army, Barony barony, FormationWindow formationWindow) {
		this.army = army;
		this.formationWindow = formationWindow;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	protected void onClick() {
		
		MailboxGUI mailbox = World.getInstance().getMainGUI().mailboxGUI;
		
		String prefix = army.toString() + "/" + pos;
		String identity = prefix + "/" + name;
		if (mailbox.isIdentityPrefixShown(prefix)) {
			if (mailbox.isIdentityPrefixShown(identity)) return;
		}

		mailbox.removeMailByPrefix(prefix);
		
		
		HeaderedMail mail = new UnitMail(UnitType.valueOf(name), army, pos, formationWindow, gui);
		mail.setIdentity(identity);
		mail.addMainLabel(UnitType.getDesc(UnitType.valueOf(name), army));
		mailbox.addMail(mail, true);
		
		gui.unHighlightUnitTypes();
	}
	
	public static Mail createUnitMail(UnitType type) {
		MailboxGUI mailbox = World.getInstance().getMainGUI().mailboxGUI;
		HeaderedMail mail = new UnitMail(type);
		mail.addMainLabel(UnitType.getBaseDesc(type));
		mailbox.addMail(mail, true);
		return mail;
	}

	public void update() {
		if (army != null) {
			this.getTooltipLabel().setText(UnitType.getDesc(type, army));
		}
	}

}
