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

//            if (actionType.equals("show") && mealId == 0/*mealNameList.size() == 0*/) {
//                System.out.println("No meals saved. Add a meal first.");
//            } else if (!actionType.matches("add|show|exit")) {
//                continue;
//            }

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
                    System.out.println();
                    for (int j = 1; j <= mealId; j++) {

                        String selectAllMealsCategories2 = String.format("SELECT * FROM meals WHERE meal_id = %d;", j);
                        preparedStatement = connection.prepareStatement(selectAllMealsCategories2);
                        ResultSet all = preparedStatement.executeQuery();
                        all.next();
                        System.out.println("Category: " + all.getString("category"));
                        System.out.println("Name: " + all.getString("meal"));
                        System.out.println("Ingredients:");

                        String selectAllIngredients2 = String.format("SELECT * FROM ingredients WHERE meal_id = %d", j);
                        preparedStatement = connection.prepareStatement(selectAllIngredients2);
                        ResultSet allIngredients2 = preparedStatement.executeQuery();
                        while (allIngredients2.next()) {
                            System.out.println(allIngredients2.getString("ingredient"));
                        }
                        System.out.println();
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