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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class PageParser {
    private static final Logger logger = Logger.getLogger(PageParser.class.getName());

    private static final List<String> DEFAULTS_SKIPPABLE_CATEGORIES =
            Arrays.asList(
                    "konjugierte form",
                    "deklinierte form",
                    "partizip i",
                    "partizip ii"
            );

    private final String pageText;
    private final List<String> skippableCategories;

    private final Set<Lemma> validLemmas = new HashSet<>();

    private Optional<String> lemmaExpressionOption = Optional.empty();
    private Optional<LemmaDto> currentLemmaDtoOption = Optional.empty();

    private int skippedLemmasCount;

    private boolean inSyllablesBlock;
    private boolean inSynonymsBlock;
    private boolean inAntonymsBlock;
    private boolean inHypernymsBlock;


    public PageParser(String pageText) {
        this(
                pageText,
                DEFAULTS_SKIPPABLE_CATEGORIES
        );
    }


    public PageParser(String pageText, List<String> skippableCategories) {
        this.pageText = pageText;
        this.skippableCategories = skippableCategories;
    }


    public PageParserResult parse() {
        Arrays
                .stream(
                        pageText.split("\r?\n")
                )
                .map(String::trim)
                .forEach(this::parseLine);

        tryToSaveCurrentLemma();

        return new PageParserResult(
                skippedLemmasCount,
                validLemmas
        );
    }


    private void parseLine(String line) {
        if (tryToParseLemmaHeader(line)) {
            return;
        }

        if (tryToParseLemmaVariant(line)) {
            return;
        }

        if (currentLemmaDtoOption.isPresent()) {
            if (tryToParseNounComponent(line)) {
                return;
            }

            if (tryToParseVerbComponent(line)) {
                return;
            }

            if (tryToParseAdjectiveComponent(line)) {
                return;
            }

            if (tryToParseSyllablesBlock(line)) {
                return;
            }

            if (tryToParseSynonymsBlock(line)) {
                return;
            }

            if (tryToParseAntonymsBlock(line)) {
                return;
            }

            if (tryToParseHypernymsBlock(line)) {
                return;
            }

            if (tryToParsePronunciation(line)) {
                return;
            }
        }
    }


    private boolean tryToParseLemmaHeader(String line) {
        Matcher matcher = Patterns.lemmaHeader.matcher(line);

        if (matcher.matches()) {
            tryToSaveCurrentLemma();
            currentLemmaDtoOption = Optional.empty();


            String expression = matcher.group(1);

            String language = matcher.group(2).trim().toLowerCase();

            lemmaExpressionOption =
                    Objects.equals(language, "deutsch") ?
                            Optional.of(expression)
                            :
                            Optional.empty();

            return true;
        }


        return false;
    }


    private boolean tryToParseLemmaVariant(String line) {
        if (!lemmaExpressionOption.isPresent()) {
            return false;
        }

        Matcher matcher = Patterns.lemmaVariant.matcher(line);

        if (matcher.matches()) {
            tryToSaveCurrentLemma();
            currentLemmaDtoOption = Optional.empty();


            String categoriesString = matcher.group(1);

            Set<String> parsedCategories =
                    parseCategoriesString(categoriesString);


            LemmaDto lemmaDto =
                    new LemmaDto(
                            lemmaExpressionOption.get(),
                            parsedCategories
                    );


            lemmaDto.isNoun =
                    parsedCategories.contains("substantiv");

            if (lemmaDto.isNoun) {
                lemmaDto.genusOption =
                        Arrays
                                .stream(Genus.values())
                                .filter(genus ->
                                        parsedCategories
                                                .stream()
                                                .anyMatch(category ->
                                                        Objects.equals(genus.getShortName(), category)
                                                )
                                )
                                .findAny();
            }

            lemmaDto.isVerb =
                    parsedCategories.contains("verb");


            lemmaDto.isAdjective =
                    parsedCategories.contains("adjektiv");


            if (canPreSkipLemma(lemmaDto)) {
                skippedLemmasCount++;
            } else {
                currentLemmaDtoOption = Optional.of(lemmaDto);
            }

            return true;
        } else {
            return false;
        }
    }


    private Set<String> parseCategoriesString(String categoriesString) {
        return Arrays
                .stream(
                        Patterns.categoriesSeparator.split(categoriesString)
                )
                .map(String::toLowerCase)
                .map(String::trim)
                .map(this::parseCategory)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(String::trim)
                .filter(category -> !category.isEmpty())
                .filter(category -> !category.startsWith("wortart|"))
                .filter(category -> !category.equals("?"))
                .collect(Collectors.toSet());
    }


    private Optional<String> parseCategory(String categoryString) {
        return
                Patterns.categoryFormats
                        .stream()
                        .map(pattern -> {
                            Matcher matcher = pattern.matcher(categoryString);

                            if (matcher.find()) {
                                return Optional.of(matcher.group(1));
                            } else {
                                return Optional.<String>empty();
                            }
                        })
                        .filter(Optional::isPresent)
                        .findAny()
                        .orElseGet(Optional::empty);
    }


    private boolean canPreSkipLemma(LemmaDto lemmaDto) {
        if (lemmaDto.categories.isEmpty()) {
            logger.warning(() -> String.format("Skipping lemma '%s', as it has no categories", lemmaDto.expression));
            return true;
        }


        if (lemmaDto.categories.stream().anyMatch(skippableCategories::contains)) {
            logger.warning(() -> String.format("Skipping lemma '%s', as it has a skippable category", lemmaDto.expression));
            return true;
        }


        if (
                (lemmaDto.isNoun && (lemmaDto.isVerb || lemmaDto.isAdjective))
                        ||
                        (lemmaDto.isVerb && lemmaDto.isAdjective)
                ) {
            logger.warning(() -> String.format("Skipping lemma '%s', as it has more than one main categories", lemmaDto.expression));

            return true;
        }

        return false;
    }


    private boolean tryToParseNounComponent(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (!lemmaDto.isNoun) {
            return false;
        }


        Matcher matcher =
                Patterns.nounDeclensionExpression.matcher(line);


        if (matcher.matches()) {
            String kasusName = matcher.group(1).toUpperCase();
            String numerusName = matcher.group(2).toUpperCase();

            Numerus numerus =
                    Numerus.valueOf(numerusName);

            Kasus kasus =
                    Kasus.valueOf(kasusName);


            Optional<String> expressionOption = parseOptionalExpression(matcher.group(3));

            expressionOption.ifPresent(expression -> {
                boolean isAnotherExpressionInMainDeclension =
                        lemmaDto.declensionMap.containsKey(numerus) &&
                                lemmaDto.declensionMap.get(numerus).containsKey(kasus);

                final Map<Numerus, Map<Kasus, String>> targetDeclensionMap;

                if (!isAnotherExpressionInMainDeclension) {
                    targetDeclensionMap = lemmaDto.declensionMap;
                } else {
                    targetDeclensionMap = lemmaDto.alternativeDeclensionMap;
                }


                targetDeclensionMap
                        .computeIfAbsent(
                                numerus,
                                key -> new HashMap<>()
                        )
                        .put(kasus, expression);
            });

            return true;
        } else {
            return false;
        }
    }


    private boolean tryToParseVerbComponent(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (!lemmaDto.isVerb) {
            return false;
        }

        if (tryToParseVerbeTense(
                line,
                () -> lemmaDto.praesensOption,
                Patterns.praesens,
                expressionOption -> lemmaDto.praesensOption = expressionOption
        )) {
            return true;
        }


        if (tryToParseVerbeTense(
                line,
                () -> lemmaDto.praeteritumOption,
                Patterns.praeteritum,
                expressionOption -> lemmaDto.praeteritumOption = expressionOption
        )) {
            return true;
        }


        if (tryToParseVerbeTense(
                line,
                () -> lemmaDto.partizipPerfektOption,
                Patterns.partizipPerfekt,
                expressionOption -> lemmaDto.partizipPerfektOption = expressionOption
        )) {
            return true;
        }


        if (tryToParseVerbeTense(
                line,
                () -> lemmaDto.imperatifSingularOption,
                Patterns.imperatifSingular,
                expressionOption -> lemmaDto.imperatifSingularOption = expressionOption
        )) {
            return true;
        }


        return false;
    }


    private boolean tryToParseVerbeTense(
            String line,
            Supplier<Optional<String>> currentTenseExpressionSupplier,
            Pattern tenseLinePattern,
            Consumer<Optional<String>> tenseExpressionSetter

    ) {
        Optional<String> tenseExpressionOption =
                currentTenseExpressionSupplier.get();

        if (!tenseExpressionOption.isPresent()) {
            Matcher matcher =
                    tenseLinePattern.matcher(line);

            if (matcher.matches()) {
                String expression =
                        matcher.group(1);

                tenseExpressionSetter.accept(
                        parseOptionalExpression(expression)
                );

                return true;
            }
        }

        return false;
    }


    private boolean tryToParseAdjectiveComponent(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (!lemmaDto.isAdjective) {
            return false;
        }


        if (!lemmaDto.comparativeOption.isPresent()) {
            Matcher comparativeMatcher = Patterns.comparative.matcher(line);

            if (comparativeMatcher.matches()) {
                String comparativeString =
                        comparativeMatcher.group(1);

                lemmaDto.comparativeOption =
                        parseOptionalExpression(comparativeString);

                return true;
            }
        }


        if (!lemmaDto.superlativeOption.isPresent()) {
            Matcher superlativeMatcher = Patterns.superlative.matcher(line);

            if (superlativeMatcher.matches()) {
                String superlativeString = superlativeMatcher.group(1);

                lemmaDto.superlativeOption =
                        parseOptionalExpression(superlativeString);

                return true;
            }
        }

        return false;
    }


    private boolean tryToParseSyllablesBlock(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (lemmaDto.syllablesOption.isPresent() && !inSyllablesBlock) {
            return false;
        }

        return tryToParseBlock(
                () -> inSyllablesBlock,
                newValue -> inSyllablesBlock = newValue,
                Patterns.syllablesHeader,
                line,
                () -> {
                    Matcher matcher = Patterns.syllables.matcher(line);

                    if (matcher.find()) {
                        String syllablesString =
                                matcher.group(1);

                        currentLemmaDtoOption.get().syllablesOption =
                                Optional.of(
                                        Arrays.asList(
                                                Patterns.syllablesSeparator.split(syllablesString)
                                        )
                                );
                    }
                }
        );
    }


    private boolean tryToParseBlock(
            Supplier<Boolean> blockFlagGetter,
            Consumer<Boolean> blockFlagSetter,
            Pattern blockHeaderPattern,
            String line,
            Runnable blockAction
    ) {
        boolean inBlock = blockFlagGetter.get();


        if (!inBlock) {
            Matcher matcher =
                    blockHeaderPattern.matcher(line);

            if (matcher.matches()) {
                blockFlagSetter.accept(true);
                return true;
            } else {
                return false;
            }
        } else {
            if (line.isEmpty()) {
                blockFlagSetter.accept(false);
            } else {
                blockAction.run();
            }

            return true;
        }
    }


    private boolean tryToParseSynonymsBlock(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (lemmaDto.synonymsOption.isPresent() && !inSynonymsBlock) {
            return false;
        }

        return tryToParseExpressionSetBlock(
                () -> inSynonymsBlock,
                newValue -> inSynonymsBlock = newValue,
                Patterns.synonymsHeader,
                line,
                Patterns.standardExpressionInSet,
                () -> lemmaDto.synonymsOption,
                newValue -> lemmaDto.synonymsOption = newValue
        );
    }

    private boolean tryToParseExpressionSetBlock(
            Supplier<Boolean> blockFlagGetter,
            Consumer<Boolean> blockFlagSetter,
            Pattern blockHeaderPattern,
            String line,
            Pattern expressionPattern,
            Supplier<Optional<Set<String>>> expressionSetGetter,
            Consumer<Optional<Set<String>>> expressionSetSetter
    ) {
        return tryToParseBlock(
                blockFlagGetter,
                blockFlagSetter,
                blockHeaderPattern,
                line,
                () -> {
                    Matcher matcher =
                            expressionPattern.matcher(line);

                    while (matcher.find()) {
                        Optional<Set<String>> expressionSetOption = expressionSetGetter.get();

                        final Set<String> expressionSet;

                        if (expressionSetOption.isPresent()) {
                            expressionSet = expressionSetOption.get();
                        } else {
                            expressionSet = new HashSet<>();
                            expressionSetSetter.accept(
                                    Optional.of(expressionSet)
                            );
                        }

                        String item = matcher.group(1);

                        expressionSet.add(item);
                    }
                }
        );
    }


    private boolean tryToParseAntonymsBlock(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (lemmaDto.antonymsOption.isPresent() && !inAntonymsBlock) {
            return false;
        }

        return tryToParseExpressionSetBlock(
                () -> inAntonymsBlock,
                newValue -> inAntonymsBlock = newValue,
                Patterns.antonymsHeader,
                line,
                Patterns.standardExpressionInSet,
                () -> lemmaDto.antonymsOption,
                newValue -> lemmaDto.antonymsOption = newValue
        );
    }


    private boolean tryToParseHypernymsBlock(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (lemmaDto.hypernymsOption.isPresent() && !inHypernymsBlock) {
            return false;
        }

        return tryToParseExpressionSetBlock(
                () -> inHypernymsBlock,
                newValue -> inHypernymsBlock = newValue,
                Patterns.hypernymsHeader,
                line,
                Patterns.standardExpressionInSet,
                () -> lemmaDto.hypernymsOption,
                newValue -> lemmaDto.hypernymsOption = newValue
        );
    }


    private boolean tryToParsePronunciation(String line) {
        LemmaDto lemmaDto = currentLemmaDtoOption.get();

        if (lemmaDto.pronunciationOption.isPresent()) {
            return false;
        }


        Matcher matcher = Patterns.pronunciation.matcher(line);

        if (matcher.find()) {
            currentLemmaDtoOption.get().pronunciationOption =
                    Optional.of(matcher.group(1));

            return true;
        } else {
            return false;
        }
    }


    private void tryToSaveCurrentLemma() {
        currentLemmaDtoOption.ifPresent(lemmaDto -> {
            if (canSkipLemma(lemmaDto)) {
                skippedLemmasCount++;
                return;
            }


            final Lemma lemma;

            if (lemmaDto.isNoun) {
                lemma = new Noun(
                        lemmaDto.expression,
                        lemmaDto.categories,
                        lemmaDto.syllablesOption.orElse(Collections.emptyList()),
                        lemmaDto.pronunciationOption,
                        lemmaDto.synonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.antonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.hypernymsOption.orElse(Collections.emptySet()),
                        lemmaDto.genusOption,
                        NounDeclension.createOption(lemmaDto.declensionMap).get(),
                        NounDeclension.createOption(lemmaDto.alternativeDeclensionMap)
                );
            } else if (lemmaDto.isVerb) {
                lemma = new Verb(
                        lemmaDto.expression,
                        lemmaDto.categories,
                        lemmaDto.syllablesOption.orElse(Collections.emptyList()),
                        lemmaDto.pronunciationOption,
                        lemmaDto.synonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.antonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.hypernymsOption.orElse(Collections.emptySet()),
                        lemmaDto.praesensOption,
                        lemmaDto.praeteritumOption,
                        lemmaDto.partizipPerfektOption,
                        lemmaDto.imperatifSingularOption
                );
            } else if (lemmaDto.isAdjective) {
                lemma = new Adjective(
                        lemmaDto.expression,
                        lemmaDto.categories,
                        lemmaDto.syllablesOption.orElse(Collections.emptyList()),
                        lemmaDto.pronunciationOption,
                        lemmaDto.synonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.antonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.hypernymsOption.orElse(Collections.emptySet()),
                        lemmaDto.comparativeOption,
                        lemmaDto.superlativeOption
                );
            } else {
                lemma = new Lemma(
                        lemmaDto.expression,
                        lemmaDto.categories,
                        lemmaDto.syllablesOption.orElse(Collections.emptyList()),
                        lemmaDto.pronunciationOption,
                        lemmaDto.synonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.antonymsOption.orElse(Collections.emptySet()),
                        lemmaDto.hypernymsOption.orElse(Collections.emptySet())
                );
            }

            validLemmas.add(lemma);
        });
    }


    private static boolean canSkipLemma(LemmaDto lemmaDto) {
        if (lemmaDto.isNoun && lemmaDto.declensionMap.isEmpty()) {
            logger.warning(() -> String.format("Skipping lemma '%s', as it is a noun without declension", lemmaDto.expression));
            return true;
        }

        return false;
    }


    private static Optional<String> parseOptionalExpression(String expression) {
        return expression.length() > 1 ?
                Optional.of(expression)
                :
                Optional.empty();
    }
}

