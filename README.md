# Fetch-Rewards-Back-End-Exercise

A web service that allows input and output via HTTP request and responses

## Technology Used

- Java 8
- Spring Boot 3.5.2
- Maven 3.8.1

## Requirements: 

Java JDK 8
1. Download: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
2. Once installed, verify your installtion by executing `java -version` in the terminal

Maven 3.8.1
1. Download: https://maven.apache.org/download.cgi
2. Install:
   * Windows installation guide: https://mkyong.com/maven/how-to-install-maven-in-windows/
   * Linux/Ubuntu installation guide: https://mkyong.com/maven/how-to-install-maven-in-ubuntu/
3. Once installed, verify your installation by executing `mvn -version` in the terminal

Git Bash
1. Download: https://git-scm.com/downloads

This Repository
1. Open Git Bash
2. Navigate to the directory you would like to place the repository in
3. Execute `git clone https://github.com/mcdermottrw/FetchRewardsExercise.git`

## Executing the Program

1. Open the terminal
2. Navigate to the root directory of the project
3. Execute `mvn spring-boot:run`
   * All dependencies will be installed during the first execution. It may take a minute...
   
## Usage

Now that the application is running, open a new terminal

**Note:** if you are using Windows' Command Line, you must escape all inner quotation marks when executing commands...

### Add Transaction [/transaction]

**Windows CMD:**
```
curl -H "Content-Type: application/json" -d "{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }" http://localhost:5000/transaction
```

**Linux Terminal:**
```
curl -H "Content-Type: application/json" \ -d '{ "payer": "DANNON", "points": 1000, "timestamp": "2020-11-02T14:00:00Z" }' \ http://localhost:5000/transaction
```



### Spend Points [/spendPoints]

**Windows CMD:**
```
curl -H "Content-Type: application/json" -d "{ \"points\": 500 }" http://localhost:5000/spendPoints
```

**Linux Terminal:**
```
curl -H "Content-Type: application/json" \ -d '{ "points": 500 }' \ http://localhost:5000/spendPoints
```



### Return Points Balances [/payerBalances]
```
curl http://localhost:5000/payerBalances
```

## Thank you!

I would like to thank Fetch Rewards for the opportunity and for reviewing my submission!
