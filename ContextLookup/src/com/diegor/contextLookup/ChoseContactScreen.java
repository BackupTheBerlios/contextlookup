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

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
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

final class ChooseContactScreen extends MainScreen implements ObserverInterface {
	// This screen should be displayed after the menu item "Lookup Contact" is
	// selected. It is the only visible screen of this app.

	private String lookupString = "Error in ContextLookup! Choose a contact first!";
	// some help to be able to use the "for" cycle for message fields extraction
	int[] recipientTypeIterator = { // TODO change to final
	Message.RecipientType.FROM, Message.RecipientType.TO,
			Message.RecipientType.CC, Message.RecipientType.BCC,
			Message.RecipientType.REPLY_TO, Message.RecipientType.SENDER, };
	int recipientTypeListLength = recipientTypeIterator.length;
	String[] fieldNameIterator = { // TODO change to final
	"From:", "To:", "CC:", "BCC:", "Reply_To:", "Sender:", };
	final ObjectListField contactsOLF = new ObjectListField();

	public ChooseContactScreen() {
		super();
		this.setTitle("Select Contact To Look Up");
	}

	// methods for custom context menu
	private MenuItem lookupItemBBServer = new MenuItem(
			"Lookup Contact on BB Server", 100, 10) {
		// this is the main action.
		// It should appear close to Show Address and Add Contact
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			Invoke.invokeApplication(Invoke.APP_TYPE_ADDRESSBOOK, null);
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			lookup_email.doLookup(lookupString);
			onClose();
		}
	};
	private MenuItem lookupItemLinkedIn = new MenuItem(
			"Lookup Contact on LinkedIn", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doLinkedInLookup(lookupString);
			onClose();
		}
	};
	private MenuItem lookupItem123people = new MenuItem(
			"Lookup Contact on 123people", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.do123peopleLookup(lookupString);
			onClose();
		}
	};
	// TODO write the doFacebookLookup
	private MenuItem lookupItemFacebook = new MenuItem(
			"Lookup Contact on Facebook", 100, 10) {
		public void run() {
			RemoteLookup rl = new RemoteLookup();
			lookupString = (String) (contactsOLF.get(contactsOLF, contactsOLF
					.getSelectedIndex()));
			rl.doFacebookLookup(lookupString);
			onClose();
		}
	};

	private MenuItem closeItem = new MenuItem("Close", 200000, 10) {
		// implementing as per UI guidelines
		public void run() {
			onClose();
		}
	};

	protected void makeMenu(Menu menu, int instance) {
		// it is called by the OS to create app menu
		menu.add(lookupItemBBServer);
		menu.add(lookupItemLinkedIn);
		// menu.add(lookupItemFacebook);
		menu.add(lookupItem123people);
		menu.add(closeItem);
	}

	public void update(final net.rim.blackberry.api.mail.Message message) {

		// First we collect all Addresses and Names from the message.
		String[/* field_type */][/* members */] addressList = new String[recipientTypeListLength][/* different_for_al_fields */];
		String[] contacts1dimArray = new String[0];
		for (int i = 0; i < recipientTypeListLength; i++) {
			// this iteration style should speed up a little
			try {
				int numberOfRecipients = message
						.getRecipients(recipientTypeIterator[i]).length;
				if (numberOfRecipients > 0) {
					addressList[i] = new String[numberOfRecipients];
					for (int j = 0; j < numberOfRecipients; j++) {
						addressList[i][j] = message
								.getRecipients(recipientTypeIterator[i])[j]
								.getName();
						addressList[i][j] = (addressList[i][j] instanceof String) ? addressList[i][j]
								: message
										.getRecipients(recipientTypeIterator[i])[j]
										.getAddr();
						Arrays.add(contacts1dimArray, addressList[i][j]);
					}
				}
			} catch (MessagingException e) {
				// if the above functionality to be placed in an own method,
				// here we should just return a null instead
				this
						.add(new RichTextField(
								"ERROR:Problem processing address fields from the message!"));
				UiApplication.getUiApplication().pushScreen(this);
				return;
			}
		}

		Arrays.sort(contacts1dimArray, new StringComparator());
		
		contactsOLF.set(contacts1dimArray);

		RichTextField emptyLine = new RichTextField("", Field.NON_FOCUSABLE);

		// make it clickable to look up for the copyrights owner
		RichTextField copyright = new RichTextField(
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

		final ButtonField feedback = new ButtonField("Feedback",
				ButtonField.CONSUME_CLICK | ButtonField.FIELD_LEFT
						| ButtonField.HCENTER);
		feedback.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (field instanceof ButtonField) {
					Browser
							.getDefaultSession()
							.displayPage(
									"http://contextlookup.blogspot.com/2011/02/contextlookup-050-is-out.html#comments");
				}
			}
		});

		HorizontalFieldManager hm = new HorizontalFieldManager();

		this.add(contactsOLF);
		this.add(emptyLine);
		this.add(copyright);
		hm.add(feedback);
		hm.add(license);
		this.add(hm);

		UiApplication.getUiApplication().pushScreen(this);
	}

	public boolean onClose() {
		UiApplication.getUiApplication().popScreen(this);
		this.deleteAll();
		return true;
	}

}
