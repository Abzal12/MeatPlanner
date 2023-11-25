package mealplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        boolean isBye = false;
        List<String> mealTypeList = new ArrayList<>();
        List<String> mealNameList = new ArrayList<>();
        List<String[]> ingredientsList = new ArrayList<>();

        while (!isBye) {

            System.out.println("What would you like to do (add, show, exit)?");
            String actionType = scanner.nextLine();

            //Invalid input format
            if (actionType.equals("show") && mealNameList.size() == 0) {
                System.out.println("No meals saved. Add a meal first.");
            } else if (!actionType.matches("add|show|exit")) {
                continue;
            }
            //Invalid input format

            switch (actionType) {
                case "exit" -> {
                    System.out.println("Bye!");
                    isBye = true;
                    return;
                }
                case "add" -> {
                    addMeal(mealTypeList, mealNameList, ingredientsList);
                }
                case "show" -> {
                    System.out.println();
                    for (int i = 0; i < mealTypeList.size(); i++) {

                        System.out.println("Category: " + mealTypeList.get(i));
                        System.out.println("Name: " + mealNameList.get(i));
                        System.out.println("Ingredients:");
                        for (String s : ingredientsList.get(i)) {

                            System.out.println(s);
                        }
                        System.out.println();
                    }
                }
            }
        }
        scanner.close();
    }

    private static void addMeal(List<String> mealTypeList, List<String> mealNameList, List<String[]> ingredientsList) {

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String mealType = null;
        while (true) {
            mealType = scanner.nextLine();
            if (mealType.matches("breakfast|lunch|dinner")) {
                break;
            }
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }
        mealTypeList.add(mealType);

        System.out.println("Input the meal's name:");
        String mealName = null;
        while (true) {
            mealName = scanner.nextLine();
            if (mealName.matches("[A-Za-z ]+")) {
                break;
            }
            System.out.println("Wrong format. Use letters only!");
        }
        mealNameList.add(mealName);

        System.out.println("Input the ingredients:");
        String ingredientsStr = null;
        while (true) {
            ingredientsStr = scanner.nextLine();
            if (ingredientsStr.matches("^([A-Za-z]+(,|\\s)\\s*)+[A-Za-z]+$")) {
                break;
            }
            System.out.println("Wrong format. Use letters only!");
        }
        String[] ingredientsArrFixed = ingredientsStr.split(", ?");
        ingredientsList.add(ingredientsArrFixed);
        System.out.println("The meal has been added!");
    }
}
