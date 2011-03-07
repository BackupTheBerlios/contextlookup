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

import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.TextMessage;

import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;

final class LookUpContactMenuItem extends ApplicationMenuItem {
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
		for (int i = 0; i < observers.size(); i++) {
			((LookUpContactScreen) observers.elementAt(i)).update(emailMessage);
		}
	}

	public void notifyObservers(TextMessage smsMessage) {
		for (int i = 0; i < observers.size(); i++) {
			((LookUpContactScreen) observers.elementAt(i)).update(smsMessage);
		}
	}

	public void notifyObservers(PhoneLogs phoneLog) {
		for (int i = 0; i < observers.size(); i++) {
			((LookUpContactScreen) observers.elementAt(i)).update(phoneLog);
		}
	}

	public void notifyObservers(MultipartMessage mmsMessage) {
		for (int i = 0; i < observers.size(); i++) {
			((LookUpContactScreen) observers.elementAt(i)).update(mmsMessage);
		}
	}

	public Object run(Object context) {
		if (context instanceof Message)
			notifyObservers((Message) context);
		else if (context instanceof TextMessage)
			notifyObservers((TextMessage) context);
		else if (context instanceof PhoneLogs)
			notifyObservers((TextMessage) context);
		else if (context instanceof MultipartMessage)
			notifyObservers((TextMessage) context);
		return context;
	}

	public String toString() {
		// toString should return the string we want to use as the label of the
		// menuItem
		return "Look Up Contact";
	}
}
