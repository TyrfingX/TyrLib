package com.tyrfing.games.tyrlib3.view.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.input.IMotionEvent;
import com.tyrfing.games.tyrlib3.input.IScrollListener;
import com.tyrfing.games.tyrlib3.input.InputManager;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.util.Layouter;
import com.tyrfing.games.tyrlib3.view.gui.WindowEvent.WindowEventType;

public class ItemList extends Window implements IScrollListener {
	
	public static final float SCROLLBAR_SIZE = 0.1f;
	public static final float SCROLLBAR_MAX_SIZE = 20;
	
	private IEventListener onShowScrollbar = new IEventListener() {
		@Override
		public void onEvent(WindowEvent event) {
			scrollbar.fadeIn(1, 0.5f);
		}
	};
	
	private IEventListener onHideScrollbar = new IEventListener() {
		@Override
		public void onEvent(WindowEvent event) {
			scrollbar.fadeOut(0, 0.5f);
		}
	};
	
	private List<ItemListEntry> itemListEntries = new ArrayList<ItemListEntry>();
	
	private int displayItems;
	
	private float padding;
	
	private boolean touching;
	
	private int displaySize;
	
	private float itemSize;
	
	private Vector2F lastPoint;
	
	private boolean offseted;
	
	private float height;
	
	private float width;
	
	private long oldPriority;
	
	private int listRotation;
	
	private float alpha;
	
	private Window scrollbar;
	
	private Orientation orientation = Orientation.Vertical;
	
	public ItemList(String name, Vector2F pos, Vector2F size, float padding, int displayItems) {
		super(name, size);
		this.setRelativePos(pos);
		this.displayItems = displayItems > 3 ? displayItems : 3;
		this.padding = padding;
		this.height = size.y;
		this.width = size.x;
		this.setPassTouchEventsThrough(true);
	
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	public void addItemListEntry(ItemListEntry itemListEntry) {
		addItemListEntry(itemListEntry, itemListEntries.size());
	}
	
	public void removeItemListEntry(ItemListEntry itemListEntry) {
		itemListEntries.remove(itemListEntry);
	}
	
	public void setScrollbar(final Window scrollbar) {
		this.scrollbar = scrollbar;
		
		resizeScrollbar();
		
		this.addChild(scrollbar);
		
		scrollbar.setInheritsAlpha(false);
		scrollbar.setInheritsFade(false);
		
		if (orientation == Orientation.Vertical) {
			scrollbar.setRelativePos(this.getSize().x, 0);
		} else {
			scrollbar.setRelativePos(0, -scrollbar.getSize().y);
		}
		
		this.addEventListener(WindowEventType.TOUCH_ENTERS, onShowScrollbar);
		this.addEventListener(WindowEventType.MOUSE_ENTERS, onShowScrollbar);
		this.addEventListener(WindowEventType.TOUCH_LEAVES, onHideScrollbar);
		this.addEventListener(WindowEventType.MOUSE_LEAVES, onHideScrollbar);
		this.addEventListener(WindowEventType.SIZE_CHANGED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				resizeScrollbar();
			}
		});
		this.addEventListener(WindowEventType.ALPHA_CHANGED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (event.getSource().getAlpha() == 0) {
					scrollbar.setAlpha(0);
				}
			}
		});
		this.addEventListener(WindowEventType.VISIBILITY_CHANGED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!event.getSource().isVisible()) {
					scrollbar.setAlpha(0);
					scrollbar.setVisible(false);
				}
			}
		});
	}
	
	private void resizeScrollbar() {
		Vector2F size = this.getSize();
		switch (orientation) {
		case Vertical:
			float height = size.y * displayItems / this.getCountEntries();
			if (height > size.y) height = size.y;
			scrollbar.setSize(Layouter.restrictX(SCROLLBAR_SIZE, SCROLLBAR_MAX_SIZE), height);
			break;
		case Horizontal:
			float width = size.x * displayItems / this.getCountEntries();
			if (width > size.x) width = size.x;
			scrollbar.setSize(width, Layouter.restrictY(SCROLLBAR_SIZE, SCROLLBAR_MAX_SIZE));
			break;
		}
	}
	
	public void addItemListEntry(ItemListEntry itemListEntry, int position) {
		WindowManager.getInstance().addWindow(itemListEntry);
		itemSize = orientation == Orientation.Vertical ? itemListEntry.getSize().y : itemListEntry.getSize().x;
		itemListEntries.add(position, itemListEntry);
		displaySize = Math.min(itemListEntries.size(), displayItems);
		addChild(itemListEntry);
		Vector2F pos = orientation == Orientation.Vertical ? 
					new Vector2F(0, (padding + itemSize) * (itemListEntries.size()-1))
				:	new Vector2F((padding + itemSize) * (itemListEntries.size()-1), 0);
		itemListEntry.setRelativePos(pos);
		
		if (itemListEntries.size() > displayItems) {
		
			for (int i = 0; i < itemListEntries.size(); ++i) {
				if (isOutsideDisplay(i)) {
					itemListEntries.get(i).endBlend();
					itemListEntries.get(i).setAlpha(0);
					itemListEntries.get(i).setInheritsFade(false);
				} else {
					itemListEntries.get(i).setAlpha(getAlpha());
					itemListEntries.get(i).setInheritsFade(true);
				}
				itemListEntries.get(i).position = i+1;
			}
		
		} else {
			for (int i = 0; i < itemListEntries.size(); ++i) {
				itemListEntries.get(i).setAlpha(getAlpha());
				itemListEntries.get(i).position = i+1;
			}
		}
		
		if (scrollbar != null) {
			resizeScrollbar();
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
				Vector2F pos = new Vector2F();
				switch (orientation) {
				case Vertical:
					pos.y = (padding + itemSize) * i;
					if (i == itemListEntries.size() - 1 && listRotation != 0) {
						pos.y = -padding - itemSize;
					}
					
					if (i == 0 && listRotation == 0) {
						pos.y = (padding + itemSize) * i;
					}
					break;
				case Horizontal:
					pos.x= (padding + itemSize) * i;
					if (i == itemListEntries.size() - 1 && listRotation != 0) {
						pos.x = -padding - itemSize;
					}
					
					if (i == 0 && listRotation == 0) {
						pos.x = (padding + itemSize) * i;
					}
					break;
				}
				
				itemListEntries.get(i).setRelativePos(pos);
				
				if (isOutsideDisplay(i)) {
					itemListEntries.get(i).setAlpha(0);
					itemListEntries.get(i).setInheritsFade(false);
				} else {
					itemListEntries.get(i).setAlpha(getAlpha());
					itemListEntries.get(i).setInheritsFade(true);
				}
			}
		}
		
		if (scrollbar != null) {
			switch (orientation) {
			case Vertical:
				scrollbar.setRelativePos(scrollbar.getRelativePos().x, this.getSize().y * listRotation / this.getCountEntries());
				break;
			case Horizontal:
				scrollbar.setRelativePos(this.getSize().x * listRotation / this.getCountEntries(), scrollbar.getRelativePos().y);
				break;
			}
		}
	}
	
	public void reposition() {
		for (int i = 0; i < itemListEntries.size(); ++i) {
			switch (orientation) {
			case Vertical:
				Vector2F pos = new Vector2F(0, (padding + itemSize) * i);
				itemListEntries.get(i).setRelativePos(pos);
				break;
			case Horizontal:
				pos = new Vector2F((padding + itemSize) * i, 0);
				itemListEntries.get(i).setRelativePos(pos);
				break;
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
	protected void onTouchDownWindow(Vector2F point, IMotionEvent event) {
		touching = true;
		lastPoint = new Vector2F(point.x, point.y);
		
		oldPriority = priority;
		priority = InputManager.FOCUS_PRIORITY;
		InputManager.getInstance().sort();
		
		super.onTouchDownWindow(point, event);
	}
	
	@Override
	public boolean onTouchMove(Vector2F point, IMotionEvent event, int fingerId) {
		
		if (!touching) return false;
		
		point = new Vector2F(point.x, 1-point.y);
		
		offseted = true;
		if (lastPoint != null && itemListEntries.size() > displayItems) {
			if (touching) {	
				Vector2F move = lastPoint.vectorTo(point);
				switch (orientation) {
				case Vertical:
					scroll(move.y*3);
					break;
				case Horizontal:
					scroll(move.x*3);
					break;
				}
			}
			
		}
		
		lastPoint = new Vector2F(point.x, point.y);
		
		return super.onTouchMove(point, event, fingerId);
	}
	
	private void scroll(float scroll) {
		
		if (listRotation >= this.getCountEntries() - displaySize && scroll < 0) return;
		if (listRotation <= 0 && scroll > 0) return;
		
		int moveUp = 0;
		
		for (int i = 0; i < itemListEntries.size(); ++i) {
			Vector2F pos = itemListEntries.get(i).getRelativePos();
			ItemListEntry entry = itemListEntries.get(i);
			
			if (orientation == Orientation.Vertical) {
			
				pos.y += scroll;
				if (isInDisplay(i)) {
					entry.setAlpha(1);
					
					if (i == (displaySize - 2) % itemListEntries.size() && pos.y >= height/2) {
						if (pos.y + entry.getSize().y >= height) {
							moveUp = -1;
						}
					}
					
					if (i == 1 && pos.y <= height/2) {
						if (pos.y <= 0) {
							moveUp = 1;
						}
					}
				} else {
					if (i == itemListEntries.size() - 1 || i == 0) {
						if (pos.y < 0){
							entry.setAlpha(1 + pos.y / entry.getSize().y);
						} else {
							entry.setAlpha(1);
						}
					} else if (i == displaySize || i == displaySize - 1) {
						if (pos.y > height - entry.getSize().y) {
							entry.setAlpha(1 - (pos.y - (height - entry.getSize().y)) / entry.getSize().y);
						} else {
							entry.setAlpha(1);
						}
					}
				}
			
			} else {
				
				pos.x += scroll;
				if (isInDisplay(i)) {
					entry.setAlpha(1);
					
					if (i == (displaySize - 2) % itemListEntries.size() && pos.x >= width/2) {
						if (pos.x + entry.getSize().x >= width) {
							moveUp = -1;
						}
					}
					
					if (i == 1 && pos.x <= width/2) {
						if (pos.x <= 0) {
							moveUp = 1;
						}
					}
				} else {
					if (i == itemListEntries.size() - 1 || i == 0) {
						if (pos.x < 0){
							entry.setAlpha(1 + pos.x / entry.getSize().x);
						} else {
							entry.setAlpha(1);
						}
					} else if (i == displaySize || i == displaySize - 1) {
						if (pos.x > width - entry.getSize().x) {
							entry.setAlpha(1 - (pos.x - (width - entry.getSize().x)) / entry.getSize().x);
						} else {
							entry.setAlpha(1);
						}
					}
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
			ItemListEntry entry = itemListEntries.get(itemListEntries.size()-1);
			itemListEntries.remove(itemListEntries.size()-1);
			itemListEntries.add(0, entry);
		} else if (direction == 1) {
			ItemListEntry entry = itemListEntries.get(0);
			itemListEntries.remove(0);
			itemListEntries.add(entry);
		}
		
		listRotation += direction;
		
		for (int i = 0; i < itemListEntries.size(); ++i) {
			itemListEntries.get(i).position = i+1;
		}
	}
	
	public void clearRotation() {
		listRotation = 0;
		if (scrollbar != null) {
			if (orientation == Orientation.Vertical) {
				scrollbar.setRelativePos(scrollbar.getRelativePos().x, 0);
			} else {
				scrollbar.setRelativePos(0, scrollbar.getRelativePos().y);
			}
		}
	}
	
	@Override
	public boolean onTouchUp(Vector2F point, IMotionEvent event, int fingerId) {
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
		clearRotation();
		itemListEntries.clear();
	}
	
	public int getRotation() {
		return listRotation;
	}
	
	public void addRotation(int rotation) {
		for (int i = 0; i < rotation; ++i) {
			rotate(1);
		}
		correctOffset();
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
	
	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.alpha = alpha;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}
}
