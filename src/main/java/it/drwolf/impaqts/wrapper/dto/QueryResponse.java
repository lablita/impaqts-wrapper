package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class QueryResponse {
	private List<KWICLine> kwicLines = new ArrayList<>();
	private List<CollocationItem> collocations = new ArrayList<>();
	private FrequencyItem frequency = new FrequencyItem();
	private List<DescResponse> descResponses = new ArrayList<>();
	private Boolean inProgress = Boolean.TRUE;
	private Integer currentSize;
	private List<String> metadataValues = new ArrayList<>();

	private WideContextResponse wideContextResponse = new WideContextResponse();

	public List<CollocationItem> getCollocations() {
		return this.collocations;
	}

	public Integer getCurrentSize() {
		return this.currentSize;
	}

	public List<DescResponse> getDescResponses() {
		return this.descResponses;
	}

	public FrequencyItem getFrequency() {
		return this.frequency;
	}

	public Boolean getInProgress() {
		return this.inProgress;
	}

	public List<KWICLine> getKwicLines() {
		return this.kwicLines;
	}

	public List<String> getMetadataValues() {
		return this.metadataValues;
	}

	public WideContextResponse getWideContextResponse() {
		return wideContextResponse;
	}

	public void setCollocations(List<CollocationItem> collocations) {
		this.collocations = collocations;
	}

	public void setCurrentSize(Integer currentSize) {
		this.currentSize = currentSize;
	}

	public void setDescResponses(List<DescResponse> descResponses) {
		this.descResponses = descResponses;
	}

	public void setFrequency(FrequencyItem frequency) {
		this.frequency = frequency;
	}

	public void setInProgress(Boolean inProgress) {
		this.inProgress = inProgress;
	}

	public void setKwicLines(List<KWICLine> kwicLines) {
		this.kwicLines = kwicLines;
	}

	public void setMetadataValues(List<String> metadataValues) {
		this.metadataValues = metadataValues;
	}

	public void setWideContextResponse(WideContextResponse wideContextResponse) {
		this.wideContextResponse = wideContextResponse;
	}
}
