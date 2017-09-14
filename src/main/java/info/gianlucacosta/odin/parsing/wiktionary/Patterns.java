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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

interface Patterns {
    Pattern lemmaHeader =
            Pattern.compile("(?i)^==\\s+([^(]+)\\s+\\(\\s*\\{\\s*\\{\\s*Sprache\\s*\\|([^}]+)}\\s*}\\s*\\)\\s+==*$");


    Pattern lemmaVariant =
            Pattern.compile("(?i)^===\\s+([^=]+?)\\s*===$");

    Pattern categoriesSeparator =
            Pattern.compile("\\s*,\\s*");

    List<Pattern> categoryFormats =
            Collections.unmodifiableList(
                    Arrays.asList(
                            Pattern.compile("(?i)\\{\\s*\\{\\s*Wortart\\s*\\|\\s*([^|]+)\\s*\\|\\s*Deutsch\\s*}\\s*}"),
                            Pattern.compile("[\\[{'\\-\\s]+(.*?)[\\]}'\\s\\-]+"),
                            Pattern.compile("([a-z0-9äöü ß\\-_]+)")
                    )
            );


    Pattern syllablesHeader =
            Pattern.compile(
                    "(?i)^\\{\\s*\\{\\s*Worttrennung\\s*}\\s*}*$"
            );

    Pattern syllables =
            Pattern.compile(":([^,{]+|\\{\\s*\\{\\s*.+?\\s*}\\s*})");

    Pattern syllablesSeparator =
            Pattern.compile("·");


    Pattern pronunciation =
            Pattern.compile("(?i)\\{\\s*\\{\\s*Lautschrift\\s*\\|\\s*\\s*(.+?)\\s*}}");


    Pattern synonymsHeader =
            Pattern.compile(
                    "(?i)^\\{\\s*\\{\\s*Synonyme\\s*}\\s*}$"
            );

    Pattern standardExpressionInSet =
            Pattern.compile(
                    "(?i)\\[\\s*\\[\\s*([^]]+)\\s*]\\s*](?!:)"
            );


    Pattern antonymsHeader =
            Pattern.compile(
                    "(?i)^\\{\\s*\\{\\s*Gegenwörter\\s*}\\s*}$"
            );


    Pattern hypernymsHeader =
            Pattern.compile(
                    "(?i)^\\{\\s*\\{\\s*Oberbegriffe\\s*}\\s*}$"
            );


    Pattern nounDeclensionExpression =
            Pattern.compile("(?i)^\\|\\s*(Nominativ|Genitiv|Dativ|Akkusativ)\\s+(Singular|Plural)[^=]*=\\s*(.*)\\s*$");


    Pattern comparative =
            Pattern.compile("(?i)^\\|\\s*Komparativ\\s*=\\s*(.+)$");

    Pattern superlative =
            Pattern.compile("(?i)^\\|\\s*Superlativ\\s*=\\s*(.+)$");


    Pattern praesens =
            Pattern.compile("(?i)^\\|\\s*Präsens_er\\s*,\\s*sie\\s*,\\s*es\\s*=\\s*(.+)$");

    Pattern praeteritum =
            Pattern.compile("(?i)^\\|\\s*Präteritum_ich\\s*=\\s*(.+)$");

    Pattern partizipPerfekt =
            Pattern.compile("(?i)^\\|\\s*Partizip\\s*II\\s*=\\s*(.+)$");

    Pattern imperatifSingular =
            Pattern.compile("(?i)^\\|\\s*Imperativ Singular\\s*=\\s*(.+)$");
}
