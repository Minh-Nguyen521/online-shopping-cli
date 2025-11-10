package com.onlineshopping;

import com.onlineshopping.ui.ConsoleUI;
import com.onlineshopping.util.SampleDataInitializer;

public class Main {
    public static void main(String[] args) {
        // Check if sample data should be initialized
        if (args.length > 0 && "--init-sample-data".equals(args[0])) {
            SampleDataInitializer initializer = new SampleDataInitializer();
            initializer.initializeSampleData();
            System.out.println("Sample data added");
            return;
        }
        
        // Start the main application
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}
