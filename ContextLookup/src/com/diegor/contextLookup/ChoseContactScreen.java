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

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.Arrays;

final class ChooseContactScreen extends MainScreen implements ObserverInterface , FieldChangeListener{
	// This screen should be displayed after the menu item "Lookup Contact" is selected.   
	// It is the only visible screen of this app. I would like to rewrite it so that it looks like the BB Addressbook.
	private EditField user_input;
	// to be able to use the "for" cycle
	int[] recipientTypeIterator= { //TODO change to final
			Message.RecipientType.FROM,
			Message.RecipientType.TO,
			Message.RecipientType.CC,
			Message.RecipientType.BCC,
			Message.RecipientType.REPLY_TO,
			Message.RecipientType.SENDER,
	};
	int recipientTypeListLength = recipientTypeIterator.length;
	String[] fieldNameIterator= { //TODO change to final
			"From:",
			"To:",
			"CC:",
			"BCC:",
			"Reply_To:",
			"Sender:",
	};
	public ChooseContactScreen()    {
		super();
	}

	// methods for custom context menu     
	private MenuItem lookupItem = new MenuItem("Lookup Contact", 100, 10) {
		// this is the main action. It should appear close to Show Address and Add Contact
		public void run() {
			RemoteLookup lookup_email = new RemoteLookup();
			Invoke.invokeApplication(Invoke.APP_TYPE_ADDRESSBOOK,null);
			lookup_email.doLookup(user_input.getText());
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
		//it is called by the OS to create app menu
		menu.add(lookupItem);
		menu.add(closeItem);
	}

	public void update(final net.rim.blackberry.api.mail.Message message) {

		String from_address; // the "from:" address field should always be available so we use it as a default
		try { from_address= message.getFrom().getAddr(); } catch (Exception ex) { from_address=""; }
		user_input = new EditField("Lookup: ",from_address    );
		//TODO substitute with the pop-up text field like in the vanilla lookup from the address book.
		user_input.setBorder(BorderFactory.createRoundedBorder( new XYEdges(10,10,10,10)));
		this.add(user_input);
	
		// Doing this in two steps to prepare for the multithreading and MVC paradigm
		// First we collect all Addresses and Names from the message.
		String[/*field type*/][/*address or name*/][/*members*/] addressList 
				= new String[recipientTypeListLength][2][/*different for all fields*/];
		for (int i=recipientTypeListLength-1;i>=0;i--) {//this iteration style should speed up a little
			try {
				int numberOfRecipients = message.getRecipients( recipientTypeIterator[i] ).length;
				if(numberOfRecipients>0) {
					addressList[i] = new String[2][numberOfRecipients];
					for (int j=numberOfRecipients-1 ;j>=0; j-- ){ //this iteration style should speed up a little
						addressList[i][0][j]= message.getRecipients(recipientTypeIterator[i])[j].getAddr();
						addressList[i][1][j]= message.getRecipients(recipientTypeIterator[i])[j].getName();
					}      
				}
			} catch (MessagingException e) {
			// if the above functionality to be placed in an own method here we should just return null instead	
				this.add(new RichTextField("ERROR:Problem processing address fields from the message!"));		
				UiApplication.getUiApplication().pushScreen(this);   
				return;	
			}
		}
		
		// From here on we start adding fields filled with the collected information.
		add(new RichTextField("Chose from Contact Address(es):"));		

		for (int i=0;i<recipientTypeListLength;i++) {	
			if ( addressList[i][0] instanceof String[]  ) { // not null
				if(addressList[i][0].length ==1){ //add a button instead of a drop down list
					ButtonField bf = new ButtonField(addressList[i][0][0], ButtonField.CONSUME_CLICK);
					LabelField lf = new LabelField(fieldNameIterator[i]) ;
					JustifiedHorizontalFieldManager hm  = new JustifiedHorizontalFieldManager(lf , bf, true  );
					// Had to create a custom Field Manager as the vanilla does not support justified layout. 
					bf.setChangeListener(this);
					this.add(hm);
				} else { //dropdown list
				ClickableObjectChoiceField ocf; 
				this.add( ocf  = new ClickableObjectChoiceField(fieldNameIterator[i],addressList[i][0] ));        
				ocf.setChangeListener(this); 
				}
			};
		}

		add(new RichTextField("Chose from Contact Name(s)"));

		for (int i=0;i<recipientTypeListLength;i++) {	
			if ( addressList[i][1] instanceof String[]  ) { // not null				
					for(int k=( addressList[i][1].length-1);k>=0;k--){ 
						// remove null members, implicitly using the reverse cycle order
						if( ! (addressList[i][1][k] instanceof String) ) Arrays.removeAt(addressList[i][1],k); 
					}
					if( addressList[i][1].length ==1 ){ //add a button instead of a drop down list
						ButtonField bf = new ButtonField(addressList[i][1][0], ButtonField.CONSUME_CLICK);
						LabelField lf = new LabelField(fieldNameIterator[i]) ;
						JustifiedHorizontalFieldManager hm  = new JustifiedHorizontalFieldManager(lf , bf, true  );
						bf.setChangeListener(this);
						this.add(hm);
					} else { // dropdown list
					ClickableObjectChoiceField ocf; 
					this.add(ocf  = new ClickableObjectChoiceField(fieldNameIterator[i],addressList[i][1] ));        
					ocf.setChangeListener(this);
				}
			} 
		}
		
		UiApplication.getUiApplication().pushScreen(this);   
	}

	public boolean onClose(){
		UiApplication.getUiApplication().popScreen(this);
		this.deleteAll();
		return true;
	}

	public void fieldChanged(Field field, int context) { // rewrites user_input with the email from a drop down list
		if (field instanceof ObjectChoiceField  ) { //assumption: all dropdown lists contain email addresses or contacts
			try{
				user_input.setText(    (String) ((ObjectChoiceField)field).getChoice(  ((ObjectChoiceField)field).getSelectedIndex()  )    );
			}catch (Exception ex){
				System.out.print (ex.toString());
			}
		} else if (field instanceof  ButtonField) {
			try{
	             ButtonField buttonField = (ButtonField) field;
	             user_input.setText(    buttonField.getLabel() );
			}catch (Exception ex){
				System.out.print (ex.toString());
			}
		}
	}
}

