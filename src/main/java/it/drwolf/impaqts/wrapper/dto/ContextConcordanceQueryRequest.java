package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class ContextConcordanceQueryRequest {
	private List<ContextConcordanceItem> items = new ArrayList<>();

	public List<ContextConcordanceItem> getItems() {
		return this.items;
	}

	public void setItems(List<ContextConcordanceItem> items) {
		this.items = items;
	}
}
