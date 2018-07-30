import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main implements EventListener {
    public static JDA jda;

    public static ArrayList<String> netflixAccounts = new ArrayList<String>();

    public static void main(String[] args)
            throws LoginException, InterruptedException{

        loadAccountsList("netflix.txt", netflixAccounts);

        jda = new JDABuilder(AccountType.BOT)
                .setToken("NDcxNzg3MzUyNTUwMDE0OTky.Djp5zg.oc-CRABaR7OH5kJO90POO0y8OpE")
                .addEventListener(new Main())
                .addEventListener(new MessageListener())
                .buildBlocking();
    }

    public void onEvent(Event event)
    {
        if (event instanceof ReadyEvent)
            System.out.println("Bot is ready!");
    }

    public static void loadAccountsList(String fileName, ArrayList accountList){
        try {
            for (String s : Files.readAllLines(Paths.get(fileName))) {
                accountList.add(s);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}