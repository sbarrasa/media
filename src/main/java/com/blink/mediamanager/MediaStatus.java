package com.blink.mediamanager;


public enum MediaStatus {
	empty,
	updateable,
	unchanged,
	added,
	updated,
	deleted,
	err;
	
	private Exception exception;
	
	public Exception getException() {
		if(this == err
			&& exception == null)
			exception = new MediaException("Unknown error");
		return exception;
	}

		
	public static MediaStatus err(Exception e) {
		MediaStatus m = err;
		m.exception = e;
		return m;
	}

	
	public String toString() {
		String msg = this.name();
		if(this == err)
			msg = msg + ": " +getException().getMessage();
		
		return msg;
	}
}
