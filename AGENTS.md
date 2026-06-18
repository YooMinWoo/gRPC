# AGENTS.md

This repository uses the Superpowers workflow as the required operating model
for agentic development work.

## Instruction Priority

Follow instructions in this order:

1. The user's direct instructions and this `AGENTS.md`.
2. Superpowers skill instructions.
3. The agent's default behavior.

If these instructions conflict with a Superpowers skill, follow the user's
direct instruction or this file first. Otherwise, treat Superpowers skills as
mandatory.

## Required Superpowers Workflow

Before responding, planning, editing, testing, reviewing, or committing, check
whether a Superpowers skill applies. If there is any reasonable chance that a
skill applies, load it before taking action and follow it.

Required defaults:

- Use `superpowers:using-superpowers` at the start of work to determine the
  relevant skills.
- Use `superpowers:brainstorming` before creative work, feature work, behavior
  changes, design decisions, or new functionality.
- Use `superpowers:writing-plans` after a design or requirement is stable and
  before implementation begins.
- Use `superpowers:using-git-worktrees` before executing implementation plans
  or starting feature work that should be isolated.
- Use `superpowers:test-driven-development` for feature work and bug fixes
  unless the user explicitly overrides that workflow.
- Use `superpowers:systematic-debugging` before fixing bugs, failing tests, or
  unexpected behavior.
- Use `superpowers:executing-plans` or
  `superpowers:subagent-driven-development` when implementing a written plan.
- Use `superpowers:verification-before-completion` before claiming work is
  complete, correct, fixed, passing, or ready.
- Use `superpowers:requesting-code-review` before finishing substantial code
  changes, and `superpowers:receiving-code-review` before acting on review
  feedback.
- Use `superpowers:finishing-a-development-branch` when implementation is done
  and the branch needs to be finalized, merged, pushed, or turned into a PR.

Do not skip a required skill because the task appears small.

## Planning And Implementation Gates

Do not start implementation while a required design, brainstorming, or planning
gate is still open.

For feature or behavior changes:

1. Explore the repository first.
2. Clarify product intent and constraints when they cannot be discovered from
   the codebase.
3. Present viable approaches and tradeoffs.
4. Produce or follow a written implementation plan.
5. Implement in small verified steps.
6. Verify before reporting completion.

If the user asks for implementation while a planning mode is active, create or
refine the implementation plan instead of editing files.

## Repository Safety Rules

- Inspect existing files, conventions, and git status before changing files.
- Keep changes scoped to the user's request.
- Do not perform unrelated refactors.
- Do not overwrite, revert, or discard user changes unless the user explicitly
  asks for that exact action.
- Prefer existing project patterns over new abstractions.
- Add tests proportionally to the risk and behavior being changed.
- Run the most relevant available verification commands before reporting
  completion.
- If verification cannot be run, clearly state why and what remains unverified.

## Project Technical Standards

This project uses Java 17, Spring Boot 4.1, Spring Data JPA, PostgreSQL, DDD,
and hexagonal architecture. Agents must treat the following documents as
mandatory project rules:

- `docs/architecture-conventions.md`
- `docs/api-conventions.md`

Before adding or changing application code, read those documents and verify the
change follows them.

Mandatory architecture rules:

- Keep domain code free of Spring, JPA, Web, and Swagger dependencies.
- Keep business logic inside domain models or domain services.
- Use application ports for use cases and external dependencies.
- Keep adapters responsible for web, persistence, and external integration
  details.
- Do not let controllers call JPA repositories directly.
- Do not let application services return Spring MVC DTOs or JPA entities.
- Do not put business logic in `JpaEntity` classes. JPA entities are persistence
  mapping objects only.
- Use PostgreSQL as the default runtime database.
- Keep the test profile on H2 unless the user explicitly changes the testing
  database strategy.
- Do not rely on H2-only SQL behavior when adding persistence code.
- Check both runtime datasource settings and test datasource settings when
  changing database configuration.

Mandatory API rules:

- Every HTTP API response must use `ApiResponse<T>`.
- Every expected domain failure must use a domain-specific `ErrorCode` whose
  code starts with the domain name.
- Global exception handling must produce the common `ApiResponse` failure shape.
- Swagger documentation must be written in Korean.
- Swagger descriptions must be friendly and detailed enough for API consumers,
  including summaries, descriptions, request fields, response fields, and error
  cases.

## Current Project Notes

Use `gradle test` for verification when Gradle is available. If the environment
does not provide Gradle or a wrapper, report that verification could not be run
and include the exact command that failed.
