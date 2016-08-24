package org.fingerlinks.mobile.android.navigator;

/**
 * Created by fabio on 06/09/15.
 */
public enum CommitTypeEnum {

    ADD(1),
    REPLACE(0);

    private final int getCommitType;

    CommitTypeEnum(int getCommitType) {
        this.getCommitType = getCommitType;
    }

    public int getGetCommitType() {
        return getCommitType;
    }
}