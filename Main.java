package mealplanner;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static int mealId = 0;
    public static void main(String[] args) throws SQLException {
        String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
        String USER = "postgres";
        String PASS = "1111";
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        connection.setAutoCommit(true);

        //        String dropTableQuery = "DROP TABLE IF EXISTS meals; DROP TABLE IF EXISTS ingredients;";
//        PreparedStatement preparedStatement = connection.prepareStatement(dropTableQuery);
//        preparedStatement.executeUpdate();
        PreparedStatement preparedStatement;
        String createTablesQuery = "CREATE TABLE IF NOT EXISTS meals (" +
                "category VARCHAR, " +
                "meal VARCHAR, " +
                "meal_id INTEGER" +
                "); " +
                "CREATE TABLE IF NOT EXISTS ingredients (" +
                "ingredient VARCHAR, " +
                "ingredient_id INTEGER, " +
                "meal_id INTEGER" +
                ");";
        preparedStatement = connection.prepareStatement(createTablesQuery);
        preparedStatement.executeUpdate();

        String mealIdQuery = "SELECT meal_id FROM meals WHERE meal_id=(SELECT max(meal_id) FROM meals);";
        preparedStatement = connection.prepareStatement(mealIdQuery);
        ResultSet mealIdSql = preparedStatement.executeQuery();
        int mealIdFromSqlToJava = 0;
        while (mealIdSql.next()) {
            mealIdFromSqlToJava = mealIdSql.getInt("meal_id");
        }
        if (mealIdFromSqlToJava > 0) {
             mealId = mealIdFromSqlToJava;
        } else {
            mealId = 0;
        }

        boolean isBye = false;

        while (!isBye) {

            System.out.println("What would you like to do (add, show, exit)?");
            String actionType = scanner.nextLine();

            if (actionType.equals("show") && mealId == 0/*mealNameList.size() == 0*/) {
                System.out.println("No meals saved. Add a meal first.");
            } else if (!actionType.matches("add|show|exit")) {
                continue;
            }

            switch (actionType) {
                case "exit" -> {
                    System.out.println("Bye!");
                    isBye = true;
                    return;
                }
                case "add" -> {
                    addMeal(connection, preparedStatement);
                }
                case "show" -> {
                    String category = null;
                    boolean flag = true;
                    while (flag) {
                        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                        category = scanner.nextLine();
                        if (!category.matches("breakfast|lunch|dinner")) {
                            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                            continue;
                        }
                        flag = false;
                    }

                    String countCategoryRows = String.format("SELECT COUNT(*) FROM meals WHERE category = '%s'", category);
                    preparedStatement = connection.prepareStatement(countCategoryRows);
                    ResultSet numberOfSpecificCategoryRows = preparedStatement.executeQuery();
                    numberOfSpecificCategoryRows.next();
                    int counter = numberOfSpecificCategoryRows.getInt("count");
                    if (counter == 0) {
                        System.out.println("No meals found.");
                        continue;
                    }
                    String selectFromMealTable = String.format("SELECT * FROM meals WHERE category = '%s'", category);
                    preparedStatement = connection.prepareStatement(selectFromMealTable);
                    ResultSet categoryMeal = preparedStatement.executeQuery();

                    for (int c = 1; c <= counter; c++) {
                        if(!categoryMeal.next()) {
                            System.out.println("No meals found.");
                        } else {
                            if (c == 1) {
                                System.out.println("Category: " + categoryMeal.getString("category"));
                                System.out.println();
                            }
                            System.out.println("Name: " + categoryMeal.getString("meal"));
                            int meal_id = categoryMeal.getInt("meal_id");

                            String selectFromIngredients = String.format("SELECT ingredient FROM ingredients " +
                                    "WHERE meal_id = %d", meal_id);
                            preparedStatement = connection.prepareStatement(selectFromIngredients);
                            ResultSet ingredeints = preparedStatement.executeQuery();
                            System.out.println("Ingredients:");
                            while (ingredeints.next()) {
                                System.out.println(ingredeints.getString("ingredient"));
                            }
                            System.out.println();
                            //categoryMeal.next();
                        }
                    }

                }
            }
        }
        scanner.close();
        preparedStatement.close();
        connection.close();
    }

    private static void addMeal(Connection connection, PreparedStatement preparedStatement) throws SQLException {

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String mealType = null;
        while (true) {
            mealType = scanner.nextLine();
            if (mealType.matches("breakfast|lunch|dinner")) {
                break;
            }
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }


        System.out.println("Input the meal's name:");
        String mealName = null;
        while (true) {
            mealName = scanner.nextLine();
            if (mealName.matches("[A-Za-z ]+")) {
                break;
            }
            System.out.println("Wrong format. Use letters only!");
        }

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

        System.out.println("The meal has been added!");

        mealId++;
        String insertIntoMeals = "INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(insertIntoMeals);
        preparedStatement.setString(1, mealType);
        preparedStatement.setString(2, mealName);
        preparedStatement.setInt(3, mealId);
        preparedStatement.executeUpdate();

        String insertIntoIngredients = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(insertIntoIngredients);
        for (int i = 0; i < ingredientsArrFixed.length; i++) {
            preparedStatement.setString(1, ingredientsArrFixed[i]);
            preparedStatement.setInt(2, i + 1);
            preparedStatement.setInt(3, mealId);
            preparedStatement.executeUpdate();
        }
    }
}