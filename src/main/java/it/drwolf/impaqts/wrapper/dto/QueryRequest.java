package it.drwolf.impaqts.wrapper.dto;

public class QueryRequest {
	private String corpusMetadatum;
	private Integer start = 0;
	private Integer end = 0;
	private String word;
	private String corpus;

	public String getCorpus() {
		return this.corpus;
	}

	public String getCorpusMetadatum() {
		return this.corpusMetadatum;
	}

	public Integer getEnd() {
		return this.end;
	}

	public Integer getStart() {
		return this.start;
	}

	public String getWord() {
		return this.word;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setCorpusMetadatum(String corpusMetadatum) {
		this.corpusMetadatum = corpusMetadatum;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
