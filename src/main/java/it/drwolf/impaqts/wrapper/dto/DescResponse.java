package it.drwolf.impaqts.wrapper.dto;

public class DescResponse {
	private String niceArg;
	private String toUrl;
	private float rel;
	private String arg;
	private String op;

	private String attribute;
	private String term;
	private String position;
	private String window;
	private Integer tokens;
	private long size;

	public String getArg() {
		return this.arg;
	}

	public String getAttribute() {
		return this.attribute;
	}

	public String getNiceArg() {
		return this.niceArg;
	}

	public String getOp() {
		return this.op;
	}

	public String getPosition() {
		return this.position;
	}

	public float getRel() {
		return this.rel;
	}

	public long getSize() {
		return this.size;
	}

	public String getTerm() {
		return this.term;
	}

	public String getToUrl() {
		return this.toUrl;
	}

	public Integer getTokens() {
		return this.tokens;
	}

	public String getWindow() {
		return this.window;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setNiceArg(String niceArg) {
		this.niceArg = niceArg;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setRel(float rel) {
		this.rel = rel;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

	public void setTokens(Integer tokens) {
		this.tokens = tokens;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	public enum OperationType {
		NEGATIVE_FILTER, POSITIVE_FILTER, QUERY;

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

