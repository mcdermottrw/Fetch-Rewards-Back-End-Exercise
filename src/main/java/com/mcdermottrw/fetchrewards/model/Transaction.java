package com.mcdermottrw.fetchrewards.model;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Transaction {

    @NotNull
    @NotBlank
    private String payer;

    @NotNull
    private Integer points;

    @NotNull
    private LocalDateTime timestamp;

    public Transaction(String payer, Integer points, LocalDateTime timestamp) {
        this.payer = payer;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /*
     * Returns the String in a JSON format
     */
    @Override
    public String toString() {
        return "{ \"payer\": \"" + payer + "\", \"points\": " + points + ", \"timestamp\": " + timestamp + " }";
    }

}
