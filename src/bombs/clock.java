package bombs;
import java.time.*;


public class clock {
	public static LocalDateTime getTime() {
		LocalDateTime tem = LocalDateTime.now();
		return tem;
	}
	public static String getDistance() {
		String timeDisplay = "遊戲時間 : ";
		String time = String.valueOf(Duration.between(start, end));
		System.out.println(time);
		String minute="";
		String bigSecond = (String)time.subSequence(time.indexOf("M")+1, time.indexOf("."));
		try{
			minute = (String)time.subSequence(time.indexOf("T")+1, time.indexOf("M"));
		}catch(java.lang.StringIndexOutOfBoundsException ex) {
			minute = "00";
			bigSecond = (String)time.subSequence(time.indexOf("T")+1, time.indexOf("."));
		}
		String smallSecond = (String)time.subSequence(time.indexOf(".")+1, time.indexOf(".")+4);
		if(minute.length()==1) {
			minute = "0"+minute;
		}
		if(bigSecond.length()==1) {
			bigSecond = "0"+bigSecond;
		}
		timeDisplay += minute+"分"+bigSecond+"."+smallSecond+"秒";
		return timeDisplay;
	}
	
	public static LocalDateTime start;
	public static LocalDateTime end;
}
