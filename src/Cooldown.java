import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Cooldown {

    public static HashMap<Long, Cooldown> cooldowns = new HashMap<>();

    private String timeCooldownOver;
    private int numGenned = 0;

    public Cooldown(long uid){
        new Thread(() -> {
            try{
                Thread.sleep(86400000);
                cooldowns.remove(uid);
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        Instant cooldown = Instant.now().plus(1, ChronoUnit.DAYS);
        timeCooldownOver = cooldown.toString();

        cooldowns.put(uid, this);
        Main.saveCooldownList();
    }

    public static void startCooldowns(){
        for(Cooldown c : cooldowns.values()){
            new Thread(() -> {
                try{
                    DateTimeFormatter fmt = DateTimeFormatter.ISO_ZONED_DATE_TIME;
                    Instant event = fmt.parse(c.timeCooldownOver, Instant::from);
                    Instant now = Instant.now();
                    Duration diff = Duration.between(now, event);
                    System.out.println("Loaded cooldown for " + c.getUid() + " waiting " + diff.toMillis() + " millis.");
                    if(diff.toMillis() > 0) Thread.sleep(diff.toMillis());
                    cooldowns.remove(c.getUid());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public long getUid(){
        for(long uid : cooldowns.keySet()){
            if(cooldowns.get(uid) == this){
                return uid;
            }
        }
        return 0;
    }

    public int getNumGenned(){
        return numGenned;
    }

    public void incNumGenned(){
        numGenned++;
        Main.saveCooldownList();
    }

    public String timeUntilReset(){
        DateTimeFormatter fmt = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        Instant event = fmt.parse(timeCooldownOver, Instant::from);
        Instant now = Instant.now();
        Duration diff = Duration.between(now, event);
        return intToStringDuration(diff.getSeconds());
    }

    private String intToStringDuration(long aDuration) {
        String result = "";

        long hours = 0, minutes = 0, seconds = 0;

        hours = aDuration / 3600;
        minutes = (aDuration - hours * 3600) / 60;
        seconds = (aDuration - (hours * 3600 + minutes * 60));

        result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return result;
    }

    public static Cooldown getCooldownForUser(long uid){
        if(cooldowns.containsKey(uid))
            return cooldowns.get(uid);
        else
            return new Cooldown(uid);
    }

}
