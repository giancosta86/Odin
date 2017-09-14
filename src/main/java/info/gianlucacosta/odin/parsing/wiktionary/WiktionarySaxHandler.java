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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class WiktionarySaxHandler extends DefaultHandler {
    private static final String TEXT_ELEMENT = "text";

    private final ExecutorService pageParsingExecutorService = Executors.newCachedThreadPool();

    private final Consumer<PageParserResult> onPageParsed;
    private final StringBuilder textBuilder = new StringBuilder();

    private boolean readingText;


    public WiktionarySaxHandler(Consumer<PageParserResult> onPageParsed) {
        this.onPageParsed = onPageParsed;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (TEXT_ELEMENT.equals(qName)) {
            readingText = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case TEXT_ELEMENT:
                parsePageText(textBuilder.toString());

                textBuilder.setLength(0);

                readingText = false;

                break;
        }
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readingText) {
            textBuilder.append(ch, start, length);
        }
    }

    private void parsePageText(String pageText) {
        pageParsingExecutorService.submit(() -> {
            PageParser pageParser = new PageParser(pageText);
            PageParserResult pageParserResult = pageParser.parse();

            onPageParsed.accept(pageParserResult);
        });
    }

    public void waitForTermination() {
        pageParsingExecutorService.shutdown();

        while (true) {
            try {
                if (pageParsingExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
                    return;
                }
            } catch (InterruptedException e) {
                //Just do nothing
            }
        }
    }
}
