package org.de.kiga3000.database;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.de.kiga3000.data.KarteikarteImpl;

/**
 * Card persistence through JPA.
 *
 * <p>Replaces four hand-written DAOs whose INSERT and UPDATE each bound 39 positional
 * parameters. That shape is what made the SQL fragile: a single misnumbered index
 * silently writes one column's value into another, and the statements duplicated the
 * whole column list four times over. The mapping now lives once, on the entity, next
 * to the fields it describes.
 *
 * <p>The remaining set-based queries - the search screens, the group move, the
 * retention purge - stay on hand-written SQL. They are aggregate operations where SQL
 * is the better tool, and turning them into JPQL would be ORM for its own sake.
 * JDBC and JPA therefore coexist here on purpose, each where it fits.
 */
public final class KarteikarteRepository {

    private static final String PERSISTENCE_UNIT = "kiga3000";

    private static EntityManagerFactory factory;

    private KarteikarteRepository() {
    }

    /**
     * The shared factory. Creating one is expensive - it parses the mapping and
     * validates it against the live schema - so it is built once and reused.
     */
    private static synchronized EntityManagerFactory factory() {
        if (null == factory || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return factory;
    }

    /** Releases the factory and with it the HikariCP pool. */
    public static synchronized void shutdown() {
        if (null != factory && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }

    /**
     * @return the card, or null when no card has that id
     */
    public static KarteikarteImpl find(int id) {
        EntityManager em = factory().createEntityManager();
        try {
            return em.find(KarteikarteImpl.class, id);
        } finally {
            em.close();
        }
    }

    /** Loads a card into an existing instance, matching the old DAO's shape. */
    public static void fill(KarteikarteImpl target, int id) {
        KarteikarteImpl found = find(id);
        if (null != found) {
            copy(found, target);
        }
    }

    /**
     * @return the generated id
     */
    public static int insert(KarteikarteImpl karte) {
        return inTransaction(em -> {
            // A card being created must not carry an id, or JPA treats it as detached.
            karte.setPrimaryKey(0);
            em.persist(karte);
            em.flush();
            return karte.getPrimaryKey();
        });
    }

    public static void update(KarteikarteImpl karte) {
        inTransaction(em -> {
            em.merge(karte);
            return null;
        });
    }

    /**
     * Deletes a card, but only if it is in the expected group.
     *
     * <p>The group check mirrors the old DAO, which took the group as a second
     * argument: it guards against deleting a card that has been moved in the meantime.
     */
    public static boolean delete(int id, byte gruppe) {
        return inTransaction(em -> {
            KarteikarteImpl karte = em.find(KarteikarteImpl.class, id);
            if (null == karte || karte.getGruppe() != gruppe) {
                return false;
            }
            em.remove(karte);
            return true;
        });
    }

    /** All cards in a group, ordered the way the search screens expect. */
    public static List<KarteikarteImpl> findByGruppe(byte gruppe) {
        EntityManager em = factory().createEntityManager();
        try {
            return em.createQuery(
                    "select k from KarteikarteImpl k where k.gruppe = :gruppe"
                            + " order by k.kindNachname, k.kindVorname",
                    KarteikarteImpl.class)
                    .setParameter("gruppe", gruppe)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ plumbing

    private interface Work<T> {
        T run(EntityManager em);
    }

    /**
     * Runs work in a transaction, rolling back on any failure.
     *
     * <p>Real transactions are the reason db/schema.sql moved from MyISAM to InnoDB:
     * MyISAM accepts BEGIN/COMMIT and silently ignores it, so a rollback would have
     * left a half-written card behind.
     */
    private static <T> T inTransaction(Work<T> work) {
        EntityManager em = factory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = work.run(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Copies every persistent field, so callers holding a reference to their own
     * instance still see the loaded values. Reflection would be shorter but would also
     * silently pick up any field added later, including ones that are not part of the
     * card.
     */
    private static void copy(KarteikarteImpl from, KarteikarteImpl to) {
        to.setPrimaryKey(from.getPrimaryKey());
        to.setGruppe(from.getGruppe());
        to.setKindGeburtsDatum(from.getKindGeburtsDatum());
        to.setEintrittsDatum(from.getEintrittsDatum());
        to.setEintrittsGrund(from.getEintrittsGrund());
        to.setAustrittsDatum(from.getAustrittsDatum());
        to.setAustrittsGrund(from.getAustrittsGrund());
        to.setKindNachname(from.getKindNachname());
        to.setKindVorname(from.getKindVorname());
        to.setGeburtsOrt(from.getGeburtsOrt());
        to.setKindWohnung(from.getKindWohnung());
        to.setReligion(from.getReligion());
        to.setStaat(from.getStaat());
        to.setKindTelefon(from.getKindTelefon());
        to.setVaterName(from.getVaterName());
        to.setVaterGeburt(from.getVaterGeburt());
        to.setVaterBeruf(from.getVaterBeruf());
        to.setMutterName(from.getMutterName());
        to.setMutterGeburt(from.getMutterGeburt());
        to.setMutterBeruf(from.getMutterBeruf());
        to.setSorgePerson(from.getSorgePerson());
        to.setArbeitsOrt1(from.getArbeitsOrt1());
        to.setArbeitsTelefon1(from.getArbeitsTelefon1());
        to.setArbeitsOrt2(from.getArbeitsOrt2());
        to.setArbeitsTelefon2(from.getArbeitsTelefon2());
        to.setGeschwisterGeburt(from.getGeschwisterGeburt());
        to.setFamilienStand(from.getFamilienStand());
        to.setElternOrt(from.getElternOrt());
        to.setElternTelefon(from.getElternTelefon());
        to.setAnzahlGeschwister(from.getAnzahlGeschwister());
        to.setImpfungen(from.getImpfungen());
        to.setTetanusZeit(from.getTetanusZeit());
        to.setKrankheiten(from.getKrankheiten());
        to.setWeitereKrankheiten(from.getWeitereKrankheiten());
        to.setGesundheitshinweise(from.getGesundheitsHinweise());
        to.setArztOrt(from.getArztOrt());
        to.setArztTelefon(from.getArztTelefon());
        to.setKrankenkasse(from.getKrankenkasse());
        to.setSonstiges(from.getSonstiges());
    }
}
