package it.drwolf.impaqts.wrapper.dto;

import it.drwolf.impaqts.wrapper.dto.corpusinfo.CorpusInfo;

import java.util.ArrayList;
import java.util.List;

public class QueryResponse {
	private Integer start = 0;
	private Integer end = 0;
	private String id; // uuid
	private List<KWICLine> kwicLines = new ArrayList<>();
	private List<CollocationItem> collocations = new ArrayList<>();
	private FrequencyItem frequency = new FrequencyItem();
	private List<DescResponse> descResponses = new ArrayList<>();
	private Boolean inProgress = Boolean.TRUE;
	private Integer currentSize;
	private List<String> metadataValues = new ArrayList<>();
	private WideContextResponse wideContextResponse = new WideContextResponse();
	private CorpusInfo corpusInfo = new CorpusInfo();
	private ErrorResponse errorResponse;
	private ReferencePositionResponse referencePositionResponse = new ReferencePositionResponse();
	private WordListResponse wordList = new WordListResponse();

	public QueryResponse(QueryRequest queryRequest) {
		this.id = queryRequest.getId();
		this.start = queryRequest.getStart();
		this.end = queryRequest.getEnd();
	}

	public List<CollocationItem> getCollocations() {
		return this.collocations;
	}

	public CorpusInfo getCorpusInfo() {
		return corpusInfo;
	}

	public Integer getCurrentSize() {
		return this.currentSize;
	}

	public List<DescResponse> getDescResponses() {
		return this.descResponses;
	}

	public Integer getEnd() {
		return end;
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

	public FrequencyItem getFrequency() {
		return this.frequency;
	}

	public String getId() {
		return id;
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

	public ReferencePositionResponse getReferencePositionResponse() {
		return referencePositionResponse;
	}

	public Integer getStart() {
		return start;
	}

	public WideContextResponse getWideContextResponse() {
		return wideContextResponse;
	}

	public WordListResponse getWordList() {
		return wordList;
	}

	public void setCollocations(List<CollocationItem> collocations) {
		this.collocations = collocations;
	}

	public void setCorpusInfo(CorpusInfo corpusInfo) {
		this.corpusInfo = corpusInfo;
	}

	public void setCurrentSize(Integer currentSize) {
		this.currentSize = currentSize;
	}

	public void setDescResponses(List<DescResponse> descResponses) {
		this.descResponses = descResponses;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public void setFrequency(FrequencyItem frequency) {
		this.frequency = frequency;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setReferencePositionResponse(ReferencePositionResponse referencePositionResponse) {
		this.referencePositionResponse = referencePositionResponse;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setWideContextResponse(WideContextResponse wideContextResponse) {
		this.wideContextResponse = wideContextResponse;
	}

	public void setWordList(WordListResponse wordList) {
		this.wordList = wordList;
	}
}
