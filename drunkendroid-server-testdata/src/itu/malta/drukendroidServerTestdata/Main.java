package itu.malta.drukendroidServerTestdata;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double ULCLat = 0;
		double ULCLong = 0;
		double LRCLat = 0;
		double LRCLong = 0;
		int startTimeStamp = 0;
		int endTimeStamp = 0;
		int trips = 0;
		int moodReadingsPrTrip = 0;
		if (args.length == 8) {
		    try {
		        ULCLat = Double.parseDouble(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("First argument must be an Double");
		        System.exit(1);
		    }
		    try {
		        ULCLong = Double.parseDouble(args[1]);
		    } catch (NumberFormatException e) {
		        System.err.println("Second argument must be an Double");
		        System.exit(1);
		    }
		    try {
		        LRCLat = Double.parseDouble(args[2]);
		    } catch (NumberFormatException e) {
		        System.err.println("Third argument must be an Double");
		        System.exit(1);
		    }
		    try {
		        LRCLong = Double.parseDouble(args[3]);
		    } catch (NumberFormatException e) {
		        System.err.println("Fourth argument must be an Double");
		        System.exit(1);
		    }
		    try {
		        startTimeStamp = Integer.parseInt(args[4]);
		    } catch (NumberFormatException e) {
		        System.err.println("Fifth argument must be an Integer");
		        System.exit(1);
		    }
		    try {
		        endTimeStamp = Integer.parseInt(args[5]);
		    } catch (NumberFormatException e) {
		        System.err.println("Sixth argument must be an Integer");
		        System.exit(1);
		    }
		    try {
		        trips = Integer.parseInt(args[6]);
		    } catch (NumberFormatException e) {
		        System.err.println("Seventh argument must be an Integer");
		        System.exit(1);
		    }
		    try {
		        moodReadingsPrTrip = Integer.parseInt(args[7]);
		    } catch (NumberFormatException e) {
		        System.err.println("Eight argument must be an Integer");
		        System.exit(1);
		    }
		new Generator(ULCLat, ULCLong, LRCLat, LRCLong, startTimeStamp, endTimeStamp, trips, moodReadingsPrTrip);  
		} else {
			System.out.println("usage : \n");
			System.out.println("ddstd ULCLat ULCLong LRCLat LRCLong startTimestamp endTimeStamp trips moodReadingsPrTrip");
			System.out.println("ULClat         : Upper left corner latitude (double)");
			System.out.println("ULClong        : Upper left corner longitude (double)");
			System.out.println("LRCLat         : Lower right corner latitude (double)");
			System.out.println("LRCLong        : Lower right corner longitude (double)");
			System.out.println("startTimeStamp : Unix integer start timestamp (integer)");
			System.out.println("endTimeStamp   : Unix integer end timestamp (integer)");
			System.out.println("trips          : The amount of trips to generate (integer)");
			System.out.println("moodReadingsPrTrip : The amount mood readings pr trip (integer)");
			System.out.println("This program will generate random mood readings within the specified latitude and longitude coordinates");
		}

	}

}
