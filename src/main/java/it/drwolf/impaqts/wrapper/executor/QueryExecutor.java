package it.drwolf.impaqts.wrapper.executor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchengine.manatee.Concordance;
import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.KWICLines;
import it.drwolf.impaqts.wrapper.dto.KWICLine;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor {

	private static final Integer MINIMUM_EXECUTION_TIME = 100;
	private final ObjectMapper objectMapper;

	public QueryExecutor() {
		this.objectMapper = new ObjectMapper();
	}

	private void executeQuery(QueryRequest queryRequest, String corpusName) throws InterruptedException, IOException {
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

	public void manageQueryRequest(String corpus, String json) {
		try {
			QueryRequest qr = this.objectMapper.readValue(json, new TypeReference<QueryRequest>() {
			});
			this.executeQuery(qr, corpus);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	public void manageQueryRequest(String corpus, String cql, Integer start, Integer end) {
		QueryRequest qr = new QueryRequest();
		qr.setWord(cql);
		qr.setStart(start);
		qr.setEnd(end);
		try {
			this.executeQuery(qr, corpus);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}
