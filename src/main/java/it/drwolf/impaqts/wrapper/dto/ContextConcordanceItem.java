package it.drwolf.impaqts.wrapper.dto;

public class ContextConcordanceItem {
	private String window;
	private Integer tokens;
	private String term;
	private String attribute; // WORD, LEMMA, ...
	private String lemmaFilterType; // ALL, ANY, NONE

	public String getAttribute() {
		return this.attribute;
	}

	public String getLemmaFilterType() {
		return this.lemmaFilterType;
	}

	public String getTerm() {
		return this.term;
	}

	public Integer getTokens() {
		return this.tokens;
	}

	public String getWindow() {
		return this.window;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setLemmaFilterType(String item) {
		this.lemmaFilterType = item;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setTokens(Integer tokens) {
		this.tokens = tokens;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	public enum LemmaFilterType {
		ANY, ALL, NONE;

		public static boolean contain(String dir) {
			try {
				QueryRequest.RequestType.valueOf(dir);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
}
