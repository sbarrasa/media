package com.blink.mediamanager;


public class MediaError extends Error {
	private static final long serialVersionUID = 1L;

	public MediaError(String msg) {
		super(msg);
	}
	
	public MediaError(Exception e) {
		super(e);
	}
}
