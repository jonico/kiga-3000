# Minimal reproductions for the upstream reports

Each directory is a standalone Maven project in its **pre-migration** state, so running the
recipe named in its `pom.xml` reproduces the behaviour described in the corresponding report.
Requires a JDK 25 on `JAVA_HOME` (the projects themselves target 17; only repro 04 needs 25
to compile its own output).

| Dir | Recipe under test | Report | Outcome |
|---|---|---|---|
| `01-dialect-xml` | `org.openrewrite.hibernate.MigrateDialect` | [rewrite-hibernate#96](https://github.com/openrewrite/rewrite-hibernate/issues/96) | **Filed.** Java, `.properties` and `.yml` are migrated; `persistence.xml`, `hibernate.cfg.xml` and Java string literals are not |
| `02-persistence-xml-version` | `org.openrewrite.hibernate.MigrateToHibernate71` | [rewrite-hibernate#97](https://github.com/openrewrite/rewrite-hibernate/issues/97) | **Filed.** Installs Hibernate 7.1 (Jakarta Persistence 3.2) but leaves the descriptor at `version="3.0"` |
| `03-validator-el` | `org.openrewrite.hibernate.validator.HibernateValidator_9_1` | not filed | **Claim was wrong.** The working baseline still interpolates after migration |
| `04-java25-sysout` | `org.openrewrite.java.migrate.UpgradeToJava25` | not filed | Reproduces, but compiles and emits identical output — recipe scoping, not a defect. Draft held |

Run any of them with:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@25
cd 01-dialect-xml
mvn rewrite:dryRun && cat target/rewrite/rewrite.patch
```

`03-validator-el` is the one to run if you only run one, because it is the negative result.
It ships the *correct* pairing (Hibernate Validator 8.0.2 + `expressly` 5.0.0), which
interpolates EL properly:

```bash
cd 03-validator-el
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt compile
"$JAVA_HOME/bin/java" -cp "target/classes:$(cat cp.txt)" com.example.Demo
# MESSAGE: length must be between 2 and 5, was 'abcdefghij'
mvn -q rewrite:run       # -> hibernate-validator 9.1.3, expressly untouched
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt compile
"$JAVA_HOME/bin/java" -cp "target/classes:$(cat cp.txt)" com.example.Demo
# MESSAGE: length must be between 2 and 5, was 'abcdefghij'   <- still works
```

Two traps that made this look like a bug at first, both worth knowing before writing any
Bean Validation report:

- `{min}` / `{max}` are **message parameters, not EL**. They interpolate with no EL
  implementation on the classpath at all, so they cannot be used to detect one.
- Hibernate Validator **disables method-call EL by default** since 6.2, so
  `${validatedValue.length()}` stays literal even on a healthy classpath. Use a bean
  property such as `${validatedValue}`.
