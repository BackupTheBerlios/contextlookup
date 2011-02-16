/* Copyright 2011 Egor Kobylkin  
 * 
 * This file is part of ContextLookup.

    ContextLookup is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ContextLookup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ContextLookup.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.diegor.contextLookup;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.system.KeypadListener;

final public class ClickableObjectChoiceField extends ObjectChoiceField implements KeypadListener  {
// to override navigationUnclick
	public ClickableObjectChoiceField() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClickableObjectChoiceField(String label, Object[] choices,
			int initialIndex, long style) {
		super(label, choices, initialIndex, style);
		// TODO Auto-generated constructor stub
	}

	public ClickableObjectChoiceField(String label, Object[] choices,
			int initialIndex) {
		super(label, choices, initialIndex);
		// TODO Auto-generated constructor stub
	}

	public ClickableObjectChoiceField(String label, Object[] choices,
			Object initialObject) {
		super(label, choices, initialObject);
		// TODO Auto-generated constructor stub
	}

	public ClickableObjectChoiceField(String label, Object[] choices) {
		super(label, choices);
		// TODO Auto-generated constructor stub
		
	}
	public boolean navigationClick(int status, int time) {
		//overriding this method to rewrite user_field with the email from the this dropdown field
		return super.navigationClick(status, time);
	}
	
	public boolean navigationUnclick(int status, int time) {
		//overriding this method to rewrite user_field with the email from the this dropdown field
		this.getChangeListener().fieldChanged(getOriginal(), getIndex());
		return super.navigationClick(status, time);
	}
	
}
