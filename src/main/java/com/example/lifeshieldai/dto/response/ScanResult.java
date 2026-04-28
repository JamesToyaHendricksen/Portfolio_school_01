package com.example.lifeshieldai.dto;

public class ScanResult {

    private String url;
    private boolean malicious;
    private int maliciousCount;
    private int harmlessCount;
    private int undetectedCount;
    private String summary;

    public ScanResult() {
    }

    public ScanResult(String url, boolean malicious,
            int maliciousCount, int harmlessCount,
            int undetectedCount, String summary) {
        this.url = url;
        this.malicious = malicious;
        this.maliciousCount = maliciousCount;
        this.harmlessCount = harmlessCount;
        this.undetectedCount = undetectedCount;
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMalicious() {
        return malicious;
    }

    public void setMalicious(boolean malicious) {
        this.malicious = malicious;
    }

    public int getMaliciousCount() {
        return maliciousCount;
    }

    public void setMaliciousCount(int maliciousCount) {
        this.maliciousCount = maliciousCount;
    }

    public int getHarmlessCount() {
        return harmlessCount;
    }

    public void setHarmlessCount(int harmlessCount) {
        this.harmlessCount = harmlessCount;
    }

    public int getUndetectedCount() {
        return undetectedCount;
    }

    public void setUndetectedCount(int undetectedCount) {
        this.undetectedCount = undetectedCount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
