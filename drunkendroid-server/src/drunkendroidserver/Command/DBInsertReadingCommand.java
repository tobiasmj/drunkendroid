package drunkendroidserver.Command;

public class DBInsertReadingCommand implements ICommand {

	long readingTime;
	double latitude, longtitude;
	int mood;
	public DBInsertReadingCommand (long readingTime, double latitude, double longtitude, int mood ) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.mood = mood;
	}
	@Override
	public void Execute() {
		// TODO Auto-generated method stub

	}

}
