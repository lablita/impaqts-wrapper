package it.drwolf.impaqts.wrapper.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchengine.manatee.Concordance;
import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.KWICLines;
import com.sketchengine.manatee.PosAttr;
import it.drwolf.impaqts.wrapper.dto.KWICLine;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor {

	public static final String STRUCTATTRLIST = "STRUCTATTRLIST";
	public static final String ATTRLIST = "ATTRLIST";
	public static final String STRUCTLIST = "STRUCTLIST";
	public static final String DOCSTRUCTURE = "DOCSTRUCTURE";
	private static final Integer MINIMUM_EXECUTION_TIME = 100;
	private final ObjectMapper objectMapper;

	public QueryExecutor() {
		this.objectMapper = new ObjectMapper();
	}

	private void executeQuery(String corpusName, QueryRequest queryRequest) throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final Concordance concordance = new Concordance();
		concordance.load_from_query(corpus, queryRequest.getWord(), 0, 0);
		int count = 0;
		int requestedSize = queryRequest.getEnd() - queryRequest.getStart();
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
			System.out.println(String.format("### 1. Finished: %s\t Time: %d", "" + concordance.finished(),
					(System.currentTimeMillis() - now)));
			List<KWICLine> kwicLines = new ArrayList<>();
			count = concordance.size();
			QueryResponse queryResponse = new QueryResponse();
			queryResponse.setCurrentSize(count);
			Integer maxLine = requestedSize;
			if (maxLine > count) {
				maxLine = count;
			}
			KWICLines kl = new KWICLines(corpus, concordance.RS(false, queryRequest.getStart(), queryRequest.getEnd()),
					"15#", "15#", "word", "word", "s", "#", 100);
			for (int linenum = 0; linenum < maxLine; linenum++) {
				if (!kl.nextline()) {
					break;
				}
				KWICLine kwicLine = new KWICLine(kl);
				kwicLines.add(kwicLine);
			}
			if (!sentKwicLines.equals(kwicLines)) {
				queryResponse.getKwicLines().addAll(kwicLines);
				sentKwicLines.clear();
				sentKwicLines.addAll(kwicLines);
			}
			queryResponse.setInProgress(!concordance.finished());
			System.out.println(this.objectMapper.writeValueAsString(queryResponse));
			Thread.sleep(5);
			System.out.println(String.format("### 2. Finished: %s\t Time: %d", "" + concordance.finished(),
					(System.currentTimeMillis() - now)));
		}
		concordance.delete();
		corpus.delete();
	}

	public void manageQueryRequest(String corpus, QueryRequest queryRequest) {
		try {
			if (queryRequest.getCorpusMetadata() != null && !queryRequest.getCorpusMetadata().isEmpty()) {
				this.retrieveMetadata(corpus, queryRequest.getCorpusMetadata());
			} else {
				this.executeQuery(corpus, queryRequest);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	private void retrieveMetadata(String corpusName, String attribute) throws JsonProcessingException {
		final Corpus corpus = new Corpus(corpusName);
		PosAttr posAttr = corpus.get_attr(attribute);
		final int posAttrRange = posAttr.id_range();
		QueryResponse queryResponse = new QueryResponse();
		for (int i = 0; i < posAttrRange; i++) {
			if (posAttr.freq(i) > 0) {
				queryResponse.getMetadataValues().add(posAttr.id2str(i));
			}
		}
		queryResponse.setCurrentSize(queryResponse.getMetadataValues().size());
		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
	}

}
