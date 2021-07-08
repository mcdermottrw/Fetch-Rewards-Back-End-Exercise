package com.mcdermottrw.fetchrewards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcdermottrw.fetchrewards.model.Transaction;
import com.mcdermottrw.fetchrewards.exception.NegativePointBalanceException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TransactionController {

    /*
     * Sorts Transaction objects by their timestamp
     * Oldest are first out...
     */
    PriorityQueue<Transaction> transactionQueue = new PriorityQueue<>(new Comparator<Transaction>() {
        @Override
        public int compare(Transaction transaction1, Transaction transaction2) {
            return transaction1.getTimestamp().compareTo(transaction2.getTimestamp());
        }
    });

    /*
     * Adds a new Transaction object to the transactionQueue.
     *
     * If the point value of the passed Transaction (passedTransaction) is negative, the method will iterate through
     * Transactions with the same payer and subtract points from those Transactions until the negative point balance
     * is paid off. Any Transactions that loose all of their points will be removed from PriorityQueue at the end of
     * the method.
     *
     */
    @PostMapping("/transaction")
    public void transaction(@Validated @RequestBody Transaction passedTransaction) throws NegativePointBalanceException {
        // If the point balance is not negative, simply add the Transaction to the PriorityQueue
        if (passedTransaction.getPoints() > 0) {
            transactionQueue.add(passedTransaction);
        }
        else {
            // Placing the PriorityQueue inside of a LinkedList keeps Transactions in order and allows for ease of use
            List<Transaction> transactionList = new LinkedList<>(transactionQueue);

            // First, we must determine whether there are enough points from the specified payer to pay off the
            // negative balance
            // Sum up point balances of all Transaction objects that have the same payer as passedTransaction
            // If the method finds that there are not enough points from old Transactions to offset the negative
            // balance of the passed Transaction, it will throw a NegativePointBalanceException and notify the user
            int payersTotalPoints = 0;

            for (Transaction oldTransaction : transactionList) {
                if (oldTransaction.getPayer().equals(passedTransaction.getPayer())) {
                    payersTotalPoints += oldTransaction.getPoints();
                }
            }

            if (Math.abs(passedTransaction.getPoints()) > payersTotalPoints) {
                throw new NegativePointBalanceException(
                        "API ERROR: You do not have enough points from the specified payer " +
                        "to offset the negative balance of this transaction"
                );
            }

            // This section of the method iterates through transactionList, searching for Transactions of the same
            // payer that can be used to pay off the negative balance of passedTransaction
            // - Pull the oldest Transaction object from the List
            // - Determine whether it's payer is the same as passedTransaction's
            // - Determine whether it's points are greater than, equal to, or less than the passedTransaction's
            //   - If greater than, take passedTransaction's negative balance away from the old Transaction and break
            //   - If equal to, remove the old Transaction from the list since its balance is now zero and break
            //   - If less than, update the point balance of passedTransaction, remove the old Transaction from the
            //     List, and continue looping
            for (Transaction oldTransaction : transactionList) {
                if (oldTransaction.getPayer().equals(passedTransaction.getPayer())) {
                    if (oldTransaction.getPoints() > Math.abs(passedTransaction.getPoints())) {
                        oldTransaction.setPoints(oldTransaction .getPoints() + passedTransaction.getPoints());
                        break;
                    }
                    else if (oldTransaction.getPoints() == Math.abs(passedTransaction.getPoints())) {
                        transactionList.remove(oldTransaction);
                        break;
                    }
                    else {
                        passedTransaction.setPoints(oldTransaction.getPoints() + passedTransaction.getPoints());
                        transactionList.remove(oldTransaction);
                    }
                }
            }

            // Once the negative balance is paid off, place the updated transactionList back into transactionQueue for
            // easier sorting later on via the PriorityQueue
            transactionQueue.clear();
            transactionQueue.addAll(transactionList);

        }
    }

    /*
     * Returns a JSON-formatted string indicating how many points were taken from each payer
     *
     * Retrieves Transaction objects from transactionQueue (ordered old -> new) and attempts to use their points to pay
     * off the point balance that the user input. If a Transaction object from the queue is not able to pay off the input
     * points, then it will be removed from the queue entirely. Otherwise, the remaining amount will just be subtracted
     * and added back to the queue
     *
     * While this is happening, the method also keeps track of the amount of points each payer is contributing the
     * spend. The payer names and their total points contributed and stored in a HashMap and converted to a JSON string
     * at the end of the method
     *
     */
    @RequestMapping("/spendPoints")
    public String spendPoints(@RequestBody Transaction transaction) throws JsonProcessingException, NegativePointBalanceException {
        // The method receives an entire Transaction object as a parameter, but the only field found within the object
        // is points so it may as well be extracted into an int variable
        int passedTransactionPoints = transaction.getPoints();

        // Put all Transactions into a LinkedList for easier iteration and iterate through them while summing
        // up the points from every Transaction. If the method finds that there are less total points than the user
        // is attempting to spend, it will throw a NegativePointBalanceException
        List<Transaction> transactionList = new LinkedList<>(transactionQueue);

        int totalPoints = 0;

        for (Transaction oldTransaction : transactionList) {
            totalPoints += oldTransaction.getPoints();
        }

        if (passedTransactionPoints > totalPoints) {
            throw new NegativePointBalanceException(
                    "API ERROR: You are attempting to spend more points than you currently have"
            );
        }

        // Take points from Transaction objects in transactionQueue until passedPoints is paid off completely
        // - Extract the oldest Transaction object from transactionQueue
        // - Determine if the points from that object is enough to pay off passedPoints
        // - Update payerPointsMap (keeps track of how many points are spent from each payer)
        Map<String, Integer> payerPointsMap = new HashMap<>();
        while(passedTransactionPoints != 0) {
            Transaction queueTransaction = transactionQueue.poll();

            String queueTransactionPayer = queueTransaction.getPayer();
            int queueTransactionPoints = queueTransaction.getPoints();

            // - If passedPoints is greater than or equal to queueTransactionPoints, subtract
            //   queueTransactionPoints from passedTransactionPoints and update the map. Any Transactions that go
            //   through this route are eliminated since they will have no points remaining afterwards
            //
            // - If passedPoints is less than queueTransactionPoints, take the remaining point balance of passedPoints
            //   from queueTransactionPoints, add queueTransactionPoints back to transactionQueue, and update the map
            if (passedTransactionPoints >= queueTransactionPoints) {
                passedTransactionPoints -= queueTransactionPoints;
                updatePayerAndPointsMap(payerPointsMap, queueTransactionPayer, queueTransactionPoints);
            } else {
                queueTransaction.setPoints(queueTransactionPoints - passedTransactionPoints);
                transactionQueue.add(queueTransaction);
                updatePayerAndPointsMap(payerPointsMap, queueTransactionPayer, passedTransactionPoints);
                break;
            }

        }
        // Returns a JSON-formatted string of payerPointsMap
        return new ObjectMapper().writeValueAsString(payerPointsMap);
    }

    /*
     * Returns a JSON-formatted String containing the total point balance for each payer in the transactionQueue
     */
    @GetMapping("/payerBalances")
    public String returnBalances() throws JsonProcessingException {
        // A HashMap to hold payers and their total point balances
        // - Key:   payer
        // - Value: total point balance
        Map<String, Integer> payerPointsMap = new HashMap<>();

        // Place all Transactions into a LinkedList
        // Go through each Transaction in the list and update payerPointsMap using the payer and points from each
        List<Transaction> transactionsList = new LinkedList<>(transactionQueue);
        for (Transaction transaction : transactionsList) {
            String payer = transaction.getPayer();
            int points = transaction.getPoints();

            updatePayerAndPointsMap(payerPointsMap, payer, points);
        }

        // Returns a JSON-formatted string of payerPointsMap
        return new ObjectMapper().writeValueAsString(payerPointsMap);
    }

    /*
     * Updates a passed HashMap by either adding a new entry entirely or adding points to an already existing key's
     * value depending on whether the passed key already exists or not
     */
    private void updatePayerAndPointsMap(Map<String, Integer> payerPointsMap, String payer, int points) {
        if (payerPointsMap.containsKey(payer)) {
            Integer value = payerPointsMap.get(payer);
            payerPointsMap.put(payer, value + points);
        }
        else {
            payerPointsMap.put(payer, points);
        }
    }
}
