package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class FrequencyOutput {
	private List<KWICLineDTO> lines = new ArrayList<>();
	private int concSize;
	private int fromP;

	public int getConcSize() {
		return this.concSize;
	}

	public int getFromP() {
		return this.fromP;
	}

	public List<KWICLineDTO> getLines() {
		return this.lines;
	}

	public void setConcSize(int concSize) {
		this.concSize = concSize;
	}

	public void setFromP(int fromP) {
		this.fromP = fromP;
	}

	public void setLines(List<KWICLineDTO> lines) {
		this.lines = lines;
	}
}
