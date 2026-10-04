package com.privacydecoy.fixtures.javasurface;

/** Inert marker for a controlled, network-free, Java-only artifact. */
public final class FixtureMarker {
    private FixtureMarker() {}
    public static String category() { return "controlled-java-only"; }
}
