/*
 * Copyright (C) 2024
 * EMMACorpus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.drwolf.impaqts.wrapper.dto;

import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.KWICLines;
import it.drwolf.impaqts.wrapper.utils.ContextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KWICLine {
	private String ref;
	private List<String> leftContext;
	private String kwic;
	private List<String> rightContext;
	private Long pos;
	private Long docNumber;
	private String startTime;
	private String videoUrl;
	private Map<String, String> references = new HashMap<>();

	public KWICLine(String ref, List<String> leftContext, String kwic, List<String> rightContext) {
		this.ref = ref;
		this.leftContext = leftContext;
		this.kwic = kwic;
		this.rightContext = rightContext;
	}

	public KWICLine(KWICLines kwicLines, Corpus corpus, boolean implicitRequest, boolean impaqts) {
		this.ref = kwicLines.get_refs();
		//Remove HTML tag from left...
		List<String> leftCtxRow = kwicLines.get_left();
		this.leftContext = leftCtxRow.stream()
				.map(ln -> ContextUtils.removeHtmlTags(ln, impaqts))
				.collect(Collectors.toList());
		//...and right context
		List<String> rightCtxRow = kwicLines.get_right();
		this.rightContext = rightCtxRow.stream()
				.map(ln -> ContextUtils.removeHtmlTags(ln, impaqts))
				.collect(Collectors.toList());

		this.kwic = ContextUtils.removeHtmlTags(ContextUtils.strip_tags(kwicLines.get_kwic()), impaqts);
		this.pos = kwicLines.get_pos();
		this.docNumber = corpus.get_struct(corpus.get_conf("DOCSTRUCTURE")).num_at_pos(pos);
		if (impaqts) {
			String[] fullRefs = corpus.get_conf("FULLREF").split(",");
			for (String fullRef : fullRefs) {
				try {
					String ref = corpus.get_attr(fullRef).pos2str(pos);
					this.references.put(fullRef, ref);
				} catch (Throwable fae) {
					// File Access Error
					System.out.printf("### Error accessing corpus for fullRef: " + fullRef);
				}
			}
		}
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
		return this.getRef().equals(kwicLine.getRef()) && this.getLeftContext().equals(kwicLine.getLeftContext())
				&& this.getKwic().equals(kwicLine.getKwic()) && this.getRightContext()
				.equals(kwicLine.getRightContext());
	}

	public Long getDocNumber() {
		return docNumber;
	}

	public String getKwic() {
		return this.kwic;
	}

	public List<String> getLeftContext() {
		return this.leftContext;
	}

	public Long getPos() {
		return this.pos;
	}

	public String getRef() {
		return this.ref;
	}

	public Map<String, String> getReferences() {
		return references;
	}

	public List<String> getRightContext() {
		return this.rightContext;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public String getVideoUrl() {
		return this.videoUrl;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getRef(), this.getLeftContext(), this.getKwic(), this.getRightContext());
	}

	public void setDocNumber(Long docNumber) {
		this.docNumber = docNumber;
	}

	public void setKwic(String kwic) {
		this.kwic = kwic;
	}

	public void setLeftContext(List<String> leftContext) {
		this.leftContext = leftContext;
	}

	public void setPos(Long pos) {
		this.pos = pos;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setReferences(Map<String, String> references) {
		this.references = references;
	}

	public void setRightContext(List<String> rightContext) {
		this.rightContext = rightContext;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
}
