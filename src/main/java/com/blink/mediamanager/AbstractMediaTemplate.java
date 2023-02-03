package com.blink.mediamanager;

import java.util.EnumMap;

public abstract class AbstractMediaTemplate implements MediaTemplate{
	private String pathStr;
	private EnumMap<MediaStatus, Integer> uploadResult = new EnumMap<>(MediaStatus.class);
	
	@Override
	public MediaTemplate setPath(String pathStr) {
		this.pathStr = pathStr;
		return this;
	}

	@Override
	public String getPath() {
		return pathStr;
	}

	@Override
	public EnumMap<MediaStatus, Integer> getUploadResult(){
		return uploadResult;
	}
	

	
	
	
}
