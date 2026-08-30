REPO: openrewrite/rewrite-hibernate
TITLE: MigrateDialect does not migrate dialects configured in persistence.xml or hibernate.cfg.xml, though it does handle .properties and .yml

## What version of OpenRewrite are you using?

rewrite-maven-plugin 6.46.1, rewrite-hibernate 2.25.0.

## What is the smallest, simplest way to reproduce the problem?

`org.openrewrite.hibernate.MigrateDialect` describes itself as migrating "**all** Hibernate
version-specific dialect classes to their generic equivalents". In practice it reaches Java
type references, `.properties` files and `.yml` files, but not XML — so a dialect named in
`persistence.xml` or `hibernate.cfg.xml`, which is the most common place to set it, is left
on a class that was removed in Hibernate 6.2.

A project with the same dialect configured five ways:

`src/main/resources/META-INF/persistence.xml`
```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
```

`src/main/resources/hibernate.cfg.xml`
```xml
<property name="hibernate.dialect">org.hibernate.dialect.PostgreSQL95Dialect</property>
```

`src/main/resources/application.properties`
```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect
```

`src/main/resources/application.yml`
```yaml
spring:
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQL10Dialect
```

`src/main/java/com/example/PropertiesConfig.java`
```java
p.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9iDialect");
```

plus a genuine Java type reference as a control:
```java
import org.hibernate.dialect.MySQL8Dialect;
// ...
return MySQL8Dialect.class.getName();
```

Run with `hibernate-core` 6.1.7.Final on the classpath:
```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.46.1:dryRun \
  -Drewrite.activeRecipes=org.openrewrite.hibernate.MigrateDialect \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-hibernate:2.25.0
```

## What did you expect to see?

All five migrated, or at least the two XML forms, since `hibernate.cfg.xml` and
`persistence.xml` are the canonical places `hibernate.dialect` is configured.

## What did you see instead?

Changed:
- `src/main/java/com/example/TypeReferenceConfig.java` (`MySQL8Dialect` -> `MySQLDialect`)
- `src/main/resources/application.properties` (`MariaDB103Dialect` -> `MariaDBDialect`)
- `src/main/resources/application.yml` (`PostgreSQL10Dialect` -> `PostgreSQLDialect`)

Unchanged:
- `src/main/resources/META-INF/persistence.xml`
- `src/main/resources/hibernate.cfg.xml`
- `src/main/java/com/example/PropertiesConfig.java` (the string literal)

The recipe reports success either way, so on an XML-configured project it is a silent no-op.

## Why this matters

`MigrateDialect` is reached from `MigrateToHibernate62` and every later chain, so a user
running `MigrateToHibernate71` on an XML-configured project gets `hibernate-core` bumped to
7.1.x while `persistence.xml` still names a class deleted in 6.2. The failure then surfaces
at boot, not at migration time.

## Notes

`recipeList` in `META-INF/rewrite/hibernate-6.2.yml` is 19 `org.openrewrite.java.ChangeType`
entries. `ChangeType` evidently covers properties and YAML as well as Java, which is why
those two work; XML and string literals fall outside it.

Non-Java configuration looks clearly intended to be in scope rather than out of it: the only
documented example for this recipe (`META-INF/rewrite/examples.yml`) is
`MigrateDialectTest#replacesMySQL5DialectInYaml`, an `application.yml` case. So XML seems more
like a gap than a boundary.

rewrite-hibernate 2.25.0 contains no recipe referencing `org.openrewrite.xml.*`, so there does
not appear to be a companion recipe meant to be combined with this one. `MigrateDialect` is in
`MigrateToHibernate62`'s `recipeList` (`hibernate-6.2.yml:29`), so all 6.2+ chains inherit it.

Happy to open a PR adding `org.openrewrite.xml.ChangeTagAttribute` / `ChangeTagValue` steps
for the two XML forms if that is the direction you would want.
