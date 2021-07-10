# Fetch-Rewards-Back-End-Exercise

A web service that allows input and output via HTTP request and responses

## Requirements: 

Java JDK 8
1. Download: `https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html`
2. Once installed, execute `java -version` in the terminal to test your installation

Maven 3.8.1
1. Download: `https://maven.apache.org/download.cgi`
2. Installation guide: `https://www.tutorialspoint.com/maven/maven_environment_setup.htm`

Git Bash
1. Download: `https://git-scm.com/downloads`

This Repository
1. Open Git Bash
2. Navigate to whichever directory you would like to place the project
   * Example: `cd "C:\Users\{Your username}\Documents"`
3. Execute `git clone https://github.com/mcdermottrw/Fetch-Rewards-Back-End-Exercise.git`

## Executing the Program

1. Open the terminal
2. Navigate to the root directory of the project
   * Example: `cd "C:\Users\{Your username}\Documents\Fetch-Rewards-Back-End-Exercise"`
3. Execute `mvn spring-boot:run`
   * Upon the first execution, all required dependencies will be installed. This may take a minute.
   
## Usage

Now that the application is running, open a new terminal

Note: when executeing via the terminal in windows, all inner quotes must be escaped using "\"

### Add Transaction [/transaction]

Windows:
```shell
curl -H "Content-Type: application/json" -d "{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }" http://localhost:5000/transaction
```

Linux:
```bash
curl -H "Content-Type: application/json" \ -d '{ "payer": "DANNON", "points": 1000, "timestamp": "2020-11-02T14:00:00Z" }' \ http://localhost:5000/transaction
```



### Spend Points [/spendPoints]

Windows:
```shell
curl -H "Content-Type: application/json" -d "{ \"points\": 500 }" http://localhost:5000/spendPoints
```

Linux:
```bash
curl -H "Content-Type: application/json" \ -d '{ "points": 500 }' \ http://localhost:5000/spendPoints
```



### Return Points Balances [/payerBalances]
```shell
curl http://localhost:5000/payerBalances
```
