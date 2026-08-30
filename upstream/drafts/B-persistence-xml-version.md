REPO: openrewrite/rewrite-hibernate
TITLE: MigrateToHibernate70/71 leaves persistence.xml at version 3.0 while installing Hibernate 7, which requires Jakarta Persistence 3.2

## What version of OpenRewrite are you using?

rewrite-maven-plugin 6.46.1, rewrite-hibernate 2.25.0.

## What is the smallest, simplest way to reproduce the problem?

`pom.xml` with `org.hibernate:hibernate-core:5.6.15.Final`, and:

`src/main/resources/META-INF/persistence.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">
  <persistence-unit name="example" transaction-type="RESOURCE_LOCAL">
    <class>com.example.Book</class>
  </persistence-unit>
</persistence>
```

plus a trivial `javax.persistence`-annotated entity. Then:

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.46.1:run \
  -Drewrite.activeRecipes=org.openrewrite.hibernate.MigrateToHibernate71 \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-hibernate:2.25.0
```

## What did you expect to see?

The descriptor lifted to the Jakarta Persistence version that the Hibernate release being
installed actually implements — 3.2 for Hibernate 7.x.

## What did you see instead?

The namespace and groupId migration are correct, but the descriptor version is not:

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
```

alongside

```xml
<groupId>org.hibernate.orm</groupId>
<artifactId>hibernate-core</artifactId>
<version>7.1.35.Final</version>
```

Both `hibernate-core` 7.0.10.Final and 7.1.35.Final declare
`jakarta.persistence:jakarta.persistence-api:3.2.0`, so the descriptor ends up two minor
versions behind the API the same recipe just installed.

## Cause

`org.openrewrite.java.migrate.jakarta.JavaxPersistenceXmlToJakartaPersistenceXml` is pinned
to 3.0 (`version: 3.0`, `persistence_3_0.xsd`) in `jakarta-ee-9.yml`, which is correct for
Jakarta EE 9. It is referenced exactly once in rewrite-hibernate — `hibernate-6.0.yml:36`,
inside `MigrateToHibernate60` — and `MigrateToHibernate71` -> `70` -> `66` -> ... -> `60`
inherits it. Nothing later in the chain raises the descriptor, so every Hibernate 6.x and 7.x
migration terminates at 3.0.

A `ChangeTagAttribute` step scoped to `persistence.xml` in `MigrateToHibernate70` (3.1 is
Hibernate 6.4+, 3.2 is Hibernate 7.0+) would close it.

## Honest scope

I did not observe a hard boot failure from this — Hibernate parses the 3.0 descriptor. The
concrete cost is that the migration is incomplete, so the user has to notice and hand-edit,
and any 3.1/3.2-only descriptor content added afterwards fails XSD validation against the
declared schema. Reporting it as an incompleteness rather than a crash.
