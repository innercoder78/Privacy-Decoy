package com.privacydecoy.ag1.javafixture;

/** Inert marker for a controlled, network-free, Java-only artifact. */
public final class FixtureMarker {
    private FixtureMarker() {}
    public static String category() { return "controlled-java-only"; }
}
