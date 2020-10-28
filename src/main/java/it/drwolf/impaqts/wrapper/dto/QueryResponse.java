package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class QueryResponse {
	private Boolean inProgress = Boolean.TRUE;
	private List<KWICLine> kwicLines = new ArrayList<>();
	private Integer currentSize;

	public Integer getCurrentSize() {
		return this.currentSize;
	}

	public Boolean getInProgress() {
		return this.inProgress;
	}

	public List<KWICLine> getKwicLines() {
		return this.kwicLines;
	}

	public void setCurrentSize(Integer currentSize) {
		this.currentSize = currentSize;
	}

	public void setInProgress(Boolean inProgress) {
		this.inProgress = inProgress;
	}

	public void setKwicLines(List<KWICLine> kwicLines) {
		this.kwicLines = kwicLines;
	}
}
