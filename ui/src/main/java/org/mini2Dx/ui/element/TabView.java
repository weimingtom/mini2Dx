/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ui.element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mini2Dx.core.controller.button.ControllerButton;
import org.mini2Dx.core.exception.MdxException;
import org.mini2Dx.ui.input.ControllerHotKeyOperation;
import org.mini2Dx.ui.input.KeyboardHotKeyOperation;
import org.mini2Dx.ui.input.UiNavigation;
import org.mini2Dx.ui.layout.LayoutRuleset;
import org.mini2Dx.ui.listener.ActionListener;
import org.mini2Dx.ui.render.ActionableRenderNode;
import org.mini2Dx.ui.render.NavigatableRenderNode;
import org.mini2Dx.ui.render.ParentRenderNode;
import org.mini2Dx.ui.render.TabViewRenderNode;

/**
 *
 */
public class TabView extends UiElement implements Navigatable {
	private final Queue<ControllerHotKeyOperation> controllerHotKeyOperations = new LinkedList<ControllerHotKeyOperation>();
	private final Queue<KeyboardHotKeyOperation> keyboardHotKeyOperations = new LinkedList<KeyboardHotKeyOperation>();
	
	private final Row tabMenuRow;
	private final Button previousTabButton, nextTabButton;
	private final List<TextButton> tabButtons = new ArrayList<TextButton>(1);
	
	private final List<Tab> tabs = new ArrayList<Tab>(1);
	
	private LayoutRuleset layout = LayoutRuleset.DEFAULT_RULESET;
	private TabViewRenderNode renderNode;
	private int currentTabIndex = 0;
	
	public TabView() {
		this(null);
	}
	
	public TabView(String id) {
		this(id, null, null);
	}
	
	public TabView(String id, Button previousTabButton, Button nextTabButton) {
		super(id);
		tabMenuRow = new Row();
		tabMenuRow.setVisibility(Visibility.VISIBLE);
		
		if(previousTabButton == null) {
			TextButton previousButton = new TextButton();
			previousButton.setText("<");
			previousButton.setVisibility(Visibility.VISIBLE);
			this.previousTabButton = previousButton;
		} else {
			this.previousTabButton = previousTabButton;
		}
		if(nextTabButton == null) {
			TextButton nextButton = new TextButton();
			nextButton.setText(">");
			nextButton.setVisibility(Visibility.VISIBLE);
			this.nextTabButton = nextButton;
		} else {
			this.nextTabButton = nextTabButton;
		}
		this.previousTabButton.addActionListener(new ActionListener() {
			@Override
			public void onActionBegin(Actionable source) {}
			
			@Override
			public void onActionEnd(Actionable source) {
				previousTab();
			}
		});
		this.nextTabButton.addActionListener(new ActionListener() {
			@Override
			public void onActionBegin(Actionable source) {}
			
			@Override
			public void onActionEnd(Actionable source) {
				nextTab();
			}
		});
	}
	
	public void add(Tab tab) {
		if(tab == null) {
			throw new MdxException("Cannot add null element to " + TabView.class.getSimpleName());
		}
		if(tabs.size() == 0) {
			tab.activateTab();
		} else {
			tab.deactivateTab();
		}
		tabs.add(tab);
		
		if(renderNode == null) {
			return;
		}
		tab.attach(renderNode);
	}
	
	public void add(int index, Tab tab) {
		if(tab == null) {
			throw new MdxException("Cannot add null element to " + TabView.class.getSimpleName());
		}
		if(tabs.size() == 0) {
			tab.activateTab();
		}  else {
			tab.deactivateTab();
		}
		tabs.add(index, tab);
		if(renderNode == null) {
			return;
		}
		tab.attach(renderNode);
	}
	
	public boolean remove(Tab tab) {
		if(renderNode != null) {
			tab.detach(renderNode);
		}
		return tabs.remove(tab);
	}
	
	public boolean remove(int index) {
		return remove(tabs.get(index));
	}
	
	@Override
	public void syncWithRenderNode() {
		while(!effects.isEmpty()) {
			renderNode.applyEffect(effects.poll());
		}
		((NavigatableRenderNode) renderNode).syncHotkeys(controllerHotKeyOperations, keyboardHotKeyOperations);
		syncTabTitles();
		syncChildStyles();
	}
	
	private void syncTabTitles() {
		if(tabs.size() > tabButtons.size()) {
			//Tabs added
			for(int i = tabButtons.size(); i < tabs.size(); i++) {
				TextButton tabButton = new TextButton();
				tabButton.setVisibility(Visibility.VISIBLE);
				tabButton.setText(tabs.get(i).getTitle());
				tabButton.addActionListener(new TabButtonActionListener(this, i));
				tabButtons.add(tabButton);
			}
		} else if(tabs.size() < tabButtons.size()) {
			//Tabs removed
			for(int i = tabButtons.size() - 1; tabButtons.size() > tabs.size(); i--) {
				tabButtons.remove(i);
			}
		}
		
		for(int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			if(tab.titleChanged()) {
				tabButtons.get(i).setText(tab.getTitle());
				tab.clearTitleChanged();
			}
		}
	}
	
	private void syncChildStyles() {
		tabMenuRow.setStyleId(renderNode.getTabMenuStyleId());
		for(int i = 0; i < tabs.size(); i++) {
			tabs.get(i).setStyleId(renderNode.getTabContentStyleId());
			tabButtons.get(i).setStyleId(renderNode.getTabButtonStyleId());
		}
		previousTabButton.setStyleId(renderNode.getTabButtonStyleId());
		nextTabButton.setStyleId(renderNode.getTabButtonStyleId());
	}

	@Override
	public void attach(ParentRenderNode<?, ?> parentRenderNode) {
		if(renderNode != null) {
			return;
		}
		renderNode = new TabViewRenderNode(parentRenderNode, this);
		tabMenuRow.attach(renderNode);
		for(int i = 0; i < tabs.size(); i++) {
			tabs.get(i).attach(renderNode);
		}
		parentRenderNode.addChild(renderNode);
	}

	@Override
	public void detach(ParentRenderNode<?, ?> parentRenderNode) {
		if(renderNode == null) {
			return;
		}
		parentRenderNode.removeChild(renderNode);
	}

	@Override
	public void setVisibility(Visibility visibility) {
		if(this.visibility == visibility) {
			return;
		}
		this.visibility = visibility;
		
		if(renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public void setStyleId(String styleId) {
		if(styleId == null) {
			return;
		}
		if(this.styleId.equals(styleId)) {
			return;
		}
		this.styleId = styleId;
		
		if(renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		
		if(renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}
	
	public Tab getCurrentTab() {
		if(currentTabIndex >= tabs.size()) {
			return null;
		}
		return tabs.get(currentTabIndex);
	}
	
	public void setCurrentTab(Tab tab) {
		int tabIndex = tabs.indexOf(tab);
		if(tabIndex < 0) {
			throw new MdxException(tab + " cannot be set to current tab as it was not added to " + TabView.class.getSimpleName() + ":" + getId());
		}
		setCurrentTabIndex(tabIndex);
	}

	public int getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(int currentTabIndex) {
		if(currentTabIndex < 0) {
			return;
		}
		if(currentTabIndex >= tabs.size()) {
			return;
		}
		if(this.currentTabIndex == currentTabIndex) {
			return;
		}
		
		tabs.get(this.currentTabIndex).deactivateTab();
		this.currentTabIndex = currentTabIndex;
		tabs.get(this.currentTabIndex).activateTab();
		
		if(renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}
	
	public void nextTab() {
		if(currentTabIndex >= tabs.size() - 1) {
			setCurrentTabIndex(0);
		} else {
			setCurrentTabIndex(currentTabIndex + 1);
		}
	}
	
	public void previousTab() {
		if(currentTabIndex <= 0) {
			setCurrentTabIndex(tabs.size() - 1);
		} else {
			setCurrentTabIndex(currentTabIndex - 1);
		}
	}

	@Override
	public ActionableRenderNode navigate(int keycode) {
		if(renderNode == null) {
			return null;
		}
		if(currentTabIndex >= tabs.size()) {
			return null;
		}
		return tabs.get(currentTabIndex).navigate(keycode);
	}
	
	@Override
	public ActionableRenderNode hotkey(int keycode) {
		if(renderNode == null) {
			return null;
		}
		ActionableRenderNode result = ((NavigatableRenderNode) renderNode).hotkey(keycode);
		if(result != null) {
			return result;
		}
		if(currentTabIndex >= tabs.size()) {
			return null;
		}
		return tabs.get(currentTabIndex).hotkey(keycode);
	}
	
	@Override
	public ActionableRenderNode hotkey(ControllerButton button) {
		if(renderNode == null) {
			return null;
		}
		ActionableRenderNode result = ((NavigatableRenderNode) renderNode).hotkey(button);
		if(result != null) {
			return result;
		}
		if(currentTabIndex >= tabs.size()) {
			return null;
		}
		return tabs.get(currentTabIndex).hotkey(button);
	}

	@Override
	public UiNavigation getNavigation() {
		if(currentTabIndex >= tabs.size()) {
			return null;
		}
		return tabs.get(currentTabIndex).getNavigation();
	}
	
	public void setPreviousTabHotkey(int keycode) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, previousTabButton, true));
	}
	
	public void setPreviousTabHotkey(ControllerButton button) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, previousTabButton, true));
	}
	
	public void setNextTabHotkey(int keycode) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, nextTabButton, true));
	}
	
	public void setNextTabHotkey(ControllerButton button) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, nextTabButton, true));
	}
	
	public void unsetPreviousTabHotkey(int keycode) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, previousTabButton, false));
	}
	
	public void unsetPreviousTabHotkey(ControllerButton button) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, previousTabButton, false));
	}
	
	public void unsetNextTabHotkey(int keycode) {
		keyboardHotKeyOperations.offer(new KeyboardHotKeyOperation(keycode, nextTabButton, false));
	}
	
	public void unsetNextTabHotkey(ControllerButton button) {
		controllerHotKeyOperations.offer(new ControllerHotKeyOperation(button, nextTabButton, false));
	}
	
	@Override
	public void setHotkey(ControllerButton button, Actionable actionable) {
		throw new MdxException(TabView.class.getSimpleName() + " only allows setPreviousTabHotkey and setNextTabHotkey methods. Set hotkeys within individual Tab instances.");
	}

	@Override
	public void setHotkey(int keycode, Actionable actionable) {
		throw new MdxException(TabView.class.getSimpleName() + " only allows setPreviousTabHotkey and setNextTabHotkey methods. Set hotkeys within individual Tab instances.");
	}

	@Override
	public void unsetHotkey(ControllerButton button, Actionable actionable) {
		throw new MdxException(TabView.class.getSimpleName() + " only allows unsetPreviousTabHotkey and unsetNextTabHotkey methods. Unset hotkeys within individual Tab instances.");
	}

	@Override
	public void unsetHotkey(int keycode, Actionable actionable) {
		throw new MdxException(TabView.class.getSimpleName() + " only allows unsetPreviousTabHotkey and unsetNextTabHotkey methods. Unset hotkeys within individual Tab instances.");
	}
	
	public LayoutRuleset getLayout() {
		return layout;
	}
	
	public void setLayout(LayoutRuleset layoutRuleset) {
		if(layoutRuleset == null) {
			return;
		}
		this.layout = layoutRuleset;
		if(renderNode == null) {
			return;
		}
		renderNode.setDirty(true);
	}
	
	private class TabButtonActionListener implements ActionListener {
		private final TabView tabView;
		private final int index;
		
		TabButtonActionListener(TabView tabView, int index) {
			this.tabView = tabView;
			this.index = index;
		}
		
		@Override
		public void onActionBegin(Actionable source) {}
		
		@Override
		public void onActionEnd(Actionable source) {
			tabView.setCurrentTabIndex(index);
		}
	}
}