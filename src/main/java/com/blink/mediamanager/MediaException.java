package com.blink.mediamanager;


public class MediaException extends Exception {
	private static final long serialVersionUID = 1L;

	public MediaException(String msg) {
		super(msg);
	}
	
	public MediaException(Exception e) {
		super(e);
	}
}
