# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

##Phase 2 UML Sequence Diagram

The deliverable for Phase 2 of the project:

[![UML Sequence Diagram](Phase_2_UML_Diagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIGEAtIGcnQMqQE4DcvQCLIgDmAdgFDkCGAxsAPaZzgiSnDkAOVmoNI3duiy5MXHnwFUhACWkATKGO68Q-QcGE41kcavXTN+KsCoBBGjWRJy8k1QBGVJDHkPKxTPQCunAMSM0sQwfgBisACi+LBmzJA80FScnCw0JiD0FLAsbJoAtAB8WqIAXNAA2vgRADIRACoRALrQAPRu5Bg4eIXQcqSKWGU0UDwAFACU5H0DTD2d2DpDI5gTHSI60D3GphZWKEvxmACqLphIq9vmltabRW5lVbUNieDg0N6nNvMbW-a71gceABxKgAW2QFz+1xQt2g9wINXqEReb2IYOQa20Vlhl3++2gw0OZm8wHgkJ20NQPXhjyRKMSJPgdXoAGs2F91ti8j1pkoykhvJSpgolJs5iJBtAAEwABhlmNEYqK2VY7DK5VlMuaAG8AL7kNjyDxeXx+TyQNjQPwsYjwYDmy0AJUgxBASGAmHSmXIKtysPmkvKAAUAPJoOrNFofLAAHVI2oARNHMKR0QmSgnoAmADRZ7goADujHk6czOazkFBVBA4FLWf1AdmRV5ksTydT4Lr5YT+aQRcwJYzWdzCcr1drQ4T+pbTeKi2gmBdbuAWFGztd7qwzoAjt5kMBJt8uUVcZSysFgCdV+30ZNT3sqXcHCVQiB+tAr5hLtAHABPd6nB2ugOIuVAsh+pzQAWYDwABWBAS8oHyP+kAAB7LjY943NyRRHpAZSfpcCo-DyIqSqSXgFtAZjgEhv51GBbARKhVicKA3ozkqxSSmhrHsRQjZcb6arQAALDKADMcaJuCKBUME6ZZhEmBeJgZQ3uCiHxMh0CmGypBTga-TkFhMI4XOVhlKQ3ivMRx4EFCD4lDQoErp+ozJpcd6OdhT4lGY8jyBBWBEXhOI+fsLnxCuxKkqMVCMl5JkRY+cLPgFQWxfAoWcjA5kzmU67LluyA2RwnHmY2ZRtoBabQGUZYjglpLMvpmYNfWCrdD0wnAGUmrSUmtWdvVWbDlmzVMqybDtWNhmGpQ5CeD4-gOOAe5WjadprRt1T0K6pDQD6ORCOKXRqRUobhpGLgoBkpCDRpkBdiOvb9oOZYNhKs4FdANXwWmk6vc4fbFl205kbOeFlOA+1vqMe0HTue7uoeuXhRSTnQBe7lPd5mO+WlZQAGZvkFyY-v+T3JQTZlnQslnBV+9jQIw0DWbZ1DgJoFOkPQmjEz474ho60BvcWcL0MgpAAOSaFWwA0PAdl5aR-R8rp8CUR+qaMowIAAF6QPIzF8fdwrq91uHfWUvGQGx5uCeZvXquJACMOoJrJSDyc9DXKap6m66S+tGyW+qQOALjQNgVAsEFUXyLkIBxxyWJ5SeKVDK5kBZfFiX2PjVwPrC8IZdRBemDTxfYfT84CkKYX5ZDZSI2+zoCtzFszFxVV-UNAOdkDE2Mq1M2Tl951CSdfUVJqntPS9I8tdNBkT0ZRoLUtJr+IuQV+Jwb7gYjPiaMdqr5Nb53qrSDQ3dY91xpNodeqQZSTWPAnfbCv2w8Qp-5xXvpSYFUr4M3wtAP+ACT4kmRvuNG6cMY13xBePOH9V5FzxKleEpN3zoP0tXLBvdcplCyt+NmHNwCUDjpofBlo3yx3jirLiv0KL0CokcYO8BQ7G1Nvbfi3dRSVRttAO2DtvROx6jPV2MoPb929r7RSCYA6MCDs-TAhtjbzSjjAOhh1GEgCNE3TOtMkBlCTlAGKjJAFTWAYQykpdnwIieMiPR9iS7CPTvyQUD5mHN0thdBuviKr00lJqLqswpEX3VPPP6EdjJb2WqaTg3hMApBCBECSsBIgADZoDVGXNAEEskjq9X9CI8oQJ6iRjROCJ+esNEG1fu-Ueq8Ik-xbpA5cxSIR6JAZDcpXiunuh6ecApIz0RIDgajPxJjkFmOxpAYAaDWl2NMtgpxuCgpuPWcQoZZCWYUJslQrmtDVn0NIAYox6N-EzDKGwjhXCeEmxYvw82ISwE8VeeIr+U9nbSIqO7T2CiFL+xUqo94TzGlaIjjohkQDLRXNmQ5UxMNumTPJPMxxZQ0CIlgHUektSMS7M8eAtF7pWbEyKeiHKiDbka3GcAUZHcyqCKttxC6mpcwACk0AhgAHI-noMhdp-zolzzlJ7IlSBFLlAAHQKsaPEzexlt4rT8L+SOsMqLWhIHaTVrx2FwBztSzS58-ShIusGMMEZWhEvqSHRpzT4W2LYNJIl-L0ST0VGrO5BIc49JsZ-XMi5tz9ICYMsl-roqQEDbAAN6JpkHmRes88SyVkItIJghx1JNlkxdZ-dxtcwHzgOaYVmTBKHUO5gW1e0AGFx0MX431GsHk63UZol5ZsOIDNJV87tvyfVRNyDIuRMlrCKLBYHSFHaw7aOjnomOjbrl0rmVg7OMbA0etvEWsyfloAAEl+W4sdAS0gkAqJEt3VSOujMiUHvwM25snT42bsTaVbmox734HDT3PtnK5Q8r5YKhwwrfyiuHSJDUkr+7fsUm7KUElRLKo3sadVngqD-l1bae0np-zcvoG+U1MBzWnU+VaoMRxbUtHtVCw2zq9HSRSJhrAsB6Cw0wLmb93rujPoCWUAAVoR0gga9EhsgGGtlUMSHQCE2+QNBH5Pvt3PAlNWdFnLOsX0692LoBbNrQQklt6IFlqoBW9mxzq1nMzfWy5y6n29E6W2zhs7eHfIER8jltt3OO2-mKkdgLZHAonaCpS4KLreDo3O2FC7zn6Ps8YlF8y01MvRF+9ED7s0eP3fpq9RmS2Mx6eQytlnTnY3RJLaWctRHoXdA51hWsjUACEqDyCTXwn5Um9mlBqwOiDyoAXlHElqeRIW-ZhenU4IKoaUbAHnTAIlNWMJlZm-uY2BJ2NszjnRXSjFB0kT436ttNE6IMX0h1jzvbLXeb65Igb4qhuSWC3JULyjwvv1otpf8ekZoxZgDQTbTAqCxxrI4KAanUXvE4HYFcW6Mv4FzADjjuY8Y6dzQRIM+AzDPDyylbr9cfHWHq50oJRPPN93CXd5gD3Yl6lQwtBn-QgA)

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
