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

import info.gianlucacosta.odin.parsing.ParserResult;

import java.util.Objects;

public class WiktionaryParserResult implements ParserResult {
    private final long skippedLemmasCount;
    private final long unsavedLemmasCount;
    private final long savedLemmasCount;


    public WiktionaryParserResult(long skippedLemmasCount, long unsavedLemmasCount, long savedLemmasCount) {
        this.skippedLemmasCount = skippedLemmasCount;
        this.unsavedLemmasCount = unsavedLemmasCount;
        this.savedLemmasCount = savedLemmasCount;
    }


    @Override
    public long getSkippedLemmasCount() {
        return skippedLemmasCount;
    }

    @Override
    public long getUnsavedLemmasCount() {
        return unsavedLemmasCount;
    }

    @Override
    public long getSavedLemmasCount() {
        return savedLemmasCount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WiktionaryParserResult)) return false;
        WiktionaryParserResult that = (WiktionaryParserResult) o;
        return skippedLemmasCount == that.skippedLemmasCount &&
                unsavedLemmasCount == that.unsavedLemmasCount &&
                savedLemmasCount == that.savedLemmasCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skippedLemmasCount, unsavedLemmasCount, savedLemmasCount);
    }

    @Override
    public String toString() {
        return "WiktionaryParserResult{" +
                "skippedLemmasCount=" + skippedLemmasCount +
                ", unsavedLemmasCount=" + unsavedLemmasCount +
                ", savedLemmasCount=" + savedLemmasCount +
                '}';
    }
}
