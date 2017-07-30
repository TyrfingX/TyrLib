package tyrfing.games.id3.lib;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import tyrfing.common.files.FileReader;
import tyrfing.common.files.FileWriter;
import tyrfing.common.game.GameActivity;
import tyrfing.games.id3.lib.rooms.DoorColor;

public class BlockQuestActivity extends GameActivity {
	protected MainGame game;
	
	@Override
	public void go() {
		
		if (FileReader.fileExists(this, "Difficulty.bs"))
	    {
			String data = FileReader.readFile(this, "Difficulty.bs");
			GlobalSettings.difficulty = Difficulty.values()[Integer.valueOf(data)];
	    }
		else
		{
			GlobalSettings.difficulty = Difficulty.MEDIUM;
		}
		
		if (FileReader.fileExists(this, "DoorColor.bs"))
	    {
			String data = FileReader.readFile(this, "DoorColor.bs");
			GlobalSettings.doorColor = DoorColor.values()[Integer.valueOf(data)];
	    }
		else
		{
			GlobalSettings.doorColor = DoorColor.RED;
		}
		
		
		game = new MainGame(this);
		

	}
	
	  @Override
	  public void onDestroy() {
		game.kill();
		super.onDestroy();
		game = null;
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.settings, menu);
	      
	      if (FileReader.fileExists(this, "Difficulty.bs"))
	      {
	    	  String data = FileReader.readFile(this, "Difficulty.bs");
	    	  GlobalSettings.difficulty = Difficulty.values()[Integer.valueOf(data)];
	    	  MenuItem item = menu.getItem(0).getSubMenu().getItem(GlobalSettings.difficulty.ordinal());
	    	  
	    	  int id = item.getItemId(); 
	    	  
	    	  if (id == R.id.easy) {
	    		  item.setChecked(true);
	    	  }
	    	  else if (id == R.id.medium) {
	    		  item.setChecked(true);
	    	  }
	    	  else if (id == R.id.hard) {
	    		  item.setChecked(true);
	    	  }

	      }
	      else
	      {
	    	  GlobalSettings.difficulty = Difficulty.MEDIUM;

	    	  MenuItem item = menu.getItem(0).getSubMenu().getItem(GlobalSettings.difficulty.ordinal());
    		  item.setChecked(true);
	      }
	      
	      if (FileReader.fileExists(this, "DoorColor.bs"))
	      {
	    	  String data = FileReader.readFile(this, "DoorColor.bs");
	    	  GlobalSettings.doorColor = DoorColor.values()[Integer.valueOf(data)];
	    	  MenuItem item = menu.getItem(1).getSubMenu().getItem(GlobalSettings.doorColor.ordinal());
	    	  
	    	  int id = item.getItemId(); 
	    	  
	    	  if (id == R.id.red) {
	    		  item.setChecked(true);
	    	  }
	    	  else if (id == R.id.blue) {
	    		  item.setChecked(true);
	    	  }
	    	  else if (id == R.id.yellow) {
	    		  item.setChecked(true);
	    	  }

	      }
	      else
	      {
	    	  GlobalSettings.doorColor = DoorColor.RED;

	    	  MenuItem item = menu.getItem(1).getSubMenu().getItem(GlobalSettings.doorColor.ordinal());
    		  item.setChecked(true);
	      }
		  
	      return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		  
	      // Handle item selection
		  
		  int id = item.getItemId(); 
    	  
    	  if (id == R.id.easy) {
    		  item.setChecked(true);
        	  GlobalSettings.difficulty = Difficulty.EASY;
        	  FileWriter.writeFile(this, "Difficulty.bs", GlobalSettings.difficulty.ordinal()+"");
        	  return true;
    	  }
    	  else if (id == R.id.medium) {
    		  item.setChecked(true);
        	  GlobalSettings.difficulty = Difficulty.MEDIUM;
        	  FileWriter.writeFile(this, "Difficulty.bs", GlobalSettings.difficulty.ordinal()+"");
        	  return true;
    	  }
    	  else if (id == R.id.hard) {
    		  item.setChecked(true);
        	  GlobalSettings.difficulty = Difficulty.HARD;
        	  FileWriter.writeFile(this, "Difficulty.bs", GlobalSettings.difficulty.ordinal()+"");
              return true;
    	  }
    	  else if (id == R.id.red) {
    		  item.setChecked(true);
        	  GlobalSettings.doorColor = DoorColor.RED;
        	  FileWriter.writeFile(this, "DoorColor.bs", GlobalSettings.doorColor.ordinal()+"");
              return true;
    	  }
    	  else if (id == R.id.blue) {
    		  item.setChecked(true);
        	  GlobalSettings.doorColor = DoorColor.BLUE;
        	  FileWriter.writeFile(this, "DoorColor.bs", GlobalSettings.doorColor.ordinal()+"");
              return true;
    	  }
    	  else if (id == R.id.yellow) {
    		  item.setChecked(true);
        	  GlobalSettings.doorColor = DoorColor.YELLOW;
        	  FileWriter.writeFile(this, "DoorColor.bs", GlobalSettings.doorColor.ordinal()+"");
              return true;
    	  }
    	  else
    	  {
    		  return super.onOptionsItemSelected(item);
    	  }
	      
	  }
	  
}