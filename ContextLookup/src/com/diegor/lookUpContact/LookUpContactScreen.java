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
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
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

final class LookUpContactScreen extends MainScreen implements ObserverInterface {
	// This screen should be displayed after the menu item "Lookup Contact" is
	// selected. It is the only visible screen of this app.

	private String lookupString = "Error in Look Up Contact! No contact was selected!";
	// some help to be able to use the "for" cycle for message fields extraction
	final int[] recipientTypeIterator = { Message.RecipientType.FROM,
			Message.RecipientType.TO, Message.RecipientType.CC,
			Message.RecipientType.BCC, Message.RecipientType.REPLY_TO,
			Message.RecipientType.SENDER, };
	final int recipientTypeListLength = recipientTypeIterator.length;
	String[] fieldNameIterator = { "From:", "To:", "CC:", "BCC:", "Reply_To:",
			"Sender:", };
	final ObjectListField contactsOLF = new ObjectListField();

	public LookUpContactScreen() {
		super();
		this.setTitle("Look Up Contact");
	}

	// methods for custom context menu
	final private MenuItem lookupItemBBServer = new MenuItem(
			"B\u0332lackBerry Server Lookup", 100, 10) {
/* implement this method to capture the keyboard 
 * protected boolean keyChar(char c,
                          int status,
                          int time)
 */
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
			"L\u0332inkedIn.com Lookup", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doLinkedInLookup(lookupString);
			onClose();
		}
	};
	final private MenuItem lookupItem123people = new MenuItem(
			"w\u0332ww.123people.com Lookup", 100, 10) {
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
			"F\u0332acebook Lookup", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doFacebookLookup(lookupString);
			onClose();
		}
	};

	final private MenuItem closeItem = new MenuItem("Close", 200000, 10) {
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

	// TODO rewrite this part with a Vector instead of Array
	final public void update(final net.rim.blackberry.api.mail.Message message) {

		String[] contacts = new String[0];
		// now we collect all Addresses and Names from the message.
		// TODO rewrite this part with a Vector instead of Array
		for (int i = recipientTypeListLength - 1; i >= 0; --i) {
			// this iteration style should speed up a little
			try {
				Address[] currentAddress = message
						.getRecipients(recipientTypeIterator[i]);
				int numberOfRecipients = currentAddress.length;
				if (numberOfRecipients > 0) {
					for (int j = numberOfRecipients - 1; j >= 0; --j) {
						String name = currentAddress[j].getName();
						Arrays.add(contacts, (name != null) ? name
								: currentAddress[j].getAddr());
					}
				}
			} catch (MessagingException e) {
				Arrays.add(contacts, "Error extracting message contacts!");
				return;
			}
		}

		Arrays.sort(contacts, StringComparator.getInstance(true));
		// ignore case and leave first position intact for the clipboard content

		String from_clipboard = Clipboard.getClipboard().toString();
		if (from_clipboard != null && from_clipboard.length() > 0) {
			from_clipboard = (from_clipboard.length() <= 30) ? from_clipboard
					: from_clipboard.substring(0, 30);
			// 30 because 42 would not fit
		} else {
			from_clipboard = "[clipboard empty]";
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

		final ButtonField feedback = new ButtonField("Your Feedback",
				ButtonField.CONSUME_CLICK | ButtonField.FIELD_LEFT
						| ButtonField.HCENTER);
		feedback.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (field instanceof ButtonField) {
					Browser
							.getDefaultSession()
							.displayPage(
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

	final public boolean onClose() {
		UiApplication.getUiApplication().popScreen(this);
		this.deleteAll();
		return true;
	}

}
