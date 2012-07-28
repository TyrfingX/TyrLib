package tyrfing.common.game.objects;

public interface IMovementListener {
	public void setListening(boolean listening);
	public boolean isListening();
	public void onFinishMovement();
}
