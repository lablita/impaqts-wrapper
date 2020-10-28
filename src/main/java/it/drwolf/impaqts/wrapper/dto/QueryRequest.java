package it.drwolf.impaqts.wrapper.dto;

public class QueryRequest {
	private Integer start = 0;
	private Integer end = 0;
	private String word;

	public Integer getEnd() {
		return this.end;
	}

	public Integer getStart() {
		return this.start;
	}

	public String getWord() {
		return this.word;
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
