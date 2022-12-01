package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class KWICLineDTO {
	private Long tokNum;
	private int hitlen;
	private List<String> refs = new ArrayList<>();
	private List<String> tblRefs = new ArrayList<>();
	private List<TokenClassDTO> leftLabel = new ArrayList<>();
	private List<TokenClassDTO> kwic = new ArrayList<>();
	private List<TokenClassDTO> rightLabel = new ArrayList<>();
	private List<String> links = new ArrayList<>();
	private String lineGroup;
	private int lineGroupId;

	public int getHitlen() {
		return this.hitlen;
	}

	public List<TokenClassDTO> getKwic() {
		return this.kwic;
	}

	public List<TokenClassDTO> getLeftLabel() {
		return this.leftLabel;
	}

	public String getLineGroup() {
		return this.lineGroup;
	}

	public int getLineGroupId() {
		return this.lineGroupId;
	}

	public List<String> getLinks() {
		return this.links;
	}

	public List<String> getRefs() {
		return this.refs;
	}

	public List<TokenClassDTO> getRightLabel() {
		return this.rightLabel;
	}

	public List<String> getTblRefs() {
		return this.tblRefs;
	}

	public Long getTokNum() {
		return this.tokNum;
	}

	public void setHitlen(int hitlen) {
		this.hitlen = hitlen;
	}

	public void setKwic(List<TokenClassDTO> kwic) {
		this.kwic = kwic;
	}

	public void setLeftLabel(List<TokenClassDTO> leftLabel) {
		this.leftLabel = leftLabel;
	}

	public void setLineGroup(String lineGroup) {
		this.lineGroup = lineGroup;
	}

	public void setLineGroupId(int lineGroupId) {
		this.lineGroupId = lineGroupId;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public void setRefs(List<String> refs) {
		this.refs = refs;
	}

	public void setRightLabel(List<TokenClassDTO> rightLabel) {
		this.rightLabel = rightLabel;
	}

	public void setTblRefs(List<String> tblRefs) {
		this.tblRefs = tblRefs;
	}

	public void setTokNum(Long tokNum) {
		this.tokNum = tokNum;
	}
}
