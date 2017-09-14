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

import info.gianlucacosta.balmung.lexicon.Adjective;
import info.gianlucacosta.balmung.lexicon.Lemma;
import info.gianlucacosta.balmung.lexicon.Noun;
import info.gianlucacosta.balmung.lexicon.Verb;
import info.gianlucacosta.odin.storage.Lexicon;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class HibernateLexicon implements Lexicon {
    private static final Logger logger = Logger.getLogger(HibernateLexicon.class.getName());

    private final SessionFactory sessionFactory;

    public HibernateLexicon(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean save(Lemma lemma) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(lemma);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }

            return true;
        } catch (Exception ex) {
            logger.warning(() ->
                    String.format("Error while saving lemma: '%s', '%s'", lemma.getExpression(), ex)
            );

            return false;
        }
    }


    @Override
    public Stream<Lemma> findLemmas() {
        return openLemmaStream(
                "info.gianlucacosta.balmung.readLemmas",
                Lemma.class
        );
    }

    private <T extends Lemma> Stream<T> openLemmaStream(String queryName, Class<T> itemsClass) {
        Session session = sessionFactory.openSession();

        try {
            Stream<T> result =
                    session
                            .createNamedQuery(
                                    queryName,
                                    itemsClass
                            )
                            .stream();

            result.onClose(session::close);

            return result;
        } catch (Exception ex) {
            session.close();

            throw ex;
        }
    }


    @Override
    public Stream<Noun> findNouns() {
        return openLemmaStream(
                "info.gianlucacosta.balmung.readNouns",
                Noun.class
        );
    }

    @Override
    public Stream<Verb> findVerbs() {
        return openLemmaStream(
                "info.gianlucacosta.balmung.readVerbs",
                Verb.class
        );
    }

    @Override
    public Stream<Adjective> findAdjectives() {
        return openLemmaStream(
                "info.gianlucacosta.balmung.readAdjectives",
                Adjective.class
        );
    }
}
