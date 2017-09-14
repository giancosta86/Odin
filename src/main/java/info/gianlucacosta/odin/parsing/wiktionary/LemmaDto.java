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

import info.gianlucacosta.balmung.lexicon.Genus;
import info.gianlucacosta.balmung.lexicon.Kasus;
import info.gianlucacosta.balmung.lexicon.Numerus;

import java.util.*;

class LemmaDto {
    public LemmaDto(String expression, Set<String> categories) {
        this.expression = expression;
        this.categories = categories;
    }

    public String expression;
    public Set<String> categories = new HashSet<>();

    public Optional<List<String>> syllablesOption = Optional.empty();

    public Optional<String> pronunciationOption = Optional.empty();

    public Optional<Set<String>> synonymsOption = Optional.empty();
    public Optional<Set<String>> antonymsOption = Optional.empty();
    public Optional<Set<String>> hypernymsOption = Optional.empty();


    public boolean isNoun;
    public Optional<Genus> genusOption = Optional.empty();
    public Map<Numerus, Map<Kasus, String>> declensionMap = new HashMap<>();
    public Map<Numerus, Map<Kasus, String>> alternativeDeclensionMap = new HashMap<>();

    public boolean isVerb;
    public Optional<String> praesensOption = Optional.empty();
    public Optional<String> praeteritumOption = Optional.empty();
    public Optional<String> partizipPerfektOption = Optional.empty();
    public Optional<String> imperatifSingularOption = Optional.empty();


    public boolean isAdjective;
    public Optional<String> comparativeOption = Optional.empty();
    public Optional<String> superlativeOption = Optional.empty();
}
