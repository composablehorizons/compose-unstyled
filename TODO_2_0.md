# TODO for 2.0 release

- [x] Remove deprecated signatures from public API
- [x] Migrate non-test `com.composables.core` usages to `com.composeunstyled`
- [x] Verify every removed `com.composables.core` public API has a `com.composeunstyled` counterpart (for non-test APIs) before deletion
- [x] Remove deprecated `com.composables.core` package and module (`core`) entirely 
- [x] Remove deprecated/empty legacy modules from settings + publishing

- [ ] Remove remaining deprecated APIs in project-owned modules (`composeunstyled*`, `internal-shared`, `core`) except copied `androidx` sources
- [ ] Remove theming `Locals` from primitives module
- [ ] Remove primitives defaults tied to theming locals (`LocalContentColor`, `LocalTextStyle`) so primitives are truly unstyled
- [ ] Remove primitives/theming coupling points (primitives must not require theming module)
- [ ] Decide fate of `internal-shared` (delete or split/move owned APIs to stable modules)
- [ ] Update demos/playgrounds to use only surviving modules and `Unstyled*` primitives imports
- [ ] Remove experimental APIs
- [ ] Write migration guide + changelog section for 1.x -> 2.0
- [ ] Introducing `styling` module
- [ ] Introduce `responsive` module
- [ ] Final verification: `./gradlew test` + demo builds pass after all removals
