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

import java.util.Enumeration;
import javax.microedition.pim.PIM;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.pdap.BlackBerryContactList;
import net.rim.blackberry.api.pdap.RemoteLookupListener;

final public class RemoteLookup {

	private BlackBerryContactList contactList;

	public RemoteLookup() {
		try {
			PIM pim = PIM.getInstance();
			// open the contact list for read/write permissions
			contactList = (BlackBerryContactList) pim.openPIMList(
					PIM.CONTACT_LIST, PIM.READ_WRITE);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public void doLookup(String contact) {
		contactList.lookup(contact, new RemoteLookupListener() {
			public void items(Enumeration results) {
				 // while (results.hasMoreElements()){  }
				// TODO not sure what would make sense to to with the results.
				// It seems ending in the addressbook screen is good enough.
			}
		});
	}

	public void doLinkedInLookup(String contact) {
		Browser.getDefaultSession().displayPage(
				"http://m.linkedin.com/members?search_term=" + contact
						+ "&filter=keywords&commit=Search");
	}

	public void do123peopleLookup(String contact) {
		Browser.getDefaultSession().displayPage(
				"http://m.123people.com/s/" + contact + "/");
	}

	public void doFacebookLookup(String contact) {
		Browser.getDefaultSession().displayPage("cod://ContextLookup/doFacebookLookup.html");
	}

}
