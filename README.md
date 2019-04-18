# ape-transaction-test
Arquillian Persistence Extension transactional test case for bug reproduction

Simple mavenized ejb project to reproduce a supposed-to-be bug in the arquillian-persistence extension. Using transactions combined with @ShouldMatchDataset will fail when using version 1.2.0.2, in version 1.1.13.0 it succeeds.
