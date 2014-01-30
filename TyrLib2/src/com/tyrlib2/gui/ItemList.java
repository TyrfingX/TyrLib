package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;

import com.tyrlib2.math.Vector2;

public class ItemList extends Window {
	
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
	
	public ItemList(String name, Vector2 pos, Vector2 size, float padding, int displayItems) {
		super(name, size);
		this.setRelativePos(pos);
		this.displayItems = displayItems;
		this.padding = padding;
		this.height = size.y;
	
	}
	
	public void addItemListEntry(ItemListEntry itemListEntry) {
		addItemListEntry(itemListEntry, itemListEntries.size());
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
	
	public ItemListEntry getEntry(int index) {
		return itemListEntries.get(index);
	}
	
	public int getCountEntries() {
		return itemListEntries.size();
	}
	
	@Override
	protected void onTouchDownWindow(Vector2 point, MotionEvent event) {
		touching = true;
		lastPoint = new Vector2(point.x, point.y);
	}
	
	@Override
	protected void onTouchMoveWindow(Vector2 point, MotionEvent event) {
		offseted = true;
		if (lastPoint != null && itemListEntries.size() > displayItems) {
			Vector2 move = lastPoint.vectorTo(point);
			int moveUp = 0;
			if (touching) {
				for (int i = 0; i < displaySize; ++i) {
					int item = i % itemListEntries.size();
					Vector2 pos = itemListEntries.get(item).getRelativePos();
					ItemListEntry entry = itemListEntries.get(item);
					if (isInDisplay(item)) {
						pos.y -= move.y;
						if (item == (displaySize - 2) % itemListEntries.size() && pos.y >= height/2) {
							if (move.y < 0) {
								entry.setAlpha(entry.getAlpha() - Math.abs(move.y)/(height/2));
							} else {
								entry.setAlpha(entry.getAlpha() + Math.abs(move.y)/(height/2));
							}
							
							if (pos.y + entry.getSize().y >= height) {
								moveUp = 1;
							}
						}
						
						if (item == 1 && pos.y <= height/2) {
							if (move.y > 0) {
								entry.setAlpha(entry.getAlpha() - Math.abs(move.y)/(height/2));
							} else {
								entry.setAlpha(entry.getAlpha() + Math.abs(move.y)/(height/2));
							}
							
							if (pos.y <= 0) {
								moveUp = -1;
							}
						}
					} else {
						if (!isOutsideDisplay(i) && (move.y < 0 && i < middle)) {
							entry.setAlpha(entry.getAlpha() + Math.abs(move.y)/(height/2));
						} else if (!isOutsideDisplay(i) && (move.y > 0 && i > middle)) {
							entry.setAlpha(entry.getAlpha() + Math.abs(move.y)/(height/2));
						} else{
							entry.setAlpha(entry.getAlpha() - Math.abs(move.y)/(height/2));
						} 
						
						if (entry.getAlpha() >= 0.8f) {
							pos.y -= move.y;
						} else {
							pos.y += move.y;
						}
					}
					
					if (moveUp == 0) {
						entry.setRelativePos(pos);
					}
				}
			}
		
			if (moveUp != 0) {
				rotate(moveUp);
				correctOffset();
			}
			
		}
		
		lastPoint = new Vector2(point.x, point.y);
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
		
		for (int i = 0; i < itemListEntries.size(); ++i) {
			itemListEntries.get(i).position = i+1;
		}
	}
	
	@Override
	protected void onTouchUpWindow(Vector2 point, MotionEvent event) {
		touching = false;
		if (offseted) {
			correctOffset();
		}
	}
	
	public void clear() {
		itemListEntries.clear();
	}


}
