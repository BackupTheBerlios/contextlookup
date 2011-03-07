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

public interface ObserverInterface {
	public void update(net.rim.blackberry.api.mail.Message message); //email and PIN
	public void update(javax.wireless.messaging.TextMessage message); //SMS
	public void update(javax.wireless.messaging.MultipartMessage message); //MMS, does not work, bug in API 5.0 
	public void update(net.rim.blackberry.api.phone.phonelogs.PhoneLogs phoneLog); //calls, does not work, bug in API 5.0
}
