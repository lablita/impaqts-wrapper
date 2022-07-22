package it.drwolf.impaqts.wrapper.dto;

import com.sketchengine.manatee.KWICLines;
import it.drwolf.impaqts.wrapper.utils.ContextUtils;

import java.util.List;
import java.util.Objects;

public class KWICLine {
	private String ref;
	private List<String> leftContext;
	private String kwic;
	private List<String> rightContext;

	public KWICLine(String ref, List<String> leftContext, String kwic, List<String> rightContext) {
		this.ref = ref;
		this.leftContext = leftContext;
		this.kwic = kwic;
		this.rightContext = rightContext;
	}

	public KWICLine(KWICLines kwicLines) {
		this.ref = kwicLines.get_refs();
		this.leftContext = kwicLines.get_left();
		this.rightContext = kwicLines.get_right();
		this.kwic = ContextUtils.strip_tags(kwicLines.get_kwic());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		KWICLine kwicLine = (KWICLine) o;
		return this.getRef().equals(kwicLine.getRef()) && this.getLeftContext()
				.equals(kwicLine.getLeftContext()) && this.getKwic()
				.equals(kwicLine.getKwic()) && this.getRightContext().equals(kwicLine.getRightContext());
	}

	public String getKwic() {
		return this.kwic;
	}

	public List<String> getLeftContext() {
		return this.leftContext;
	}

	public String getRef() {
		return this.ref;
	}

	public List<String> getRightContext() {
		return this.rightContext;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getRef(), this.getLeftContext(), this.getKwic(), this.getRightContext());
	}

	public void setKwic(String kwic) {
		this.kwic = kwic;
	}

	public void setLeftContext(List<String> leftContext) {
		this.leftContext = leftContext;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setRightContext(List<String> rightContext) {
		this.rightContext = rightContext;
	}
}
