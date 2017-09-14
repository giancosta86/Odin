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

import info.gianlucacosta.balmung.lexicon.*;
import info.gianlucacosta.odin.storage.Lexicon;
import info.gianlucacosta.odin.storage.hibernate.HibernateLexicon;
import info.gianlucacosta.odin.storage.hibernate.LocalDatabaseTestBase;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class WiktionaryParserTest extends LocalDatabaseTestBase {
    private Lexicon lexicon;

    @Override
    public void init() {
        super.init();

        lexicon = new HibernateLexicon(sessionFactory);
    }


    @Test
    public void testAuf() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        2
                );

        Set<Lemma> expectedLemmas =
                Arrays.stream(new Lemma[]{
                        new Lemma(
                                "auf",
                                Collections.singleton("präposition"),
                                Collections.singletonList("auf"),
                                Optional.of("aʊ̯f"),
                                Arrays.stream(new String[]{"bei", "zu"}).collect(Collectors.toSet()),
                                Collections.emptySet(),
                                Collections.emptySet()
                        ),

                        new Lemma(
                                "auf",
                                Collections.singleton("adverb"),
                                Collections.singletonList("auf"),
                                Optional.of("aʊ̯f"),
                                Collections.singleton("offen"),
                                Arrays.stream(new String[]{"zu", "geschlossen"}).collect(Collectors.toSet()),
                                Collections.emptySet()
                        )
                })
                        .collect(Collectors.toSet());

        testPageParsing(
                "auf.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testLiest() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        1,
                        0,
                        0
                );

        testPageParsing(
                "liest.xml",
                expectedParserResult,
                Collections.emptySet()
        );
    }


    @Test
    public void testBuecher() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        1,
                        0,
                        0
                );

        testPageParsing(
                "buecher.xml",
                expectedParserResult,
                Collections.emptySet()
        );
    }


    @Test
    public void testLibro() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        0
                );

        testPageParsing(
                "libro.xml",
                expectedParserResult,
                Collections.emptySet()
        );
    }


    @Test
    public void testHoch() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        2
                );

        Set<Lemma> expectedLemmas =
                Arrays.stream(new Lemma[]{
                        new Adjective(
                                "hoch",
                                Collections.singleton("adjektiv"),
                                Collections.singletonList("hoch"),
                                Optional.of("hoːχ"),
                                Arrays.stream(
                                        new String[]{
                                                "angesehen", "angestaut", "angestiegen", "bedeutend",
                                                "emporragend", "fein", "führend", "gehoben", "groß",
                                                "haushoch", "hochgestellt", "hochstehend",
                                                "hochgewachsen", "lang", "lebenslänglich", "maßlos",
                                                "ragend", "sehr", "stattlich", "übergeordnet",
                                                "überteuert", "viel", "vornehm"
                                        }).collect(Collectors.toSet()),

                                Arrays.stream(
                                        new String[]{
                                                "tief", "niedrig", "klein", "nieder"
                                        }).collect(Collectors.toSet()),

                                Collections.emptySet(),
                                Optional.of("höher"),
                                Optional.of("höchsten")
                        ),

                        new Lemma(
                                "hoch",
                                Collections.singleton("adverb"),
                                Collections.singletonList("hoch"),
                                Optional.of("hoːχ"),
                                Arrays.stream(new String[]{"sehr", "äußerst"}).collect(Collectors.toSet()),
                                Collections.emptySet(),
                                Collections.emptySet()
                        )
                })
                        .collect(Collectors.toSet());


        testPageParsing(
                "hoch.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testNie() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        Set<Lemma> expectedLemmas =
                Arrays.stream(new Lemma[]{
                        new Lemma(
                                "nie",
                                Collections.singleton("adverb"),
                                Collections.singletonList("nie"),
                                Optional.of("niː"),
                                Arrays.stream(
                                        new String[]{
                                                "niemals", "nimmer", "nie und nimmer"
                                        }).collect(Collectors.toSet()),

                                Collections.singleton("immer"),

                                Collections.emptySet()
                        )
                })
                        .collect(Collectors.toSet());

        testPageParsing(
                "nie.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testScrambledNie() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        Set<Lemma> expectedLemmas =
                Arrays.stream(new Lemma[]{
                        new Lemma(
                                "nie",
                                Collections.singleton("adverb"),
                                Collections.singletonList("nie"),
                                Optional.of("niː"),
                                Arrays.stream(
                                        new String[]{
                                                "niemals", "nimmer", "nie und nimmer"
                                        }).collect(Collectors.toSet()),

                                Collections.singleton("immer"),

                                Collections.emptySet()
                        )
                })
                        .collect(Collectors.toSet());

        testPageParsing(
                "scrambledNie.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testEinige() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        2,
                        0,
                        1
                );

        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Lemma(
                                "einige",
                                Arrays.stream(new String[]{
                                        "indefinitpronomen",
                                        "numerale"
                                }).collect(Collectors.toSet()),

                                Arrays.asList("ei", "ni", "ge"),
                                Optional.of("ˈaɪ̯nɪɡə"),
                                Collections.emptySet(),
                                Arrays.stream(new String[]{"alle", "keine", "nichts"}).collect(Collectors.toSet()),
                                Collections.emptySet()
                        )
                );

        testPageParsing(
                "einige.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testLesen() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Verb(
                                "lesen",
                                Arrays.stream(new String[]{
                                        "verb",
                                        "unregelmäßig"
                                }).collect(Collectors.toSet()),

                                Arrays.asList("le", "sen"),
                                Optional.of("ˈleːzn̩"),

                                Collections.emptySet(),

                                Arrays.stream(new String[]{"drucken", "schreiben"}).collect(Collectors.toSet()),

                                Arrays.stream(new String[]{
                                        "aufnehmen", "bilden", "ernten", "kommunizieren",
                                        "lehren", "ordnen", "vermitteln", "verstehen"
                                }).collect(Collectors.toSet()),

                                Optional.of("liest"),
                                Optional.of("las"),
                                Optional.of("gelesen"),
                                Optional.of("lies")
                        )
                );

        testPageParsing(
                "lesen.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testRennen() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Verb(
                                "rennen",
                                Collections.singleton("verb"),

                                Arrays.asList("ren", "nen"),
                                Optional.of("ˈʀɛnən"),

                                Collections.singleton("laufen"),

                                Arrays.stream(new String[]{
                                        "bummeln", "schleichen", "trödeln"
                                }).collect(Collectors.toSet()),

                                Collections.singleton("fortbewegen"),

                                Optional.of("rennt"),
                                Optional.of("rannte"),
                                Optional.of("gerannt"),
                                Optional.of("renn")
                        )
                );

        testPageParsing(
                "rennen.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testBuch() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        NounDeclension declension = NounDeclension.createOption(
                "Buch",
                "Buch",
                "Buch",
                "Buchs",

                "Bücher",
                "Bücher",
                "Büchern",
                "Bücher"
        ).get();


        Optional<NounDeclension> alternativeDeclensionOption = NounDeclension.createOption(
                null,
                null,
                "Buche",
                "Buches",

                null,
                null,
                null,
                null
        );


        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Noun(
                                "Buch",
                                Arrays.stream(new String[]{
                                        "substantiv",
                                        "n"
                                }).collect(Collectors.toSet()),

                                Collections.singletonList("Buch"),
                                Optional.of("buːχ"),

                                Collections.emptySet(),

                                Arrays.stream(new String[]{
                                        "CD", "Film", "Heft", "Loseblattsammlung"
                                }).collect(Collectors.toSet()),

                                Arrays.stream(new String[]{
                                        "Anweisung", "Liste", "Maß", "Massenmedium",
                                }).collect(Collectors.toSet()),

                                Optional.of(Genus.NEUTRAL),
                                declension,
                                alternativeDeclensionOption
                        )
                );


        testPageParsing(
                "buch.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testQuiz() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        NounDeclension declension = NounDeclension.createOption(
                "Quiz",
                "Quiz",
                "Quiz",
                "Quiz",

                "Quiz",
                "Quiz",
                "Quiz",
                "Quiz"
        ).get();


        Optional<NounDeclension> alternativeDeclensionOption = NounDeclension.createOption(
                null,
                null,
                null,
                null,

                "Quizze",
                "Quizze",
                "Quizzen",
                "Quizze"
        );

        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Noun(
                                "Quiz",
                                Arrays.stream(new String[]{
                                        "substantiv",
                                        "n"
                                }).collect(Collectors.toSet()),

                                Collections.singletonList("Quiz"),
                                Optional.of("kvɪs"),

                                Collections.emptySet(),

                                Arrays.stream(new String[]{
                                        "Brettspiel", "Kartenspiel"
                                }).collect(Collectors.toSet()),

                                Collections.singleton("Spiel"),

                                Optional.of(Genus.NEUTRAL),
                                declension,
                                alternativeDeclensionOption
                        )
                );

        testPageParsing(
                "quiz.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testBank() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        2
                );

        NounDeclension declension_noun_1 = NounDeclension.createOption(
                "Bank",
                "Bank",
                "Bank",
                "Bank",

                "Bänke",
                "Bänke",
                "Bänken",
                "Bänke"
        ).get();

        Optional<NounDeclension> alternativeDeclensionOption_noun_1 = Optional.empty();


        NounDeclension declension_noun_2 = NounDeclension.createOption(
                "Bank",
                "Bank",
                "Bank",
                "Bank",

                "Banken",
                "Banken",
                "Banken",
                "Banken"
        ).get();

        Optional<NounDeclension> alternativeDeclensionOption_noun_2 = Optional.empty();

        Set<Lemma> expectedLemmas =
                Arrays.stream(
                        new Lemma[]{
                                new Noun(
                                        "Bank",
                                        Arrays.stream(new String[]{
                                                "substantiv",
                                                "f",
                                                "bänke"
                                        }).collect(Collectors.toSet()),

                                        Collections.singletonList("Bank"),
                                        Optional.of("baŋk"),

                                        Arrays.stream(new String[]{
                                                "Lage", "Theke", "Tresen", "Auswechselbank", "Ersatzbank"
                                        }).collect(Collectors.toSet()),

                                        Collections.emptySet(),

                                        Arrays.stream(new String[]{
                                                "Sitzgelegenheit", "Stadtmöbel"
                                        }).collect(Collectors.toSet()),

                                        Optional.of(Genus.FEMININ),
                                        declension_noun_1,
                                        alternativeDeclensionOption_noun_1
                                ),


                                new Noun(
                                        "Bank",
                                        Arrays.stream(new String[]{
                                                "substantiv",
                                                "f",
                                                "banken"
                                        }).collect(Collectors.toSet()),

                                        Collections.singletonList("Bank"),
                                        Optional.of("baŋk"),

                                        Arrays.stream(new String[]{
                                                "Bankhaus", "Geldhaus", "Geldinstitut",
                                                "Finanzinstitut", "Finanzunternehmen",
                                                "Kreditinstitut", "Kasino", "Spielbank"
                                        }).collect(Collectors.toSet()),

                                        Collections.emptySet(),

                                        Arrays.stream(new String[]{
                                                "Gebäude", "Unternehmen"
                                        }).collect(Collectors.toSet()),

                                        Optional.of(Genus.FEMININ),
                                        declension_noun_2,
                                        alternativeDeclensionOption_noun_2
                                )
                        }
                ).collect(Collectors.toSet());


        testPageParsing(
                "bank.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    @Test
    public void testProfessor() {
        WiktionaryParserResult expectedParserResult =
                new WiktionaryParserResult(
                        0,
                        0,
                        1
                );

        NounDeclension declension = NounDeclension.createOption(
                "Professor",
                "Professor",
                "Professor",
                "Professors",

                "Professoren",
                "Professoren",
                "Professoren",
                "Professoren"
        ).get();

        Optional<NounDeclension> alternativeDeclensionOption = Optional.empty();

        Set<Lemma> expectedLemmas =
                Collections.singleton(
                        new Noun(
                                "Professor",
                                Arrays.stream(new String[]{
                                        "substantiv",
                                        "m"
                                }).collect(Collectors.toSet()),

                                Arrays.asList("Pro", "fes", "sor"),
                                Optional.of("pʀoˈfɛsoːɐ̯"),

                                Collections.singleton("Hochschullehrer"),

                                Collections.singleton("Privatdozent"),

                                Collections.emptySet(),

                                Optional.of(Genus.MASKULIN),
                                declension,
                                alternativeDeclensionOption
                        )
                );

        testPageParsing(
                "professor.xml",
                expectedParserResult,
                expectedLemmas
        );
    }


    private InputStream getFakeWiktionaryPageStream(String pageFilename) {
        return getClass().getResourceAsStream(
                String.format("fakeWiktionary/%s", pageFilename)
        );
    }


    private void testPageParsing(
            String pageFilename,
            WiktionaryParserResult expectedResult,
            Set<Lemma> expectedLemmas
    ) {
        try (InputStream pageInputStream =
                     getFakeWiktionaryPageStream(pageFilename)) {

            WiktionaryParser parser =
                    new WiktionaryParser(pageInputStream, lexicon);

            WiktionaryParserResult parserResult =
                    parser.parse();

            assertThat(
                    parserResult,
                    equalTo(expectedResult)
            );

            try (Stream<Lemma> parsedLemmasStream =
                         lexicon.findLemmas()) {

                Set<Lemma> parsedLemmas =
                        parsedLemmasStream.collect(Collectors.toSet());

                assertThat(
                        parsedLemmas,
                        equalTo(expectedLemmas)
                );
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
