package com.blink.mediamanager;

import java.util.Map;
import java.util.HashMap;

public class ProcessResult <T extends Enum<T>>{
	private Integer cntTotal = 0;
	private Integer cntProcessed = 0;
	private Map<T, Integer> ProcessStatus = new HashMap<>();
	
	public Integer cntToProcess() {
		return cntTotal() - cntProcessed();
	}
	
	public Integer cntTotal() {
		return cntTotal;
	}
	
	public Integer cntProcessed() {
		return cntTotal;
	}
	
	public Map<T, Integer> getProcessStatus(){
		return ProcessStatus;
	}
	
	public ProcessResult<T> setTotal(Integer value) {
		this.cntTotal = value;
		return this;
	}
		
	public Integer incTotal() {
		return ++cntTotal;
	}
	
	
	public Integer incProcessed(T key) {
		Integer cnt = getProcessStatus().get(key);
			if(cnt== null)
				cnt= 0;
			
		getProcessStatus().put(key, ++cnt);
		cntProcessed++;
		
		return cnt;
	}
	
	public String toString() {
		return String.format("Total: %d, Processed: %d, To process: %d, Status: %s", 
				cntTotal(),
				cntProcessed(),
				cntToProcess(),
				ProcessStatus);
	}
}
