#!/bin/bash
# Compare the versions pinned in docs.openrewrite.org install snippets against what
# is available on MAVEN CENTRAL.
#
# READ THIS BEFORE QUOTING THE OUTPUT. "MISSING" here means "not on Maven Central",
# NOT "does not exist" and NOT "documentation bug".
#
# OpenRewrite publishes its releases - plugins included - to the Code Genome Project
# repository at https://artifacts.codegenomeproject.org/maven, which requires
# authentication (a download token tied to a CGP sign-in). Maven Central receives
# releases too, but lags that train by roughly one release. So the documented
# coordinates can be perfectly valid on CGP while 404-ing on Central.
#
# CGP cannot be used to verify existence without credentials: it answers HTTP 401
# with `www-authenticate: Basic realm="Code Genome Project"` for every path,
# including deliberately non-existent versions such as 99.99.99. So an unauthenticated
# check can never distinguish "published" from "absent" there.
#
# What this script therefore does measure, usefully: the gap a reader hits when they
# copy a documented command and build against Central alone with no CGP token. That
# is why arm A of the experiment pinned 6.46.1 rather than the documented 6.47.0.
set -uo pipefail

PAGES=(
  "recipes/java/migrate/upgradetojava21"
  "recipes/java/migrate/upgradetojava25"
  "recipes/java/migrate/upgradetojava17"
  "running-recipes/popular-recipe-guides/migrate-to-java-21"
  "running-recipes/popular-recipe-guides/migrate-to-java-25"
  "recipes/java/testing/junit5/junit4to5migration"
  "recipes/java/dependencies/upgradedependencyversion"
  "running-recipes/getting-started"
)

central_path() {
  case "$1" in
    rewrite-maven-plugin)        echo "org/openrewrite/maven/rewrite-maven-plugin" ;;
    rewrite-gradle-plugin)       echo "org/openrewrite/rewrite-gradle-plugin" ;;
    rewrite-migrate-java)        echo "org/openrewrite/recipe/rewrite-migrate-java" ;;
    rewrite-testing-frameworks)  echo "org/openrewrite/recipe/rewrite-testing-frameworks" ;;
    rewrite-static-analysis)     echo "org/openrewrite/recipe/rewrite-static-analysis" ;;
    rewrite-recipe-bom)          echo "org/openrewrite/recipe/rewrite-recipe-bom" ;;
    rewrite-java-dependencies)   echo "org/openrewrite/recipe/rewrite-java-dependencies" ;;
    rewrite-spring)              echo "org/openrewrite/recipe/rewrite-spring" ;;
    *)                           echo "" ;;
  esac
}

latest_of() {
  local p; p=$(central_path "$1")
  [ -n "$p" ] || { echo "?"; return; }
  local v
  v=$(curl -s "https://repo1.maven.org/maven2/$p/maven-metadata.xml" \
      | grep -oE '<release>[^<]+</release>' | sed -E 's#</?release>##g')
  [ -n "$v" ] && echo "$v" || echo "?"
}

exists_on_central() {
  local art="$1" ver="$2" p; p=$(central_path "$1")
  [ -n "$p" ] || { echo "n/a"; return; }
  curl -s -o /dev/null -w '%{http_code}' \
    "https://repo1.maven.org/maven2/$p/$ver/$art-$ver.pom"
}

printf '%-58s %-28s %-9s %-9s %s\n' PAGE ARTIFACT:DOC_VERSION HTTP CENTRAL VERDICT
printf '%s\n' "-------------------------------------------------------------------------------------------------------------------"

MISMATCHES=0
CHECKED=0
for page in "${PAGES[@]}"; do
  html=$(curl -sL "https://docs.openrewrite.org/$page")
  [ -n "$html" ] || { printf '%-58s %s\n' "$page" "(fetch failed)"; continue; }

  # artifact:version as it appears in -Drewrite.recipeArtifactCoordinates and
  # plugin coordinates, plus <artifactId>/<version> pairs in xml snippets
  pairs=$(printf '%s' "$html" \
    | grep -oE '(rewrite-[a-z-]+):([0-9]+\.[0-9]+\.[0-9]+)' \
    | sort -u)

  [ -n "$pairs" ] || continue
  while IFS= read -r pv; do
    art="${pv%%:*}"; ver="${pv##*:}"
    [ -n "$(central_path "$art")" ] || continue
    CHECKED=$((CHECKED+1))
    code=$(exists_on_central "$art" "$ver")
    rel=$(latest_of "$art")
    if [ "$code" = "200" ]; then
      verdict="ok"
    else
      verdict="MISSING"
      MISMATCHES=$((MISMATCHES+1))
    fi
    printf '%-58s %-28s %-9s %-9s %s\n' "$page" "$pv" "$code" "$rel" "$verdict"
  done <<< "$pairs"
done

echo
echo "checked $CHECKED documented coordinates; $MISMATCHES not resolvable from Maven Central"
echo
echo "NOTE: 'MISSING' = absent from Maven Central only. These versions may well exist"
echo "      on the authenticated Code Genome Project repo that OpenRewrite publishes to"
echo "      (https://artifacts.codegenomeproject.org/maven). That repo returns HTTP 401"
echo "      for every path - even bogus versions - so existence cannot be checked"
echo "      without a download token. This is NOT evidence of a documentation defect."
