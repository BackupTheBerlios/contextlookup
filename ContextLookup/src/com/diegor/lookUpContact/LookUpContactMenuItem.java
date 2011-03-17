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

import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.TextMessage;

import com.diego.lookUpContact.LookUpContactResource;

import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;

final class LookUpContactMenuItem extends ApplicationMenuItem implements LookUpContactResource {
	
	   // Create a ResourceBundle object to contain the localized resources.
    private static ResourceBundle rb = ResourceBundle.getBundle(BUNDLE_ID,BUNDLE_NAME);

	// adds new menu item to the "view email" screen of Blackberry Email
	private Vector observers;

	public LookUpContactMenuItem(int order) {
		super(0x01010000);
		// place it after "next unopened item" close to "view contact"
		observers = new Vector();
	}

	public void addObserver(LookUpContactScreen esls) {
		observers.addElement(esls);
	}

	/*
	 * public void removeObserver(ChooseContactScreen esls){
	 * observers.removeElement(esls); }
	 */
	// Go through vector of observers and call update on them
	public void notifyObservers(Message emailMessage) {
		for (int i = 0; i < observers.size(); ++i) {
			((LookUpContactScreen) observers.elementAt(i)).update(emailMessage);
		}
	}

	public void notifyObservers(TextMessage smsMessage) {
		for (int i = 0; i < observers.size(); ++i) {
			((LookUpContactScreen) observers.elementAt(i)).update(smsMessage);
		}
	}

	public void notifyObservers(PhoneCallLog phoneLog) {
		for (int i = 0; i < observers.size(); ++i) {
			((LookUpContactScreen) observers.elementAt(i)).update(phoneLog);
		}
	}

	public void notifyObservers(MultipartMessage mmsMessage) {
		for (int i = 0; i < observers.size(); ++i) {
			((LookUpContactScreen) observers.elementAt(i)).update(mmsMessage);
		}
	}

	public void notifyObservers(Contact contact) {
		for (int i = 0; i < observers.size(); ++i) {
			((LookUpContactScreen) observers.elementAt(i)).update(contact);
		}
	}

	public Object run(Object context) {
		if (context instanceof Message) {
			notifyObservers((Message) context);
		} else if (context instanceof TextMessage) {
			notifyObservers((TextMessage) context);
		} else if (context instanceof PhoneCallLog) {
			notifyObservers((PhoneCallLog) context);
		} else if (context instanceof MultipartMessage) {
			notifyObservers((MultipartMessage) context);
		} else if (context instanceof Contact) {
			notifyObservers((Contact) context);
		} else if (context != null) {
			Dialog.alert("Debugging Info:" + context.toString());
		}
		return context;
	}

	public String toString() {
		// toString should return the string we want to use as the label of the
		// menuItem
		return rb.getString(I18N_MAIN_MENU_ITEM);
	}
}
