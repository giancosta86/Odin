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

package info.gianlucacosta.odin.storage.hibernate;

import info.gianlucacosta.balmung.lexicon.*;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HibernateLexiconTest extends LocalDatabaseTestBase {
    private HibernateLexicon hibernateLexicon;

    private final Set<Lemma> testGenericLemmas;
    private final Set<Noun> testNouns;
    private final Set<Verb> testVerbs;
    private final Set<Adjective> testAdjectives;

    private final Set<Lemma> allTestLemmas;


    public HibernateLexiconTest() {
        testGenericLemmas = new HashSet<>();

        testGenericLemmas.add(
                new Lemma(
                        "Alpha Generic Lemma",
                        Collections.singleton("Generic")
                )
        );

        testGenericLemmas.add(
                new Lemma(
                        "Beta Generic Lemma",
                        Collections.singleton("Generic")
                )
        );


        testNouns = new HashSet<>();

        testNouns.add(
                new Noun(
                        "Alpha Noun",
                        Collections.singleton("Noun"),
                        buildSimpleNounDeclension("Test")
                )
        );

        testNouns.add(
                new Noun(
                        "Beta Noun",
                        Collections.singleton("Noun"),
                        buildSimpleNounDeclension("Test 2")
                )
        );


        testVerbs = new HashSet<>();

        testVerbs.add(
                new Verb(
                        "Alpha Verb",
                        Collections.singleton("Verb")
                )
        );

        testVerbs.add(
                new Verb(
                        "Beta Verb",
                        Collections.singleton("Verb")
                )
        );


        testAdjectives = new HashSet<>();

        testAdjectives.add(
                new Adjective(
                        "Alpha Adjective",
                        Collections.singleton("Adjective")
                )
        );

        testAdjectives.add(
                new Adjective(
                        "Beta Adjective",
                        Collections.singleton("Adjective")
                )
        );

        allTestLemmas = new HashSet<>();
        allTestLemmas.addAll(testGenericLemmas);
        allTestLemmas.addAll(testNouns);
        allTestLemmas.addAll(testVerbs);
        allTestLemmas.addAll(testAdjectives);
    }


    @Override
    public void init() {
        super.init();

        hibernateLexicon = new HibernateLexicon(sessionFactory);
    }

    @Test
    public void savingGenericLemmasShouldWork() {
        testLemmaSaving(testGenericLemmas);
    }


    private void testLemmaSaving(Collection<? extends Lemma> lemmasToSave) {
        boolean savingResult =
                lemmasToSave
                        .stream()
                        .allMatch(hibernateLexicon::save);

        assertThat(
                savingResult,
                is(true)
        );
    }


    @Test
    public void savingNounsShouldWork() {
        testLemmaSaving(testNouns);
    }

    @Test
    public void savingVerbsShouldWork() {
        testLemmaSaving(testVerbs);
    }

    @Test
    public void savingAdjectivesShouldWork() {
        testLemmaSaving(testAdjectives);
    }


    @Test
    public void savingLemmasTwiceShouldAlwaysReturnFalse() {
        assertThat(
                allTestLemmas
                        .stream()
                        .allMatch(hibernateLexicon::save),

                is(true)
        );

        assertThat(
                allTestLemmas
                        .stream()
                        .allMatch(hibernateLexicon::save),

                is(false)
        );
    }


    @Test
    public void findingGenericLemmasShouldWork() {
        testLemmaRetrieval(
                testGenericLemmas,
                hibernateLexicon::findLemmas
        );
    }


    private <T extends Lemma> void testLemmaRetrieval(
            Set<T> lemmaSet,
            Supplier<Stream<T>> lexiconFindingMethod

    ) {
        lemmaSet
                .forEach(hibernateLexicon::save);


        try (Stream<T> retrievedLemmas =
                     lexiconFindingMethod.get()) {

            Set<T> retrievedSet =
                    retrievedLemmas.collect(Collectors.toSet());

            assertEquals(
                    lemmaSet,
                    retrievedSet
            );
        }
    }


    @Test
    public void findingNounsShouldWork() {
        testLemmaRetrieval(
                testNouns,
                hibernateLexicon::findNouns
        );
    }


    @Test
    public void findingVerbsShouldWork() {
        testLemmaRetrieval(
                testVerbs,
                hibernateLexicon::findVerbs
        );
    }


    @Test
    public void findingAdjectivesShouldWork() {
        testLemmaRetrieval(
                testAdjectives,
                hibernateLexicon::findAdjectives
        );
    }


    @Test
    public void findingAllLemasTogetherShouldWork() {
        testLemmaRetrieval(
                allTestLemmas,
                hibernateLexicon::findLemmas
        );
    }


    private static NounDeclension buildSimpleNounDeclension(String repeatedWord) {
        return NounDeclension.createOption(
                repeatedWord,
                repeatedWord,
                repeatedWord,
                repeatedWord,

                repeatedWord,
                repeatedWord,
                repeatedWord,
                repeatedWord
        ).get();
    }
}
