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
		// EMAIL_EDIT, SMS_VIEW don't work, MMS_... not tested yet
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_EMAIL_VIEW, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_SMS_EDIT, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_SMS_VIEW, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_MMS_VIEW, myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_MMS_EDIT, myMenuitem);
		ApplicationMenuItemRepository.getInstance()
				.addMenuItem(
						ApplicationMenuItemRepository.MENUITEM_MESSAGE_LIST,
						myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_PHONELOG_VIEW,
				myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_ADDRESSBOOK_LIST,
				myMenuitem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(
				ApplicationMenuItemRepository.MENUITEM_ADDRESSCARD_VIEW,
				myMenuitem);
		LookUpContactScreen ccs = new LookUpContactScreen();
		myMenuitem.addObserver(ccs);
	}
}
