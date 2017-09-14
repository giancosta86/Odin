/*^
  ===========================================================================
  Odin
  ===========================================================================
  Copyright (C) 2017 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.odin.parsing.wiktionary;

import info.gianlucacosta.odin.parsing.Parser;
import info.gianlucacosta.odin.storage.LemmaSaver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WiktionaryParser implements Parser {
    private final InputStream sourceStream;
    private final LemmaSaver lemmaSaver;

    private final AtomicLong skippedLemmasCount = new AtomicLong();
    private final AtomicLong unsavedLemmasCount = new AtomicLong();
    private final AtomicLong savedLemmasCount = new AtomicLong();

    private final ExecutorService savingExecutorService;

    public WiktionaryParser(InputStream sourceStream, LemmaSaver lemmaSaver) {
        this(sourceStream, lemmaSaver, false);
    }


    public WiktionaryParser(InputStream sourceStream, LemmaSaver lemmaSaver, boolean multithreadedSaving) {
        this.sourceStream = sourceStream;
        this.lemmaSaver = lemmaSaver;

        if (multithreadedSaving) {
            savingExecutorService = Executors.newCachedThreadPool();
        } else {
            savingExecutorService = Executors.newSingleThreadExecutor();
        }
    }


    @Override
    public WiktionaryParserResult parse() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            WiktionarySaxHandler saxHandler = new WiktionarySaxHandler(this::processPageParserResult);
            saxParser.parse(sourceStream, saxHandler);
            saxHandler.waitForTermination();
            waitForSavingExecutorService();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return new WiktionaryParserResult(
                skippedLemmasCount.get(),
                unsavedLemmasCount.get(),
                savedLemmasCount.get()
        );
    }


    private void processPageParserResult(PageParserResult pageParserResult) {
        skippedLemmasCount.addAndGet(pageParserResult.getSkippedLemmasCount());

        pageParserResult
                .getValidLemmas()
                .forEach(lemma ->
                        savingExecutorService.submit(() -> {
                            if (lemmaSaver.save(lemma)) {
                                savedLemmasCount.incrementAndGet();
                            } else {
                                unsavedLemmasCount.incrementAndGet();
                            }
                        }));
    }


    private void waitForSavingExecutorService() {
        savingExecutorService.shutdown();

        while (true) {
            try {
                if (savingExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
                    return;
                }
            } catch (InterruptedException e) {
                //Just do nothing
            }
        }
    }
}
