package it.drwolf.impaqts.wrapper.dto;

import it.drwolf.impaqts.wrapper.query.QueryPattern;

public class QueryRequest {
	private String corpusMetadatum;
	private Integer start = 0;
	private Integer end = 0;
	private String word;
	private String corpus;
	private String cql;
	private boolean queryInCql = false;
	private QueryPattern queryPattern;
	private CollocationQueryRequest collocationQueryRequest;
	private SortQueryRequest sortQueryRequest;
	private FrequencyQueryRequest frequencyQueryRequest;
	private ContextConcordanceQueryRequest contextConcordanceQueryRequest;
	private WideContextRequest wideContextRequest;
	private ConcordanceFromCollocationParameters concordanceFromCollocationParameters;
	private String queryType;

	public CollocationQueryRequest getCollocationQueryRequest() {
		return this.collocationQueryRequest;
	}

	public ConcordanceFromCollocationParameters getConcordanceFromCollocationParameters() {
		return this.concordanceFromCollocationParameters;
	}

	public ContextConcordanceQueryRequest getContextConcordanceQueryRequest() {
		return this.contextConcordanceQueryRequest;
	}

	public String getCorpus() {
		return this.corpus;
	}

	public String getCorpusMetadatum() {
		return this.corpusMetadatum;
	}

	public String getCql() {
		return this.cql;
	}

	public Integer getEnd() {
		return this.end;
	}

	public FrequencyQueryRequest getFrequencyQueryRequest() {
		return this.frequencyQueryRequest;
	}

	public boolean getQueryInCql() {
		return this.queryInCql;
	}

	public QueryPattern getQueryPattern() {
		return this.queryPattern;
	}

	public String getQueryType() {
		return this.queryType;
	}

	public SortQueryRequest getSortQueryRequest() {
		return this.sortQueryRequest;
	}

	public Integer getStart() {
		return this.start;
	}

	public WideContextRequest getWideContextRequest() {
		return this.wideContextRequest;
	}

	public String getWord() {
		return this.word;
	}

	public boolean isQueryInCql() {
		return this.queryInCql;
	}

	public void setCollocationQueryRequest(CollocationQueryRequest collocationQueryRequest) {
		this.collocationQueryRequest = collocationQueryRequest;
	}

	public void setConcordanceFromCollocationParameters(
			ConcordanceFromCollocationParameters concordanceFromCollocationParameters) {
		this.concordanceFromCollocationParameters = concordanceFromCollocationParameters;
	}

	public void setContextConcordanceQueryRequest(ContextConcordanceQueryRequest contextConcordanceQueryRequest) {
		this.contextConcordanceQueryRequest = contextConcordanceQueryRequest;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setCorpusMetadatum(String corpusMetadatum) {
		this.corpusMetadatum = corpusMetadatum;
	}

	public void setCql(String cql) {
		this.cql = cql;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public void setFrequencyQueryRequest(FrequencyQueryRequest frequencyQueryRequest) {
		this.frequencyQueryRequest = frequencyQueryRequest;
	}

	public void setQueryInCql(boolean queryInCql) {
		this.queryInCql = queryInCql;
	}

	public void setQueryPattern(QueryPattern queryPattern) {
		this.queryPattern = queryPattern;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public void setSortQueryRequest(SortQueryRequest sortQueryRequest) {
		this.sortQueryRequest = sortQueryRequest;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setWideContextRequest(WideContextRequest wideContextRequest) {
		this.wideContextRequest = wideContextRequest;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public enum RequestType {
		VISUAL_QUERY_REQUEST, TEXTUAL_QUERY_REQUEST, CONTEXT_QUERY_REQUEST, COLLOCATION_REQUEST, QUICK_SORT_REQUEST, MULTI_FREQUENCY_QUERY_REQUEST, CONC_FREQUENCY_QUERY_REQUEST, POSITIVE_FREQUEQUENCY_CONCORDANCE_QUERY_REQUEST, WIDE_CONTEXT_QUERY_REQUEST, CORPUS_INFO;

		public static boolean contain(String dir) {
			try {
				RequestType.valueOf(dir);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
}
