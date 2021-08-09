package thito.nodeflow.internal;

import thito.nodeflow.api.*;

import java.util.*;

public class VersionImpl implements Version {

    public static Version parse(String versionString) {
        // R-1.12.0
        // B-1.0
        String[] split = versionString.split("[^\\w\\d]");
        State state = null;
        Integer major = null;
        Integer minor = null;
        Integer build = null;
        for (String s : split) {
            State testState = State.getState(s);
            if (testState == null) {
                try {
                    Integer testInteger = Integer.valueOf(s);
                    if (major == null) {
                        major = testInteger;
                    } else if (minor == null) {
                        minor = testInteger;
                    } else if (build == null) {
                        build = testInteger;
                    }
                } catch (Throwable t) {
                }
            } else {
                state = testState;
            }
        }
        if (state == null) state = State.RELEASE;
        if (major == null) major = 0;
        if (minor == null) minor = 0;
        if (build == null) build = 0;
        return new VersionImpl(state, major, minor, build);
    }

    private State state;
    private int major, minor, build;

    public VersionImpl(State state, int major, int minor, int build) {
        this.state = state;
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    @Override
    public int compareTo(Version o) {
        int stateCompare = getState().compareTo(o.getState());
        if (stateCompare != 0) return stateCompare;
        int majorCompare = Integer.compare(getMajor(), o.getMajor());
        if (majorCompare != 0) return majorCompare;
        int minorCompare = Integer.compare(getMinor(), o.getMinor());
        if (minorCompare != 0) return minorCompare;
        int buildCompare = Integer.compare(getBuild(), o.getBuild());
        if (buildCompare != 0) return buildCompare;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        Version version = (Version) o;
        return major == version.getMajor() && minor == version.getMinor() && build == version.getBuild() && state == version.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, major, minor, build);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getBuild() {
        return build;
    }

    @Override
    public String toString() {
        return state.getProperName()+" "+getMajor()+"."+getMinor()+"."+getBuild();
    }
}
