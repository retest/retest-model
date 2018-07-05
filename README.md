# recheck-model

[![Build Status](https://travis-ci.com/retest/retest-model.svg?branch=master)](https://travis-ci.com/retest/retest-model)
[![license](https://img.shields.io/badge/license-AGPL-brightgreen.svg)](https://github.com/retest/recheck-web/blob/master/LICENSE)
[![PRs welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg)](https://github.com/retest/recheck-web/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
[![code with hearth by retest](https://img.shields.io/badge/%3C%2F%3E%20with%20%E2%99%A5%20by-retest-C1D82F.svg)](https://retest.de/en/)

The model of both [retest](https://retest.de/) and [recheck](https://retest.de/recheck/).

## Building

To build this project locally, you have to skip JAR signing.

For normal builds use:

```
mvn deploy -Dgpg.skip=true
```

For making releases use:

```
mvn release:prepare -Darguments="-Dgpg.skip=true"
```

## License

This project is licensed under the [AGPL license](LICENSE).
