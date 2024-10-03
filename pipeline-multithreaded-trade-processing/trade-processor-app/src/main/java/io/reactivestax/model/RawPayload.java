package io.reactivestax.model;


public class RawPayload {
    private String tradeId;
    private String payload;
    private String validityStatus;
    private String lookupStatus;
    private String postedStatus;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getValidityStatus() {
        return validityStatus;
    }

    public void setValidityStatus(String validityStatus) {
        this.validityStatus = validityStatus;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getLookupStatus() {
        return lookupStatus;
    }

    public void setLookupStatus(String lookupStatus) {
        this.lookupStatus = lookupStatus;
    }

    public String getPostedStatus() {
        return postedStatus;
    }

    public void setPostedStatus(String postedStatus) {
        this.postedStatus = postedStatus;
    }
}
