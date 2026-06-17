# Claude Code Plugin Install Audit

**Last updated:** 2026-06-17 (finalization pass)  
**OS:** macOS Darwin / zsh  
**Repo root:** `/Users/ps/Desktop/The Farmer Was Replaced`  
**Claude Code version:** 2.1.104  
**Node:** v22.22.0 ✅ · **npm:** 10.9.4  

---

## Preflight Results

| Check | Result |
|-------|--------|
| OS / shell | macOS Darwin / zsh |
| Repo root | ✅ `package.json` + `.git` confirmed |
| `claude --version` | ✅ 2.1.104 |
| `node --version` | ✅ v22.22.0 (≥ 20 required) |
| `npm --version` | ✅ 10.9.4 |
| `claude plugin list` | ✅ supported |
| `claude plugin marketplace list` | ✅ supported |

---

## Final 9-Category Audit Table

|  # | Category | Status | Verification evidence |
| -: | -------- | ------ | --------------------- |
| 1 | **Caveman** | ✅ Installed | `caveman@caveman` v25d22f864ad6 ✔ enabled in `claude plugin list`; hooks: SessionStart + UserPromptSubmit wired via plugin manifest; `/caveman` slash command available |
| 2 | **Firecrawl + Exa** | ⚠️ Partial | Firecrawl: `firecrawl@claude-plugins-official` v1.0.9 ✔ enabled; `firecrawl-cli` installed; **needs `FIRECRAWL_API_KEY`** before `/firecrawl:setup`. Exa: `exa` MCP ✓ Connected at `https://mcp.exa.ai/mcp?tools=web_search_advanced_exa` |
| 3 | **Compound Engineering** | ✅ Installed | `compound-engineering@compound-engineering-plugin` v3.13.0 ✔ enabled; marketplace `EveryInc/compound-engineering-plugin` registered |
| 4 | **Higgsfield** | 🚫 Disabled — security review | 4 skills installed via `npx skills add higgsfield-ai/skills` but **Snyk flagged all 4 Critical Risk** (2 also High Risk by Gen scanner). Symlinks removed from `.claude/skills/`; source quarantined to `.agents/skills-quarantined/`. Not deleted. See Quarantine section. |
| 5a | **Skill Creator** | ✅ Installed | `skill-creator@claude-plugins-official` ✔ enabled (pre-existing) |
| 5b | **Frontend Design** | ✅ Installed | `frontend-design@claude-plugins-official` v7ed523140f50 ✔ enabled (pre-existing) |
| 5c | **Security Guidance** | ✅ Installed | `security-guidance@claude-plugins-official` v7ed523140f50 ✔ enabled; installed this session |
| 5d | **Legal** | ✅ Installed | `legal@knowledge-work-plugins` v1.3.0 ✔ enabled; marketplace `anthropics/knowledge-work-plugins` registered. ⚠️ Use only for legal-workflow assistance / drafting / issue-spotting — not as legal advice. Optional integrations (Slack, DocuSign, etc.) need separate auth. |
| 6 | **OpenAI Codex** | ✅ Installed | `codex@openai-codex` v1.0.4 ✔ enabled; `@openai/codex` CLI installed; `codex login status` → **"Logged in using ChatGPT"**. Auth complete, ready to use. |
| 7 | **BuildPartner.ai** | ❌ Blocked | Install script inspected: step `[1/3] Create your account` requires email or `--token`. Execution halted per instructions. Awaiting account/token from user. |
| 8a | **Morph (plugin)** | ✅ Installed | `morph-compact@morph` v0.2.8 ✔ enabled; marketplace `morphllm/morph-claude-code-plugin` registered |
| 8b | **Morph MCP** | ⚠️ Blocked | `MORPH_API_KEY` not in environment. `claude mcp add filesystem-with-morph` not run. Awaiting key from user. |
| 9 | **CodeBurn** | ✅ Installed | `npm install -g codeburn` succeeded; `codeburn --help` full command list confirmed; `codeburn status` reads local Claude Code JSONL sessions only (confirmed read-only; today: $1.10 / 32 calls, month: $183.45 / 1884 calls) |

---

## Higgsfield Quarantine

**Security scanner results from `npx skills add higgsfield-ai/skills`:**

| Skill | Gen | Socket | Snyk |
|-------|-----|--------|------|
| higgsfield-generate | Safe | 0 alerts | 🔴 Critical Risk |
| higgsfield-marketplace-cards | 🟠 High Risk | 0 alerts | 🔴 Critical Risk |
| higgsfield-product-photoshoot | 🟠 High Risk | 0 alerts | 🔴 Critical Risk |
| higgsfield-soul-id | Safe | 0 alerts | 🔴 Critical Risk |

**Action taken (reversible, nothing deleted):**

| What | Before | After |
|------|--------|-------|
| Claude Code symlinks | `.claude/skills/higgsfield-{generate,marketplace-cards,product-photoshoot,soul-id}` (active) | **Removed** |
| Source directories | `.agents/skills/higgsfield-{...}/` | **Moved** to `.agents/skills-quarantined/higgsfield-{...}/` |
| motion-design skill | `.claude/skills/motion-design` → `.agents/skills/motion-design` | **Untouched** |

**To reinstate:** move directories back to `.agents/skills/` and re-create symlinks in `.claude/skills/`. Only do after owner reviews findings at https://skills.sh/higgsfield-ai/skills.

---

## Pending Auth — Completion Steps

| Tool | Blocker | What to provide | Next command (when ready) |
|------|---------|-----------------|--------------------------|
| Firecrawl | No API key | Key from firecrawl.dev | Set `FIRECRAWL_API_KEY` env var, then run `/firecrawl:setup` in Claude Code |
| OpenAI Codex | ✅ RESOLVED | — | Logged in via ChatGPT; ready |
| Morph MCP | `MORPH_API_KEY` absent | Key from morph.so | `claude mcp add filesystem-with-morph --scope user -e MORPH_API_KEY="$MORPH_API_KEY" -- npx --prefer-offline -y @morphllm/morphmcp` |
| BuildPartner.ai | Account required | Email signup or `--token` | `curl buildpartner.ai/install.sh \| sh` and follow prompts, or `curl buildpartner.ai/install.sh \| sh -s -- --token=YOUR_TOKEN` |

---

## Failed / Blocked Commands (exact errors)

| Command | Exact error / reason | Category |
|---------|---------------------|----------|
| `claude plugin marketplace add higgsfield-ai/skills` | `Failed to parse marketplace file ... plugins.0.skills: Invalid input` — skills repo, not a plugin manifest | 4 – Higgsfield |
| `curl buildpartner.ai/install.sh \| sh` | Halted at inspection: step `[1/3] Create your account` requires email or `--token` | 7 – BuildPartner |
| `claude mcp add filesystem-with-morph ...` | Not executed — `MORPH_API_KEY` not in environment | 8b – Morph MCP |
| `higgsfield auth login` | `higgsfield: not found` — no `higgsfield` CLI exists | 4 – Higgsfield |

---

## Currently Active Plugin & MCP Inventory

### Plugins (`claude plugin list` — all ✔ enabled)

```
caveman@caveman                                   v25d22f864ad6
claude-code-setup@claude-plugins-official         v1.0.0
code-review@claude-plugins-official               unknown
codex@openai-codex                                v1.0.4         ⚠️ needs auth
compound-engineering@compound-engineering-plugin  v3.13.0
firecrawl@claude-plugins-official                 v1.0.9         ⚠️ needs API key
frontend-design@claude-plugins-official           v7ed523140f50
legal@knowledge-work-plugins                      v1.3.0
morph-compact@morph                               v0.2.8
security-guidance@claude-plugins-official         v7ed523140f50
skill-creator@claude-plugins-official             unknown
vercel@claude-plugins-official                    v0.40.1
```

### MCP servers (connected)

```
lottiefiles-creator    npx -y @lottiefiles/creator-mcp@latest   ✓ Connected
exa                    https://mcp.exa.ai/mcp?...               ✓ Connected
claude.ai Context7     https://mcp.context7.com/mcp             ✓ Connected
claude.ai Figma        https://mcp.figma.com/mcp                ✓ Connected
claude.ai Google Drive https://drivemcp.googleapis.com/mcp/v1   ✓ Connected
```

### Skills (active)

```
motion-design    .claude/skills/motion-design → .agents/skills/motion-design   ✅ active
```

### Skills (quarantined — do not use)

```
higgsfield-generate            .agents/skills-quarantined/   🚫 quarantined
higgsfield-marketplace-cards   .agents/skills-quarantined/   🚫 quarantined
higgsfield-product-photoshoot  .agents/skills-quarantined/   🚫 quarantined
higgsfield-soul-id             .agents/skills-quarantined/   🚫 quarantined
```
