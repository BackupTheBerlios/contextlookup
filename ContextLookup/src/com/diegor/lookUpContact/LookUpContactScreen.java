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

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringComparator;

import com.diego.lookUpContact.LookUpContactResource;

final class LookUpContactScreen extends MainScreen implements  LookUpContactResource {
	   // Create a ResourceBundle object to contain the localized resources.
    private static ResourceBundle rb = ResourceBundle.getBundle(BUNDLE_ID,BUNDLE_NAME);
	// This screen should be displayed after the menu item "Lookup Contact" is
	// selected. It is the only visible screen of this app.
	private String lookupString =rb.getString(I18N_ERROR_NO_CNTCT);
	// some help to be able to use the "for" cycle for message fields extraction
	final ObjectListField contactsOLF = new ObjectListField();

	public  LookUpContactScreen(String[] contacts) {
		super();
		this.setTitle(rb.getString(I18N_MAIN_SCREEN_TITLE));

		Arrays.sort(contacts, StringComparator.getInstance(true));
		// ignore case and leave first position intact for the clipboard content

		String from_clipboard = Clipboard.getClipboard().toString();
		if (from_clipboard != null && from_clipboard.length() > 0) {
			from_clipboard = (from_clipboard.length() <= 30) ? from_clipboard
					: from_clipboard.substring(0, 30);
			// 30 because 42 would not fit
		} else {
			from_clipboard =  rb.getString(I18N_CLPBRD_EMPTY);
		}

		Arrays.add(contacts, from_clipboard);

		contactsOLF.set(contacts);

		final RichTextField emptyLine = new RichTextField("",
				Field.NON_FOCUSABLE);

		// TODO make it clickable to look up for the copyrights owner
		final RichTextField copyright = new RichTextField(
				"Copyright 2011 Egor Kobylkin", Field.NON_FOCUSABLE);

		// TODO change ButtonField to HyperlinkButtonField
		final ButtonField license = new ButtonField("License GPL3"
		// , 0x0000FF, 0xFFFFFF, 0x0000FF, 0, 0,
				, ButtonField.CONSUME_CLICK | ButtonField.FIELD_LEFT
						| ButtonField.HCENTER);
		license.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (field instanceof ButtonField) {
					Browser.getDefaultSession().displayPage(
							"http://www.gnu.org/licenses/gpl.html");
				}
			}
		});

		final ButtonField feedback = new ButtonField(rb.getString(I18N_FEEDBACK_BTTN),
				ButtonField.CONSUME_CLICK | ButtonField.FIELD_LEFT
						| ButtonField.HCENTER);
		feedback.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (field instanceof ButtonField) {
					Browser.getDefaultSession().displayPage(
							"http://contextlookup.blogspot.com");
				}
			}
		});

		final HorizontalFieldManager hm = new HorizontalFieldManager();

		this.add(contactsOLF);
		this.add(emptyLine);
		this.add(copyright);
		hm.add(feedback);
		hm.add(license);
		this.add(hm);

		UiApplication.getUiApplication().pushScreen(this);
	}

	
	// methods for custom context menu
	final private MenuItem lookupItemBBServer = new MenuItem(
			rb.getString(I18N_ACTION_NAME) +" "+"BlackBerry Server", 100, 10) {
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			Invoke.invokeApplication(Invoke.APP_TYPE_ADDRESSBOOK, null);
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			lookup_email.doLookup(lookupString);
			onClose();
		}
	};
	final private MenuItem lookupItemLinkedIn = new MenuItem(
			rb.getString(I18N_ACTION_NAME) +" "+"LinkedIn.com", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doLinkedInLookup(lookupString);
			onClose();
		}
	};
	final private MenuItem lookupItem123people = new MenuItem(
			rb.getString(I18N_ACTION_NAME) +" "+"123people.com", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.do123peopleLookup(lookupString);
			onClose();
		}
	};
	// TODO write the doFacebookLookup
	final private MenuItem lookupItemFacebook = new MenuItem(
			rb.getString(I18N_ACTION_NAME) +" "+"Facebook", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doFacebookLookup(lookupString);
			onClose();
		}
	};

	final private MenuItem closeItem = new MenuItem(rb.getString(I18N_MENU_CLOSE), 200000, 10) {
		// implementing as per UI guidelines
		public void run() {
			onClose();
		}
	};

	// TODO add Xing lookup
	final protected void makeMenu(Menu menu, int instance) {
		// it is called by the OS to create app menu
		menu.add(lookupItemBBServer);
		menu.add(lookupItemLinkedIn);
		// menu.add(lookupItemFacebook);
		menu.add(lookupItem123people);
		menu.add(closeItem);
	}


	final public boolean onClose() {
		UiApplication.getUiApplication().popScreen(this);
		this.deleteAll();
		return true;
	}

}
