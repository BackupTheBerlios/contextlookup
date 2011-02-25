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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

final class ChooseContactScreen extends MainScreen implements
		ObserverInterface, FieldChangeListener {
	// This screen should be displayed after the menu item "Lookup Contact" is
	// selected. It is the only visible screen of this app. I would like to
	// rewrite it so that it looks like the BB Addressbook.
	private ButtonField user_input;
	// to be able to use the "for" cycle
	int[] recipientTypeIterator = { // TODO change to final
	Message.RecipientType.FROM, Message.RecipientType.TO,
			Message.RecipientType.CC, Message.RecipientType.BCC,
			Message.RecipientType.REPLY_TO, Message.RecipientType.SENDER, };
	int recipientTypeListLength = recipientTypeIterator.length;
	String[] fieldNameIterator = { // TODO change to final
	"From:", "To:", "CC:", "BCC:", "Reply_To:", "Sender:", };

	public ChooseContactScreen() {
		super();
	}

	// methods for custom context menu
	private MenuItem lookupItem = new MenuItem("Lookup Contact", 100, 10) {
		// this is the main action.
		// It should appear close to Show Address and Add Contact
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			Invoke.invokeApplication(Invoke.APP_TYPE_ADDRESSBOOK, null);
			lookup_email.doLookup(user_input.getLabel());
			onClose();
		}
	};
	private MenuItem lookupItemLinkedIn = new MenuItem(
			"Lookup Contact on LinkedIn", 100, 10) {
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			lookup_email.doLinkedInLookup(user_input.getLabel());
			onClose();
		}
	};
	private MenuItem lookupItem123people = new MenuItem(
			"Lookup Contact on 123people", 100, 10) {
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			lookup_email.do123peopleLookup( user_input.getLabel());
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
		menu.add(lookupItem);
		menu.add(lookupItemLinkedIn);
		menu.add(lookupItem123people);
		menu.add(closeItem);
	}

	public void update(final net.rim.blackberry.api.mail.Message message) {

		String from_address;
		// the "from:" address field should always be
		// available so we use it as a default
		try {
			from_address = message.getFrom().getName();
			from_address =(from_address instanceof String)?from_address:message.getFrom().getAddr();
		} catch (Exception ex) {
			from_address = "Choose A Contact Below";
		}
		user_input = new ButtonField( from_address);
		LabelField lfl=new LabelField( "Lookup:");
		JustifiedHorizontalFieldManager hml = new JustifiedHorizontalFieldManager(
				lfl, user_input, true);
		// Had to create a custom Field Manager as the vanilla one
		// does not support justified layout.
		this.add(hml);
		SeparatorField sf = new SeparatorField(SeparatorField.LINE_HORIZONTAL);
		this.add(sf);

		// Doing this in two steps to prepare for the multithreading and MVC
		// paradigm. First we collect all Addresses and Names from the message.
		String[/* field_type */][/* members */] addressList = new String[recipientTypeListLength][/* different_for_al_fields */];
		for (int i = recipientTypeListLength - 1; i >= 0; i--) {
			// this iteration style should speed up a little
			try {
				int numberOfRecipients = message
						.getRecipients(recipientTypeIterator[i]).length;
				if (numberOfRecipients > 0) {
					addressList[i] = new String[numberOfRecipients];
					for (int j = numberOfRecipients - 1; j >= 0; j--) {
						addressList[i][j] = message
								.getRecipients(recipientTypeIterator[i])[j]
								.getName();
						addressList[i][j] = (addressList[i][j] instanceof String) ? addressList[i][j]
								: message
										.getRecipients(recipientTypeIterator[i])[j]
										.getAddr();
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

		// From here on we start adding fields filled with the collected
		// information.
		add(new RichTextField("Choose Another Contact To Look Up:"));

		for (int rt = 0; rt < recipientTypeListLength; rt++) {
			if (addressList[rt] instanceof String[]) { // email not null
				if (addressList[rt].length == 1) {
					// add a button instead of a drop down list
					ButtonField bf = new ButtonField(addressList[rt][0],
							ButtonField.CONSUME_CLICK);
					LabelField lf = new LabelField(fieldNameIterator[rt]);
					JustifiedHorizontalFieldManager hm = new JustifiedHorizontalFieldManager(
							lf, bf, true);
					// Had to create a custom Field Manager as the vanilla one
					// does not support justified layout.
					bf.setChangeListener(this);
					this.add(hm);
				} else { // dropdown list
					ClickableObjectChoiceField ocf;
					this.add(ocf = new ClickableObjectChoiceField(
							fieldNameIterator[rt], addressList[rt]));
					ocf.setChangeListener(this);
				}
			}
			;
		}
		SeparatorField sf2 = new SeparatorField(SeparatorField.LINE_HORIZONTAL);
		this.add(sf2);
/*		ButtonField feedback = new ButtonField("Feedback and License",
				ButtonField.CONSUME_CLICK | ButtonField.FIELD_HCENTER
						| ButtonField.HCENTER);
		feedback.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (field instanceof ButtonField) {
					Browser
							.getDefaultSession()
							.displayPage(
									"http://developer.berlios.de/forum/forum.php?forum_id=36545");
				}
			}
		});
		this.add(feedback);*/
		add(new RichTextField("Copyright 2011 Egor Kobylkin, GPL3"));
		
		UiApplication.getUiApplication().pushScreen(this);
	}

	public boolean onClose() {
		UiApplication.getUiApplication().popScreen(this);
		this.deleteAll();
		return true;
	}

	public void fieldChanged(Field field, int context) {
		// rewrites user_input with the email from a drop down list
		if (field instanceof ObjectChoiceField) {
			// assumption: all dropdown lists contain email addresses or
			// contacts
			try {
				user_input.setLabel((String) ((ObjectChoiceField) field)
						.getChoice(((ObjectChoiceField) field)
								.getSelectedIndex()));
			} catch (Exception ex) {
				System.out.print(ex.toString());
			}
		} else if (field instanceof ButtonField) {
			try {
				ButtonField buttonField = (ButtonField) field;
				user_input.setLabel(buttonField.getLabel());
			} catch (Exception ex) {
				System.out.print(ex.toString());
			}
		}
	}
}
