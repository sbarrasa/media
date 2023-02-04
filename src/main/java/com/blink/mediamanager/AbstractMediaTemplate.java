package com.blink.mediamanager;


public abstract class AbstractMediaTemplate implements MediaTemplate{
	private String pathStr;
	private ProcessResult<MediaStatus> uploadResult = new ProcessResult<>();
	
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
	public ProcessResult<MediaStatus> getProcessResult(){
		return uploadResult;
	}
	
	@Override
	public MediaTemplate setProcessResult(ProcessResult<MediaStatus> uploadResult) {
		this.uploadResult = uploadResult;
		return this;
	}
	
	
	
}
