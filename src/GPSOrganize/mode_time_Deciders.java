package GPSOrganize;

import java.util.Date;

import jp.ac.ut.csis.pflow.geom.STPoint;

public class mode_time_Deciders {
	
	public static int modeDecider(STPoint point, Date startdate, Date finishdate){ //returns 1 if disaster 
		if(point.getTimeStamp().after(startdate)&&point.getTimeStamp().before(finishdate)){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	public static int modeDeciderbyTime(Date date, Date startdate, Date finishdate){ //returns 1 if disaster 
		if(date.after(startdate)&&date.before(finishdate)){
			return 1;
		}
		else{
			return 0;
		}
	}
}
