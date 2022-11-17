package it.drwolf.impaqts.wrapper.executor.corpusinfo;

import com.sketchengine.manatee.Corpus;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.AlignedDetail;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.CorpusAttribute;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.CorpusInfo;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.CorpusInfoAttribute;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.CorpusInfoStructure;
import it.drwolf.impaqts.wrapper.dto.corpusinfo.PosItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CorpusInfoRetriever {
	public CorpusInfo retrieveCorpusInfo(String corpusName) {
		Corpus corpus = new Corpus(corpusName);
		CorpusInfo corpusInfo = new CorpusInfo();
		corpusInfo.setAligned(this.getAlignedValue(corpus));
		corpusInfo.setAlignedDetails(this.getAlignedDetails(corpus, corpusInfo.getAligned()));
		corpusInfo.setAlsizes(this.getAlSizes(corpus));
		corpusInfo.setAttributes(this.getCorpusAttributes(corpus));
		try {
			corpusInfo.setCompiled(this.getCompiled(corpus));
		} catch (IOException e) {
			System.err.println("### Unable to retrieve compiled info");
		}
		corpusInfo.setDefaultattr(corpus.get_conf("DEFAULTATTR"));
		corpusInfo.setDeffilterlink("1".equals(corpus.get_conf("DEFFILTERLINK")));
		corpusInfo.setDiachronic(this.getDiachronic(corpus));
		corpusInfo.setDocStructure(corpus.get_conf("DOCSTRUCTURE"));
		corpusInfo.setEncoding(corpus.get_conf("ENCODING"));
		corpusInfo.setErrsetdoc(corpus.get_conf("ERRSETDOC"));
		corpusInfo.setFreqttattrs(this.getFreqTtAttrs(corpus));
		// TODO gramrels
		corpusInfo.setInfo(corpus.get_conf("INFO"));
		corpusInfo.setInfohref(corpus.get_conf("INFOHREF"));
		List<String> structList = getListFromSplitValues(corpus.get_conf("STRUCTLIST"));
		boolean isErrorCorpus = structList.contains("err") && structList.contains("corr");
		corpusInfo.setErrorCorpus(isErrorCorpus);
		corpusInfo.setLang(corpus.get_conf("LANGUAGE"));
		corpusInfo.setLposlist(this.corpConfPairs(corpus, "LPOSLIST"));
		corpusInfo.setName(corpus.get_conf("NAME"));
		corpusInfo.setNewversion(corpus.get_conf("NEWVERSION"));
		corpusInfo.setRighttoleft("1".equals(corpus.get_conf("RIGHTTOLEFT")));
		corpusInfo.setShortref(corpus.get_conf("SHORTREF"));
		corpusInfo.setSizes(this.getSizes(corpus));
		corpusInfo.setStructctx(corpus.get_conf("STRUCTCTX"));
		corpusInfo.setStructures(this.retrieveStructures(corpus, structList));
		// TODO subcorpattrs
		corpusInfo.setTagsetdoc(corpus.get_conf("TAGSETDOC"));
		corpusInfo.setTermdef(corpus.get_conf("TERMDEF"));
		corpusInfo.setUnicameral("1".equals(corpus.get_conf("NOLETTERCASE")));
		corpusInfo.setWposlist(this.corpConfPairs(corpus, "WPOSLIST"));
		corpusInfo.setWsattr(corpus.get_conf("WSATTR"));
		corpusInfo.setWsdef(corpus.get_conf("WSDEF"));
		return corpusInfo;
	}

	private List<CorpusInfoStructure> retrieveStructures(Corpus corpus, List<String> structList) {
		final List<String> structattrList = getListFromSplitValues(corpus.get_conf("STRUCTATTRLIST"));
		return structList.stream().map(struct -> {
			CorpusInfoStructure cis = new CorpusInfoStructure();
			cis.setName(struct);
			cis.setLabel(corpus.get_conf(struct + ".LABEL"));
			structattrList.stream().forEach(sa -> {
				String[] token = sa.split("\\.");
				if (struct.equals(token[0])) {
					CorpusInfoAttribute cia = new CorpusInfoAttribute();
					cia.setName(token[1]);
					cia.setLabel(corpus.get_conf(sa + ".LABEL"));
					cia.setDynamic(corpus.get_conf(sa + ".DYNAMIC"));
					cia.setFromattr(corpus.get_conf(sa + ".FROMATTR"));
					cis.getAttributes().add(cia);
				}
			});
			return cis;
		}).collect(Collectors.toList());
	}

	private List<String> getFreqTtAttrs(Corpus corpus) {
		String freqttattrs = corpus.get_conf("FREQTTATTRS");
		if (freqttattrs != null && !freqttattrs.isEmpty()) {
			freqttattrs = freqttattrs.replaceAll("\\|", ",").replaceAll("\\*", "");
			return getListFromSplitValues(freqttattrs);
		}
		return Collections.emptyList();
	}

	private static boolean filterIfNotEmpty(String d) {
		if (d == null || d.isEmpty() || d.equals("0") || d.equals("None")) {
			return false;
		}
		return true;
	}

	private List<String> getDiachronic(Corpus corpus) {
		String diachronic = corpus.get_conf("DIACHRONIC");
		if (diachronic != null && !diachronic.isEmpty()) {
			getListFromSplitValues(diachronic);
		}
		return Collections.emptyList();
	}

	private static List<String> getListFromSplitValues(String diachronic) {
		return Arrays.asList(diachronic.split(",")).stream().filter(d -> {
			return filterIfNotEmpty(d);
		}).collect(Collectors.toList());
	}

	private Date getCompiled(Corpus corpus) throws IOException {
		String pathString = corpus.get_conf("PATH");
		final Path path = Path.of(pathString);
		if (Files.exists(path)) {
			FileTime fileTime = (FileTime) Files.getAttribute(path, "creationTime");
			Date d = new Date();
			d.setTime(fileTime.toMillis());
			return d;
		}
		return null;
	}

	private List<CorpusAttribute> getCorpusAttributes(Corpus corpus) {
		List<String> attrList = Arrays.stream(corpus.get_conf("ATTRLIST").split(",")).collect(Collectors.toList());
		return attrList.stream().map(item -> {
			CorpusAttribute cia = new CorpusAttribute();
			cia.setName(item);
			cia.setLabel(corpus.get_conf(item + ".LABEL"));
			// struct_attr_stats sempre = 1
			cia.setIdRange(corpus.get_attr(item).id_range());
			cia.setDynamic(corpus.get_conf(item + ".DYNAMIC"));
			cia.setFromattr(corpus.get_conf(item + ".FROMATTR"));
			return cia;
		}).collect(Collectors.toList());
	}

	private Map<String, String> getSizes(Corpus corpus) {
		Map<String, String> sizesMap = new HashMap<>();
		String sizes = corpus.get_sizes();
		if (sizes != null && !sizes.isEmpty()) {
			String[] sizesLines = sizes.split("\n");
			Arrays.stream(sizesLines).forEach(sizeLine -> {
				String[] sls = sizeLine.split(" ");
				if (sls.length == 2) {
					sizesMap.put(sls[0].trim(), sls[1].trim());
				}
			});
		}
		return Collections.emptyMap();
	}

	private List<List<String>> getAlSizes(Corpus corpus) {
		List<List<String>> alSizes = new ArrayList<>();
		String sizes = corpus.get_sizes();
		if (sizes != null && !sizes.isEmpty()) {
			String[] sizesLines = sizes.split("\n");
			Arrays.stream(sizesLines).forEach(sizeLine -> {
				String[] sls = sizeLine.split(" ");
				if (sls.length == 4) {
					List<String> alSize = new ArrayList<>();
					alSize.add(sls[2]);
					alSize.add(sls[1]);
					alSize.add(sls[3]);
					alSizes.add(alSize);
				}
			});
		}
		return Collections.emptyList();
	}

	private List<AlignedDetail> getAlignedDetails(Corpus corpus, List<String> aligned) {
		List<AlignedDetail> alignedDetails = new ArrayList<>();
		aligned.forEach(al -> {
			Corpus alCorpus = new Corpus(al);
			String attrListString = alCorpus.get_conf("ATTRLIST");
			if (attrListString != null && !attrListString.isEmpty()) {
				AlignedDetail alignedDetail = new AlignedDetail();
				final String[] attrs = attrListString.split(",");
				List<String> poslist = this.corpConfPairs(corpus, "WPOSLIST");
				List<String> lposlist = poslist;
				Optional<String> lempos = Arrays.stream(attrs).filter(att -> att.equals("lempos")).findFirst();
				if (lempos.isPresent()) {
					lposlist = this.corpConfPairs(corpus, "LPOSLIST");
				}
				alignedDetail.setName(alCorpus.get_conf("NAME"));
				alignedDetail.setLanguage_name(alCorpus.get_conf("LANGUAGE"));
				alignedDetail.setWposlist(poslist.stream().map(pl -> {
					PosItem pi = new PosItem();
					pi.setN(pl.substring(0, 1));
					pi.setV(pl.substring(1, 2));
					return pi;
				}).collect(Collectors.toList()));
				alignedDetail.setWposlist(lposlist.stream().map(pl -> {
					PosItem pi = new PosItem();
					pi.setN(pl.substring(0, 1));
					pi.setV(pl.substring(1, 2));
					return pi;
				}).collect(Collectors.toList()));
				alignedDetail.setHasCase(!alCorpus.get_conf("NOLETTERCASE").equals(1));
				Optional<String> lemmas = Arrays.stream(attrs).filter(att -> att.equals("lemma")).findFirst();
				alignedDetail.setHasLemma(lempos.isPresent() || lemmas.isPresent());
				alignedDetail.setTagsetdoc(alCorpus.get_conf("TAGSETDOC"));
			}
		});
		return alignedDetails;
	}

	private List<String> corpConfPairs(Corpus corpus, String label) {
		String val = corpus.get_conf(label);
		if (val == null || val.isEmpty()) {
			val = "";
		}
		List<String> confPairs = new ArrayList<>();
		for (int i = 0; i < val.length(); i = i + 2) {
			confPairs.add(val.substring(i, i + 2));
		}
		return confPairs;
	}

	private List<String> getAlignedValue(Corpus corpus) {
		final String aligned = corpus.get_conf("ALIGNED");
		if (aligned != null && !aligned.isEmpty()) {
			return getListFromSplitValues(aligned);
		}
		return Collections.emptyList();
	}
}
