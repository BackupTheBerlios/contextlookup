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

import java.util.Vector;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;

final class LookupContactMenuItem extends ApplicationMenuItem{
	//add new menu item to the "view email" screen of Blackberry Email
    private Message selectedMessage;
	private Vector observers;
    	
	public LookupContactMenuItem(int order){
        super(0x01010000); // place it after "next unopened item" close to "view contact" BB own menu item
        observers= new Vector();
    }
	public void addObserver(ChooseContactScreen esls){
		observers.addElement(esls);
	}
/*	public void removeObserver(ChooseContactScreen esls){
		observers.removeElement(esls); 
	}*/
	     //Go through vector of observers and call update on them  
      public void notifyObservers(){
    	  for(int i=0;i<observers.size();i++){
	              ((ChooseContactScreen)observers.elementAt(i)).update(selectedMessage);  
   	       }  
	 }  

	public Object run(Object context){
	   selectedMessage = (Message) context;  
       notifyObservers();
      return context;
    }
	public String toString(){
	    //toString should return the string we want to use as the label of the menuItem
		return "Lookup Contact";
    }
}
