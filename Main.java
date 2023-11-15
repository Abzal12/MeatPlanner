package mealplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
    String mealType = scanner.nextLine();
    System.out.println("Input the meal's name:");
    String mealName = scanner.nextLine();
    System.out.println("Input the ingredients:");

    String ingredientsStr = scanner.nextLine();
    String[] ingredientsArr = ingredientsStr.split(", ?");

    System.out.println("Category: " + mealType);
    System.out.println("Name: " + mealName);
    System.out.println("Ingredients:");
    for (String ingredient : ingredientsArr) {
      System.out.println(ingredient);
    }
    System.out.println("The meal has been added!");
    scanner.close();
  }
}