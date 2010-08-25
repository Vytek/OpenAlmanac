package it.almanac;

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
