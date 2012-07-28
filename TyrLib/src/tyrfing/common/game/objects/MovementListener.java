package tyrfing.common.game.objects;

public abstract class MovementListener {
	
	public MovementListener()
	{
		listening = true;
	}
	
	private boolean listening;
	public void setListening(boolean listening)
	{
		this.listening = listening;
	}
	
	public boolean isListening()
	{
		return listening;
	}
	
	public abstract void onFinishMovement();

}
