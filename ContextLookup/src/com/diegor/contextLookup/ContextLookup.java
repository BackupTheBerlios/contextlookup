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

import net.rim.blackberry.api.menuitem.ApplicationMenuItemRepository;
import net.rim.device.api.ui.UiApplication;

final public class ContextLookup extends UiApplication {
	public static void main(String[] args){
		ContextLookup theApp = new ContextLookup();
		theApp.enterEventDispatcher();
	}
	public ContextLookup(){
		LookupContactMenuItem myMenuitem = new LookupContactMenuItem(0);
        ApplicationMenuItemRepository.getInstance().addMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_VIEW,myMenuitem);
        ChooseContactScreen ccs = new ChooseContactScreen();
        myMenuitem.addObserver(ccs);
	}
}

