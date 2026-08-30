# Experiment harness

The measurement code and raw evidence behind the A/B results in the top-level `README.md`.
Previously this lived only outside the repository, which made the numbers unauditable.

| File | Purpose |
|---|---|
| `measure.py` | Reads a subagent's own transcript JSONL and produces the metrics JSON: input/output tokens, wall-clock decomposed into active vs stalled (45s threshold) |
| `extract-steps.py` | Extracts the high-level step list an arm actually performed, from its transcript |
| `rewrite-runs.py` | Counts and times OpenRewrite plugin invocations within an arm |
| `denials.py` | Counts permission denials, which are a confound on wall-clock |
| `exclude-docs.py` | Separates documentation-writing turns from migration turns |
| `brief-common.md` | The shared brief both arms received, so the only difference is the Moderne/OpenRewrite instruction |
| `prewarm.sh`, `prewarm-round2.sh` | Pre-populate each arm's own Maven repository, so dependency download time is outside the measured window |
| `reset-db.sh`, `reset-db-round2.sh` | Restore the MySQL schema and seed between runs |
| `launch-app.sh` | Start the built application for manual verification |
| `docs-version-audit.sh` | The docs-vs-Maven-Central version scan. **Its conclusion was wrong** and the script carries a header saying so: OpenRewrite publishes to the authenticated Code Genome Project repository, so Central lagging is expected |
| `arm-*.t0` | Start-time markers |
| `arm-*-metrics.json` | The computed metrics for each arm |
| `transcripts/*.jsonl` | Raw subagent transcripts. `arm-a-ABORTED-run1.jsonl` is retained deliberately: it is the run that was discarded, and why |

Note that `measure.py` exists in this form because of a measurement error worth not
repeating: subagent turns are **not** `isSidechain` entries in the parent session
transcript. Each subagent writes its own JSONL, and that is the only place its token counts
appear. The first version of this script measured the wrong file.
