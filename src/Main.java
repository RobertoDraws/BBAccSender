import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Main implements EventListener {
    public static JDA jda;

    public static HashMap<String, ArrayList<String>> accountsList = new HashMap<>();

    public static void main(String[] args)
            throws LoginException, InterruptedException{

        loadAllAccounts();
        loadCooldownList();

        jda = new JDABuilder(AccountType.BOT)
                .setToken("bottoken")
                .addEventListener(new Main())
                .addEventListener(new MessageListener())
                .buildBlocking();
    }

    public void onEvent(Event event)
    {
        if (event instanceof ReadyEvent)
            System.out.println("Bot is ready!");
        else if(event instanceof GuildMemberLeaveEvent){
            GuildMemberLeaveEvent e = (GuildMemberLeaveEvent)event;
            String user = e.getUser().getName() + "#" + e.getUser().getDiscriminator();
            e.getGuild().getTextChannelById("425800280588812298").sendMessage(user + " has left!").queue();
        }
    }

    public static void loadAllAccounts(){
        loadAccountsList("WWE");
        loadAccountsList("UFC");
        loadAccountsList("Roku");
        loadAccountsList("Uplay");
        loadAccountsList("Udemy");
        loadAccountsList("NHL");
        loadAccountsList("Fitbit");
        loadAccountsList("Roblox");
        loadAccountsList("Spotify");
        loadAccountsList("HBO");
        loadAccountsList("Pandora");
        loadAccountsList("Xfinity");
        loadAccountsList("4Shared");
        loadAccountsList("Sephora");
        loadAccountsList("HotStar");
        loadAccountsList("Fortnite");
        loadAccountsList("Minecraft");
        loadAccountsList("Eurosport");
        loadAccountsList("DirectTV");
        loadAccountsList("Dominos");
        loadAccountsList("Reddit");
        loadAccountsList("Crunchyroll");
        loadAccountsList("VPNZenmate");
        loadAccountsList("Subway");
        loadAccountsList("CBS");
        loadAccountsList("Hulu");
        loadAccountsList("Plex");
        loadAccountsList("AMC");
        loadAccountsList("Virgin");
        loadAccountsList("Chegg");
        loadAccountsList("McAfee");
        loadAccountsList("NBA");
        loadAccountsList("Surfeasy");
        loadAccountsList("Mediafire");
        loadAccountsList("Dominos");
        loadAccountsList("LoL");
        loadAccountsList("Origin");
        loadAccountsList("LoL");
        loadAccountsList("Fasttech");
        loadAccountsList("Activision");
        loadAccountsList("BurgerKing");
        loadAccountsList("Footlocker");
        loadAccountsList("Tidal");
        loadAccountsList("Grammarly");
        loadAccountsList("NordVPN");
        loadAccountsList("Pornhub");
        loadAccountsList("MLB");
        loadAccountsList("HideMyAss");
        loadAccountsList("Formula1");
        loadAccountsList("RCTheatres");
        loadAccountsList("FoxMatchPass");
        loadAccountsList("Boomerang");
        loadAccountsList("Showtime");
        loadAccountsList("ShakeShack");
        loadAccountsList("PrimantiBros");
        loadAccountsList("Napster");
        loadAccountsList("SteamKeys");
        loadAccountsList("Udemy");
        loadAccountsList("Barnes&Noble");
        loadAccountsList("Scribd");
        loadAccountsList("MyCanal");
        loadAccountsList("ExpressVPN");
        loadAccountsList("SocialClub");
        loadAccountsList("Saavn");
        loadAccountsList("TorGuard");
        loadAccountsList("Jetblue");
    }

    public static void loadAccountsList(String accountType){
        try {
            ArrayList<String> accountList = new ArrayList<>();

            if(!Files.exists(Paths.get("accounts/"+accountType+".txt"))){
                BufferedWriter out = new BufferedWriter(new FileWriter("accounts/"+accountType+".txt", true));
                out.write("");
                out.close();
            }

            for (String s : Files.readAllLines(Paths.get("accounts/"+accountType+".txt"))) {
                accountList.add(s);
            }

            if(Files.exists(Paths.get("accounts/used/"+accountType+".txt"))) {
                for (String s : Files.readAllLines(Paths.get("accounts/used/"+accountType+".txt"))){
                    if(accountList.contains(s))
                        accountList.remove(s);
                }
            }
            accountsList.put(accountType, accountList);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToFile(String fileName, String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write(str);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCooldownList(){
        try {
            String json = "";
            for(String s : Files.readAllLines(Paths.get("cooldowns.json"))){
                json += s;
            }

            Cooldown.cooldowns = new Gson().fromJson(json, new TypeToken<HashMap<Long, Cooldown>>(){}.getType());
            Cooldown.startCooldowns();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void saveCooldownList(){
        try {
            PrintWriter writer = new PrintWriter("cooldowns.json", "UTF-8");
            writer.print(new Gson().toJson(Cooldown.cooldowns));
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void log(String s){
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
        String dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        appendToFile("logs/" + date + ".txt", "[" + dateTime + "] " + s + "\n");
    }

}
