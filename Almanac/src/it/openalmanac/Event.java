//OpenAlmanac
//Copyright (C) 2011 Enrico Speranza
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package it.openalmanac;

public class Event {
	private String eventname;
	private String description;
	private int photoRes;

	public Event(String eventname, String description, int photoRes) {
		super();
		this.eventname = eventname;
		this.description = description;
		this.photoRes = photoRes;
	}

	public String getEventName() {
		return eventname;
	}

	public String getDescription() {
		return description;
	}

	public int getPhotoRes() {
		return photoRes;
	}

	/**
	 * @param eventname
	 *            the eventname to set
	 */
	public void setEventName(String eventname) {
		this.eventname = eventname;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param photoRes
	 *            the photoRes to set
	 */
	public void setPhotoRes(int photoRes) {
		this.photoRes = photoRes;
	}
}
