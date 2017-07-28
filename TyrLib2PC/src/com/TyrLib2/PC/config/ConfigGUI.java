package com.TyrLib2.PC.config;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import com.TyrLib2.PC.config.Config.ScreenState;
import com.tyrlib2.math.Vector2;

public class ConfigGUI extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7415266331530805769L;

	private CheckboxGroup group;
	private Choice sizes;
	private Config config;
	
	private OnStartGame startGame;
	
	public ConfigGUI(OnStartGame startGame) {
		
		this.startGame = startGame;
		
		config = new Config();
		config.screenState = ScreenState.FULLSCREEN;
		config.screenSize = new Vector2(1200, 675);
		
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            }
        });
        
        this.setTitle("Config");
        this.setResizable(false);
        this.setSize(200, 200);
        
        this.setLocationRelativeTo(null);
		
        this.setLayout(new GridLayout(4, 1));
        
        group = new CheckboxGroup();
        Checkbox chkFull = new Checkbox("Fullscreen", group, true);
        chkFull.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {             
               if (e.getStateChange()==1) {
            	   config.screenState = ScreenState.FULLSCREEN; 
               }
            }
         });
        
        final Checkbox chkOther = new Checkbox("", group, false);
        chkOther.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {             
               if (e.getStateChange()==1) {
            	   config.screenState = ScreenState.WINDOWED; 
               }
            }
         });
        
        group.setSelectedCheckbox(chkOther);
        config.screenState = ScreenState.WINDOWED;
        chkFull.disable();
        
        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        p.add(chkOther, BorderLayout.WEST);
        sizes = new Choice();
        sizes.addItem("1280x720");
        sizes.addItem("800x600");
        sizes.addItem("480x240");
        
        sizes.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {             
               if (e.getItem().equals("800x600")) {
            	   config.screenSize = new Vector2(800, 600); 
               } else if (e.getItem().equals("1280x720")) {
            	   config.screenSize = new Vector2(1280, 720); 
               } else {
            	   config.screenSize = new Vector2(480, 240); 
               }
            }
         });
        
        sizes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chkOther.setState(true);
                config.screenState = ScreenState.WINDOWED; 
            }

        });
        
        p.add(sizes, BorderLayout.CENTER);
        
        Button start = new Button("Start");
        
        this.add(chkFull);
        this.add(p);
        this.add(new Label(""));
        this.add(start);
        
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onStart();
            }
        });
	}
	
	public void onStart() {
		
		try {
			URL url = getClass().getResource("/");
			String path = url.getPath() + "config.xml";
			FileOutputStream fileOut = new FileOutputStream(path);
			PrintStream printStream = new PrintStream(fileOut);
			printStream.print("<Config>\n");
			printStream.print("\t<WindowMode  mode=\"" + config.screenState.toString() + "\"/>\n");
			if (config.screenState == ScreenState.WINDOWED) {
				printStream.print("\t<WindowSize  x=\"" + config.screenSize.x + "\" y=\"" + config.screenSize.y + "\"/>\n");
			}
			printStream.print("</Config>");
			printStream.close();
			fileOut.close();
		} catch (Exception e2) {
			
		}
		
		this.setVisible(false);
		startGame.startGame(config);
	}
	
}
