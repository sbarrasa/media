package com.blink.mediamanager;

public interface Processable<T extends Enum<T>> {
	public ProcessResult<T> getProcessResult();
	public MediaTemplate setProcessResult(ProcessResult<T> uploadResult);
}
