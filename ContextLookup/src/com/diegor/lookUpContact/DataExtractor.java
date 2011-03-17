package com.diegor.lookUpContact;

import javax.microedition.pim.Contact;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;

import com.diego.lookUpContact.LookUpContactResource;

final public class DataExtractor implements ObserverInterface, LookUpContactResource {
	// Create a ResourceBundle object to contain the localized resources.
	private static ResourceBundle rb = ResourceBundle.getBundle(BUNDLE_ID,
			BUNDLE_NAME);
	// some help to be able to use the "for" cycle for message fields extraction
	final int[] emailRecipientTypeIterator = { Message.RecipientType.FROM,
			Message.RecipientType.TO, Message.RecipientType.CC,
			Message.RecipientType.BCC, Message.RecipientType.REPLY_TO,
			Message.RecipientType.SENDER, };
	final int emailRecipientTypeListLength = emailRecipientTypeIterator.length;
	String[] emailFieldNameIterator = { "From", "To", "CC", "BCC", "Reply_To",
			"Sender", };

	// TODO rewrite this part with a Vector instead of Array
	final public void update(final net.rim.blackberry.api.mail.Message message) {
		String[] contacts = new String[0];
		// now we collect all Addresses and Names from the message.
		// TODO rewrite this part with a Vector instead of Array
		for (int i = emailRecipientTypeListLength - 1; i >= 0; --i) {
			// this iteration style should speed up a little
			try {
				Address[] currentAddress = message
						.getRecipients(emailRecipientTypeIterator[i]);
				int numberOfRecipients = currentAddress.length;
				if (numberOfRecipients > 0) {
					for (int j = numberOfRecipients - 1; j >= 0; --j) {
						String name = currentAddress[j].getName();
						Arrays.add(contacts, (name != null) ? name
								: currentAddress[j].getAddr());
					}
				}
			} catch (MessagingException e) {
				Arrays
						.add(contacts, rb
								.getString(I18N_ERROR_CANT_EXTRCT_CNTCT));
			}
		}
		new LookUpContactScreen(contacts);
	}

	final public void update(
			final javax.wireless.messaging.MultipartMessage message) {
		String[] contacts = new String[0];
		// now we collect all Addresses and Names from the message.
		// TODO rewrite this part with a Vector instead of Array
		for (int i = emailRecipientTypeListLength - 1; i >= 0; --i) {
			String[] currentAddress = message
					.getAddresses(emailFieldNameIterator[i]);
			if (currentAddress != null) {
				int numberOfRecipients = currentAddress.length;
				for (int j = numberOfRecipients - 1; j >= 0; --j) {
					Arrays.add(contacts, currentAddress[j]);
				}
			}
		}
		if (contacts.length == 0) {
			contacts[0] = rb.getString(I18N_ERROR_CANT_EXTRCT_CNTCT);
		}
		new  LookUpContactScreen(contacts);
	}

	final public void update(final javax.wireless.messaging.TextMessage message) {
		String[] contacts = new String[1];
		String address = message.getAddress();
		contacts[0] = (address != null) ? address : rb
				.getString(I18N_ERROR_CANT_EXTRCT_CNTCT);
		new  LookUpContactScreen(contacts);
	}

	final public void update(
			final net.rim.blackberry.api.phone.phonelogs.PhoneCallLog phoneLog) {
		String[] contacts = new String[1];
		PhoneCallLogID participant = phoneLog.getParticipant();
		String name = participant.getName();
		contacts[0] = (name != null) ? name : participant.getNumber();
		new  LookUpContactScreen(contacts);
		return;
	}

	/*
	 * for the conference calls } else if (callLogEntry instanceof
	 * ConferencePhoneCallLog) { ConferencePhoneCallLog cpcl =
	 * (ConferencePhoneCallLog) callLogEntry; int numberOfParticipants =
	 * cpcl.numberOfParticipants(); if (numberOfParticipants > 0) { for (int j =
	 * 0; j < numberOfParticipants; ++j) { participant =
	 * cpcl.getParticipantAt(j); String name = participant.getName();
	 * Arrays.add(contacts, (name != null) ? name : participant .getNumber()); }
	 * } } else { contacts[0] = "Unable to extract contact name or phone"; }
	 */

	final public void update(final javax.microedition.pim.Contact contact) {
		String[] contacts = new String[1];
		// TODO write this code
		contacts[0] = getDisplayName(contact);
		if (contacts[0] == null) {
			contacts[0] = rb.getString(I18N_ERROR_CANT_EXTRCT_CNTCT);
		}
		new  LookUpContactScreen(contacts);
	}

	final static String getDisplayName(Contact contact) {
		// http://supportforums.blackberry.com/t5/user/viewprofilepage/user-id/93709
		// http://supportforums.blackberry.com/t5/Java-Development/PIM-gt-Contact-gt-Address-Fields/m-p/553324#M112593
		if (contact == null) {
			return null;
		}
		String displayName = null;
		// First, see if there is a meaningful name set for the contact.
		if (contact.countValues(Contact.NAME) > 0) {
			final String[] name = contact.getStringArray(Contact.NAME, 0);
			final String firstName = name[Contact.NAME_GIVEN];
			final String lastName = name[Contact.NAME_FAMILY];
			if (firstName != null && lastName != null) {
				displayName = firstName + " " + lastName;
			} else if (firstName != null) {
				displayName = firstName;
			} else if (lastName != null) {
				displayName = lastName;
			}
			if (displayName != null) {
				final String namePrefix = name[Contact.NAME_PREFIX];
				if (namePrefix != null) {
					displayName = namePrefix + " " + displayName;
				}
				return displayName;
			}
		}
		// If not, use the company name.
		if (contact.countValues(Contact.ORG) > 0) {
			final String companyName = contact.getString(Contact.ORG, 0);
			if (companyName != null) {
				return companyName;
			}
		}
		return displayName;
	}

}
