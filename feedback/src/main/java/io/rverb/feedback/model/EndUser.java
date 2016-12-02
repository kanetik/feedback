package io.rverb.feedback.model;

import java.io.Serializable;

public class EndUser implements Serializable {
    private static final long serialVersionUID = 354L;

    public String supportId;
    public String emailAddress;
    public String userIdentifier;

    public EndUser(String supportId) {
        this.supportId = supportId;
        this.emailAddress = "";
        this.userIdentifier = "";
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    @Override
    public String toString() {
        return "supportId: " + supportId + " | emailAddress: " + emailAddress
                + " | userIdentifier: " + userIdentifier;

    }
}
