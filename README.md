# JavaFormatter
A very *very* simple Java source code prettifier.  
Why? Because I needed one for my CS classes before I went insane.

Note: This prettifier accepts malformed inputs and does not care if it even compiles.

## Input
`inFile.java`, or `inFile.txt`

## Output
`outFile.java`

## Processing
- Removes leading line numbers.
  
  ```java
  1 package io.github.racoondog.example
  2
  3 public class Main {
  4     public static void main(String... args) {}
  5 }
  ```
  becomes
  ```java
  package io.github.racoondog.example

  public class Main {
      public static void main(String... args) {}
  }
  ```

- Fixes wrongly indented code.
  
  ```java
  public class Main {
  public static void main(String... args) {
  System.out.println("Hello, world!");
  }
  }
  ```
  becomes
  ```java
  public class Main {
      public static void main(String... args) {
          System.out.println("Hello, world!");
      }
  }
  ```
  
- Adds newline after switch cases.
  ```java
  switch (someInt) {
      case 1: System.out.println("One");
          break;
      case 2: System.out.println("Two");
          break;
      default: throw new IllegalStateException("Unexpected number");
  }
  ```
  becomes
  ```java
  switch (someInt) {
      case 1:
          System.out.println("One");
          break;
      case 2:
          System.out.println("Two");
          break;
      default:
          throw new IllegalStateException("Unexpected number");
  }
  ```
