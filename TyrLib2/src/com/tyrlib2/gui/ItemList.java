package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.input.IScrollListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;

public class ItemList extends Window implements IScrollListener {
	
	private List<ItemListEntry> itemListEntries = new ArrayList<ItemListEntry>();
	
	private int displayItems;
	
	private float padding;
	
	private boolean touching;
	
	private int displaySize;
	
	private float itemSize;
	
	private Vector2 lastPoint;
	
	private int middle;
	
	private boolean offseted;
	
	private float height;
	
	private long oldPriority;
	
	private int listRotation;
	
	public ItemList(String name, Vector2 pos, Vector2 size, float padding, int displayItems) {
		super(name, size);
		this.setRelativePos(pos);
		this.displayItems = displayItems > 3 ? displayItems : 3;
		this.padding = padding;
		this.height = size.y;
	
	}
	
	public void addItemListEntry(ItemListEntry itemListEntry) {
		addItemListEntry(itemListEntry, itemListEntries.size());
	}
	
	public void removeItemListEntry(ItemListEntry itemListEntry) {
		itemListEntries.remove(itemListEntry);
	}
	
	public void addItemListEntry(ItemListEntry itemListEntry, int position) {
		WindowManager.getInstance().addWindow(itemListEntry);
		itemSize = itemListEntry.getSize().y;
		itemListEntries.add(position, itemListEntry);
		displaySize = Math.min(itemListEntries.size(), displayItems);
		middle = displaySize / 2;
		addChild(itemListEntry);
		Vector2 pos = new Vector2(0, (padding + itemSize) * (itemListEntries.size()-1));
		itemListEntry.setRelativePos(pos);
		
		if (itemListEntries.size() > displayItems) {
		
			for (int i = 0; i < itemListEntries.size(); ++i) {
				if (isOutsideDisplay(i)) {
					itemListEntries.get(i).setAlpha(0);
				} else if (isInDisplay(i)) {
					itemListEntries.get(i).setAlpha(1);
				} else {
					itemListEntries.get(i).setAlpha(0.8f);
				}
				itemListEntries.get(i).position = i+1;
			}
		
		} else {
			for (int i = 0; i < itemListEntries.size(); ++i) {
				itemListEntries.get(i).setAlpha(1);
				itemListEntries.get(i).position = i+1;
			}
		}
	}
	
	private boolean isOutsideDisplay(int index) {
		return index < 0 || index >= displaySize;
	}
	
	private boolean isInDisplay(int index) {
		return index > 0 && index < displaySize - 1;
	}
	
	public void correctOffset() {
		if (itemListEntries.size() > displayItems) {
			for (int i = 0; i < itemListEntries.size(); ++i) {
				Vector2 pos = new Vector2(0, (padding + itemSize) * i);
				itemListEntries.get(i).moveTo(pos, 0.1f);
				
				if (isOutsideDisplay(i)) {
					itemListEntries.get(i).setAlpha(0);
				} else if (isInDisplay(i)) {
					itemListEntries.get(i).setAlpha(1);
				} else {
					itemListEntries.get(i).setAlpha(0.8f);
				}
			}
		}
	}
	
	public void reposition() {
		for (int i = 0; i < itemListEntries.size(); ++i) {
			Vector2 pos = new Vector2(0, (padding + itemSize) * i);
			itemListEntries.get(i).moveTo(pos, 0.1f);
		}
		
	}
	
	public ItemListEntry getEntry(int index) {
		return itemListEntries.get(index);
	}
	
	public int getCountEntries() {
		return itemListEntries.size();
	}
	
	@Override
	protected void onTouchDownWindow(Vector2 point, IMotionEvent event) {
		touching = true;
		lastPoint = new Vector2(point.x, point.y);
		
		oldPriority = priority;
		priority = InputManager.FOCUS_PRIORITY;
		InputManager.getInstance().sort();
	}
	
	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		
		if (!touching) return false;
		
		point = new Vector2(point.x, 1-point.y);
		
		offseted = true;
		if (lastPoint != null && itemListEntries.size() > displayItems) {
			if (touching) {	
				Vector2 move = lastPoint.vectorTo(point);
				scroll(move.y*3);
			}
			
		}
		
		lastPoint = new Vector2(point.x, point.y);
		
		return false;
	}
	
	private void scroll(float scroll) {
		
		int moveUp = 0;
		
		for (int i = 0; i < displaySize; ++i) {
			int item = i % itemListEntries.size();
			Vector2 pos = itemListEntries.get(item).getRelativePos();
			ItemListEntry entry = itemListEntries.get(item);
			if (isInDisplay(item)) {
				pos.y -= scroll;
				if (item == (displaySize - 2) % itemListEntries.size() && pos.y >= height/2) {
					if (scroll < 0) {
						entry.setAlpha(entry.getAlpha() - Math.abs(scroll)/(height/2));
					} else {
						entry.setAlpha(entry.getAlpha() + Math.abs(scroll)/(height/2));
					}
					
					if (pos.y + entry.getSize().y >= height) {
						moveUp = 1;
					}
				}
				
				if (item == 1 && pos.y <= height/2) {
					if (scroll > 0) {
						entry.setAlpha(entry.getAlpha() - Math.abs(scroll)/(height/2));
					} else {
						entry.setAlpha(entry.getAlpha() + Math.abs(scroll)/(height/2));
					}
					
					if (pos.y <= 0) {
						moveUp = -1;
					}
				}
			} else {
				if (!isOutsideDisplay(i) && (scroll < 0 && i < middle)) {
					entry.setAlpha(entry.getAlpha() + Math.abs(scroll)/(height/2));
				} else if (!isOutsideDisplay(i) && (scroll > 0 && i > middle)) {
					entry.setAlpha(entry.getAlpha() + Math.abs(scroll)/(height/2));
				} else{
					entry.setAlpha(entry.getAlpha() - Math.abs(scroll)/(height/2));
				} 
				
				if (entry.getAlpha() >= 0.8f) {
					pos.y -= scroll;
				} else {
					pos.y += scroll;
				}
			}
			
			if (moveUp == 0) {
				entry.setRelativePos(pos);
			}
		}
		
		if (moveUp != 0) {
			rotate(moveUp);
			correctOffset();
		}
	}
	
	private void rotate(int direction) {
		if (direction == -1) {
			ItemListEntry entry = itemListEntries.get(0);
			itemListEntries.remove(0);
			itemListEntries.add(entry);
		} else if (direction == 1) {
			ItemListEntry entry = itemListEntries.get(itemListEntries.size()-1);
			itemListEntries.remove(itemListEntries.size()-1);
			itemListEntries.add(0, entry);
		}
		
		listRotation += direction;
		
		for (int i = 0; i < itemListEntries.size(); ++i) {
			itemListEntries.get(i).position = i+1;
		}
	}
	
	public void clearRotation() {
		if (listRotation < 0) {
			for (int i = 0; i < -listRotation; ++i) {
				rotate(1);
			}
		} else {
			for (int i = 0; i < listRotation; ++i) {
				rotate(-1);
			}
		}
	}
	
	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		if (touching) {
			touching = false;
			if (offseted) {
				correctOffset();
			}
			
			priority = oldPriority;
			InputManager.getInstance().sort();
		}
		
		return false;
	}
	
	public void clear() {
		itemListEntries.clear();
	}
	
	public int getMaxVisibleItems() {
		return displayItems;
	}

	@Override
	public boolean onScroll(float rotation) {
		if (hovered) {
			if (itemListEntries.size() > displayItems) {
				scroll(rotation/10);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void setReceiveTouchEvents(boolean receiveTouchEvents) {
		
		if (receiveTouchEvents && !this.receiveTouchEvents) {
			InputManager.getInstance().addScrollListener(this);
			InputManager.getInstance().addMoveListener(this);
		} else if (!receiveTouchEvents && this.receiveTouchEvents){
			InputManager.getInstance().removeScrollListener(this);
			InputManager.getInstance().removeMoveListener(this);
		}
		
		super.setReceiveTouchEvents(receiveTouchEvents);
	}


}
