package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class ViewOptionRequest {
	private List<String> attributesKwic = new ArrayList<>();

	private List<String> attributesCtx = new ArrayList<>();

	public List<String> getAttributesCtx() {
		return attributesCtx;
	}

	public List<String> getAttributesKwic() {
		return attributesKwic;
	}

	public void setAttributesCtx(List<String> attributesCtx) {
		this.attributesCtx = attributesCtx;
	}

	public void setAttributesKwic(List<String> attributesKwic) {
		this.attributesKwic = attributesKwic;
	}
}
