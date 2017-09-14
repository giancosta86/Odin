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

import info.gianlucacosta.balmung.lexicon.Lemma;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

class PageParserResult {
    private final int skippedLemmasCount;

    private final Set<Lemma> validLemmas;


    public PageParserResult(int skippedLemmasCount, Set<Lemma> validLemmas) {
        this.skippedLemmasCount = skippedLemmasCount;
        this.validLemmas = Collections.unmodifiableSet(validLemmas);
    }


    public int getSkippedLemmasCount() {
        return skippedLemmasCount;
    }


    public Set<Lemma> getValidLemmas() {
        return validLemmas;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageParserResult)) return false;
        PageParserResult that = (PageParserResult) o;
        return skippedLemmasCount == that.skippedLemmasCount &&
                Objects.equals(validLemmas, that.validLemmas);
    }


    @Override
    public int hashCode() {
        return Objects.hash(skippedLemmasCount, validLemmas);
    }


    @Override
    public String toString() {
        return "PageParserResult{" +
                "skippedLemmasCount=" + skippedLemmasCount +
                ", validLemmas=" + validLemmas +
                '}';
    }
}
