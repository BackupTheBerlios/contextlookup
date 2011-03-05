/* Copyright 2011 Egor Kobylkin  
 * 
 * This file is part of  LookUpContact.

    LookUpContact is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LookUpContact is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with  LookUpContact.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.diegor.lookUpContact;

import net.rim.blackberry.api.menuitem.ApplicationMenuItemRepository;
import net.rim.device.api.ui.UiApplication;

final public class LookUpContact extends UiApplication {
	public static void main(String[] args) {
		LookUpContact theApp = new LookUpContact();
		theApp.enterEventDispatcher();
	}

	public LookUpContact() {
		LookUpContactMenuItem myMenuitem = new LookUpContactMenuItem(0);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_EMAIL_VIEW, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_MESSAGE_LIST, myMenuitem);
		// MENUITEM_MESSAGE_LIST MENUITEM_ADDRESSBOOK_LIST
		// MENUITEM_ADDRESSCARD_VIEW MENUITEM_MMS_VIEW MENUITEM_SMS_VIEW
		LookUpContactScreen ccs = new LookUpContactScreen();
		myMenuitem.addObserver(ccs);
	}
}
